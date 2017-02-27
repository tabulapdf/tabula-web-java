package technology.tabula.tabula_web;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.UUID;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import junit.framework.TestCase;
import technology.tabula.tabula_web.workspace.DocumentPage;
import technology.tabula.tabula_web.workspace.FileWorkspaceDAO;
import technology.tabula.tabula_web.workspace.WorkspaceEntry;
import technology.tabula.tabula_web.workspace.WorkspaceException;

public class FileWorkspaceDAOTest extends TestCase {

	private FileWorkspaceDAO fw;

	protected void setUp() throws Exception {
		super.setUp();
		
	}
	

	protected void tearDown() throws Exception {
		
		super.tearDown();
	}
	
	public void testWorkspaceCreated() throws WorkspaceException {
		//this.fw = new FileWorkspaceDAO(System.getProperty("java.io.tmpdir"));
		//System.out.println(this.fw.getWorkspace().toString());
	}
	
	public void testAddEntry() throws WorkspaceException, JsonIOException, JsonSyntaxException, IOException {
		this.fw = new FileWorkspaceDAO(Paths.get(System.getProperty("java.io.tmpdir"), UUID.randomUUID().toString()).toString());
		fw.addToWorkspace(new WorkspaceEntry("original.pdf", "11111-22222-44444", "1234234", 3, 42000, new int[] { 800 }), 
				Arrays.asList(new DocumentPage[] { new DocumentPage(800, 800, 1, 0, true) }));
		System.out.println(this.fw.getWorkspace().toString());
	}

}
