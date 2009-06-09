package edu.shu.nlt.crunchbase.analysis.ontotech;

import java.io.File;
import java.io.PrintStream;
import java.util.Collection;

import edu.nlt.shallow.data.tags.Word;
import edu.nlt.shallow.parser.WordTokenizer;
import edu.nlt.util.InputUtil;
import edu.nlt.util.LPMultiThreader;
import edu.nlt.util.processor.LineProcessor;

public class Experimentation implements LineProcessor {

	public static void main(String[] args) {
		System.out.println("hello world? ".matches(".*\\?$"));

		Experimentation finder = new Experimentation();

		LPMultiThreader lineProcessorMT = new LPMultiThreader(4, finder);

		InputUtil.process(new File("output", "tweets_all.txt"), lineProcessorMT);

		lineProcessorMT.close();

		finder.printResults(System.out);
	}

	private WordTokenizer tokenzier = new WordTokenizer(false);

	private int totalMatches = 0;

	public void printResults(PrintStream out) {
		out.println("Total matches: " + totalMatches);
	}

	@Override
	public void processLine(String value) {
		value = value.trim();
		Collection<Word> words = tokenzier.getWords(value);

		boolean occurance1 = false;
		boolean occurance2 = false;
		for (Word word : words) {
			if (word.toString().toLowerCase().equals("iphone")) {
				occurance1 = true;
			}

			// if (word.toString().toLowerCase().equals("want")) {
			// occurance2 = true;
			// }

			if (value.matches(".*\\?$")) {
				occurance2 = true;
			}

		}

		if (occurance1 && occurance2) {
			totalMatches++;
			System.out.println(value);
		}
	}
}
