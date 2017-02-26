package technology.tabula.tabula_web.routes;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import spark.Request;
import spark.Response;
import spark.Route;
import technology.tabula.Cell;
import technology.tabula.RectangularTextContainer;
import technology.tabula.TextChunk;
import technology.tabula.json.TextChunkSerializer;
import technology.tabula.tabula_web.extractor.CoordSpec;
import technology.tabula.tabula_web.extractor.Extractor;
import technology.tabula.tabula_web.extractor.TableWithSpecIndex;
import technology.tabula.tabula_web.extractor.TableWithSpecIndexSerializer;
import technology.tabula.tabula_web.workspace.WorkspaceDAO;
import technology.tabula.writers.CSVWriter;

public class ExtractData implements Route {
	
	class TableSerializerExclusionStrategy implements ExclusionStrategy {

        @Override
        public boolean shouldSkipClass(Class<?> arg0) {
            return false;
        }

        @Override
        public boolean shouldSkipField(FieldAttributes fa) {
            return !fa.hasModifier(Modifier.PUBLIC);
        }
    }
	
	private WorkspaceDAO workspaceDAO;

	public ExtractData(WorkspaceDAO workspaceDAO) {
		this.workspaceDAO = workspaceDAO;
	}

	@Override
	public Object handle(Request request, Response response) throws Exception {
		String requestedCoords = request.queryParams("coords");
		Type targetClassType = new TypeToken<ArrayList<CoordSpec>>() { }.getType();
	    ArrayList<CoordSpec> specs = new Gson().fromJson(requestedCoords, targetClassType);
	    
		Collections.sort(specs);
		
		int i = 0;
		for(CoordSpec spec: specs) {
			spec.spec_index = i++;
		}
		
		List<TableWithSpecIndex> tables = Extractor.extractTables(this.workspaceDAO.getDocumentPath(request.params(":file_id")), specs);
		
		String requestedFormat = request.params(":format");
		if (requestedFormat == null) requestedFormat = "json";
						
		StringBuilder sb;
		switch(requestedFormat) {
		
		case "csv":
			sb = new StringBuilder();
			for (TableWithSpecIndex t: tables) {
				new CSVWriter().write(sb, t);
			}
			response.type("text/csv");
			response.header("Content-Disposition", "attachment; filename=\"tabula.csv\"");
			return sb.toString();
			
		case "json":
			Gson gson = new GsonBuilder()
			   .addSerializationExclusionStrategy(new TableSerializerExclusionStrategy())
	           .registerTypeAdapter(TableWithSpecIndex.class, new TableWithSpecIndexSerializer())
	           .registerTypeAdapter(RectangularTextContainer.class, new TextChunkSerializer())
	           .registerTypeAdapter(Cell.class, new TextChunkSerializer())
	           .registerTypeAdapter(TextChunk.class, new TextChunkSerializer())
	           .create();
			
			response.type("application/json");
			return gson.toJson(tables);
		}
		
		return "";

	}

}
