package edu.shu.nlt.ontology.ontotech.extractionrules;

import java.util.Collection;
import java.util.HashSet;

import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLIndividual;

import edu.shu.nlt.crunchbase.data.base.Company;
import edu.shu.nlt.ontology.ontotech.OntologyUpdater;

/**
 * Applies the first-order logic rule:
 * 
 * E.x y ( mentionedInSentence(x) and mentionedInSentence(y) and company(x) and
 * company(y) and competitor(x,y) )
 * 
 */
public class CompetitorRule implements ExtractionRule {

	public Collection<String> getCompetitorStrings(Collection<Company> companies) {

		HashSet<String> competitors = new HashSet<String>();

		for (Company company : companies) {
			if (company.getCompanyInfo() != null && company.getCompanyInfo().getCompetitors() != null) {
				for (Company competitor : company.getCompanyInfo().getCompetitors()) {
					competitors.add(competitor.getName().toLowerCase());
				}
			}

		}
		return competitors;

	}

	@Override
	public OWLAxiom addAxiom(ExtractionContext context) {
		String sentence = context.getSentence().toLowerCase();

		for (String competitor : getCompetitorStrings(context.getCrunchbaseMatches().getCompanyMatches())) {
			if (sentence.contains(competitor)) {

				OntologyUpdater ontUpdater = context.getOntologyUpdater();

				OWLIndividual intentionOwl = context.getSentenceIntentionOwl();
				ontUpdater.assertIsClass(intentionOwl, "CompetitiveComparison");

				OWLAxiom hasIntentionAxiom = ontUpdater.assertProperty(context.getSentenceOwl(), "hasIntention",
						intentionOwl);

				return hasIntentionAxiom;
			}
		}
		return null;
	}
}
