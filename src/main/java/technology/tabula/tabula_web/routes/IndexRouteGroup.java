package technology.tabula.tabula_web.routes;


import java.util.Scanner;

import spark.RouteGroup;
import technology.tabula.tabula_web.App;
import technology.tabula.tabula_web.JsonTransformer;
import technology.tabula.tabula_web.workspace.WorkspaceDAO;

import static spark.Spark.*;


public class IndexRouteGroup implements RouteGroup {

    private static final String index;

    static {
        index = new Scanner(App.class.getClassLoader().getResourceAsStream("public/index.html"), "UTF-8").useDelimiter("\\A").next();
    }

    private static final String[] indexes = new String[]{"pdf/:file_id", "help", "about"};
    private WorkspaceDAO workspaceDAO;

    public IndexRouteGroup(WorkspaceDAO workspaceDAO) {
        this.workspaceDAO = workspaceDAO;
    }

    @Override
    public void addRoutes() {
        for (String path : indexes) {
            get(path, (req, rsp) -> index);
        }
        post("upload.json", new UploadRoute(workspaceDAO), new JsonTransformer());
    }
}