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

    // ReadFasta reader = new ReadFasta(">1\nACGT\nACGT\nACGT\nACGT\nACGT");
    for(Sequence seq: reader) {
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
      Html output = new Html(ps, seq);
      output.format();
    }
  }
}