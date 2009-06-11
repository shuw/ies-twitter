package edu.shu.nlt.ontology.ontotech.extractionrules;

import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLIndividual;

import edu.shu.nlt.ontology.ontotech.OntologyUpdater;

/**
 * Has some words that indicate a proposition
 * 
 */
public class PropositionRule extends RegexRuleAbstract {
	public PropositionRule() {
		super(regexPatterns, false);
	}

	public static final String[] regexPatterns = { ".* must .*" };

	@Override
	public OWLAxiom getMatchedAxiom(ExtractionContext context) {
		OntologyUpdater ontUpdater = context.getOntologyUpdater();

		OWLIndividual intentionOwl = context.getSentenceIntentionOwl();
		ontUpdater.assertIsClass(intentionOwl, "Proposition");
		return ontUpdater.assertProperty(context.getSentenceOwl(), "hasIntention", intentionOwl);
	}

}
