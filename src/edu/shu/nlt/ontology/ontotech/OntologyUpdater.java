package edu.shu.nlt.ontology.ontotech;

import java.io.File;
import java.net.URI;
import java.util.Collections;

import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.io.RDFXMLOntologyFormat;
import org.semanticweb.owl.model.AddAxiom;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLClassAssertionAxiom;
import org.semanticweb.owl.model.OWLCommentAnnotation;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLEntityAnnotationAxiom;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyChangeException;
import org.semanticweb.owl.model.OWLOntologyCreationException;
import org.semanticweb.owl.model.OWLOntologyManager;
import org.semanticweb.owl.util.OWLEntityRemover;

public class OntologyUpdater {
	private static void addAxiom(OWLOntologyManager manager, OWLOntology ontology, OWLAxiom axiom) {
		synchronized (manager) {
			try {

				manager.applyChange(new AddAxiom(ontology, axiom));
			} catch (OWLOntologyChangeException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private URI base;
	private OWLDataFactory dataFactory;

	private OWLOntologyManager manager;

	private OWLOntology ontology;

	private File outputFile;

	private OWLEntityRemover remover;

	public OntologyUpdater(File input, File output) {
		super();

		this.outputFile = output;
		manager = OWLManager.createOWLOntologyManager();

		try {
			initialize(manager, input);
		} catch (OWLOntologyCreationException e) {
			throw new RuntimeException(e);
		}
	}

	private void initialize(OWLOntologyManager manager, File inputFile) throws OWLOntologyCreationException {
		this.manager = manager;
		this.dataFactory = manager.getOWLDataFactory();
		this.remover = new OWLEntityRemover(manager, Collections.singleton(ontology));
		this.ontology = manager.loadOntologyFromPhysicalURI(inputFile.toURI());
		this.base = ontology.getURI();
	}

	public OWLAxiom assertCommentAnnotation(OWLIndividual individual, String value) {

		OWLCommentAnnotation comment = dataFactory.getCommentAnnotation(value);

		OWLEntityAnnotationAxiom axiom = dataFactory.getOWLEntityAnnotationAxiom(individual, comment);

		addAxiom(manager, ontology, axiom);

		return axiom;
	}

	public OWLAxiom assertDataProperty(OWLIndividual individual, String propertyName, String value) {
		OWLDataProperty property = dataFactory.getOWLDataProperty(URI.create(base + "#" + propertyName));

		OWLDataPropertyAssertionAxiom axiom = dataFactory.getOWLDataPropertyAssertionAxiom(individual, property, value);

		addAxiom(manager, ontology, axiom);

		return axiom;
	}

	public OWLAxiom assertIsClass(OWLIndividual individual, String typeName) {

		OWLClass owlType = dataFactory.getOWLClass(URI.create(base + "#" + typeName));
		OWLClassAssertionAxiom axiom = dataFactory.getOWLClassAssertionAxiom(individual, owlType);

		addAxiom(manager, ontology, axiom);
		return axiom;
	}

	public OWLAxiom assertProperty(OWLIndividual source, String predicateName, OWLIndividual target) {

		OWLObjectProperty predicate = dataFactory.getOWLObjectProperty(URI.create(base + "#" + predicateName));

		OWLObjectPropertyAssertionAxiom axiom = dataFactory.getOWLObjectPropertyAssertionAxiom(source, predicate,
				target);

		addAxiom(manager, ontology, axiom);
		return axiom;
	}

	public OWLDataFactory getDataFactory() {
		return dataFactory;
	}

	public OWLIndividual getIndividual(String name) {
		return dataFactory.getOWLIndividual(URI.create(base + "#" + name));
	}

	public OWLOntology getOntology() {
		return ontology;
	}

	public void removeIndividual(OWLIndividual individual) {

		individual.accept(remover);

		try {
			manager.applyChanges(remover.getChanges());
		} catch (OWLOntologyChangeException e) {
			throw new RuntimeException(e);
		}
	}

	public void save() {
		try {
			manager.saveOntology(ontology, new RDFXMLOntologyFormat(), (outputFile).toURI());
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

}
