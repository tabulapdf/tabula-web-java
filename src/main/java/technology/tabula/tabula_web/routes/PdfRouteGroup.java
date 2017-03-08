package technology.tabula.tabula_web.routes;

import spark.Request;
import spark.Response;
import spark.RouteGroup;
import technology.tabula.tabula_web.JsonTransformer;
import technology.tabula.tabula_web.workspace.WorkspaceDAO;
import technology.tabula.tabula_web.workspace.WorkspaceException;

import static spark.Spark.*;

public class PdfRouteGroup implements RouteGroup {

	private WorkspaceDAO workspaceDAO;

	public PdfRouteGroup(WorkspaceDAO workspaceDAO) {
		this.workspaceDAO = workspaceDAO;
	}

	private Object deleteDocument(Request request, Response response) {
        try {
            this.workspaceDAO.deleteDocument(request.params(":file_id"));
        } catch (WorkspaceException e) {
            halt(500, e.getMessage());
        }
        return "";
    }

	@Override
	public void addRoutes() {
		get(":file_id/metadata.json", (req, rsp) -> {
			rsp.type("application/json");
			return this.workspaceDAO.getDocumentMetadata(req.params(":file_id"));
		}, new JsonTransformer());
		
		post(":file_id/data", new ExtractDataRoute(this.workspaceDAO));

		delete(":file_id", (req, rsp) -> deleteDocument(req, rsp));

		post(":file_id", (req, rsp) -> {
		    if (req.queryParams("_method").equals("delete")) {
		        return deleteDocument(req, rsp);
            }
            else {
		        halt(400);
		        return "";
            }
		});
	}
}
