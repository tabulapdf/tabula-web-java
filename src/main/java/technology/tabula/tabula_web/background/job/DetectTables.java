package technology.tabula.tabula_web.background.job;

import com.google.gson.Gson;
import org.apache.pdfbox.pdmodel.PDDocument;
import technology.tabula.ObjectExtractor;
import technology.tabula.Page;
import technology.tabula.PageIterator;
import technology.tabula.Rectangle;
import technology.tabula.detectors.NurminenDetectionAlgorithm;
import technology.tabula.tabula_web.workspace.WorkspaceDAO;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class DetectTables extends Job {


    private final String filePath;
    private final String documentId;
    private final UUID batch;
    private WorkspaceDAO workspaceDAO;

    public DetectTables(String filePath, String documentId, UUID batch, WorkspaceDAO workspaceDAO) {
        super(batch);
        this.filePath = filePath;
        this.documentId = documentId;
        this.batch = batch;
        this.workspaceDAO = workspaceDAO;
    }

    @Override
    public void perform() throws Exception {
        PDDocument document = PDDocument.load(filePath);
        ObjectExtractor extractor = new ObjectExtractor(document);
        PageIterator it = extractor.extract();
        int pageCount = extractor.getPageCount();
        NurminenDetectionAlgorithm nda = new NurminenDetectionAlgorithm();
        ArrayList<List<double[]>> pageAreasByPage = new ArrayList<>();

        try {
            while (it.hasNext()) {
                Page page = it.next();

                at((pageCount + page.getPageNumber()) / 2, pageCount, "auto-detecting tables...");

                List<Rectangle> areas = nda.detect(page);

                pageAreasByPage.add(areas.stream()
                        .map(r -> new double[]{r.getLeft(), r.getTop(), r.getWidth(), r.getHeight()})
                        .collect(Collectors.toList()));
            }
        }
        catch (Exception e) {
            // TODO report exception properly
            System.out.println("EXCEPTION IN DETECTTABLES");
        }

        StringBuilder sb = new StringBuilder();
        new Gson().toJson(pageAreasByPage, sb);

        this.workspaceDAO.addFile(new ByteArrayInputStream(sb.toString().getBytes("UTF-8")), this.documentId, "tables.json");

        at(100, 100, "complete");

    }
}
