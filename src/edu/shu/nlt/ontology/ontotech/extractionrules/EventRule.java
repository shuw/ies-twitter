package edu.shu.nlt.ontology.ontotech.extractionrules;

import java.util.LinkedList;

import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLIndividual;

import edu.shu.nlt.crunchbase.Crunchbase;
import edu.shu.nlt.crunchbase.data.base.Company;
import edu.shu.nlt.ie.NamedEntity;
import edu.shu.nlt.ontology.ontotech.OntologyUpdater;
import edu.shu.nlt.ontology.ontotech.PopulateTweets;

/**
 * exists.x.y tweet(z) and isReferringTo(z, x) and Company(x) and isLocatedAt(z,
 * y) -> exists.b CompanyEvent(b) and isLocatedAt
 * 
 * exists.x.y tweet(z) and isReferringTo(z, x) and Product(x) and isLocatedAt(y)
 * -> exists.b ProductEvent(b)
 * 
 * 
 */
public class EventRule implements ExtractionRule {

	public static int eventCount = 0;

	@Override
	public OWLAxiom addAxiom(ExtractionContext context) {

		OntologyUpdater ontology = context.getOntologyUpdater();

		LinkedList<OWLIndividual> locations = new LinkedList<OWLIndividual>();

		for (NamedEntity namedEntity : context.getNamedEntityMatches()) {

			if (namedEntity.getType() == NamedEntity.Type.Location) {

				String locationName = namedEntity.getName();

				if (Crunchbase.getInstance().getCompanyList().getCompany(locationName) == null
						&& Crunchbase.getInstance().getProductsList().getProduct(locationName) == null) {

					String normalizedName = PopulateTweets.normalizeOntologyName(namedEntity.getName());

					if (locationName.toLowerCase().equals("Linux")) {
						System.out.println("Hello");
					}

					OWLIndividual locationOwl = ontology.getIndividual(normalizedName);
					ontology.assertIsClass(locationOwl, "Location");

					locations.add(locationOwl);
				}
			}

		}
		OWLAxiom axiom = null;
		for (OWLIndividual locationOwl : locations) {

			if (context.getCrunchbaseMatches().getCompanyMatches().size() > 0) {
				OWLIndividual eventOwl = ontology.getIndividual("detectedEvent" + eventCount++);
				axiom = ontology.assertProperty(context.getSentenceOwl(), "isReferringTo", eventOwl);

				ontology.assertIsClass(eventOwl, "Event");

				ontology.assertProperty(eventOwl, "isLocatedAt", locationOwl);

				for (Company company : context.getCrunchbaseMatches().getCompanyMatches()) {

					ontology
							.assertProperty(eventOwl, "isAttendedBy", ontology.getIndividual(company.getCrunchBaseId()));

				}
			}

		}
		return axiom;

	}
}
