package edu.shu.nlt.crunchbase.data.expanded;

import java.io.File;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import edu.shu.nlt.crunchbase.data.JsonUtil;
import edu.shu.nlt.crunchbase.data.base.Company;

/**
 * Partial product information from crunchbase
 * 
 * @author shu
 * 
 */
public class ProductInfo {
	private Company company;

	private ProductInfo(File file) throws JSONException, IOException {
		super();

		initalize(file);
	}

	/**
	 * Test method
	 */
	public static void main(String[] args) {
		ProductInfo facebookInfo = ProductInfo.getInstance("facebook-platform");

		facebookInfo.getCompany().printDetails(System.out);
	}

	private void initalize(File file) throws JSONException, IOException {

		JSONObject productJson = JsonUtil.GetJsonObject(file);

		company = !productJson.isNull("company") ? Company.getInstance(productJson.getJSONObject("company")) : null;

	}

	public Company getCompany() {
		return company;
	}

	public static ProductInfo getInstance(String crunchbaseId) {

		File cachedInfo = new File("cache/crunchbase/product", crunchbaseId);

		if (cachedInfo.exists()) {
			try {
				return new ProductInfo(cachedInfo);
			} catch (JSONException e) {
				if (cachedInfo.length() != 0) {
					throw new RuntimeException(e);
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return null;

	}
}
