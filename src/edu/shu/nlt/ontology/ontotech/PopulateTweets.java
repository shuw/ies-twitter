package edu.shu.nlt.ontology.ontotech;

import java.io.File;
import java.io.PrintStream;
import java.util.Collection;

import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLIndividual;

import edu.nlt.util.InputUtil;
import edu.nlt.util.LPMultiThreader;
import edu.nlt.util.processor.LineProcessor;
import edu.shu.nlt.crunchbase.NamedEntityRecognizer;
import edu.shu.nlt.crunchbase.NamedEntityRecognizer.NamedMatches;
import edu.shu.nlt.crunchbase.data.base.Company;
import edu.shu.nlt.crunchbase.data.base.Person;
import edu.shu.nlt.crunchbase.data.base.Product;
import edu.shu.nlt.ie.NERUtil;
import edu.shu.nlt.ie.NamedEntity;
import edu.shu.nlt.ontology.ontotech.extractionrules.ExtractionContext;
import edu.shu.nlt.ontology.ontotech.extractionrules.RuleMatcher;
import edu.stanford.nlp.ie.crf.CRFClassifier;

/**
 * Populates ontology with Tweet Instances
 * 
 * @author shu
 * 
 */
public class PopulateTweets implements LineProcessor {

	/**
	 * Used during testing to restrict ontology size
	 */
	private static final int c_maxNumOfTweetsToPopulate = 10;

	public static final boolean c_useCRFClassifier = true;

	public static final boolean c_populateInstancesOnlyIfAxiomsMatched = true;

	public static void main(String[] args) {
		OntologyUpdater ontologyUpdater = new OntologyUpdater(new File("output/crunchbase.owl"), new File(
				"output/Tweets.owl"));

		PopulateTweets finder = new PopulateTweets(ontologyUpdater);

		LPMultiThreader lineProcessorMT = new LPMultiThreader(4, finder);

		InputUtil.process(new File("output", "tweets_all.txt"), lineProcessorMT);

		lineProcessorMT.close();

		ontologyUpdater.save();
		finder.printResults(System.out);
	}

	private CRFClassifier classifier;
	private NamedEntityRecognizer neRecognizer;

	private OntologyUpdater ontology;

	private RuleMatcher ruleMatcher;

	private int totalMatches = 0;

	private int totalProcessed = 0;

	private int tweetId = 0;

	public PopulateTweets(OntologyUpdater ontologyUpdater) {
		super();
		this.ontology = ontologyUpdater;
		this.neRecognizer = NamedEntityRecognizer.getInstance();
		this.ruleMatcher = RuleMatcher.getInstance();

		if (c_useCRFClassifier) {
			try {
				classifier = CRFClassifier.getClassifier(new File("classifiers", "ner-eng-ie.crf-4-conll-distsim.ser"));
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}
	}

	public void printResults(PrintStream out) {
		out.println("Total matches: " + totalMatches);
	}

	@Override
	public void processLine(String value) {
		if (totalMatches >= c_maxNumOfTweetsToPopulate) {
			return;
		}

		totalProcessed++;
		value = value.trim();

		NamedMatches neMatches = neRecognizer.match(value);

		if (neMatches.getTotalMatches() > 0) {
			OWLIndividual tweet = ontology.getIndividual("tweet" + tweetId++);

			ExtractionContext context = new ExtractionContext(ontology, tweet, value, neMatches);
			Collection<OWLAxiom> newAxiomAsserts;

			newAxiomAsserts = ruleMatcher.getAxioms(context);

			if (!c_populateInstancesOnlyIfAxiomsMatched || newAxiomAsserts.size() > 0) {
				ontology.assertCommentAnnotation(tweet, value);
				ontology.assertIsClass(tweet, "Tweet");
				totalMatches++;
				System.out.println(totalProcessed + "\t New individual: " + value);

				for (Company company : neMatches.getCompanyMatches()) {
					ontology.assertProperty(tweet, "isReferringTo", ontology.getIndividual(company.getCrunchBaseId()));
				}

				for (Person person : neMatches.getPersonMatches()) {
					ontology.assertProperty(tweet, "isReferringTo", ontology.getIndividual(person.getCrunchBaseId()));
				}

				for (Product product : neMatches.getProductMatches()) {
					ontology.assertProperty(tweet, "isReferringTo", ontology.getIndividual(product.getCrunchBaseId()));
				}

				if (classifier != null) {

					Collection<NamedEntity> namedEntities = NERUtil.getNamedEntities(classifier, value);

					for (NamedEntity namedEntity : namedEntities) {

						if (namedEntity.getType() == NamedEntity.Type.Location) {

							OWLIndividual locationOwl = ontology.getIndividual(namedEntity.getName());
							ontology.assertIsClass(locationOwl, "Location");
							ontology.assertProperty(tweet, "isLocatedAt", locationOwl);
						}

						else {

							boolean isAlreadyInCrunchbase = false;
							String type;
							if (namedEntity.getType() == NamedEntity.Type.Person) {
								type = "Person";
							} else if (namedEntity.getType() == NamedEntity.Type.Organization) {
								type = "Organization";
							} else {
								type = "NamedEntity";
							}

							if (!isAlreadyInCrunchbase) {
								OWLIndividual namedEntityOwl = ontology.getIndividual(namedEntity.getName()
										.toLowerCase().replace(" ", "-"));

								ontology.assertIsClass(namedEntityOwl, type);

								ontology.assertCommentAnnotation(namedEntityOwl, namedEntity.getName());
								ontology.assertProperty(tweet, "isReferringTo", namedEntityOwl);
							}
						}

						System.out.println(namedEntity);
					}

				}

			}

		}

	}
}
