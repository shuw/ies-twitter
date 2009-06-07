package edu.shu.nlt.crunchbase.data.expanded;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.shu.nlt.crunchbase.data.JsonUtil;
import edu.shu.nlt.crunchbase.data.base.Company;
import edu.shu.nlt.crunchbase.data.base.Employee;
import edu.shu.nlt.crunchbase.data.base.Product;

public class CompanyInfo {

	public static CompanyInfo getInstance(String crunchbaseId) {

		File cachedInfo = new File("cache/crunchbase/company", crunchbaseId);

		if (cachedInfo.exists()) {
			try {
				return new CompanyInfo(cachedInfo);

			} catch (JSONException ex) {
				if (cachedInfo.length() != 0) {
					throw new RuntimeException(ex);
				}

			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		return null;
	}

	/**
	 * Test method
	 */
	public static void main(String[] args) {
		CompanyInfo facebookInfo = CompanyInfo.getInstance("facebook");
		for (Employee employee : facebookInfo.getEmployees())
			employee.printDetails(System.out);

		for (Product product : facebookInfo.getProducts())
			product.printDetails(System.out);

		for (Company competitor : facebookInfo.getCompetitors())
			competitor.printDetails(System.out);

	}

	private List<Company> competitors;

	private List<Employee> employees;

	private int numOfEmployees;

	private List<Product> products;

	private CompanyInfo(File file) throws JSONException, IOException {
		super();

		initalize(file);

	}

	private void initalize(File file) throws JSONException, IOException {

		JSONObject companyJson = JsonUtil.GetJsonObject(file);

		numOfEmployees = !companyJson.isNull("number_of_employees") ? companyJson.getInt("number_of_employees") : 0;

		// Parse products
		{
			JSONArray productsJson = companyJson.getJSONArray("products");

			products = new ArrayList<Product>(productsJson.length());
			for (int i = 0; i < productsJson.length(); i++)
				products.add(Product.getInstance(productsJson.getJSONObject(i)));
		}

		// Parse employees
		{
			JSONArray employeesJson = companyJson.getJSONArray("relationships");

			employees = new ArrayList<Employee>(employeesJson.length());

			for (int i = 0; i < employeesJson.length(); i++)
				employees.add(Employee.getInstance(employeesJson.getJSONObject(i)));
		}

		// Parse competitors
		{
			JSONArray competitorsJson = companyJson.getJSONArray("competitions");

			competitors = new ArrayList<Company>(competitorsJson.length());

			for (int i = 0; i < competitorsJson.length(); i++)
				competitors.add(Company.getInstance(competitorsJson.getJSONObject(i).getJSONObject("competitor")));

		}

	}

	public List<Company> getCompetitors() {
		return competitors;
	}

	public List<Employee> getEmployees() {
		return employees;
	}

	public int getNumOfEmployees() {
		return numOfEmployees;
	}

	public List<Product> getProducts() {
		return products;
	}

}
