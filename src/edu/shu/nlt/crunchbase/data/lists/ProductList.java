package edu.shu.nlt.crunchbase.data.lists;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Hashtable;

import org.json.JSONArray;
import org.json.JSONException;

import edu.shu.nlt.crunchbase.data.JsonUtil;
import edu.shu.nlt.crunchbase.data.base.Product;

public class ProductList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) throws JSONException, IOException {

		ProductList products = new ProductList(new File("data/crunchbase/products.js"));

		int hasCompany = 0;

		for (Product product : products.getProducts()) {
			product.printDetails(System.out);

			if (product.getProductInfo() != null) {
				if (product.getProductInfo().getCompany() != null) {
					hasCompany++;
				}
			}
		}

		System.out.println("Total # of products: " + products.getProducts().size());
		System.out.println("Total with company defined: " + hasCompany);
	}

	private Hashtable<String, Product> products;

	public ProductList(File file) {
		try {
			initializeInstances(file);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public ProductList() {
		products = new Hashtable<String, Product>();
	}

	private void initializeInstances(File companyFile) throws JSONException, IOException {
		JSONArray jsonArray = JsonUtil.GetJsonArray(companyFile);

		products = new Hashtable<String, Product>(jsonArray.length());

		for (int i = 0; i < jsonArray.length(); i++) {
			Product product = Product.getInstance(jsonArray.getJSONObject(i));
			addProduct(product);
		}
	}

	public void addProduct(Product product) {
		products.put(product.getName().toLowerCase(), product);
	}

	public Product getProduct(String name) {
		return products.get(name.toLowerCase());
	}

	public Collection<Product> getProducts() {
		return products.values();
	}
}
