package technology.tabula.tabula_web.routes;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;

import spark.Request;
import spark.Response;
import spark.Route;
import technology.tabula.tabula_web.background.JobExecutor;
import technology.tabula.tabula_web.background.job.GenerateDocumentData;
import technology.tabula.tabula_web.workspace.WorkspaceDAO;

public class UploadRoute implements Route {
	
	private WorkspaceDAO workspaceDAO;

	public UploadRoute(WorkspaceDAO workspaceDAO) {
		this.workspaceDAO = workspaceDAO;
	}

	@Override
	public Object handle(Request request, Response response) throws Exception {
		request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement(System.getProperty("java.io.tmpdir")));
		
		Part part = request.raw().getPart("files[]");
		String originalFilename = part.getSubmittedFileName();
		String documentId = UUID.randomUUID().toString();
		
		try (InputStream input = part.getInputStream()) {
            workspaceDAO.addFile(input, documentId, "document.pdf");
		}
		
		UUID jobBatch = UUID.randomUUID();
		JobExecutor executor = JobExecutor.getInstance();
		
		executor.submitJob(
				new GenerateDocumentData(this.workspaceDAO.getDocumentPath(documentId), originalFilename, documentId, 
						this.workspaceDAO.getDocumentDir(documentId), new int[] { 800 }, jobBatch, workspaceDAO)
				);
		
		return "";
	}

}
