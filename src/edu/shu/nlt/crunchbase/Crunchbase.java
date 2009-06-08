package edu.shu.nlt.crunchbase;

import edu.shu.nlt.crunchbase.data.lists.CompanyList;
import edu.shu.nlt.crunchbase.data.lists.PersonList;
import edu.shu.nlt.crunchbase.data.lists.ProductList;

public class Crunchbase {

	private static Crunchbase s_crunchBase;

	public static Crunchbase getInstance() {
		if (s_crunchBase == null)
			s_crunchBase = new Crunchbase();

		return s_crunchBase;
	}

	private CompanyList companyList;

	private PersonList personList;

	private ProductList productsList;

	public Crunchbase() {
		productsList = ProductList.getInstance();
		personList = PersonList.getInstance();
		companyList = CompanyList.getInstance();
	}

	public CompanyList getCompanyList() {
		return companyList;
	}

	public PersonList getPersonList() {
		return personList;
	}

	public ProductList getProductsList() {
		return productsList;
	}

	public void setCompanyList(CompanyList companyList) {
		this.companyList = companyList;
	}

	public void setPersonList(PersonList personList) {
		this.personList = personList;
	}

	public void setProductsList(ProductList productsList) {
		this.productsList = productsList;
	}

}
