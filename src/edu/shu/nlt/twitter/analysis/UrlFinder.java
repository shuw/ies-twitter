package edu.shu.nlt.twitter.analysis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.nlt.shallow.data.CountHolder;
import edu.nlt.shallow.data.Keyable;
import edu.nlt.shallow.data.table.KeyCounterTable;
import edu.nlt.util.InputUtil;
import edu.nlt.util.processor.LineProcessor;

public class UrlFinder implements LineProcessor {
	private Pattern pattern = Pattern.compile("http://[^ ]+", Pattern.CASE_INSENSITIVE);
	private KeyCounterTable<UrlKey> urlCounterTable;

	public UrlFinder() {
		urlCounterTable = new KeyCounterTable<UrlKey>();

	}

	class UrlKey implements Keyable {
		private String url;

		public String getUrl() {
			return url;
		}

		public UrlKey(String url) {
			super();
			this.url = url;
		}

		@Override
		public String getKey() {
			return url;
		}

		@Override
		public String toString() {
			return url;
		}
	}

	int totalCount1 = 0;
	int totalTweets = 0;

	@Override
	public void processLine(String value) {
		Matcher matcher = pattern.matcher(value);
		totalTweets++;
		while (matcher.find()) {
			String matchedUrl = matcher.group();
			System.out.println("Found url: " + matchedUrl);

			urlCounterTable.add(new UrlKey(matchedUrl));
			totalCount1++;
		}

	}

	public void printResults(PrintStream stream) {
		final String outputFormat = "%1$s\t%2$s";

		Collection<CountHolder<UrlKey>> results = urlCounterTable.getReverseSorted();

		int totalCount = 0;
		for (CountHolder<UrlKey> result : results) {

			totalCount += result.getCount();
			stream.println(String.format(outputFormat, result.getComponent(), result.getCount()));

		}
		System.out.println("Tweets: " + totalTweets);
		System.out.println("(1) Total URLs found: " + totalCount1);
		System.out.println("Total URLs found: " + totalCount);

	}

	public static void main(String[] args) throws FileNotFoundException {
		UrlFinder urlFinder = new UrlFinder();

		boolean useSystemIn = false;
		if (useSystemIn) {

			InputUtil.process(System.in, urlFinder);
		} else {
			InputUtil.process(new File("output", "tweets_all.txt"), urlFinder);
		}

		File file = new File("output", "urls_sorted_unique.txt");

		urlFinder.printResults(new PrintStream(new FileOutputStream(file)));

	}

}
