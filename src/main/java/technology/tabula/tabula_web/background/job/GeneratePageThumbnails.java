package technology.tabula.tabula_web.background.job;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import technology.tabula.tabula_web.workspace.WorkspaceDAO;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.UUID;

public class GeneratePageThumbnails extends Job {

    private final String filePath;
    private final String documentId;
    private final PDDocument pdfDocument;
    private WorkspaceDAO workspaceDAO;

    private static final int SIZE = 800;
    final static Logger logger = LoggerFactory.getLogger(GeneratePageThumbnails.class);

    public GeneratePageThumbnails(String filePath, String documentId, UUID batch, WorkspaceDAO workspaceDAO) throws Exception {
        super(batch);
        this.filePath = filePath;
        this.documentId = documentId;
        this.workspaceDAO = workspaceDAO;

        this.pdfDocument = PDDocument.load(new File(this.filePath));
    }

    @Override
    public void perform() throws Exception {
        int totalPages = pdfDocument.getNumberOfPages();
        PDFRenderer renderer = new PDFRenderer(pdfDocument);


        for (int pi = 0; pi < totalPages; pi++) {
            BufferedImage image = renderer.renderImageWithDPI(pi, 75);
            int imageWidth = image.getWidth();
            int imageHeight = image.getHeight();
            float scale = (float) SIZE / (float) imageWidth;

            BufferedImage bi = new BufferedImage(SIZE, Math.round(imageHeight * scale), image.getType());
            bi.getGraphics().drawImage(image.getScaledInstance(SIZE, Math.round(imageHeight * scale), Image.SCALE_SMOOTH), 0, 0, null);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(bi, "png", out);
            InputStream in = new ByteArrayInputStream(out.toByteArray());

            String fname = String.format("document_%d_%d.png", SIZE, pi + 1);
            logger.info("Adding page thumbnail {}", fname);
            workspaceDAO.addFile(in, documentId, fname);
            at(pi, totalPages, "Generating page thumbnails...");
        }

        this.pdfDocument.close();
    }

}
