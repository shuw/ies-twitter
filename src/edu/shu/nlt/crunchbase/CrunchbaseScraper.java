package edu.shu.nlt.crunchbase;

import java.io.File;

import edu.shu.nlt.crunchbase.data.base.Company;
import edu.shu.nlt.crunchbase.data.base.Person;
import edu.shu.nlt.crunchbase.data.base.Product;

public class CrunchbaseScraper implements Runnable {

	public static CrunchbaseScraper getInstance() {
		return new CrunchbaseScraper("cache/crunchbase");
	}

	public static void main(String[] args) {
		getInstance().run();
	}

	private String cachePath;

	private Crunchbase crunchbase;

	private CrunchbaseScraper(String path) {
		crunchbase = Crunchbase.getInstance();

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
		for (Product product : crunchbase.getProductsList().getProducts()) {
			ensureCache("product", product.getCrunchBaseId());
		}

		for (Person person : crunchbase.getPersonList().getPeople()) {
			ensureCache("person", person.getCrunchBaseId());
		}

		for (Company company : crunchbase.getCompanyList().getCompanies()) {
			ensureCache("company", company.getCrunchBaseId());
		}

	}
}
