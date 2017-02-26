package technology.tabula.tabula_web.routes;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import spark.Request;
import spark.Response;
import spark.Route;
import technology.tabula.tabula_web.extractor.CoordSpec;

public class ExtractData implements Route {

	@Override
	public Object handle(Request request, Response response) throws Exception {
		String requestedCoords = request.queryParams("coords");
		Type targetClassType = new TypeToken<ArrayList<CoordSpec>>() { }.getType();
	    ArrayList<CoordSpec> coords = new Gson().fromJson(requestedCoords, targetClassType);
	    
		Collections.sort(coords);
	}

}
