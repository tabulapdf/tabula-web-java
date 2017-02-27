package technology.tabula.tabula_web.workspace;

public class DocumentPage {

	public double width;
	public double height;
	public int number;
	public int rotation;
	public boolean hasText;
	
	public DocumentPage(double width, double height, int number, int rotation, boolean hasText) {
		this.width = width;
		this.height = height;
		this.number = number;
		this.rotation = rotation;
		this.hasText = hasText;
	}
}
