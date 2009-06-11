package edu.shu.nlt.ontology.ontotech;

import java.io.File;
import java.io.PrintStream;
import java.util.Collection;
import java.util.HashSet;

import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLIndividual;

import edu.nlt.util.InputUtil;
import edu.nlt.util.LPMultiThreader;
import edu.nlt.util.processor.LineProcessor;
import edu.shu.nlt.crunchbase.NamedEntityRecognizer;
import edu.shu.nlt.crunchbase.NamedEntityRecognizer.CrunchbaseMatches;
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
	private static final int c_maxNumOfTweetsToPopulate = 100;

	public static final boolean c_populateInstancesOnlyIfAxiomsMatched = true;

	public static final boolean c_useCRFClassifier = true;

	public static void main(String[] args) {
		OntologyUpdater ontology = new OntologyUpdater(new File("data/ontology/IESTwitter.owl"), new File(
				"output/Tweets.owl"));

		PopulateTweets finder = new PopulateTweets(ontology);

		LPMultiThreader lineProcessorMT = new LPMultiThreader(4, finder);

		InputUtil.process(new File("output", "tweets_all.txt"), lineProcessorMT);

		lineProcessorMT.close();

		// Populate ontology with crunchbase company information
		(new PopulateCrunchbase(ontology, finder.getCompaniesMentioned())).run();

		ontology.save();
		finder.printResults(System.out);
	}

	public HashSet<Company> getCompaniesMentioned() {
		return companiesMentioned;
	}

	public static String normalizeOntologyName(String value) {
		value = value.toLowerCase().replaceAll(" ", "-"); // remove punctuation
		value = value.replace("--+", "-");

		value = value.replaceAll("[^\\w]+", ""); // remove punctuation

		return value;

	}

	private CRFClassifier classifier;

	private NamedEntityRecognizer neRecognizer;

	private OntologyUpdater ontology;

	private RuleMatcher ruleMatcher;

	private OWLIndividual technologyTopic;

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
		companiesMentioned = new HashSet<Company>();
		technologyTopic = ontology.getIndividual("TechnologyTopic");
		ontology.assertIsClass(technologyTopic, "Technology");
	}

	public void printResults(PrintStream out) {
		out.println("Total matches: " + totalMatches);
	}

	private HashSet<Company> companiesMentioned;

	@Override
	public void processLine(String value) {
		if (totalMatches >= c_maxNumOfTweetsToPopulate) {
			return;
		}

		totalProcessed++;
		value = value.trim();

		CrunchbaseMatches neMatches = neRecognizer.match(value);

		if (neMatches.getTotalMatches() > 0) {
			OWLIndividual tweet = ontology.getIndividual("tweet" + tweetId++);
			OWLIndividual intention = ontology.getIndividual("tweet" + tweetId++ + "Intention");

			Collection<NamedEntity> namedEntities = classifier != null ? NERUtil.getNamedEntities(classifier, value)
					: null;

			ExtractionContext context = new ExtractionContext(ontology, tweet, intention, value, neMatches,
					namedEntities);

			Collection<OWLAxiom> newAxiomAsserts;

			newAxiomAsserts = ruleMatcher.getAxioms(context);

			if (!c_populateInstancesOnlyIfAxiomsMatched || newAxiomAsserts.size() > 0) {
				// Assert named entity relationship
				//
				{
					ontology.assertCommentAnnotation(tweet, value);
					ontology.assertIsClass(tweet, "Tweet");
					ontology.assertProperty(tweet, "hasTopic", technologyTopic);

					totalMatches++;
					System.out.println(totalProcessed + "\t New individual: " + value);

					for (Company company : neMatches.getCompanyMatches()) {
						PopulateCrunchbase.assertCompany(ontology, company);
						companiesMentioned.add(company);

						ontology.assertProperty(tweet, "isReferringTo", ontology.getIndividual(company
								.getCrunchBaseId()));
					}

					for (Person person : neMatches.getPersonMatches()) {
						PopulateCrunchbase.assertPerson(ontology, person);

						ontology.assertProperty(tweet, "isReferringTo", ontology
								.getIndividual(person.getCrunchBaseId()));
					}

					for (Product product : neMatches.getProductMatches()) {
						PopulateCrunchbase.assertProduct(ontology, product);

						ontology.assertProperty(tweet, "isReferringTo", ontology.getIndividual(product
								.getCrunchBaseId()));
					}
				}

				// Assert crunchbase relationship
				//
				if (classifier != null) {

					for (NamedEntity namedEntity : namedEntities) {
						String normalizedName = normalizeOntologyName(namedEntity.getName());

						if (normalizedName.length() > 3) {

							if (namedEntity.getType() != NamedEntity.Type.Location) {

								String type;
								if (namedEntity.getType() == NamedEntity.Type.Person) {
									type = "Person";
								} else if (namedEntity.getType() == NamedEntity.Type.Organization) {
									type = "Organization";
								} else {
									type = "NamedEntity";
								}

								OWLIndividual namedEntityOwl = ontology.getIndividual(normalizedName);

								ontology.assertIsClass(namedEntityOwl, type);

								ontology.assertCommentAnnotation(namedEntityOwl, namedEntity.getName());
								ontology.assertProperty(tweet, "isReferringTo", namedEntityOwl);

							}

							System.out.println("Also found using CRF: " + namedEntity);
						}
					}

				}

			}

		}

	}
}
