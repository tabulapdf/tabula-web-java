package technology.tabula.tabula_web.extractor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.pdfbox.pdmodel.PDDocument;

import technology.tabula.ObjectExtractor;
import technology.tabula.Page;
import technology.tabula.PageIterator;
import technology.tabula.Table;
import technology.tabula.extractors.BasicExtractionAlgorithm;
import technology.tabula.extractors.ExtractionAlgorithm;
import technology.tabula.extractors.SpreadsheetExtractionAlgorithm;

public class Extractor {

	@SuppressWarnings("Convert2Diamond")
    public static List<TableWithSpecIndex> extractTables(String pdfPath, List<CoordSpec> specs) throws IOException {
		
		Map<Integer, List<CoordSpec>> specsByPage = specs.stream().collect(Collectors.groupingBy(CoordSpec::getPage));
		//noinspection Convert2Diamond
		List<Integer> pages = new ArrayList<Integer>(specsByPage.keySet());
		Collections.sort(pages);
		
		PDDocument document = PDDocument.load(pdfPath);
		ObjectExtractor extractor = new ObjectExtractor(document);
		
		SpreadsheetExtractionAlgorithm sea = new SpreadsheetExtractionAlgorithm();
		BasicExtractionAlgorithm bea = new BasicExtractionAlgorithm();
		
		PageIterator pageIterator = extractor.extract(pages);

		//noinspection Convert2Diamond
		List<TableWithSpecIndex> rv = new ArrayList<TableWithSpecIndex>();
		
		while (pageIterator.hasNext()) {
			Page p = pageIterator.next();
			for (CoordSpec spec: specsByPage.get(p.getPageNumber())) {
				boolean useSpreadsheetExtractionMethod;
				if (spec.extraction_method.equals("spreadsheet") || spec.extraction_method.equals("original")) {
					useSpreadsheetExtractionMethod = spec.extraction_method.equals("spreadsheet");
				}
				else {
					useSpreadsheetExtractionMethod = sea.isTabular(p);
				}
				
				Page area = p.getArea(spec.y1, spec.x1, spec.y2, spec.x2);
				ExtractionAlgorithm tableExtractor = useSpreadsheetExtractionMethod ? sea : bea;
				for (Table t: tableExtractor.extract(area)) {
					rv.add(new TableWithSpecIndex(t, spec.spec_index));
				}
			}
		}
		extractor.close();
		return rv;
	}
}
