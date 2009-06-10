package edu.shu.nlt.twitter.experiment;

import java.io.File;
import java.io.PrintStream;
import java.util.Collection;

import edu.nlt.util.InputUtil;
import edu.nlt.util.LPMultiThreader;
import edu.nlt.util.processor.LineProcessor;
import edu.shu.nlt.ie.NERUtil;
import edu.shu.nlt.ie.NamedEntity;
import edu.stanford.nlp.ie.crf.CRFClassifier;

public class FindNamedEntities implements LineProcessor {
	public static void main(String[] args) {
		CRFClassifier classifier = null;

		try {
			classifier = CRFClassifier.getClassifier(new File("classifiers", "ner-eng-ie.crf-4-conll-distsim.ser"));

		} catch (Exception e) {
			e.printStackTrace();
		}

		if (classifier != null) {
			FindNamedEntities task = new FindNamedEntities(classifier);

			InputUtil.process(new File("output", "tweets_all.txt"), task);

			task.printResults(System.out);
		}
	}

	private CRFClassifier classifier;

	public FindNamedEntities(CRFClassifier classifier) {
		super();
		this.classifier = classifier;
	}

	private int countWithAtLeast1Match = 0;
	private int totalMatches = 0;

	public void printResults(PrintStream out) {
		out.println("total matches: " + totalMatches);
		out.println("lines with at least 1 match: " + countWithAtLeast1Match);
	}

	@Override
	public void processLine(String value) {
		try {
			Collection<NamedEntity> entities = NERUtil.getNamedEntities(classifier, value);
			totalMatches += entities.size();

			if (entities.size() > 0)
				countWithAtLeast1Match++;

			for (NamedEntity entity : entities)
				System.out.print(entity.toString());

			System.out.println();
		} catch (Exception e) {
			e.printStackTrace();

		}

	}

}
