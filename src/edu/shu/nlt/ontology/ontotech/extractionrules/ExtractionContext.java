package edu.shu.nlt.ontology.ontotech.extractionrules;

import edu.shu.nlt.crunchbase.analysis.NamedEntityRecognizer.NamedMatches;

public class ExtractionContext {
	private String sentence;
	private NamedMatches namedEntitiesInSentence;

	public ExtractionContext(String sentence, NamedMatches namedEntitiesInSentence) {
		super();
		this.sentence = sentence;
		this.namedEntitiesInSentence = namedEntitiesInSentence;
	}

	public String getSentence() {
		return sentence;
	}

	public NamedMatches getNamedEntitiesInSentence() {
		return namedEntitiesInSentence;
	}

}
