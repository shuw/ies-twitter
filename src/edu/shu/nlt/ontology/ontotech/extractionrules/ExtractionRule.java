package edu.shu.nlt.ontology.ontotech.extractionrules;

import org.semanticweb.owl.model.OWLAxiom;

public interface ExtractionRule {

	/**
	 * * @return Matching axiom. Null if not matched.
	 */
	OWLAxiom addAxiom(ExtractionContext context);

}
