package edu.shu.nlt.ontology.ontotech.extractionrules;

import java.util.regex.Pattern;

import org.semanticweb.owl.model.OWLAxiom;

public abstract class RegexRuleAbstract implements ExtractionRule {

	private boolean isCaseSensitive = false;

	private Pattern[] myPatterns;

	public RegexRuleAbstract(String[] regexPatterns, boolean isCaseInsensitive) {
		super();

		this.isCaseSensitive = isCaseInsensitive;

		myPatterns = new Pattern[regexPatterns.length];

		for (int i = 0; i < regexPatterns.length; i++) {
			this.myPatterns[i] = Pattern.compile(regexPatterns[i]);
		}

	}

	@Override
	public OWLAxiom addAxiom(ExtractionContext context) {

		String value = isCaseSensitive ? context.getSentence().toLowerCase() : context.getSentence();

		for (Pattern myPattern : myPatterns) {
			if (myPattern.matcher(value).find()) {

				return getMatchedAxiom(context);
			}
		}
		return null;

	}

	public abstract OWLAxiom getMatchedAxiom(ExtractionContext context);
}
