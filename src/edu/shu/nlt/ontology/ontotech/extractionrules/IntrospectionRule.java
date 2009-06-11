package edu.shu.nlt.ontology.ontotech.extractionrules;

import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLIndividual;

import edu.shu.nlt.ontology.ontotech.OntologyUpdater;

/**
 * Sentence refers to "I" more than once
 * 
 */
public class IntrospectionRule extends RegexRuleAbstract {
	public IntrospectionRule() {
		super(regexPatterns, false);
	}

	public static final String[] regexPatterns = { "I .*.*I .*$" };

	@Override
	public OWLAxiom getMatchedAxiom(ExtractionContext context) {
		OntologyUpdater ontUpdater = context.getOntologyUpdater();

		OWLIndividual intentionOwl = context.getSentenceIntentionOwl();
		ontUpdater.assertIsClass(intentionOwl, "Introspection");
		return ontUpdater.assertProperty(context.getSentenceOwl(), "hasIntention", intentionOwl);
	}

}
