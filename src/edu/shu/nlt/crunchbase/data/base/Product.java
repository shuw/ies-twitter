package edu.shu.nlt.crunchbase.data.base;

import java.io.PrintStream;

import org.json.JSONException;
import org.json.JSONObject;

import edu.nlt.shallow.data.Keyable;
import edu.shu.nlt.crunchbase.data.expanded.ProductInfo;

public class Product implements Keyable {
	public static Product getInstance(JSONObject jsonObject) {
		try {
			String name = jsonObject.getString("name");

			String crunchbaseID = jsonObject.getString("permalink");

			return new Product(crunchbaseID, name);

		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}

	private String crunchBaseId;

	private String name;

	private ProductInfo productInfo;

	private Product(String crunchBaseId, String name) {
		super();
		this.name = name;
		this.crunchBaseId = crunchBaseId;
	}

	public String getCrunchBaseId() {
		return crunchBaseId;
	}

	@Override
	public String getKey() {
		return crunchBaseId;
	}

	public String getName() {
		return name;
	}

	public ProductInfo getProductInfo() {
		if (productInfo == null) {
			productInfo = ProductInfo.getInstance(getCrunchBaseId());
		}

		return productInfo;
	}

	public void printDetails(PrintStream stream) {
		stream.println(name + " ID: " + crunchBaseId);

	}

	public void setCrunchBaseId(String crunchBaseId) {
		this.crunchBaseId = crunchBaseId;
	}

	public void setName(String name) {
		this.name = name;
	}
}
