package edu.shu.nlt.crunchbase;

import java.io.File;

import edu.shu.nlt.crunchbase.data.Company;
import edu.shu.nlt.crunchbase.data.Person;
import edu.shu.nlt.crunchbase.data.Product;
import edu.shu.nlt.crunchbase.data.lists.CompanyList;
import edu.shu.nlt.crunchbase.data.lists.PersonList;
import edu.shu.nlt.crunchbase.data.lists.ProductList;

public class CrunchbaseScraper implements Runnable {

	public static CrunchbaseScraper getInstance() {
		return new CrunchbaseScraper("cache/crunchbase");
	}
	public static void main(String[] args) {
		getInstance().run();
	}
	private String cachePath;

	private CompanyList companyList;

	private PersonList personList;

	private ProductList productList;

	private CrunchbaseScraper(String path) {
		companyList = CompanyList.getInstance();
		personList = PersonList.getInstance();
		productList = ProductList.getInstance();
		this.cachePath = path;
	}

	private void ensureCache(String type, String crunchbaseId) {

		File file = new File(cachePath + "/" + type, crunchbaseId);
		if (!file.exists()) {
			String url = "http://api.crunchbase.com/v/1/" + type + "/" + crunchbaseId + ".js";
			FileDownload.download(url, file.toString());
			System.out.println("downloaded: " + url);

		} else {
			System.out.println("skipped cached " + file.getName());
		}

	}

	@Override
	public void run() {
		for (Product product : productList.getProducts()) {
			ensureCache("product", product.getCrunchBaseId());
		}

		for (Person person : personList.getPeople()) {
			ensureCache("person", person.getCrunchBaseId());
		}

		for (Company company : companyList.getCompanies()) {
			ensureCache("company", company.getCrunchBaseId());
		}

	}
}
