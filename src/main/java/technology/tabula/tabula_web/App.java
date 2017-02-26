package technology.tabula.tabula_web;
import static spark.Spark.*;

import java.io.FileNotFoundException;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import spark.route.RouteOverview;
import technology.tabula.tabula_web.routes.IndexRoute;
import technology.tabula.tabula_web.routes.PdfRoute;
import technology.tabula.tabula_web.routes.PdfsRoute;
import technology.tabula.tabula_web.workspace.WorkspaceDAO;


public class App {
	
	public static void main(String[] args) throws JsonIOException, JsonSyntaxException, FileNotFoundException {
		staticFiles.location("/public");
		RouteOverview.enableRouteOverview();
		
		WorkspaceDAO workspaceDAO = new WorkspaceDAO(Settings.getDataDir());
		
	    path("/", new IndexRoute());
	    path("/pdfs/", new PdfsRoute(workspaceDAO));
	    path("/pdf/", new PdfRoute(workspaceDAO));
	  
	}
}

