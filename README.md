# DNA Formatter

# Synopsis

```bash
gradle build
java -Xmx6G -classpath build/classes/java/main:. dnaformatter.Main Homo_sapiens.GRCh38.dna.chromosome.22.fa genes.bed output.html
```

# Creating BED

Assumes that Ensembl API and BioPerl are available from `../../ensembl/ensembl/modules` and `../../thirdparty/bioperl-live` respectively. Code connects to live DB and dumps all genes for a given chromsome.

```bash
mkdir -p bed_dumps
for chr in 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 X Y MT; do
perl -I ../../thirdparty/bioperl-live/ -I ../../ensembl/ensembl/modules/ src/main/perl/fetch_genes.pl --host ensembldb.ensembl.org --port 3306 --user anonymous --species human --chromosome $chr --output bed_dumps/${chr}.bed;
done
```

# Getting FASTA

```bash
mkdir -p fasta
for chr in 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 X Y MT; do
  echo Getting Fasta $chr
  curl -s "ftp://ftp.ensembl.org/pub/current_fasta/homo_sapiens/dna/Homo_sapiens.GRCh38.dna.chromosome.${chr}.fa.gz" | gzip -dc > fasta/${chr}.fa
  echo Wrote it to fasta/${chr}.fa
done
```

If you do not want to use the flat files you can use the Perl API instead. The following will fetch human unmasked DNA from ensembldb.ensembl.org for the API's release number in FASTA.

```bash
perl ./src/main/perl/fetch_dna_from_db.pl --chromosome Y
```

# Creating the HTML

```bash
mkdir -p html_output
for chr in 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 X Y MT; do
  echo Running $chr HTML production
  java -Xmx6G -classpath build/classes/java/main:. dnaformatter.Main fasta/${chr}.fa bed_dumps/${chr}.bed html_output/${chr}.html
done
```

# Creating PDF

```bash
mkdir -p pdf_output
for chr in 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 X Y MT; do
  echo Running $chr PDF production
  HTML=html_output/${chr}.html PDF=pdf_output/${chr}.pdf gradle myRun
done
```

# Now with added bsub

## Building HTML

```bash
mkdir -p html_output
for chr in 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 X Y MT; do
  echo Running $chr HTML production
  bsub -I -M 16384 -R "rusage[mem=16384]" java -Xmx16G -classpath build/classes/java/main:. dnaformatter.Main fasta/${chr}.fa bed_dumps/${chr}.bed html_output/${chr}.html
done
```

## Building PDFs

```bash
mkdir -p pdf_output
for chr in 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 X Y MT; do
  echo Running $chr PDF production
  export HTML=html_output/${chr}.html
  export PDF=pdf_output/${chr}.pdf
  export GRADLE_OPTS="-Dorg.gradle.daemon=false"
  bsub -I -M 16384 -R "rusage[mem=16384]" gradle myRun
done
```

## Front Cover Work

### Building FASTAs

```bash
mkdir -p small_fasta
perl src/main/perl/seq_subseq.pl
```

### Generating HTML

```bash
mkdir -p small_html
gradle jar
for chr in 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 X Y; do
  java -classpath ./build/libs/dna-formatter-0.1.0.jar dnaformatter.FrontPageMain small_fasta/${chr}.fa small_html/${chr}.html 13 370
done
```

### Generating PDFs

```bash
mkdir -p small_pdf
gradle jar
for chr in 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 X Y; do
  export HTML=small_html/${chr}.html
  export PDF=small_pdf/${chr}.pdf
  export GRADLE_OPTS="-Dorg.gradle.daemon=false"
  gradle myRun
done
```