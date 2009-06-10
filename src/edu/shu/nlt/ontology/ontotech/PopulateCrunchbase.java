package edu.shu.nlt.ontology.ontotech;

import java.io.File;

import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLOntologyChangeException;
import org.semanticweb.owl.model.OWLOntologyCreationException;

import edu.shu.nlt.crunchbase.Crunchbase;
import edu.shu.nlt.crunchbase.data.base.Company;
import edu.shu.nlt.crunchbase.data.base.Employee;
import edu.shu.nlt.crunchbase.data.base.Product;
import edu.shu.nlt.crunchbase.data.expanded.CompanyInfo;
import edu.shu.nlt.crunchbase.data.lists.CompanyList;

/**
 * Populates ontology with Crunchbase Individuals
 * 
 * @author shu
 * 
 */
public class PopulateCrunchbase implements Runnable {

	private static final int c_minCompanyEmployees = 10;

	// Development variable to speed up ontology creation time
	//
	// -1 for unlimited
	private static final int maxCompaniesToCreate = Integer.MAX_VALUE;

	public static void main(String[] args) throws OWLOntologyCreationException {

		OntologyUpdater ontology = new OntologyUpdater(new File("data/ontology/IESTwitter.owl"), new File(
				"output/CrunchbaseInstances.owl"));

		(new PopulateCrunchbase(ontology)).run();
	}

	private OntologyUpdater ontology;

	private CompanyList companyList;

	public PopulateCrunchbase(OntologyUpdater ontology) {

		this.ontology = ontology;

		this.companyList = Crunchbase.getInstance().getCompanyList();
	}

	private void populateInstances() throws OWLOntologyChangeException {

		int companiesCreated = 0;
		int totalCompaniesProcessed = 0;
		for (Company company : companyList.getCompanies()) {
			totalCompaniesProcessed++;
			CompanyInfo companyInfo = company.getCompanyInfo();

			if (companyInfo != null && companyInfo.getEmployees().size() >= c_minCompanyEmployees) {
				if (++companiesCreated > maxCompaniesToCreate) {
					return;
				}

				System.out.println("Processing company: " + company.getName() + "  total processed: "
						+ totalCompaniesProcessed);

				OWLIndividual companyOwl = ontology.createIndividual("Company", company.getCrunchBaseId());
				ontology.assertDataProperty(companyOwl, "hasCrunchbaseId", company.getCrunchBaseId());

				for (Employee employee : companyInfo.getEmployees()) {
					OWLIndividual employeeOwl = ontology.createIndividual("Person", employee.getPerson()
							.getCrunchBaseId());
					ontology.assertDataProperty(employeeOwl, "hasCrunchbaseId", employee.getPerson().getCrunchBaseId());
					ontology.assertProperty(companyOwl, "isEmployerOf", employeeOwl);
				}

				for (Product product : companyInfo.getProducts()) {
					OWLIndividual productOwl = ontology.createIndividual("Product", product.getCrunchBaseId());
					ontology.assertDataProperty(productOwl, "hasCrunchbaseId", product.getCrunchBaseId());
					ontology.assertProperty(companyOwl, "hasProduct", productOwl);
				}
			}
		}
	}

	@Override
	public void run() {
		try {
			populateInstances();

			ontology.save();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
