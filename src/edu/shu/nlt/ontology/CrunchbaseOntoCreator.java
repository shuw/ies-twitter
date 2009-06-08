package edu.shu.nlt.ontology;

import java.net.URI;

import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.io.OWLXMLOntologyFormat;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyCreationException;
import org.semanticweb.owl.model.OWLOntologyManager;
import org.semanticweb.owl.model.OWLOntologyStorageException;

public class CrunchbaseOntoCreator {
	public CrunchbaseOntoCreator() {
	}

	public static void main(String[] args) {
		try {
			// A simple example of how to load and save an ontology
			// We first need to obtain a copy of an OWLOntologyManager, which,
			// as the
			// name suggests, manages a set of ontologies. An ontology is unique
			// within
			// an ontology manager. To load multiple copies of an ontology,
			// multiple managers
			// would have to be used.
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			// We load an ontology from a physical URI - in this case we'll load
			// the pizza
			// ontology.
			URI physicalURI = URI.create("http://www.co-ode.org/ontologies/pizza/2007/02/12/pizza.owl");
			// Now ask the manager to load the ontology
			OWLOntology ontology = manager.loadOntologyFromPhysicalURI(physicalURI);
			// Print out all of the classes which are referenced in the ontology
			for (OWLClass cls : ontology.getReferencedClasses()) {
				System.out.println(cls);
			}
			// Now save a copy to another location in OWL/XML format (i.e.
			// disregard the
			// format that the ontology was loaded in).
			// (To save the file on windows use a URL such as
			// "file:/C:\\windows\\temp\\MyOnt.owl")
			URI physicalURI2 = URI.create("file:/tmp/MyOnt2.owl");
			manager.saveOntology(ontology, new OWLXMLOntologyFormat(), physicalURI2);
			// Remove the ontology from the manager
			manager.removeOntology(ontology.getURI());
		} catch (OWLOntologyCreationException e) {
			System.out.println("The ontology could not be created: " + e.getMessage());
		} catch (OWLOntologyStorageException e) {
			System.out.println("The ontology could not be saved: " + e.getMessage());
		}

	}
}
