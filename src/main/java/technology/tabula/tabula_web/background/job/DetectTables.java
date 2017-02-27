package technology.tabula.tabula_web.background.job;

import org.apache.pdfbox.pdmodel.PDDocument;
import technology.tabula.ObjectExtractor;
import technology.tabula.Page;
import technology.tabula.PageIterator;
import technology.tabula.Rectangle;
import technology.tabula.detectors.NurminenDetectionAlgorithm;

import java.util.List;
import java.util.UUID;

public class DetectTables extends Job {


    private final String filePath;
    private final String outputDir;
    private final UUID batch;

    public DetectTables(String filePath, String outputDir, UUID batch) {
        super(batch);
        this.filePath = filePath;
        this.outputDir = outputDir;
        this.batch = batch;
    }

    @Override
    public void perform() throws Exception {
        PDDocument document = PDDocument.load(filePath);
        ObjectExtractor extractor = new ObjectExtractor(document);
        PageIterator it = extractor.extract();
        int pageCount = extractor.getPageCount();
        NurminenDetectionAlgorithm nda = new NurminenDetectionAlgorithm();

        while (it.hasNext()) {
            Page page = it.next();

            at((pageCount + page.getPageNumber()) / 2, pageCount, "auto-detecting tables...");

            List<Rectangle> areas = nda.detect(page);

        }


    }
}
