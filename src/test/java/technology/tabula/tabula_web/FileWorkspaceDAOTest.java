package technology.tabula.tabula_web;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import junit.framework.TestCase;
import technology.tabula.tabula_web.workspace.DocumentPage;
import technology.tabula.tabula_web.workspace.FileWorkspaceDAO;
import technology.tabula.tabula_web.workspace.WorkspaceDocument;
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
	    List<DocumentPage> pages;
		fw = new FileWorkspaceDAO(Paths.get(System.getProperty("java.io.tmpdir"), UUID.randomUUID().toString()).toString());
		fw.addToWorkspace(new WorkspaceDocument("original.pdf", "11111-22222-44444", "1234234", 3, 42000, new int[] { 800 }),
				Arrays.asList(new DocumentPage[] { new DocumentPage(800, 800, 1, 0, true) }));

		assertEquals(1, fw.getWorkspace().size());

		// check that pages.json was generated
        pages = fw.getFilePages("11111-22222-44444");
        assertEquals(pages.size(), 1);
        assertEquals(pages.get(0).number, 1);

		// add another
		fw.addToWorkspace(new WorkspaceDocument("original2.pdf", "11111-22222-55555", "1234234", 3, 42000, new int[] { 800 }),
				Arrays.asList(new DocumentPage[] { new DocumentPage(800, 800, 1, 90, true) }));

		assertEquals(2, fw.getWorkspace().size());

        // check that pages.json was generated
        pages = fw.getFilePages("11111-22222-55555");
        assertEquals(pages.size(), 1);
        assertEquals(pages.get(0).number, 1);
        assertEquals(pages.get(0).rotation, 90);


        // check that new entries are added first
		assertEquals(fw.getWorkspace().get(0).original_filename, "original2.pdf");
		assertEquals(fw.getWorkspace().get(1).original_filename, "original.pdf");


	}

}
