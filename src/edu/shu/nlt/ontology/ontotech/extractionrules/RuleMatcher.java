package edu.shu.nlt.ontology.ontotech.extractionrules;

import java.util.Collection;
import java.util.LinkedList;

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
	private ExtractionRule QuestionAnswerRule = new RegexExtractionRule(".*\\?[ ]+[^\\?]+$", "QuestionAnswerRule");

	/**
	 * Question
	 * 
	 * find named entity, in a question sentence
	 */
	private ExtractionRule QuestionRule = new RegexExtractionRule(".*\\?$", "QuestionRule");

	private CompetitorRule CompetitorRule = new CompetitorRule();

	private ExtractionRule[] Rules = { CompetitorRule };

	/**
	 * Return matching results
	 */
	public Collection<ExtractionRule> getMatches(ExtractionContext extractionContext) {

		LinkedList<ExtractionRule> matches = new LinkedList<ExtractionRule>();

		for (ExtractionRule rule : Rules) {
			if (rule.isMatch(extractionContext)) {
				matches.add(rule);
			}
		}
		return matches;

	}

	private RuleMatcher() {
		// do nothing
	};
}
