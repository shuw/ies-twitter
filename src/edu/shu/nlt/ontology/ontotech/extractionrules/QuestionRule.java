package edu.shu.nlt.ontology.ontotech.extractionrules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLIndividual;

import edu.shu.nlt.crunchbase.NamedEntityRecognizer.CrunchbaseMatches;
import edu.shu.nlt.crunchbase.data.base.Company;
import edu.shu.nlt.crunchbase.data.base.Person;
import edu.shu.nlt.crunchbase.data.base.Product;
import edu.shu.nlt.ontology.ontotech.OntologyUpdater;

public class QuestionRule implements ExtractionRule {

	private Pattern simplePattern;

	public static final String c_namedEntityMarker = "{NamedEntity}";

	private String namedPattern;

	public QuestionRule(boolean matchNamedEntity) {
		if (matchNamedEntity) {

			namedPattern = ".*" + c_namedEntityMarker + "[^?!.]\\?$";
		} else {
			simplePattern = Pattern.compile(".*\\?$");
		}

	}

	public synchronized Collection<String> getLowerCaseMatchStrings(CrunchbaseMatches matches) {

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
	public OWLAxiom addAxiom(ExtractionContext context) {
		// compare case-insensitive
		String sentence = context.getSentence().toLowerCase();

		boolean isFound = true;

		if (simplePattern != null) {
			Matcher matcher = simplePattern.matcher(sentence);
			isFound = matcher.find();
		} else {

			for (String namedEntity : getLowerCaseMatchStrings(context.getCrunchbaseMatches())) {
				String toMatch = namedPattern.replace(c_namedEntityMarker, namedEntity.toLowerCase());

				if (sentence.matches(toMatch)) {
					isFound = true;
					break;
				}

			}
		}

		if (isFound) {
			OntologyUpdater ontUpdater = context.getOntologyUpdater();

			OWLIndividual intentionOwl = ontUpdater.getIndividual("QuestionInd");
			ontUpdater.assertIsClass(intentionOwl, "Question");

			return ontUpdater.assertProperty(context.getSentenceOwl(), "hasIntention", intentionOwl);
		}
		return null;

	}
}
