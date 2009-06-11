package edu.shu.nlt.ontology.ontotech.extractionrules;

import java.util.Collection;
import java.util.LinkedList;

import org.semanticweb.owl.model.OWLAxiom;

public class RuleMatcher {

	private static RuleMatcher s_myInstance;

	public static RuleMatcher getInstance() {
		if (s_myInstance == null) {
			s_myInstance = new RuleMatcher();
		}
		return s_myInstance;
	}

	/**
	 * Question, followed by an answer
	 */
	private ExtractionRule StatementRule = new StatementRule();

	/**
	 * Question
	 * 
	 * find named entity, in a question sentence
	 */
	private ExtractionRule QuestionRule = new QuestionRule(false);

	private CompetitorRule CompetitorRule = new CompetitorRule();

	private EventRule EventRule = new EventRule();

	private ExtractionRule[] Rules = { CompetitorRule, QuestionRule, EventRule };

	/**
	 * Return matching results
	 */
	public Collection<OWLAxiom> getAxioms(ExtractionContext extractionContext) {

		LinkedList<OWLAxiom> matches = new LinkedList<OWLAxiom>();

		for (ExtractionRule rule : Rules) {
			OWLAxiom axiom = rule.addAxiom(extractionContext);

			if (axiom != null) {
				matches.add(axiom);
			}
		}
		return matches;

	}

	private RuleMatcher() {
		// do nothing
	};
}
