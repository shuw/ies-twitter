package edu.shu.nlt.ontology.ontotech.extractionrules;

import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLIndividual;

import edu.shu.nlt.ontology.ontotech.OntologyUpdater;

public class SurpriseRule extends RegexRuleAbstract {
	public SurpriseRule() {
		super(regexPatterns, false);
	}

	public static final String[] regexPatterns = { ".*!$" };

	@Override
	public OWLAxiom getMatchedAxiom(ExtractionContext context) {
		OntologyUpdater ontUpdater = context.getOntologyUpdater();

		OWLIndividual intentionOwl = context.getSentenceIntentionOwl();
		ontUpdater.assertIsClass(intentionOwl, "Surprise");
		return ontUpdater.assertProperty(context.getSentenceOwl(), "hasIntention", intentionOwl);
	}

}
