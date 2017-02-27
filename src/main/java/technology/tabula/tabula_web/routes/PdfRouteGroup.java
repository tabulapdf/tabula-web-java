package technology.tabula.tabula_web.routes;

import spark.RouteGroup;
import technology.tabula.tabula_web.JsonTransformer;
import technology.tabula.tabula_web.workspace.WorkspaceDAO;

import static spark.Spark.*;

public class PdfRouteGroup implements RouteGroup {

	private WorkspaceDAO workspaceDAO;

	public PdfRouteGroup(WorkspaceDAO workspaceDAO) {
		this.workspaceDAO = workspaceDAO;
	}

	@Override
	public void addRoutes() {
		get(":file_id/metadata.json", (req, rsp) -> {
			rsp.type("application/json");
			return this.workspaceDAO.getFileMetadata(req.params(":file_id")); 
		}, new JsonTransformer());
		
		post(":file_id/data", new ExtractDataRoute(this.workspaceDAO));
	}
}
