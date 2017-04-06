package technology.tabula.tabula_web;

import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.File;
import java.io.IOException;

public class Utils {

    private static final String DEFAULT_PASSWORD = "";

    public static PDDocument openPDF(String documentPath) throws IOException {
        PDDocument document = PDDocument.load(new File(documentPath));
        if (document.isEncrypted()) {
            document.close();
            document = PDDocument.load(new File(documentPath), DEFAULT_PASSWORD);
            document.setAllSecurityToBeRemoved(true);
        }
        return document;
    }
}
