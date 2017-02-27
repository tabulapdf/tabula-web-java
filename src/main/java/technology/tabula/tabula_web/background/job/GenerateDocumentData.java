package technology.tabula.tabula_web.background.job;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import technology.tabula.tabula_web.extractor.PagesInfoExtractor;
import technology.tabula.tabula_web.workspace.DocumentPage;
import technology.tabula.tabula_web.workspace.WorkspaceDAO;
import technology.tabula.tabula_web.workspace.WorkspaceEntry;

public class GenerateDocumentData extends Job {

	String filePath; 
	String originalFilename; 
	String id; 
	String outputDir; 
	int[] thumbnailSizes; 
	UUID batch;

	private WorkspaceDAO workspaceDAO;
	
	public GenerateDocumentData(String filePath, String originalFilename, String id, String outputDir,
			int[] thumbnailSizes, UUID batch, WorkspaceDAO workspaceDAO) {
		
		super(batch);
		this.filePath = filePath;
		this.originalFilename = originalFilename;
		this.id = id;
		this.outputDir = outputDir;
		this.thumbnailSizes = thumbnailSizes;
		this.batch = batch;
		
		this.workspaceDAO = workspaceDAO;
	}

	@Override
	public void perform() throws Exception {
	    at(1, 100, "opening workspace...");
	    
	    System.out.println("perform y lpmqlp");
	    
	    List<DocumentPage> pages = PagesInfoExtractor.pagesInfo(this.filePath);
	    
	    // TODO file size
	    WorkspaceEntry we = new WorkspaceEntry(originalFilename, id, 
	    		Long.toString(LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond()), 
	    		pages.size(), new File(this.filePath).length(), thumbnailSizes);
	    
	    if (!pages.stream().anyMatch(dp -> dp.hasText)) {
	    	at(0, 100, "No text data found");
	    	throw new Exception("No text data found");
	    }
	    
	    this.workspaceDAO.addToWorkspace(we, pages);
	    	    
	    at(100, 100, "Complete");
	}

}
