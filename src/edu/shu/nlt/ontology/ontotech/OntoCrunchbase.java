package edu.shu.nlt.ontology.ontotech;

import java.io.File;
import java.net.URI;

import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyCreationException;
import org.semanticweb.owl.model.OWLOntologyManager;

import edu.shu.nlt.crunchbase.Crunchbase;
import edu.shu.nlt.crunchbase.data.base.Product;

public class OntoCrunchbase implements Runnable {

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

	public void initialize(OWLOntologyManager manager) throws OWLOntologyCreationException {
		this.manager = manager;

		crunchbase = Crunchbase.getInstance();

		ontology = manager.loadOntologyFromPhysicalURI((new File("data/ontology/TechOpinions.owl")).toURI());
	}

	public static void main(String[] args) throws OWLOntologyCreationException {

		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

		(new OntoCrunchbase(manager)).run();
	}

	private Crunchbase crunchbase;

	@Override
	public void run() {
		populateInstances();
	}

	private void populateInstances() {
		OWLDataFactory dataFactory = manager.getOWLDataFactory();

		for (Product product : crunchbase.getProductsList().getProducts()) {
			OWLIndividual newClass = dataFactory.getOWLIndividual(URI.create(ontology.getURI() + "/product/#"
					+ product.getCrunchBaseId()));

			
//			dataFactory.getOWLDataPropertyAssertionAxiom(arg0, arg1, arg2)
			
			System.out.println(newClass.getURI());
		}
	}
}
