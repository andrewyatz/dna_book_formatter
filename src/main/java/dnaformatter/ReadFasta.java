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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.File;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.IOException;

public class ReadFasta implements Iterable<Sequence>, Iterator<Sequence> {

  private BufferedReader br;
  private String lastName;
  private Sequence currentSequence;
  private Pattern headerPattern = Pattern.compile("^>(\\w+)\\s?");

  //Once constructed get the next sequence
  public ReadFasta(Reader r) throws IOException {
    this.br = new BufferedReader(r);
    this.lastName = null;
    this.currentSequence = getNextSequence();
  }

  public ReadFasta(File file) throws IOException {
    this(new FileInputStream(file));
  }

  public ReadFasta(InputStream is) throws IOException {
    this(new InputStreamReader(is));
  }

  public ReadFasta(String fastaString) throws IOException {
    this(new StringReader(fastaString));
  }

  public Iterator<Sequence> iterator() {
    return this;
  }

  public boolean hasNext() {
    return this.currentSequence != null;
  }

  public Sequence next() {
    Sequence toReturn = this.currentSequence;
    try {
      this.currentSequence = getNextSequence();
    }
    catch(IOException e) {
      throw new RuntimeException(e);
    }
    return toReturn;
  }

  public Sequence getNextSequence() throws IOException {
    String line;
    Sequence sequence = null;
    
    while((line = br.readLine()) != null) {
      if(sequence == null) {
        sequence = new Sequence();
      }
      //FASTA header
      if(line.startsWith(">")) {
        Matcher m = headerPattern.matcher(line);
        String name = null;
        if(m.find()) {
          name = m.group(1);
        }
        if(name == null) {
          throw new RuntimeException("Cannot find the sequence name in "+line);
        }
        // If last name was null then this is the 1st sequence
        if(this.lastName == null) {
          sequence.setName(name);
          this.lastName = name;
        }
        // We are already in the middle of iteration
        // this is the next record so set the last name to sequence and break
        else {
          sequence.setName(this.lastName);
          this.lastName = name;
          break;
        }
      }
      else {
        List<Element> seqList = sequence.getSequence();
        for(Character c: line.toCharArray()) {
          seqList.add(new Element(c));
        }
      }
    }
    // Ran out of lines
    if(sequence != null && sequence.getName() == null) {
      sequence.setName(this.lastName);
    }
    return sequence;
  }

  public void remove() {
    throw new UnsupportedOperationException("Cannot remove items from iterator");
  }

  public void close() throws IOException {
    this.br.close();
  }
}
