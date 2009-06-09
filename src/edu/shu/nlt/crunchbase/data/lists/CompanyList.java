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

	public static CompanyList getInstance(File file) {
		if (s_companyList == null)
			s_companyList = new CompanyList(file);
		return s_companyList;
	}

	public static void main(String[] args) throws JSONException, IOException {

		CompanyList companies = getInstance(new File("data/crunchbase/companies.js"));

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
			populateInstances(companyFile);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public CompanyList() {
		companyTable = new Hashtable<String, Company>();
	}

	private void populateInstances(File companyFile) throws JSONException, IOException {
		JSONArray jsonArray = JsonUtil.GetJsonArray(companyFile);

		companyTable = new Hashtable<String, Company>(jsonArray.length());

		for (int i = 0; i < jsonArray.length(); i++) {
			Company newCompany = Company.getInstance(jsonArray.getJSONObject(i));

			addCompany(newCompany);
		}
	}

	public void addCompany(Company company) {
		companyTable.put(company.getName().toLowerCase(), company);
	}

	public Collection<Company> getCompanies() {
		return companyTable.values();
	}

	public Company getCompany(String name) {
		return companyTable.get(name.toLowerCase());
	}
}
