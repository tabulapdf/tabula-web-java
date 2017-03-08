package technology.tabula.tabula_web.routes;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.UUID;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Route;
import technology.tabula.tabula_web.background.JobExecutor;
import technology.tabula.tabula_web.background.job.DetectTables;
import technology.tabula.tabula_web.background.job.GenerateDocumentData;
import technology.tabula.tabula_web.background.job.GeneratePageThumbnails;
import technology.tabula.tabula_web.workspace.WorkspaceDAO;

public class UploadRoute implements Route {

    class UploadStatus {
        public final String filename;
        public final boolean success;
        public final String file_id;
        public final String upload_id;

        UploadStatus(String filename, boolean success, String file_id, String upload_id) {
            this.filename = filename;
            this.success = success;
            this.file_id = file_id;
            this.upload_id = upload_id;
        }
    }
	
	private WorkspaceDAO workspaceDAO;
    final static Logger logger = LoggerFactory.getLogger(UploadRoute.class);

    public UploadRoute(WorkspaceDAO workspaceDAO) {
		this.workspaceDAO = workspaceDAO;
	}

	@Override
	public Object handle(Request request, Response response) throws Exception {
        // TODO support upload of multiple files
		request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement(System.getProperty("java.io.tmpdir")));
		
		Part part = request.raw().getPart("files[]");
		String originalFilename = part.getSubmittedFileName();
		String documentId = UUID.randomUUID().toString();
		
		try (InputStream input = part.getInputStream()) {
            workspaceDAO.addFile(input, documentId, "document.pdf");
		}
		
		UUID jobBatch = UUID.randomUUID();
		JobExecutor executor = JobExecutor.getInstance();

		logger.info("Starting Job Batch {}", jobBatch.toString());

        String documentPath = this.workspaceDAO.getDocumentPath(documentId);
		executor.submitJobs(
				new GenerateDocumentData(documentPath, originalFilename, documentId,
                        new int[] { 800 }, jobBatch, workspaceDAO),
				new DetectTables(documentPath, documentId, jobBatch, workspaceDAO),
				new GeneratePageThumbnails(documentPath, documentId, jobBatch, workspaceDAO)
		);


        ArrayList<UploadStatus> resp = new ArrayList<UploadStatus>();
        resp.add(new UploadStatus(originalFilename, true, documentId, jobBatch.toString()));

		return resp;
	}

}
