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

use Bio::EnsEMBL::Registry;
use Bio::EnsEMBL::DBSQL::DBConnection;

my $output = $ARGV[0];
open my $fh, '>', $output or die "Cannot open $output for writing: $?";

my $chr = '22';

my %args = (-HOST => 'ensembldb.ensembl.org', -USER => 'anonymous');
Bio::EnsEMBL::Registry->load_registry_from_db(%args);
my $dbc = Bio::EnsEMBL::DBSQL::DBConnection->new(%args, -DBNAME => 'ensembl_production_90' );

my $group_lookup = $dbc->sql_helper()->execute_into_hash(-SQL => 'select name, biotype_group from biotype');

my $sa = Bio::EnsEMBL::Registry->get_adaptor('human', 'core', 'slice');
my $slice = $sa->fetch_by_region('toplevel', $chr);
my $genes = $slice->get_all_Genes();
while ( my $gene = shift @{$genes} ) {
  my $canonical = $gene->canonical_transcript();
  my $bt = $canonical->biotype();
  next if $bt =~ /antisense/;
  my $group = $group_lookup->{$bt};
  my $type;
  if($group eq 'coding') {
    $type = 'CODING';
  }
  elsif($group =~ /noncoding/) {
    $type = 'NONCODING';
  }
  elsif($group eq 'pseudogene') {
    $type = 'PSEUDOGENE';
  }
  else {
    next;
  }

  if($type eq 'CODING') {
    my $utrs = $canonical->get_all_five_prime_UTRs();
    my $last_five_prime_utr = undef;
    foreach my $utr (@{$canonical->get_all_five_prime_UTRs()}) {
      if($last_five_prime_utr) {
        write_intron($last_five_prime_utr, $utr);
      }
      print $fh join("\t", $chr, $utr->seq_region_start()-1, $utr->seq_region_end(), 'UTR');
      print $fh "\n";
      $last_five_prime_utr = $utr;
    }
    my $trans_exons = $canonical->get_all_translateable_Exons();
    my $length = scalar(@{$trans_exons});
    my $iter = 1;
    my $last_exon = undef;
    foreach my $exon (@{$trans_exons}) {

      if($last_exon) { # only trigger when we have a last exon
        write_intron($last_exon, $exon);
      }

      my $exon_start = $exon->seq_region_start();
      my $exon_end = $exon->seq_region_end();
      my @leading_codon;
      my @trailing_codon;
      if($iter == 1) { #first exon check init exon
        my $codon_start = $exon_start;
        my $codon_end = $exon_start+2;
        $exon_start = $exon_start+3;
        my $codon_type = ($canonical->seq_region_strand() == -1) ? 'ENDCODON' : 'STARTCODON';
		    @leading_codon = ($chr, $codon_start-1, $codon_end, $codon_type);
      }
	    if($iter == $length) { # last exon check; alter
        my $codon_start = $exon_end - 2;
        my $codon_end = $exon_end;
        $exon_end = $exon_end-3;
        my $codon_type = ($canonical->seq_region_strand() == -1) ? 'STARTCODON' : 'ENDCODON';
        @trailing_codon = ($chr, $codon_start-1, $codon_end, $codon_type);
	    }
      if(@leading_codon) {
        print $fh join("\t", @leading_codon);
        print $fh "\n";
      }
      if($exon_start != $exon_end) {
        print $fh join("\t", $chr, $exon_start-1, $exon_end, 'CDS');
        print $fh "\n";
      }
      if(@trailing_codon) {
        print $fh join("\t", @trailing_codon);
        print $fh "\n";
      }

      $last_exon = $exon;
      $iter++;
    }
    my $last_three_prime_utr = undef;
    foreach my $utr (@{$canonical->get_all_three_prime_UTRs()}) {
      if($last_three_prime_utr) {
        write_intron($last_three_prime_utr, $utr);
      }
      print $fh join("\t", $chr, $utr->seq_region_start()-1, $utr->seq_region_end(), 'UTR');
      print $fh "\n";
      $last_three_prime_utr = $utr;
    }
  }
  else {
    foreach my $exon (@{$canonical->get_all_Exons()}) {
      print $fh join("\t", $chr, $exon->seq_region_start()-1, $exon->seq_region_end(), $type);
      print $fh "\n";
    }
  }
}

sub write_intron {
  my ($last_exon, $exon) = @_;
  my $intron_start = $last_exon->seq_region_end()+1;
  my $intron_end = $exon->seq_region_start()-1;
  print $fh join("\t", $chr, $intron_start-1, $intron_end, 'INTRON');
  print $fh "\n";
  return;
}