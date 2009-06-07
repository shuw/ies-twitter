package edu.shu.nlt.crunchbase.data;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Hashtable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CompanyList {

	public static CompanyList getInstance() {
		return new CompanyList(new File("data/crunchbase/companies.js"));
	}

	public static void main(String[] args) throws JSONException, IOException {

		CompanyList companies = getInstance();

		for (Company company : companies.getCompanies())
			System.out.println(company.getName() + " ID: " + company.getCrunchBaseId());

		System.out.println("Total # of companies: " + companies.getCompanies().size());
	}

	private Hashtable<String, Company> companyTable;

	public CompanyList(File companyFile) {
		try {
			initialize(companyFile);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void initialize(File companyFile) throws JSONException, IOException {
		JSONArray jsonArray = JsonUtil.GetJsonArray(companyFile);

		companyTable = new Hashtable<String, Company>(jsonArray.length());

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			String name = jsonObject.getString("name");
			String crunchbaseID = jsonObject.getString("permalink");

			Company newCompany = new Company(crunchbaseID, name);

			companyTable.put(name.toLowerCase(), newCompany);
		}
	}

	public Collection<Company> getCompanies() {
		return companyTable.values();
	}

	public Company getCompany(String name) {
		return companyTable.get(name.toLowerCase());
	}
}
