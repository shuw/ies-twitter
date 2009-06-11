package edu.shu.nlt.ontology.ontotech.extractionrules;

import java.util.ArrayList;
import java.util.regex.Pattern;

import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLIndividual;

import edu.shu.nlt.ontology.ontotech.OntologyUpdater;

public class DesireRule implements ExtractionRule {

	public static final String[] regexPatterns = { ".*i want.*", ".*i need.*" };

	private Pattern[] myPatterns;

	public DesireRule() {
		super();

		myPatterns = new Pattern[regexPatterns.length];

		for (int i = 0; i < regexPatterns.length; i++) {
			this.myPatterns[i] = Pattern.compile(regexPatterns[i]);
		}

	}

	@Override
	public OWLAxiom addAxiom(ExtractionContext context) {

		for (Pattern myPattern : myPatterns) {
			if (myPattern.matcher(context.getSentence().toLowerCase()).find()) {

				OntologyUpdater ontUpdater = context.getOntologyUpdater();

				OWLIndividual intentionOwl = context.getSentenceIntentionOwl();
				ontUpdater.assertIsClass(intentionOwl, "Desire");
				return ontUpdater.assertProperty(context.getSentenceOwl(), "hasIntention", intentionOwl);

			}
		}
		return null;

	}
}
