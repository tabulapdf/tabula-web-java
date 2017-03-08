package technology.tabula.tabula_web.extractor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;

import technology.tabula.ObjectExtractor;
import technology.tabula.Page;
import technology.tabula.PageIterator;
import technology.tabula.tabula_web.workspace.DocumentPage;

public class PagesInfoExtractor {

	@SuppressWarnings("Convert2Diamond")
    public static List<DocumentPage> pagesInfo(String pdfPath) throws IOException {
		PDDocument document = PDDocument.load(pdfPath);
		ObjectExtractor extractor = new ObjectExtractor(document);
		
		ArrayList<DocumentPage> rv = new ArrayList<DocumentPage>(); 
		
		PageIterator it = extractor.extract();
		Page p;
		while (it.hasNext()) {
			p = it.next();
			rv.add(new DocumentPage(p.getWidth(), p.getHeight(), p.getPageNumber(), p.getRotation(), p.hasText()));
		}

		extractor.close();
		
		return rv;
	}
}
