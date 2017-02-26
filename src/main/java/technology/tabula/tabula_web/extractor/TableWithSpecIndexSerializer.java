package technology.tabula.tabula_web.extractor;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import technology.tabula.json.TableSerializer;

public class TableWithSpecIndexSerializer implements JsonSerializer<TableWithSpecIndex>  {

	public JsonElement serialize(TableWithSpecIndex tableWithSpecIndex, Type type,
            JsonSerializationContext context) {
		
		TableSerializer s = new TableSerializer();
		
		JsonObject o = (JsonObject) s.serialize(tableWithSpecIndex.table, type, context);
		o.addProperty("spec_index", tableWithSpecIndex.specIndex);
		
		return o;
	
	}
}
