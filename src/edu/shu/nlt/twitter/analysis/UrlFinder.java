package edu.shu.nlt.twitter.analysis;

import edu.nlt.shallow.data.Keyable;
import edu.nlt.shallow.data.table.CounterTable;
import edu.nlt.shallow.data.table.KeyCounterTable;
import edu.shu.nlt.twitter.crawler.data.Status;
import edu.shu.nlt.twitter.crawler.data.Timeline;
import edu.shu.nlt.twitter.crawler.repository.DiskCache;
import edu.shu.nlt.twitter.crawler.repository.PersistentCache;

public class UrlFinder implements Runnable {

	private PersistentCache repository;
	private CounterTable<String> urlCounterTable;

	public UrlFinder(PersistentCache repository) {
		super();
		this.repository = repository;
		this.urlCounterTable = new KeyCounterTable();
	}

	@Override
	public void run() {
		findMatches("I think");

	}

	private void findMatches(String contains) {
		contains = contains.toLowerCase();

		int totalCount = 0;
		int matchCount = 0;
		for (String key : repository.getKeysMatching(".*" + Timeline.FilePostfix + "$")) {

			Timeline timeline = Timeline.getInstance(repository, key.replace(Timeline.FilePostfix, ""));

			for (Status status : timeline.getStatusList()) {
				totalCount++;
				String statusText = status.getText();
				if (statusText.toLowerCase().contains(contains)) {
					matchCount++;
					System.out.println(timeline.getUserScreenName() + ": " + statusText);
				}
			}
		}

		System.out.println("*************************************************");
		System.out.println("% matches = " + ((double) matchCount / (double) totalCount) * 100.0);
		System.out.println("matches - " + matchCount + "   total - " + totalCount);
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
