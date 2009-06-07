package edu.shu.nlt.data.crunchbase;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Hashtable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProductList {

	public ProductList(File file) {
		try {
			initialize(file);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private Hashtable<String, Product> products;

	private void initialize(File companyFile) throws JSONException, IOException {
		JSONArray jsonArray = JsonUtil.GetJsonArray(companyFile);

		products = new Hashtable<String, Product>(jsonArray.length());

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			String name = jsonObject.getString("name");
			String crunchbaseID = jsonObject.getString("permalink");

			products.put(name.toLowerCase(), new Product(crunchbaseID, name));
		}
	}

	public Collection<Product> getProducts() {
		return products.values();
	}

	public Product getProduct(String name) {
		return products.get(name.toLowerCase());
	}

	public static void main(String[] args) throws JSONException, IOException {

		ProductList products = new ProductList(new File("data/crunchbase/products.js"));

		for (Product product : products.getProducts())
			System.out.println(product.getName() + " ID: " + product.getCrunchBaseId());

		System.out.println("Total # of products: " + products.getProducts().size());
	}
}
