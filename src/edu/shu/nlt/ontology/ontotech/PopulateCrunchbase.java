package edu.shu.nlt.ontology.ontotech;

import java.io.File;
import java.io.PrintStream;
import java.util.Collection;

import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLOntologyChangeException;
import org.semanticweb.owl.model.OWLOntologyCreationException;

import edu.shu.nlt.crunchbase.Crunchbase;
import edu.shu.nlt.crunchbase.data.base.Company;
import edu.shu.nlt.crunchbase.data.base.Employee;
import edu.shu.nlt.crunchbase.data.base.Person;
import edu.shu.nlt.crunchbase.data.base.Product;
import edu.shu.nlt.crunchbase.data.expanded.CompanyInfo;

/**
 * Populates ontology with Crunchbase Individuals
 * 
 * @author shu
 * 
 */
public class PopulateCrunchbase implements Runnable {

	private static final int c_minCompanyEmployees = 2;

	// Development variable to speed up ontology creation time
	//
	// -1 for unlimited
	private static final int maxCompaniesToCreate = Integer.MAX_VALUE;

	public static void main(String[] args) throws OWLOntologyCreationException {

		OntologyUpdater ontology = new OntologyUpdater(new File("data/ontology/IESTwitter.owl"), new File(
				"output/crunchbase.owl"));

		(new PopulateCrunchbase(ontology, Crunchbase.getInstance().getCompanyList().getCompanies())).run();
	}

	private Collection<Company> companyList;

	private OntologyUpdater ontology;

	public PopulateCrunchbase(OntologyUpdater ontology, Collection<Company> companies) {

		this.ontology = ontology;

		this.companyList = companies;
	}

	public void printDetails(PrintStream out) {
		out.println("Companies created: " + companiesCreated);
		out.println("Totalo companies scanned: " + totalCompaniesProcessed);
	}

	private int companiesCreated = 0;
	private int totalCompaniesProcessed = 0;

	private void populateInstances() throws OWLOntologyChangeException {

		for (Company company : companyList) {
			totalCompaniesProcessed++;
			CompanyInfo companyInfo = company.getCompanyInfo();

			if (companyInfo != null && companyInfo.getEmployees().size() >= c_minCompanyEmployees) {
				if (++companiesCreated > maxCompaniesToCreate) {
					return;
				}

				System.out.println("Populating company: " + company.getName() + "  total processed: "
						+ totalCompaniesProcessed);

				OWLIndividual companyOwl = assertCompany(ontology, company);

				for (Employee employee : companyInfo.getEmployees()) {
					OWLIndividual employeeOwl = assertPerson(ontology, employee.getPerson());
					ontology.assertProperty(companyOwl, "isEmployerOf", employeeOwl);
				}

				for (Product product : companyInfo.getProducts()) {
					OWLIndividual productOwl = assertProduct(ontology, product);
					ontology.assertProperty(companyOwl, "hasProduct", productOwl);
				}
			}
		}
	}

	public static OWLIndividual assertCompany(OntologyUpdater ontology, Company company) {
		OWLIndividual companyOwl = ontology.getIndividual(company.getCrunchBaseId());
		ontology.assertIsClass(companyOwl, "TechnologyCompany");
		ontology.assertDataProperty(companyOwl, "hasCrunchbaseId", company.getCrunchBaseId());

		return companyOwl;
	}

	public static OWLIndividual assertPerson(OntologyUpdater ontology, Person person) {
		OWLIndividual employeeOwl = ontology.getIndividual(person.getCrunchBaseId());
		ontology.assertIsClass(employeeOwl, "Person");
		ontology.assertDataProperty(employeeOwl, "hasCrunchbaseId", person.getCrunchBaseId());

		return employeeOwl;
	}

	public static OWLIndividual assertProduct(OntologyUpdater ontology, Product product) {
		OWLIndividual productOwl = ontology.getIndividual(product.getCrunchBaseId());
		ontology.assertIsClass(productOwl, "Product");
		ontology.assertDataProperty(productOwl, "hasCrunchbaseId", product.getCrunchBaseId());

		return productOwl;
	}

	@Override
	public void run() {
		try {
			populateInstances();
			printDetails(System.out);

			ontology.save();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
