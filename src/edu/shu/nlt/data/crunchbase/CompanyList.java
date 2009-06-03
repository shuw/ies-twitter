package edu.shu.nlt.data.crunchbase;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CompanyList {

	public CompanyList(File companyFile) throws JSONException, IOException {
		initialize(companyFile);
	}

	private List<Company> companies;

	private void initialize(File companyFile) throws JSONException, IOException {
		JSONArray jsonArray = JsonUtil.GetJsonArray(companyFile);

		companies = new ArrayList<Company>(jsonArray.length());

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			String name = jsonObject.getString("name");
			String crunchbaseID = jsonObject.getString("permalink");

			companies.add(new Company(crunchbaseID, name));
		}
	}

	public Collection<Company> getCompanies() {
		return companies;
	}

	public static void main(String[] args) throws JSONException, IOException {

		CompanyList companies = new CompanyList(new File("data/crunchbase/companies.js"));

		for (Company company : companies.getCompanies())
			System.out.println(company.getName() + " ID: " + company.getCrunchBaseId());

		System.out.println("Total # of companies: " + companies.getCompanies().size());
	}
}
