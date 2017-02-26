package technology.tabula.tabula_web.routes;


import java.util.Scanner;

import spark.RouteGroup;
import technology.tabula.tabula_web.App;

import static spark.Spark.get;


public class IndexRoute implements RouteGroup {

	private String[] indexes = new String[] { "pdf/:file_id", "help", "about" }; 
	
	@Override
	public void addRoutes() {
		for(String path: indexes) {
			get(path, (req, rsp) -> { return this.getIndex(); });
		}
	}
	
	@SuppressWarnings("resource")
	private String getIndex() {
		String index = new Scanner(App.class.getClassLoader().getResourceAsStream("public/index.html"), "UTF-8").useDelimiter("\\A").next();
		return index;
	}
	
}