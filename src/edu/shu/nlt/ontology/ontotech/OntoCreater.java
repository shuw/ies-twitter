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

public class OntoCreater {
	public OntoCreater(File input, File output) {
		super();

		this.outputFile = output;
		manager = OWLManager.createOWLOntologyManager();

		try {
			initialize(manager, input);
		} catch (OWLOntologyCreationException e) {
			throw new RuntimeException(e);
		}
	}

	private URI base;
	private OWLDataFactory dataFactory;

	private OWLOntologyManager manager;

	private OWLOntology ontology;

	private File outputFile;

	public void initialize(OWLOntologyManager manager, File inputFile) throws OWLOntologyCreationException {
		this.manager = manager;
		this.dataFactory = manager.getOWLDataFactory();

		this.ontology = manager.loadOntologyFromPhysicalURI(inputFile.toURI());
		this.base = ontology.getURI();
	}

	protected void assertProperty(OWLIndividual source, String predicateName, OWLIndividual target)
			throws OWLOntologyChangeException {

		OWLObjectProperty predicate = dataFactory.getOWLObjectProperty(URI.create(base + "#" + predicateName));

		OWLObjectPropertyAssertionAxiom newAxiom = dataFactory.getOWLObjectPropertyAssertionAxiom(source, predicate,
				target);

		manager.applyChange(new AddAxiom(ontology, newAxiom));
	}

	protected void save() throws UnknownOWLOntologyException, OWLOntologyStorageException {
		manager.saveOntology(ontology, new RDFXMLOntologyFormat(), (outputFile).toURI());
	}

	protected OWLIndividual createIndividual(String type, String crunchbaseId) throws OWLOntologyChangeException {
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
}
