package edu.shu.nlt.ontology.ontotech;

import java.io.File;
import java.io.PrintStream;
import java.util.Collection;

import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLIndividual;

import edu.nlt.util.InputUtil;
import edu.nlt.util.LPMultiThreader;
import edu.nlt.util.processor.LineProcessor;
import edu.shu.nlt.crunchbase.analysis.NamedEntityRecognizer;
import edu.shu.nlt.crunchbase.analysis.NamedEntityRecognizer.NamedMatches;
import edu.shu.nlt.ontology.ontotech.extractionrules.ExtractionContext;
import edu.shu.nlt.ontology.ontotech.extractionrules.RuleMatcher;

/**
 * Populates ontology with Tweet Instances
 * 
 * @author shu
 * 
 */
public class PopulateTweets implements LineProcessor {

	public static void main(String[] args) {
		OntologyUpdater ontologyUpdater = new OntologyUpdater(new File("data/ontology/IESTwitter.owl"), new File(
				"output/Tweets.owl"));

		PopulateTweets finder = new PopulateTweets(ontologyUpdater);

		LPMultiThreader lineProcessorMT = new LPMultiThreader(4, finder);

		InputUtil.process(new File("output", "tweets_all.txt"), lineProcessorMT);

		lineProcessorMT.close();

		ontologyUpdater.save();
		finder.printResults(System.out);
	}

	private NamedEntityRecognizer neRecognizer;
	private OntologyUpdater ontologyUpdater;

	private RuleMatcher ruleMatcher;

	private int totalMatches = 0;
	private int totalProcessed = 0;

	private int tweetId = 0;

	public PopulateTweets(OntologyUpdater ontologyUpdater) {
		super();
		this.ontologyUpdater = ontologyUpdater;
		this.neRecognizer = NamedEntityRecognizer.getInstance();
		this.ruleMatcher = RuleMatcher.getInstance();
	}

	public void printResults(PrintStream out) {
		out.println("Total matches: " + totalMatches);
	}

	/**
	 * Used during testing to restrict ontology size
	 */
	private static final int maxNumOfTweetsToPopulate = 10;

	@Override
	public void processLine(String value) {
		if (totalMatches >= maxNumOfTweetsToPopulate) {
			return;
		}

		totalProcessed++;
		value = value.trim();

		NamedMatches neMatches = neRecognizer.match(value);

		if (neMatches.getTotalMatches() > 0) {
			OWLIndividual tweet = ontologyUpdater.getIndividual("tweet" + tweetId++);

			ExtractionContext context = new ExtractionContext(ontologyUpdater, tweet, value, neMatches);
			Collection<OWLAxiom> newAxiomAsserts;

			newAxiomAsserts = ruleMatcher.getAxioms(context);

			if (newAxiomAsserts.size() > 0) {
				ontologyUpdater.assertCommentAnnotation(tweet, value);
				ontologyUpdater.assertIsClass(tweet, "Tweet");
				totalMatches++;
				System.out.println(totalProcessed + "\t " + value);
			}

		}

	}
}
