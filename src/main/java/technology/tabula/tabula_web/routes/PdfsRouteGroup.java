package technology.tabula.tabula_web.routes;


import spark.RouteGroup;
import technology.tabula.tabula_web.JsonTransformer;
import technology.tabula.tabula_web.workspace.WorkspaceDAO;

import static spark.Spark.get;

import java.nio.file.Files;
import java.nio.file.Paths;

import javax.servlet.http.HttpServletResponse;

public class PdfsRouteGroup implements RouteGroup {
	
	private WorkspaceDAO workspaceDAO;

	public PdfsRouteGroup(WorkspaceDAO workspaceDAO) {
		this.workspaceDAO = workspaceDAO;
	}

	@Override
	public void addRoutes() {
		get("workspace.json", (req, rsp) -> {
			rsp.type("application/json");
			return this.workspaceDAO.getWorkspace();
		}, new JsonTransformer());
		
		get(":file_id/pages.json", (req, rsp) -> {
			rsp.type("application/json");
			return this.workspaceDAO.getFilePages(req.params(":file_id"));
		}, new JsonTransformer());
		
		get(":file_id/*", (req, rsp) -> {
			rsp.type("image/png");
			String id = req.params(":file_id");
			String image = req.splat()[0];
			
			HttpServletResponse raw = rsp.raw();
			raw.getOutputStream().write(Files.readAllBytes(Paths.get(this.workspaceDAO.getDataDir(), "pdfs", id, image)));
			raw.getOutputStream().flush();
			raw.getOutputStream().close();
			return raw;
		});

	}
}
