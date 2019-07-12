/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dnaformatter;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

import dnaformatter.ReadFasta;

public class Main {
  public static void main(String args[]) throws Exception {
    // System.out.println("Hello, world!");
    ReadFasta reader = new ReadFasta(new File(args[0]));
    ReadBed bed = new ReadBed(new File(args[1]));
    PrintStream ps = new PrintStream(new BufferedOutputStream(new FileOutputStream(new File(args[2]))));

    int fontSize = Html.DEFAULT_FONT_SIZE;
    int basesPerLine = Html.DEFAULT_WIDTH;
    String pageSize = Html.DEFAULT_PAGE_SIZE;
    if(args.length > 3) {
      if(args.length >= 5) {
        fontSize = Integer.parseInt(args[3]);
        basesPerLine = Integer.parseInt(args[4]);
      }
      if(args.length == 6) {
        pageSize = args[5];
      }
    }

    // ReadFasta reader = new ReadFasta(">1\nACGT\nACGT\nACGT\nACGT\nACGT");
    System.out.println("Reading FASTA from "+args[0]);
    for(Sequence seq: reader) {
      System.out.println("Iterating through BED from "+args[1]);
      for(BedRecord record: bed) {
        seq.markup(record, true); //markup and ignore clashing regions
      }
      // seq.markupZeroHalf(1, 4, State.CDS, false);
      // seq.markupOneClosed(1, 2, State.UTR, true);
      // System.err.println(seq.getName()+" : "+seq.getSequence().size());
      // for(Element e: seq.getSequence()) {
      //   System.out.println("\t"+e.getBase()+" : "+e.getState().toString());
      // }

      // PrintStream ps = new PrintStream(System.out);
      // Html output = new Html(3, ps, seq);
      System.out.println("Writing out to " + args[2]);
      Html output = new Html(fontSize, basesPerLine, ps, seq, pageSize);
      output.format();
    }
  }
}