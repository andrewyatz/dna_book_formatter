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

  GENOMIC("black", false, false), 
  UTR("green", false, true), 
  CDS("red", true, true), 
  NONCODING("blue", true, true), 
  PSEUDOGENE("grey", true, true),
  STARTCODON("red", true, true),
  ENDCODON("red", true, true);

  private String colour;
  private boolean upperCase;
  private boolean backgroundColour;

  private State(String colour, boolean upperCase, boolean backgroundColour) {
    this.colour = colour;
    this.upperCase = upperCase;
    this.backgroundColour = backgroundColour;
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

  public static State ignoreCaseValueOf(String valueOf) {
    return State.valueOf(valueOf.toUpperCase());
  }

}