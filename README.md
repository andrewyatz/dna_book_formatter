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
