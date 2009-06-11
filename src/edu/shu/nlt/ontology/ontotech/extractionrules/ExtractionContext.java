package edu.shu.nlt.ontology.ontotech.extractionrules;

import java.util.Collection;

import org.semanticweb.owl.model.OWLIndividual;

import edu.shu.nlt.crunchbase.NamedEntityRecognizer.CrunchbaseMatches;
import edu.shu.nlt.ie.NamedEntity;
import edu.shu.nlt.ontology.ontotech.OntologyUpdater;

public class ExtractionContext {

	private CrunchbaseMatches crunchbaseMatches;

	private Collection<NamedEntity> namedEntityMatches;
	private OntologyUpdater ontologyUpdater;

	private String sentence;

	private OWLIndividual sentenceOwl;
	private OWLIndividual sentenceIntentionOwl;

	public ExtractionContext(OntologyUpdater ontologyUpdater, OWLIndividual sentenceOwl,
			OWLIndividual sentenceIntentionOwl, String sentence, CrunchbaseMatches namedEntitiesInSentence,
			Collection<NamedEntity> namedEntityMatches) {
		super();
		this.ontologyUpdater = ontologyUpdater;
		this.sentenceOwl = sentenceOwl;
		this.sentenceIntentionOwl = sentenceIntentionOwl;
		this.sentence = sentence;
		this.crunchbaseMatches = namedEntitiesInSentence;
		this.namedEntityMatches = namedEntityMatches;
	}

	public CrunchbaseMatches getCrunchbaseMatches() {
		return crunchbaseMatches;
	}

	public Collection<NamedEntity> getNamedEntityMatches() {
		return namedEntityMatches;
	}

	public OntologyUpdater getOntologyUpdater() {
		return ontologyUpdater;
	}

	public String getSentence() {
		return sentence;
	}

	public OWLIndividual getSentenceIntentionOwl() {
		return sentenceIntentionOwl;
	}

	public OWLIndividual getSentenceOwl() {
		return sentenceOwl;
	}

}
