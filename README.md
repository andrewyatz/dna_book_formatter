# DNA Formatter

# Synopsis

```bash
gradle build
java -Xmx6G -classpath build/classes/java/main:. dnaformatter.Main Homo_sapiens.GRCh38.dna.chromosome.22.fa genes.bed output.html
```

# Creating BED

```bash
./main/perl/fetch_genes.pl genes.bed
```

# Getting FASTA

```bash
wget ftp://ftp.ensembl.org/pub/current_fasta/homo_sapiens/dna/Homo_sapiens.GRCh38.dna.chromosome.22.fa.gz
gunzip Homo_sapiens.GRCh38.dna.chromosome.22.fa.gz
```

# Notes

Pretty much everything is hard-coded to chromosome 22