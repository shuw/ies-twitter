package edu.shu.nlt.ontology.ontotech;

import org.semanticweb.owl.model.OWLOntologyCreationException;

public class PopulateAll {

	public static void main(String[] args) throws OWLOntologyCreationException {
		PopulateCrunchbase.main(null);
		PopulateTweets.main(null);
	}
}
