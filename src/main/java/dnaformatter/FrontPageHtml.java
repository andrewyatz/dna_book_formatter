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

import java.io.PrintStream;
import java.util.List;

public class FrontPageHtml {

  public static final String PAGE_SIZE = "size: 800mm 370mm;";
  public static final int DEFAULT_WIDTH = 200;
  public static final int DEFAULT_FONT_SIZE = 13;

  private int basesPerLine;
  private int fontSize;
  private PrintStream ps;
  private Sequence sequence;

  public FrontPageHtml(PrintStream ps, Sequence sequence) {
    this(DEFAULT_FONT_SIZE, DEFAULT_WIDTH, ps, sequence);
  }

  public FrontPageHtml(int basesPerLine, PrintStream ps, Sequence sequence) {
    this(DEFAULT_FONT_SIZE, basesPerLine, ps, sequence);
  }

  public FrontPageHtml(int fontSize, int basesPerLine, PrintStream ps, Sequence sequence) {
    this.basesPerLine = basesPerLine;
    this.ps = ps;
    this.sequence = sequence;
    this.fontSize = fontSize;
  }

  public void printHeader() {
    ps.println("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
    ps.println("<head>");
    ps.println("<style>");
    ps.println("@page { " + PAGE_SIZE + "}");
    ps.println("body {");
    ps.println("font-family: monospace;");
    ps.println("font-size: " + fontSize + "px;");
    ps.println("}");
    ps.println(".pos {");
    ps.println("font-weight: bold;");
    ps.println("}");
    ps.println("</style>");
    ps.println("</head>");
    ps.println("<body>");
  }

  public void printFooter() {
    ps.println("</body>");
    ps.println("</html>");
  }

  public void format() {
    printHeader();
    printBody();
    printFooter();
    ps.flush();
  }

  public void printBody() {
    List<Element> seqList = sequence.getSequence();
    int totalLength = seqList.size();
    int maxCoord = 0;

    for (int minCoord = 0; minCoord < totalLength; minCoord += this.basesPerLine) {
      maxCoord += this.basesPerLine;
      if (maxCoord > totalLength) {
        maxCoord = totalLength;
      }

      State lastState = State.GENOMIC;
      for (int i = minCoord; i < maxCoord; i++) {
        Element e = seqList.get(i);
        State currentState = e.getState();
        // New block and figure out if we need to close the last one down
        if (currentState != lastState) {
          if (lastState != State.GENOMIC) {
            ps.print("</span>"); // Close down last state
          }

          if (currentState != State.GENOMIC) {
            ps.printf("<span class='%s'>", currentState.toString()); // open a new state
          }
        }
        lastState = currentState;
        // Print sequence
        ps.print(Character.toLowerCase(e.getBase()));
      }

      // We were still in an active state when the loop finished so close down the
      // markup
      if (lastState != State.GENOMIC) {
        ps.print("</span>");
      }
      // And break the line
      ps.println("<br/>");
    }
  }
}