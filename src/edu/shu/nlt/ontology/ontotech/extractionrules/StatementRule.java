package edu.shu.nlt.ontology.ontotech.extractionrules;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.semanticweb.owl.model.OWLAxiom;

/**
 * Matches sentences that end in a period or exclamation
 * 
 */
public class StatementRule implements ExtractionRule {

	private Pattern simplePattern;

	public StatementRule() {

		simplePattern = Pattern.compile(".*[.!]$");

	}

	@Override
	public OWLAxiom addAxiom(ExtractionContext context) {
		// compare case-insensitive
		String sentence = context.getSentence().toLowerCase();

		boolean isFound = true;

		if (simplePattern != null) {
			Matcher matcher = simplePattern.matcher(sentence);
			isFound = matcher.find();
		}

		if (isFound) {

			return context.getOntologyUpdater().assertIsClass(context.getSentenceOwl(), "Question");
		}
		return null;

	}
}
