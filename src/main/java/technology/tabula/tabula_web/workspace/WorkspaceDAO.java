package technology.tabula.tabula_web.workspace;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.nio.file.Paths;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public class WorkspaceDAO {
	
	private String dataDir;
	private Workspace workspace;

	public WorkspaceDAO(String dataDir) throws JsonIOException, JsonSyntaxException, FileNotFoundException {
		this.dataDir = dataDir;
		this.readWorkspace();
	}
	
	public synchronized void readWorkspace() throws JsonIOException, JsonSyntaxException, FileNotFoundException {
		Type targetClassType = new TypeToken<Workspace>() { }.getType();
	    this.workspace = new Gson().fromJson(new FileReader(Paths.get(this.dataDir, "pdfs", "workspace.json").toString()), targetClassType);
	}

	public List<WorkspaceEntry> getWorkspace() {
		try {
			this.readWorkspace();
		} catch (JsonIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return this.workspace;
	}
	
	public WorkspaceEntry getFileMetadata(String id) {
		try {
			this.readWorkspace();
		} catch (JsonIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (WorkspaceEntry we: this.workspace) {
			if (we.id.equals(id)) return we;
		}
		
		return null;
	}
	
	public List<DocumentPage> getFilePages(String id) throws JsonIOException, JsonSyntaxException, FileNotFoundException {
		Type targetClassType = new TypeToken<List<DocumentPage>>() { }.getType();
	    return new Gson().fromJson(new FileReader(Paths.get(this.dataDir, "pdfs", id, "pages.json").toString()), targetClassType);
	}
	
	public String getDocumentPath(String id) {
		return Paths.get(this.dataDir, "pdfs", id, "document.pdf").toString();
	}

	public String getDataDir() {
		return dataDir;
	}

}
