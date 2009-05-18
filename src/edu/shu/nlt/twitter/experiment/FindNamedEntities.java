package edu.shu.nlt.twitter.experiment;

import java.io.File;
import java.util.Collection;

import edu.nlt.util.InputUtil;
import edu.nlt.util.processor.LineProcessor;
import edu.shu.nlt.twitter.ie.NERUtil;
import edu.shu.nlt.twitter.ie.NamedEntity;
import edu.stanford.nlp.ie.crf.CRFClassifier;

public class FindNamedEntities implements Runnable {
	private CRFClassifier classifier;

	public FindNamedEntities(CRFClassifier classifier) {
		super();
		this.classifier = classifier;
	}

	@Override
	public void run() {

		System.out.println("System ready! ");

		InputUtil.process(System.in, new TweetProcessor());

	}

	private class TweetProcessor implements LineProcessor {

		@Override
		public void processLine(String value) {
			Collection<NamedEntity> entities = NERUtil.getNamedEntities(classifier, value);

			for (NamedEntity entity : entities) {
				System.out.print(entity.toString());
			}
			System.out.println();
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
			FindNamedEntities task = new FindNamedEntities(classifier);

			task.run();

		}
	}

}
