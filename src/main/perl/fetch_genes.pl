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
    foreach my $utr (@{$canonical->get_all_five_prime_UTRs()}) {
      print $fh join("\t", $chr, $utr->seq_region_start()-1, $utr->seq_region_end(), 'UTR');
      print $fh "\n";
    }
    foreach my $exon (@{$canonical->get_all_translateable_Exons()}) {
      print $fh join("\t", $chr, $exon->seq_region_start()-1, $exon->seq_region_end(), 'CDS');
      print $fh "\n";
    }
    foreach my $utr (@{$canonical->get_all_three_prime_UTRs()}) {
      print $fh join("\t", $chr, $utr->seq_region_start()-1, $utr->seq_region_end(), 'UTR');
      print $fh "\n";
    }
  }
  else {
    foreach my $exon (@{$canonical->get_all_Exons()}) {
      print $fh join("\t", $chr, $exon->seq_region_start()-1, $exon->seq_region_end(), $type);
      print $fh "\n";
    }
  }
}