package edu.shu.nlt.ontology.ontotech.extractionrules;

import org.semanticweb.owl.model.OWLIndividual;

import edu.shu.nlt.crunchbase.analysis.NamedEntityRecognizer.NamedMatches;
import edu.shu.nlt.ontology.ontotech.OntologyUpdater;

public class ExtractionContext {

	private OntologyUpdater ontologyUpdater;

	private OWLIndividual sentenceOwl;
	private String sentence;

	public OWLIndividual getSentenceOwl() {
		return sentenceOwl;
	}

	public ExtractionContext(OntologyUpdater ontologyUpdater, OWLIndividual sentenceOwl, String sentence,
			NamedMatches namedEntitiesInSentence) {
		super();
		this.ontologyUpdater = ontologyUpdater;
		this.sentenceOwl = sentenceOwl;
		this.sentence = sentence;
		this.namedEntitiesInSentence = namedEntitiesInSentence;
	}

	private NamedMatches namedEntitiesInSentence;

	public NamedMatches getNamedEntitiesInSentence() {
		return namedEntitiesInSentence;
	}

	public OntologyUpdater getOntologyUpdater() {
		return ontologyUpdater;
	}

	public String getSentence() {
		return sentence;
	}

}
