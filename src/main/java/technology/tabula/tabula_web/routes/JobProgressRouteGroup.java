package technology.tabula.tabula_web.routes;

import spark.RouteGroup;
import static spark.Spark.get;

public class JobProgressRouteGroup implements RouteGroup {

    @Override
    public void addRoutes() {
        get(":upload_id/json", (req, rsp) -> { return ""; }); // TODO: implement
        get(":upload_id", (req, rsp) -> { return ""; }); // TODO: implement
    }
}
