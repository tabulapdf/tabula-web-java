package technology.tabula.tabula_web.background.job;

import org.jpedal.PdfDecoder;
import org.jpedal.exception.PdfException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import technology.tabula.tabula_web.extractor.PagesInfoExtractor;
import technology.tabula.tabula_web.workspace.DocumentPage;
import technology.tabula.tabula_web.workspace.WorkspaceDAO;
import technology.tabula.tabula_web.workspace.WorkspaceDocument;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

public class GeneratePageThumbnails extends Job {

    private final String filePath;
    private final String documentId;
    private final PdfDecoder decoder;
    private WorkspaceDAO workspaceDAO;

    private static final int[] SIZES = new int[]{800};
    final static Logger logger = LoggerFactory.getLogger(GeneratePageThumbnails.class);

    public GeneratePageThumbnails(String filePath, String documentId, UUID batch, WorkspaceDAO workspaceDAO) throws Exception {
        super(batch);
        this.filePath = filePath;
        this.documentId = documentId;
        this.workspaceDAO = workspaceDAO;
        this.decoder = new PdfDecoder(true);
        this.decoder.openPdfFile(filePath);
        decoder.setExtractionMode(0);
        decoder.useHiResScreenDisplay(true);
    }

    @Override
    public void perform() throws Exception {
        int totalPages = decoder.getPageCount();

        for (int i = 1; i <= totalPages; i++) {
            BufferedImage image = decoder.getPageAsImage(i);
            int imageWidth = image.getWidth();
            int imageHeight = image.getHeight();

            for (int si = 0; si < SIZES.length; si++) {
                int s = SIZES[si];
                float scale = (float) s / (float) imageWidth;
                BufferedImage bi = new BufferedImage(s, Math.round(imageHeight * scale), image.getType());
                bi.getGraphics().drawImage(image.getScaledInstance(s, Math.round(imageHeight * scale), Image.SCALE_SMOOTH), 0, 0, null);

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                ImageIO.write(bi, "png", out);
                InputStream in = new ByteArrayInputStream(out.toByteArray());

                String fname = String.format("document_%d_%d.png", s, i);
                logger.info("Adding page thumbnail {}", fname);
                workspaceDAO.addFile(in, documentId, fname);
                at(i, totalPages, "Generating page thumbnails...");
            }
        }
        decoder.closePdfFile();
    }

}
