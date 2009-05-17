package edu.shu.nlt.twitter.analysis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;

import edu.nlt.shallow.data.Keyable;
import edu.shu.nlt.twitter.crawler.data.Status;
import edu.shu.nlt.twitter.crawler.data.Timeline;
import edu.shu.nlt.twitter.crawler.repository.DiskCache;
import edu.shu.nlt.twitter.crawler.repository.PersistentCache;

public class TweetPrinter implements Runnable {
	private PersistentCache repository;

	public TweetPrinter(PersistentCache repository) {
		super();
		this.repository = repository;
	}

	@Override
	public void run() {

		File file = new File("output", "tweets_all.txt");

		try {
			printAll(new PrintStream(new FileOutputStream(file)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void printAll(PrintStream stream) {

		int totalCount = 0;

		for (String key : repository.getKeysMatching(".*" + Timeline.FilePostfix + "$")) {

			Timeline timeline = Timeline.getInstance(repository, key.replace(Timeline.FilePostfix, ""));

			for (Status status : timeline.getStatusList()) {
				totalCount++;
				String statusText = status.getText();

				System.out.println(statusText);
				stream.println(statusText);
			}
		}

		System.out.println("Total count");
	}

	public static void main(String[] args) {
		(new TweetPrinter(new DiskCache("cache"))).run();
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

	}
}
