#!/bin/bash

mem="4096m"
input_fasta=22.fa
input_bed=sorted.bed
basename=350by350_200bp_perline
output_html=${basename}.html
output_pdf=${basename}.pdf

if [ ! -f $input_fasta ]; then
  echo "Cannot find $input_fasta"
fi

echo "Build JARs"
gradle jar
gradle jarHtmlToPdf
echo "Write HTML"
java -Xmx${mem} -Xms${mem} -jar build/libs/dna-formatter-0.1.0.jar $input_fasta $input_bed $output_html
echo "Write PDF"
HTML=$output_html PDF=$output_pdf gradle myRun
echo "Done?"
