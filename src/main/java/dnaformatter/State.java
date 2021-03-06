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

public enum State {

  GENOMIC("black", false, false, false), 
  NONCODING("black", false, false, false), 
  STARTCODON("#FAEB00", false, true, false),
  UTR("#FAEB00", false, true, false), 
  CDS("#FAEB00", false, true, false), 
  INTRON("#ffffaa", false, true, false),
  ENDCODON("#FAEB00", false, true, false),
  CODON("#FAEB00", false, true, false),
  PSEUDOGENE("#dddddd", false, true, false);

  private String colour;
  private boolean upperCase;
  private boolean backgroundColour;
  private boolean bold;

  private State(String colour, boolean upperCase, boolean backgroundColour, boolean bold) {
    this.colour = colour;
    this.upperCase = upperCase;
    this.backgroundColour = backgroundColour;
    this.bold = bold;
  }

  public String getColour() {
    return this.colour;
  }

  public boolean isUpperCase() {
    return this.upperCase;
  }

  public boolean isBackgroundColour() {
    return this.backgroundColour;
  }

  public boolean isBold() {
    return this.bold;
  }

  public static State ignoreCaseValueOf(String valueOf) {
    return State.valueOf(valueOf.toUpperCase());
  }

}