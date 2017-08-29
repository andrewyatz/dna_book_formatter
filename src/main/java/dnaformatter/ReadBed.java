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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

public class ReadBed implements Iterable<BedRecord>, Iterator<BedRecord> {

  private BufferedReader br;
  private String line;
  public ReadBed(File file) throws IOException {
    br = new BufferedReader(new FileReader(file));
    line = br.readLine(); // read the first line
  }
  
  public Iterator<BedRecord> iterator() {
    return this;
  }

  public boolean hasNext() {
    return null != line;
  }

  public BedRecord next() {
    String[] splitLine = line.split("\t");
    BedRecord record = new BedRecord(splitLine[0], Integer.valueOf(splitLine[1]).intValue(), Integer.valueOf(splitLine[2]).intValue(), splitLine[3]);
    try {
      line = br.readLine();
    } 
    catch(IOException e) {
      throw new RuntimeException(e);
    }
    return record;
  }

  public void close() throws IOException {
    br.close();
  }

}