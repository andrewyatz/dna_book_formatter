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

public class FrontPageMain {
  public static void main(String args[]) throws Exception {
    ReadFasta reader = new ReadFasta(new File(args[0]));
    PrintStream ps = new PrintStream(new BufferedOutputStream(new FileOutputStream(new File(args[1]))));

    int fontSize;
    int basesPerLine;
    if (args.length == 4) {
      fontSize = Integer.parseInt(args[2]);
      basesPerLine = Integer.parseInt(args[3]);
    } else {
      fontSize = FrontPageHtml.DEFAULT_FONT_SIZE;
      basesPerLine = FrontPageHtml.DEFAULT_WIDTH;
    }
    System.out.println("Reading FASTA from " + args[0]);
    for (Sequence seq : reader) {
      System.out.println("Writing out to " + args[1]);
      FrontPageHtml output = new FrontPageHtml(fontSize, basesPerLine, ps, seq);
      output.format();
    }
  }
}