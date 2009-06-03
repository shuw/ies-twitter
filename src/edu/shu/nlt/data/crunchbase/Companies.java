package edu.shu.nlt.data.crunchbase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class Companies {

	public Companies(File companyFile) {

		try {
			initialize(companyFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	
	
	private void initialize(File companyFile) throws FileNotFoundException, JSONException {
		FileReader reader = new FileReader(companyFile);

		JSONTokener tokenizer = new JSONTokener(reader);

		JSONArray jsonArray = new JSONArray(tokenizer);

		
		
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			String name = jsonObject.getString("name");
			String crunchbaseID = jsonObject.getString("permalink");
		}
	}

	public static void main(String[] args) {

		new Companies(new File("data/companies.js"));

	}
}
