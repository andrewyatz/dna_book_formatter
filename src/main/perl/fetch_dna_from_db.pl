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
) or die "Could not parse command line args";

$host //= 'ensembldb.ensembl.org';
$port //= 3306;
$user //= 'anonymous';
$species //= 'homo_sapiens';

die "No chromosome given" if !$chromosome;
die "No output location given" if !$output;
open my $fh, '>', $output or die "Cannot open $output for writing: $?";

warn 'Connecting to database server '.$host;
my %args = (-HOST => $host, -USER => $user, -PORT => $port);
warn 'Loading database registry';
Bio::EnsEMBL::Registry->load_registry_from_db(%args);

my $sa = Bio::EnsEMBL::Registry->get_adaptor($specie, 'core', 'slice');
if(!$sa) {
	die "Cannot get slice adaptor for the species ${species}";
}

my $slice = $sa->fetch_by_region('toplevel', $chromosome);
warn 'Fetching and writing fully assembled DNA for '.$chromosome;

my $serializer = Bio::EnsEMBL::Utils::IO::FASTASerializer->new($fh);
$serializer->print_Seq($slice);
close $fh;
warn 'Finished';