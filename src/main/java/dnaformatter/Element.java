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

public class Element {

  private Character base;
  private State state;

  public Element(Character base) {
    this(base, State.GENOMIC);
  }
  public Element(Character base, State state) {
    this.base = base;
    this.state = state;
  }

  public Character getBase() {
    return this.base;
  }
  
  public State getState() {
    return this.state;
  }

  public void setState(State state) {
    this.state = state;
  }
}