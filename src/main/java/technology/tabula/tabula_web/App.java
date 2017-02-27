package technology.tabula.tabula_web;
import static spark.Spark.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import spark.route.RouteOverview;
import technology.tabula.tabula_web.background.JobExecutor;
import technology.tabula.tabula_web.routes.IndexRouteGroup;
import technology.tabula.tabula_web.routes.PdfRouteGroup;
import technology.tabula.tabula_web.routes.PdfsRouteGroup;
import technology.tabula.tabula_web.workspace.WorkspaceDAO;
import technology.tabula.tabula_web.workspace.FileWorkspaceDAO;


public class App {
	
	public static void main(String[] args) throws JsonIOException, JsonSyntaxException, IOException {
		staticFiles.location("/public");
		RouteOverview.enableRouteOverview();
		
		WorkspaceDAO workspaceDAO = new FileWorkspaceDAO(Settings.getDataDir());
		JobExecutor jobExecutor = JobExecutor.getInstance();
				
	    path("/", new IndexRouteGroup(workspaceDAO));
	    path("/pdfs/", new PdfsRouteGroup(workspaceDAO));
	    path("/pdf/", new PdfRouteGroup(workspaceDAO));
	  
	}
}

