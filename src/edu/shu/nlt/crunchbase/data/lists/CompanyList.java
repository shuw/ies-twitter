package edu.shu.nlt.crunchbase.data.lists;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Hashtable;

import org.json.JSONArray;
import org.json.JSONException;

import edu.shu.nlt.crunchbase.data.JsonUtil;
import edu.shu.nlt.crunchbase.data.base.Company;

public class CompanyList {
	private static CompanyList s_companyList;

	public static CompanyList getInstance() {
		if (s_companyList == null)
			s_companyList = new CompanyList(new File("data/crunchbase/companies.js"));
		return s_companyList;
	}

	public static void main(String[] args) throws JSONException, IOException {

		CompanyList companies = getInstance();

		int greaterThan10Employees = 0;

		for (Company company : companies.getCompanies()) {
			company.printDetails(System.out);

			if (company.getCompanyInfo() != null && company.getCompanyInfo().getNumOfEmployees() > 10) {
				greaterThan10Employees++;
			}
		}

		System.out.println("Total # of companies: " + companies.getCompanies().size());
		System.out.println("Total with more than 10 employees: " + greaterThan10Employees);
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
			Company newCompany = Company.getInstance(jsonArray.getJSONObject(i));

			companyTable.put(newCompany.getName().toLowerCase(), newCompany);
		}
	}

	public Collection<Company> getCompanies() {
		return companyTable.values();
	}

	public Company getCompany(String name) {
		return companyTable.get(name.toLowerCase());
	}
}
