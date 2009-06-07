package edu.shu.nlt.crunchbase.data;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

public class JsonUtil {

	public static JSONArray GetJsonArray(File file) throws JSONException, IOException {
		FileReader reader = new FileReader(file);

		JSONTokener tokenizer = new JSONTokener(reader);

		JSONArray jsonArray = new JSONArray(tokenizer);

		reader.close();

		return jsonArray;

	}

}
