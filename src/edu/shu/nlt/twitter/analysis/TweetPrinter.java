package edu.shu.nlt.twitter.analysis;

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
		printAll();

	}

	private void printAll() {

		int totalCount = 0;

		for (String key : repository.getKeysMatching(".*" + Timeline.FilePostfix + "$")) {

			Timeline timeline = Timeline.getInstance(repository, key.replace(Timeline.FilePostfix, ""));

			for (Status status : timeline.getStatusList()) {
				totalCount++;
				String statusText = status.getText();

				System.out.println(statusText);
			}
		}

		System.out.println("Total count");
	}

	public static void main(String[] args) {
		(new UrlFinder(new DiskCache("cache"))).run();
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
