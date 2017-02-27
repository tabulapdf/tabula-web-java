package technology.tabula.tabula_web.workspace;

public class WorkspaceEntry {
	  public String original_filename;
	  public String id;
	  public String time;
	  public int page_count;
	  public long size;
	  public int[] thumbnail_sizes;
	
	  
	  public WorkspaceEntry(String original_filename, String id, String time, int page_count, long size,
			int[] thumbnail_sizes) {
		this.original_filename = original_filename;
		this.id = id;
		this.time = time;
		this.page_count = page_count;
		this.size = size;
		this.thumbnail_sizes = thumbnail_sizes;
	}
	  
	  
	  
}
