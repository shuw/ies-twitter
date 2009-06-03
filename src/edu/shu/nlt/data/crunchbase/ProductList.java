package edu.shu.nlt.data.crunchbase;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProductList {

	public ProductList(File companyFile) throws JSONException, IOException {
		initialize(companyFile);
	}

	private List<Company> products;

	private void initialize(File companyFile) throws JSONException, IOException {
		JSONArray jsonArray = JsonUtil.GetJsonArray(companyFile);

		products = new ArrayList<Company>(jsonArray.length());

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			String name = jsonObject.getString("name");
			String crunchbaseID = jsonObject.getString("permalink");

			products.add(new Company(crunchbaseID, name));
		}
	}

	public Collection<Company> getProducts() {
		return products;
	}

	public static void main(String[] args) throws JSONException, IOException {

		ProductList products = new ProductList(new File("data/crunchbase/products.js"));

		for (Company company : products.getProducts())
			System.out.println(company.getName() + " ID: " + company.getCrunchBaseId());

		System.out.println("Total # of products: " + products.getProducts().size());
	}
}
