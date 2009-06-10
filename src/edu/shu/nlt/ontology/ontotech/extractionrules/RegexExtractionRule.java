package edu.shu.nlt.ontology.ontotech.extractionrules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.shu.nlt.crunchbase.analysis.NamedEntityRecognizer.NamedMatches;
import edu.shu.nlt.crunchbase.data.base.Company;
import edu.shu.nlt.crunchbase.data.base.Person;
import edu.shu.nlt.crunchbase.data.base.Product;

public class RegexExtractionRule implements ExtractionRule {

	private Pattern simplePattern;
	private String ruleName;

	private String namedPattern;

	public static final String c_namedEntityMarker = "{NamedEntity}";

	public RegexExtractionRule(String regex, String ruleName) {
		if (regex.contains(c_namedEntityMarker)) {
			namedPattern = regex;
		} else {
			simplePattern = Pattern.compile(regex);
		}

		// this.myPattern = regex;
		this.ruleName = ruleName;
	}

	@Override
	public String getRuleName() {
		return ruleName;
	}

	public synchronized Collection<String> getLowerCaseMatchStrings(NamedMatches matches) {

		ArrayList<String> matchStrings = new ArrayList<String>(matches.getCompanyMatches().size()
				+ matches.getPersonMatches().size() + matches.getProductMatches().size());

		for (Company company : matches.getCompanyMatches())
			matchStrings.add(company.getName().toLowerCase());

		for (Person person : matches.getPersonMatches())
			matchStrings.add(person.getFirstName().toLowerCase() + " " + person.getLastName().toLowerCase());

		for (Product product : matches.getProductMatches())
			matchStrings.add(product.getName().toLowerCase());

		return matchStrings;

	}

	@Override
	public boolean isMatch(ExtractionContext extractionSentence) {
		// compare case-insensitive
		String sentence = extractionSentence.getSentence().toLowerCase();

		if (simplePattern != null) {
			Matcher matcher = simplePattern.matcher(sentence);
			return matcher.find();
		} else {

			for (String namedEntity : getLowerCaseMatchStrings(extractionSentence.getNamedEntitiesInSentence())) {
				String toMatch = namedPattern.replace(c_namedEntityMarker, namedEntity.toLowerCase());

				if (sentence.matches(toMatch)) {
					return true;
				}

			}
		}
		return false;

	}
}
