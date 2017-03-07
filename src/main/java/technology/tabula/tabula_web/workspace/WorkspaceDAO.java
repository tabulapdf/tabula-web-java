package technology.tabula.tabula_web.workspace;

import java.io.InputStream;
import java.util.List;


public interface WorkspaceDAO {
	
	Workspace getWorkspace() throws WorkspaceException;

	void addToWorkspace(WorkspaceDocument we, List<DocumentPage> pages) throws WorkspaceException;

	WorkspaceDocument getFileMetadata(String documentId) throws WorkspaceException;

	List<DocumentPage> getFilePages(String documentId) throws WorkspaceException;

	String getDocumentPath(String documentId);

	InputStream getPageImage(String documentId, int pageNumber) throws WorkspaceException;

	void addFile(InputStream stream, String documentId, String filename) throws WorkspaceException;

}