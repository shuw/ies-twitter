package edu.shu.nlt.ontology.ontotech.extractionrules;

import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLIndividual;

import edu.shu.nlt.ontology.ontotech.OntologyUpdater;

public class DesireRule extends RegexRuleAbstract {

	public DesireRule() {
		super(regexPatterns, true);
	}

	public static final String[] regexPatterns = { ".*i want.*", ".*i need.*" };

	@Override
	public OWLAxiom getMatchedAxiom(ExtractionContext context) {
		OntologyUpdater ontUpdater = context.getOntologyUpdater();

		OWLIndividual intentionOwl = context.getSentenceIntentionOwl();
		ontUpdater.assertIsClass(intentionOwl, "Desire");
		return ontUpdater.assertProperty(context.getSentenceOwl(), "hasIntention", intentionOwl);

	}
}
