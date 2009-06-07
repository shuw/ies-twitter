package edu.shu.nlt.crunchbase.data.lists;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Hashtable;

import org.json.JSONArray;
import org.json.JSONException;

import edu.shu.nlt.crunchbase.data.JsonUtil;
import edu.shu.nlt.crunchbase.data.base.Product;

public class ProductList {

	public static ProductList getInstance() {
		return new ProductList(new File("data/crunchbase/products.js"));
	}

	public static void main(String[] args) throws JSONException, IOException {

		ProductList products = getInstance();

		for (Product product : products.getProducts())
			product.printDetails(System.out);

		System.out.println("Total # of products: " + products.getProducts().size());
	}

	private Hashtable<String, Product> products;

	public ProductList(File file) {
		try {
			initialize(file);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void initialize(File companyFile) throws JSONException, IOException {
		JSONArray jsonArray = JsonUtil.GetJsonArray(companyFile);

		products = new Hashtable<String, Product>(jsonArray.length());

		for (int i = 0; i < jsonArray.length(); i++) {
			Product product = Product.getInstance(jsonArray.getJSONObject(i));
			products.put(product.getName().toLowerCase(), product);
		}
	}

	public Product getProduct(String name) {
		return products.get(name.toLowerCase());
	}

	public Collection<Product> getProducts() {
		return products.values();
	}
}
