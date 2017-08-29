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

import java.util.ArrayList;
import java.util.List;

public class Sequence {

  private List<Element> sequence;
  private String name;

  public Sequence() {
    this.sequence = new ArrayList<Element>();
  }

  public Sequence(String name) {
    setName(name);
  }

  public List<Element> getSequence() {
    return this.sequence;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return this.name;
  }

  public void markup(BedRecord record, boolean ignore) {
    markupZeroHalf(record.getStart(), record.getEnd(), record.getState(), ignore);
  }

  /**
   * Use this to annoate UCSC BED (0-start, half-open)
   */
  public void markupZeroHalf(int start, int end, State state, boolean ignore) {
    boolean regionAlreadySet = false;
    // if we are ignoring clashing regions then skip this test
    if(!ignore) {  
      for(int i = start; i < end; i++) {
        Element e = sequence.get(i);
        if(e.getState() != State.GENOMIC) {
          //throw new RuntimeException("Cannot overwrite position "+i+" (zero based coord) with state "+state+" because it was already set to state "+e.getState());
          regionAlreadySet = true;
          break;
        }
      }
    }
    // If the region wasn't already set to something then loop through and set
    if(!regionAlreadySet) {
      for(int i = start; i < end; i++) {
        sequence.get(i).setState(state);
      }
    }
  }

  /**
   * Use this to annotate Ensembl/GFF (1-start, fully-closed)
   */
  public void markupOneClosed(int start, int end, State state, boolean ignore) {
    markupZeroHalf(start-1, end, state, ignore);
  }
}