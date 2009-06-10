package edu.shu.nlt.ontology.ontotech;

import java.io.File;
import java.io.PrintStream;
import java.util.Collection;

import edu.nlt.util.InputUtil;
import edu.nlt.util.LPMultiThreader;
import edu.nlt.util.processor.LineProcessor;
import edu.shu.nlt.crunchbase.analysis.NamedEntityRecognizer;
import edu.shu.nlt.crunchbase.analysis.NamedEntityRecognizer.NamedMatches;
import edu.shu.nlt.ontology.ontotech.extractionrules.ExtractionContext;
import edu.shu.nlt.ontology.ontotech.extractionrules.ExtractionRule;
import edu.shu.nlt.ontology.ontotech.extractionrules.RuleMatcher;

/**
 * Populates ontology with Tweet Instances
 * 
 * @author shu
 * 
 */
public class PopulateTweets implements LineProcessor {

	public static void main(String[] args) {

		PopulateTweets finder = new PopulateTweets();

		LPMultiThreader lineProcessorMT = new LPMultiThreader(4, finder);

		InputUtil.process(new File("output", "tweets_all.txt"), lineProcessorMT);

		lineProcessorMT.close();

		finder.printResults(System.out);
	}

	private int totalMatches = 0;
	private int totalProcessed = 0;

	public void printResults(PrintStream out) {
		out.println("Total matches: " + totalMatches);
	}

	private NamedEntityRecognizer neRecognizer;
	private RuleMatcher ruleMatcher;

	public PopulateTweets() {
		this.neRecognizer = NamedEntityRecognizer.getInstance();
		this.ruleMatcher = RuleMatcher.getInstance();
	}

	@Override
	public void processLine(String value) {
		totalProcessed++;
		value = value.trim();

		NamedMatches neMatches = neRecognizer.match(value);

		if (neMatches.getTotalMatches() > 0) {
			ExtractionContext context = new ExtractionContext(value, neMatches);

			Collection<ExtractionRule> matchingRules = ruleMatcher.getMatches(context);

			if (matchingRules.size() > 0) {
				totalMatches++;
				System.out.println(totalProcessed + "\t " + value);
			}
		}

	}
}
