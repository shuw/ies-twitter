package edu.shu.nlt.twitter.ie;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.AnswerAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.WordAnnotation;

public class NERUtil {
	private static void queueNamedEntity(List<NamedEntity> list, String namedEntity, String answer) {
		if (namedEntity != "") {
			list.add(new NamedEntity(namedEntity.trim(), answer));
		}
	}

	public static Collection<NamedEntity> getNamedEntities(CRFClassifier classifier, String tweet) {
		if (tweet.length() < 3) {
			return new ArrayList<NamedEntity>(0);
		}

		List<List<CoreLabel>> results = classifier.classify(tweet);
		String prevAnswer = null;
		String answer = null;
		String namedEntity = "";

		LinkedList<NamedEntity> namedEntities = new LinkedList<NamedEntity>();

		for (List<CoreLabel> result : results) {

			for (CoreLabel label : result) {
				String word = label.get(WordAnnotation.class);
				answer = label.get(AnswerAnnotation.class);

				if (!answer.equals(prevAnswer)) {

					queueNamedEntity(namedEntities, namedEntity, prevAnswer);
					namedEntity = ""; // reset
				}

				if (!answer.equals("O")) {
					namedEntity += word + " ";
				}
				prevAnswer = answer;
			}
		}
		queueNamedEntity(namedEntities, namedEntity, prevAnswer);

		return namedEntities;
	}

}
