package technology.tabula.tabula_web.workspace;

import java.io.InputStream;
import java.util.List;


public interface WorkspaceDAO {
	
	Workspace getWorkspace() throws WorkspaceException;

	void addToWorkspace(WorkspaceEntry we, List<DocumentPage> pages) throws WorkspaceException;

	WorkspaceEntry getFileMetadata(String documentId) throws WorkspaceException;

	List<DocumentPage> getFilePages(String documentId) throws WorkspaceException;

	String getDocumentPath(String documentId);
	
	String getDocumentDir(String documentId);

	String getDataDir();
	
	void addFile(InputStream stream, String documentId, String filename) throws WorkspaceException;

}