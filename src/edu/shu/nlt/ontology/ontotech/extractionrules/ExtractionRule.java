package edu.shu.nlt.ontology.ontotech.extractionrules;


public interface ExtractionRule {

	boolean isMatch(ExtractionContext extractionSentence);

	String getRuleName();

}
