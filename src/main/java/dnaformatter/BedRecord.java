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

public class BedRecord {

  private String chromosome;
  private int start;
  private int end;
  private String type;

  public BedRecord(String chromosome, int start, int end, String type) {
    this.chromosome = chromosome;
    this.start = start;
    this.end = end;
    this.type = type;
  }
  public String getChromosome() {
    return chromosome;
  }

  public int getStart() {
    return start;
  }

  public int getEnd() {
    return end;
  }


  public String getType() {
    return type;
  }

  public State getState() {
    return State.ignoreCaseValueOf(getType());
  }
}