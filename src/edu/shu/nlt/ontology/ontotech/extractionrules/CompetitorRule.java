package edu.shu.nlt.ontology.ontotech.extractionrules;

import java.util.Collection;
import java.util.HashSet;

import edu.shu.nlt.crunchbase.data.base.Company;

/**
 * Applies the first-order logic rule:
 * 
 * E.x y ( mentionedInSentence(x) and mentionedInSentence(y) and company(x) and
 * company(y) and competitor(x,y) )
 * 
 */
public class CompetitorRule implements ExtractionRule {

	@Override
	public String getRuleName() {
		return "Competitors";
	}

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
	public boolean isMatch(ExtractionContext extractionSentence) {
		String sentence = extractionSentence.getSentence().toLowerCase();

		for (String competitor : getCompetitorStrings(extractionSentence.getNamedEntitiesInSentence()
				.getCompanyMatches())) {
			if (sentence.contains(competitor)) {
				return true;
			}
		}
		return false;
	}
}
