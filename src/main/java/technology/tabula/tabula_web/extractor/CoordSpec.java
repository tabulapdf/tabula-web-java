package technology.tabula.tabula_web.extractor;

public class CoordSpec implements Comparable<CoordSpec> {
	public Integer page;
	public String extraction_method;
	public String selection_id;
	public float x1;
	public float x2;
	public float y1;
	public float y2;
	public float width;
	public float height;

	@Override
	public int compareTo(CoordSpec o) {
		if (!this.page.equals(o.page)) return this.page.compareTo(o.page); 
		
		Integer thisy = ((int) Math.min(this.y1, this.y1)) / 10;
		Integer othery = ((int) Math.min(o.y1, o.y1)) / 10;
		
		if (!thisy.equals(othery)) return thisy.compareTo(othery);
		
		Float thisx = Math.min(this.x1, this.x2);
		Float otherx = Math.min(o.x1, o.x2);
		
		return thisx.compareTo(otherx);
	}
}