package edu.shu.nlt.ontology.ontotech;

import java.io.File;
import java.net.URI;

import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.io.RDFXMLOntologyFormat;
import org.semanticweb.owl.model.AddAxiom;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLClassAssertionAxiom;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyChangeException;
import org.semanticweb.owl.model.OWLOntologyCreationException;
import org.semanticweb.owl.model.OWLOntologyManager;
import org.semanticweb.owl.model.OWLOntologyStorageException;
import org.semanticweb.owl.model.UnknownOWLOntologyException;

import edu.shu.nlt.crunchbase.data.base.Company;
import edu.shu.nlt.crunchbase.data.base.Employee;
import edu.shu.nlt.crunchbase.data.base.Product;
import edu.shu.nlt.crunchbase.data.expanded.CompanyInfo;
import edu.shu.nlt.crunchbase.data.lists.CompanyList;

public class OntoCrunchbase implements Runnable {

	private static final int c_minCompanyEmployees = 10;

	// Development variable to speed up ontology creation time
	//
	// -1 for unlimited
	private static final int maxCompaniesToCreate = Integer.MAX_VALUE;

	public static void main(String[] args) throws OWLOntologyCreationException {

		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

		(new OntoCrunchbase(manager)).run();
	}

	private URI base;

	private CompanyList companyList;

	private OWLDataFactory dataFactory;

	private OWLOntologyManager manager;

	private OWLOntology ontology;

	public OntoCrunchbase(OWLOntologyManager manager) {
		super();

		try {
			initialize(manager);
		} catch (OWLOntologyCreationException e) {
			throw new RuntimeException(e);
		}
	}

	private OWLIndividual createIndividual(String type, String crunchbaseId) throws OWLOntologyChangeException {
		OWLIndividual newIndividual = dataFactory.getOWLIndividual(URI.create(base + "/crunchbase/" + type + "#"
				+ crunchbaseId));

		// Declare crunchbase Id
		{
			OWLDataProperty hasCrunchbaseId = dataFactory.getOWLDataProperty(URI.create(base + "#hasCrunchbaseId"));

			OWLDataPropertyAssertionAxiom idAssertion = dataFactory.getOWLDataPropertyAssertionAxiom(newIndividual,
					hasCrunchbaseId, crunchbaseId);

			manager.applyChange(new AddAxiom(ontology, idAssertion));
		}

		// Decalre type of
		{
			OWLClass companyClass = dataFactory.getOWLClass(URI.create(base + "#" + type));
			OWLClassAssertionAxiom isClassOfAxiom = dataFactory.getOWLClassAssertionAxiom(newIndividual, companyClass);
			manager.applyChange(new AddAxiom(ontology, isClassOfAxiom));
		}

		return newIndividual;
	}

	private void assertProperty(OWLIndividual source, String predicateName, OWLIndividual target)
			throws OWLOntologyChangeException {

		OWLObjectProperty predicate = dataFactory.getOWLObjectProperty(URI.create(base + "#" + predicateName));

		OWLObjectPropertyAssertionAxiom newAxiom = dataFactory.getOWLObjectPropertyAssertionAxiom(source, predicate,
				target);

		manager.applyChange(new AddAxiom(ontology, newAxiom));
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

	private void save() throws UnknownOWLOntologyException, OWLOntologyStorageException {
		manager
				.saveOntology(ontology, new RDFXMLOntologyFormat(), (new File("output/CrunchbaseInstances.owl"))
						.toURI());
	}

	public void initialize(OWLOntologyManager manager) throws OWLOntologyCreationException {
		this.manager = manager;
		this.dataFactory = manager.getOWLDataFactory();
		this.companyList = new CompanyList(new File("data/crunchbase/companies.js"));
		this.ontology = manager.loadOntologyFromPhysicalURI((new File("data/ontology/IESTwitter.owl")).toURI());
		this.base = ontology.getURI();
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
