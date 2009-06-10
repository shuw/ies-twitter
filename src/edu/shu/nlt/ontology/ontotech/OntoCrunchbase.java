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
public class OntoCrunchbase extends OntoCreater implements Runnable {

	private static final int c_minCompanyEmployees = 10;

	// Development variable to speed up ontology creation time
	//
	// -1 for unlimited
	private static final int maxCompaniesToCreate = Integer.MAX_VALUE;

	public static void main(String[] args) throws OWLOntologyCreationException {

		(new OntoCrunchbase(new File("data/ontology/IESTwitter.owl"), new File("output/CrunchbaseInstances.owl")))
				.run();
	}

	private CompanyList companyList;

	public OntoCrunchbase(File input, File output) {
		super(input, output);
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

				OWLIndividual companyOwl = createIndividual("Company", company.getCrunchBaseId());

				for (Employee employee : companyInfo.getEmployees()) {
					OWLIndividual employeeOwl = createIndividual("Person", employee.getPerson().getCrunchBaseId());
					assertProperty(companyOwl, "isEmployerOf", employeeOwl);
				}

				for (Product product : companyInfo.getProducts()) {
					OWLIndividual productOwl = createIndividual("Product", product.getCrunchBaseId());
					assertProperty(companyOwl, "hasProduct", productOwl);
				}
			}
		}
	}

	@Override
	public void run() {
		try {
			populateInstances();

			save();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
