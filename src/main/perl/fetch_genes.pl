#!/usr/bin/env perl

#Licensed under the Apache License, Version 2.0 (the "License");
#you may not use this file except in compliance with the License.
#You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
#Unless required by applicable law or agreed to in writing, software
#distributed under the License is distributed on an "AS IS" BASIS,
#WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#See the License for the specific language governing permissions and
#limitations under the License.

use strict;
use warnings;

use Bio::EnsEMBL::ApiVersion;
use Bio::EnsEMBL::Registry;
use Bio::EnsEMBL::DBSQL::DBConnection;

use Getopt::Long;

my $output;
my $chromosome;
my ($host, $user, $port, $species, $production_db);
GetOptions(
  'output=s' => \$output,
  'chromosome=s' => \$chromosome,
  'host=s' => \$host,
  'port=i' => \$port,
  'user=s' => \$user,
  'species=s' => \$species,
  'production_db=s' => \$production_db) or die "Could not parse command line args";

$host //= 'ensembldb.ensembl.org';
$port //= 3306;
$user //= 'anonymous';
$production_db //= 'ensembl_production_'.Bio::EnsEMBL::ApiVersion::software_version();
$species //= 'homo_sapiens';

die "No chromosome given" if !$chromosome;
die "No output location given" if !$output;
open my $fh, '>', $output or die "Cannot open $output for writing: $?";

warn 'Connecting to database server '.$host;
my %args = (-HOST => $host, -USER => $user, -PORT => $port);
warn 'Loading database registry';
Bio::EnsEMBL::Registry->load_registry_from_db(%args);
warn 'Connecting to '.$production_db;
my $dbc = Bio::EnsEMBL::DBSQL::DBConnection->new(%args, -DBNAME => $production_db );

my @biotypes = qw/protein_coding pseudogene polymorphic_pseudogene processed_pseudogene transcribed_processed_pseudogene transcribed_unitary_pseudogene transcribed_unprocessed_pseudogene unitary_pseudogene unprocessed_pseudogene/;
my $group_lookup = $dbc->sql_helper()->execute_into_hash(-SQL => 'select name, biotype_group from biotype');

my $sa = Bio::EnsEMBL::Registry->get_adaptor($species, 'core', 'slice');
my $ga = Bio::EnsEMBL::Registry->get_adaptor($species, 'core', 'gene');
if(!$sa) {
  die "Cannot get slice adaptor for the species ${species}";
}

my $slice = $sa->fetch_by_region('toplevel', $chromosome);
warn 'Fetching all genes for chromosome '.$chromosome;

my @genes = sort {$a->seq_region_start() <=> $b->seq_region_start}
            grep {
              if(uc($chromosome) eq 'Y') { # fetch the raw gene then compare names. If they are the same then exclude
                my $raw_gene = $ga->fetch_by_stable_id($_->stable_id());
                ($raw_gene->seq_region_name() eq $_->seq_region_name());
              }
              else {
                1;
              }
            } # filter out things that are actually on a par if on ChrY
            map { @{$slice->get_all_Genes(undef, undef, 1, undef, $_)} }
            @biotypes;

warn "Writing genes to ${output}";
while ( my $gene = shift @genes ) {
  my $canonical = $gene->canonical_transcript();
  my $id = $canonical->stable_id();
  my $bt = $canonical->biotype();
  next if $bt =~ /antisense/;
  next if $bt =~ /nonsense/;
  my $group = $group_lookup->{$bt};
  my $type;
  if($group eq 'coding') {
    $type = 'CODING';
  }
  # elsif($group =~ /noncoding/) {
  #   $type = 'NONCODING';
  # }
  elsif($group =~ /pseudogene/) {
    $type = 'PSEUDOGENE';
  }
  else {
    next;
  }

  # THIS CODE IS NOT CORRECT. IT
  # - get all translatale exons and assumes our API truncates the exons to its start/end (translatable). It does not
  # - things are sorted by seq region start; they're were nto
  # - confuses 5' and 3' with seq region positions

  if($type eq 'CODING') {

    my $cds_features = $canonical->get_all_CDS();
    my $start_cds = $cds_features->[0];
    my $end_cds = $cds_features->[-1];
    my ($start_codon_start, $start_codon_end, $stop_codon_start, $stop_codon_end) = (0,0,0,0);
    # Create start/stop codons
    if($canonical->strand() == -1) {
      ($start_codon_start, $start_codon_end, $stop_codon_start, $stop_codon_end) = (
        $end_cds->seq_region_start(), ($end_cds->seq_region_start+2),
        ($start_cds->seq_region_end()-2), $start_cds->seq_region_end(),
      );
      $start_cds->seq_region_end($start_cds->seq_region_end()-3);
      $end_cds->seq_region_start($end_cds->seq_region_start()+3);
    }
    else {
      ($start_codon_start, $start_codon_end, $stop_codon_start, $stop_codon_end) = (
        $start_cds->seq_region_start(), ($start_cds->seq_region_start()+2),
        ($end_cds->seq_region_end()-2), $end_cds->seq_region_end(),
      );
      $start_cds->seq_region_start($start_cds->seq_region_start()+3);
      $end_cds->seq_region_end($end_cds->seq_region_end()-3);
    }
    my $start_codon = Bio::EnsEMBL::Feature->new(
      -start => $start_codon_start, -end => $start_codon_end, strand => $canonical->strand(),
      -slice => $canonical->slice(), -analysis => $canonical->analysis());
    my $end_codon = Bio::EnsEMBL::Feature->new(
      -start => $stop_codon_start, -end => $stop_codon_end, strand => $canonical->strand(),
      -slice => $canonical->slice(), -analysis => $canonical->analysis());

    my @features = (
      @{ $cds_features},
      @{ $canonical->get_all_five_prime_UTRs()},
      @{ $canonical->get_all_three_prime_UTRs()},
      @{ $canonical->get_all_Introns()},
      $start_codon, $end_codon
    );
    foreach my $feature (sort { $a->seq_region_start() <=> $b->seq_region_start() } @features) {
      write_feature($feature, $id);
    }
  }
  else {
    foreach my $exon (sort { $a->seq_region_start() <=> $b->seq_region_start() } @{$canonical->get_all_Exons()}) {
      write_feature($exon, $id, $type);
    }
  }
}

sub write_feature {
  my ($feature, $id, $type_override) = @_;
  my $r = ref($feature);
  my $type = undef;
  if($type_override) {
    $type = $type_override;
  }
  else {
    $type = {
      'Bio::EnsEMBL::CDS' => 'CDS',
      'Bio::EnsEMBL::Intron' => 'INTRON',
      'Bio::EnsEMBL::UTR' => 'UTR',
      'Bio::EnsEMBL::Feature' => 'CODON',
    }->{$r};
  }
  print $fh join("\t", $chromosome, $feature->seq_region_start()-1, $feature->seq_region_end(), $type, $feature->seq_region_strand(), $id);
  print $fh "\n";
}

sub write_intron {
  my ($last_exon, $exon, $id) = @_;
  my $intron_start = $last_exon->seq_region_end()+1;
  my $intron_end = $exon->seq_region_start()-1;
  print $fh join("\t", $chromosome, $intron_start-1, $intron_end, 'INTRON', $id);
  print $fh "\n";
  return;
}