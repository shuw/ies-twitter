package edu.shu.nlt.twitter.experiment;

import java.io.File;
import java.util.Collection;

import edu.nlt.util.InputUtil;
import edu.nlt.util.processor.LineProcessor;
import edu.shu.nlt.twitter.ie.NERUtil;
import edu.shu.nlt.twitter.ie.NamedEntity;
import edu.stanford.nlp.ie.crf.CRFClassifier;

public class NERTask implements Runnable {
	private CRFClassifier classifier;

	public NERTask(CRFClassifier classifier) {
		super();
		this.classifier = classifier;
	}

	@Override
	public void run() {

		System.out.println("System ready! ");

		// InputUtil.process(System.in, processor);
		InputUtil.process(new File("output", "tweets_all.txt"), new TweetProcessor());
	}

	private class TweetProcessor implements LineProcessor {
		private int sentencesTotal = 0;
		private int sentencesWithNE = 0;
		private int NETotal = 0;

		@Override
		public void processLine(String value) {
			sentencesTotal++;
			Collection<NamedEntity> entities = NERUtil.getNamedEntities(classifier, value);

			if (entities.size() > 0) {
				sentencesWithNE++;
			}

			NETotal += entities.size();

			System.out.println("Total: " + sentencesTotal + " TotalNE: " + sentencesWithNE + " NE:" + NETotal);
		}

		public int getSentencesTotal() {
			return sentencesTotal;
		}

		public int getSentencesWithNE() {
			return sentencesWithNE;
		}

	}

	public static void main(String[] args) {
		CRFClassifier classifier = null;

		try {
			classifier = CRFClassifier.getClassifier(new File("classifiers", "ner-eng-ie.crf-4-conll-distsim.ser"));

		} catch (Exception e) {
			e.printStackTrace();
		}

		if (classifier != null) {
			NERTask task = new NERTask(classifier);

			task.run();

		}
	}

}
