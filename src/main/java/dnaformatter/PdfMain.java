package dnaformatter;

import java.io.*;
import com.lowagie.text.DocumentException;
import org.xhtmlrenderer.pdf.ITextRenderer;

public class PdfMain {

  public static void main(String[] args) throws IOException, DocumentException {
    String inputFile;
    String outputFile;

    // pick filenames from environment, note gradle does not take the same environment by default
    if(args.length == 0) {
      inputFile = System.getenv("HTML"); 
      outputFile = System.getenv("PDF");
    }
    else {
      inputFile = args[0];
      outputFile = args[1];
    }

    if(inputFile == null || inputFile.length() == 0) {
      System.out.println("# error: input filename is empty");
    }
    else if(outputFile == null || outputFile.length() == 0) {
      System.out.println("# error: output filename is empty");
    }
    else {
      String url = new File(inputFile).toURI().toURL().toString();
      OutputStream os = new FileOutputStream(outputFile);
      ITextRenderer renderer = new ITextRenderer();
      renderer.setDocument(url);
      renderer.layout();
      renderer.createPDF(os);
      os.close();
    }
  }
}
