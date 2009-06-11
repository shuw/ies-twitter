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
	private ExtractionRule questionRule = new QuestionRule(false);

	private CompetitorRule competitorRule = new CompetitorRule();

	private EventRule eventRule = new EventRule();

	private DesireRule desireRule = new DesireRule();

	private IntrospectionRule introspectionRule = new IntrospectionRule();

	private SurpriseRule surpriseRule = new SurpriseRule();

	private PropositionRule propositionRule = new PropositionRule();

	private ExtractionRule[] Rules = { desireRule, eventRule, questionRule, competitorRule, introspectionRule,
			surpriseRule, propositionRule };

	// private ExtractionRule[] Rules = { propositionRule };

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
