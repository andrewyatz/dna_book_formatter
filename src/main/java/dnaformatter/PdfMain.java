package dnaformatter;

import java.io.*;
import com.lowagie.text.DocumentException;
import org.xhtmlrenderer.pdf.ITextRenderer;

public class PdfMain {

  public static void main(String[] args) throws IOException, DocumentException {
    String inputFile;
    String outputFile;
    if(args.length == 0) {
      inputFile = System.getenv("HTML");
      outputFile = System.getenv("PDF");
    }
    else {
      inputFile = args[0];
      outputFile = args[1];
    }
    String url = new File(inputFile).toURI().toURL().toString();
    OutputStream os = new FileOutputStream(outputFile);
    ITextRenderer renderer = new ITextRenderer();
    renderer.setDocument(url);
    renderer.layout();
    renderer.createPDF(os);
    os.close();
  }
}