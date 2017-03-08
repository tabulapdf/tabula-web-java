package technology.tabula.tabula_web.workspace;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;


/**
 * An implementation of {@link WorkspaceDAO} that stores data in the file system
 */
public class FileWorkspaceDAO implements WorkspaceDAO {
	
	private String dataDir;
	private Workspace workspace;
	private Path workspacePath;

	public FileWorkspaceDAO(String dataDir) throws JsonIOException, JsonSyntaxException, IOException, WorkspaceException {
		this.dataDir = dataDir;
		this.workspacePath = Paths.get(this.dataDir, "pdfs", "workspace.json");
		
		// create if doesn't exist
		if (!Files.exists(this.workspacePath)) {
			Files.createDirectories(this.workspacePath.getParent());
			this.workspace = new Workspace();
			this.flushWorkspace();
		}
		
		this.readWorkspace();
	}
	
	private synchronized void readWorkspace() throws WorkspaceException {
	    try {
            FileReader fr = new FileReader(workspacePath.toString());
            Type targetClassType = new TypeToken<Workspace>() { }.getType();
            this.workspace = new Gson().fromJson(fr, targetClassType);
            fr.close();
        }
        catch (IOException e) {
            throw new WorkspaceException(e);
        }

	}

	/* (non-Javadoc)
	 * @see technology.tabula.tabula_web.workspace.WorkspaceDAO#getWorkspace()
	 */
	@Override
	public Workspace getWorkspace() throws WorkspaceException {
        this.readWorkspace();
		return this.workspace;
	}
	
	private synchronized void flushWorkspace() throws WorkspaceException {
		Gson gson = new GsonBuilder().create();
        FileWriter fw = null;
        try {
            fw = new FileWriter(this.workspacePath.toString());
            gson.toJson(this.workspace,	fw);
            fw.close();
        } catch (IOException e) {
            throw new WorkspaceException(e);
        }
	}
	
	/* (non-Javadoc)
	 * @see technology.tabula.tabula_web.workspace.WorkspaceDAO#addDocument(technology.tabula.tabula_web.workspace.WorkspaceDocument, java.util.List)
	 */
	@Override
	public synchronized void addDocument(WorkspaceDocument we, List<DocumentPage> pages) throws WorkspaceException {
        this.readWorkspace();
		this.workspace.add(0, we);

		// add :document_id/pages.json file
        StringBuilder sb = new StringBuilder();
        new Gson().toJson(pages, sb);

        try {
            this.addFile(new ByteArrayInputStream(sb.toString().getBytes("UTF-8")), we.id, "pages.json");
        } catch (UnsupportedEncodingException e) {
           throw new WorkspaceException(e);
        }
        this.flushWorkspace();
	}

	@Override
	public synchronized void deleteDocument(String documentId) throws WorkspaceException {
	    this.readWorkspace();
	    this.workspace.removeIf(wd -> wd.id.equals(documentId));
	    this.flushWorkspace();

        Path path = Paths.get(this.getDataDir(), "pdfs", documentId);
        try {
            Files.walk(path)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (IOException e) {
            throw new WorkspaceException(e);
        }
    }

	/* (non-Javadoc)
	 * @see technology.tabula.tabula_web.workspace.WorkspaceDAO#getDocumentMetadata(java.lang.String)
	 */
	@Override
	public WorkspaceDocument getDocumentMetadata(String id) throws WorkspaceException {
        this.readWorkspace();
		return this.workspace.stream().filter(we -> we.id.equals(id)).findFirst().get();
	}
	
	/* (non-Javadoc)
	 * @see technology.tabula.tabula_web.workspace.WorkspaceDAO#getDocumentPages(java.lang.String)
	 */
	@Override
	public List<DocumentPage> getDocumentPages(String id) throws WorkspaceException {
		Type targetClassType = new TypeToken<List<DocumentPage>>() { }.getType();
	    try {
			return new Gson().fromJson(new FileReader(Paths.get(this.dataDir, "pdfs", id, "pages.json").toString()), targetClassType);
		} catch (JsonIOException | JsonSyntaxException | FileNotFoundException e) {
			throw new WorkspaceException(e);
		}
	}
	
	/* (non-Javadoc)
	 * @see technology.tabula.tabula_web.workspace.WorkspaceDAO#getDocumentPath(java.lang.String)
	 */
	@Override
	public String getDocumentPath(String id) {
		return Paths.get(this.getDataDir(), "pdfs", id, "document.pdf").toString();
	}
	
	@Override
	public String getDocumentDir(String documentId) {
		// TODO Auto-generated method stub
		return Paths.get(this.getDataDir(), "pdfs", documentId).toString();
	}


	/* (non-Javadoc)
	 * @see technology.tabula.tabula_web.workspace.WorkspaceDAO#getDataDir()
	 */
	@Override
	public String getDataDir() {
		return dataDir;
	}

	@Override
	public void addFile(InputStream stream, String documentId, String filename) throws WorkspaceException {
		Path p = Paths.get(this.getDataDir(), "pdfs", documentId);
		
		if (!Files.isDirectory(p)) {
			try {
				Files.createDirectories(p);
			} catch (IOException e) {
				throw new WorkspaceException(e);
			}
		}
		
        try {
			Files.copy(stream, Paths.get(this.getDataDir(), "pdfs", documentId, filename), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new WorkspaceException(e);
		}
	}


}
