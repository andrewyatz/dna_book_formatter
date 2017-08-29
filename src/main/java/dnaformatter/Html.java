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

public class Html {

  public static final int DEFAULT_WIDTH = 150;

  private int basesPerLine;
  private PrintStream ps;
  private Sequence sequence;

  public Html(PrintStream ps, Sequence sequence) {
    this(DEFAULT_WIDTH, ps, sequence);
  }
  public Html(int basesPerLine, PrintStream ps, Sequence sequence) {
    this.basesPerLine = basesPerLine;
    this.ps = ps;
    this.sequence = sequence;
  }

  public void printHeader() {
    ps.println("<html>");
    ps.println("<style>");
    ps.println("body {");
    ps.println("font-family: monospace;");
    ps.println("}");
    ps.println(".pos {");
    ps.println("font-weight: bold;");
    ps.println("}");
    for(State state: State.values()) {
      ps.println("."+state.toString()+" {");
      ps.println("color: "+state.getColour()+";");
      if(state.isUpperCase()) {
        ps.println("text-transform: uppercase;");
      }
      ps.println("}");
    }
    ps.println("</style>");
    ps.println("<body>");
  }

  public void printFooter() {
    // ps.println("");
    // ps.println("");
    // ps.println("");
    // ps.println("");
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
    int lengthWidth = Integer.toString(totalLength).length();
    int maxCoord = 0;
    String numberPaddingFormat = "<span class='pos'>%0"+lengthWidth+"d</span>&nbsp;&nbsp;";
    
    for(int minCoord = 0; minCoord < totalLength; minCoord += this.basesPerLine) {
      maxCoord += this.basesPerLine;
      if(maxCoord > totalLength) {
        maxCoord = totalLength;
      }

      ps.printf(numberPaddingFormat, minCoord+1);

      State lastState = State.GENOMIC;
      // System.err.println("Working on "+minCoord+" to "+maxCoord);
      for(int i = minCoord; i < maxCoord; i++) {
        Element e = seqList.get(i);
        State currentState = e.getState();
        // New block and figure out if we need to close the last one down
        if(currentState != lastState) {
          if(lastState != State.GENOMIC) {
            ps.print("</span>"); //Close down last state
          }

          if(currentState != State.GENOMIC) {
            ps.printf("<span class='%s'>", currentState.toString()); //open a new state
          }
        }
        lastState = currentState;
        //Print sequence
        ps.print(Character.toLowerCase(e.getBase()));
      }

      //We were still in an active state when the loop finished so close down the markup
      if(lastState != State.GENOMIC) {
        ps.print("</span>");
      }
      // And break the line
      ps.println("<br/>");
    }
  }
}