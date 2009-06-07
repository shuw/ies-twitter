package edu.shu.nlt.twitter.analysis.readers;

import edu.shu.nlt.twitter.analysis.TimelineProcessor;
import edu.shu.nlt.twitter.crawler.data.Timeline;
import edu.shu.nlt.twitter.crawler.repository.PersistentCache;

public class CacheReader implements Runnable {
	private PersistentCache repository;
	private TimelineProcessor processor;

	public CacheReader(PersistentCache repository, TimelineProcessor processor) {
		super();
		this.repository = repository;
		this.processor = processor;
	}

	@Override
	public void run() {
		int tweetsProcessed = 0;

		for (String key : repository.getKeysMatching(".*" + Timeline.FilePostfix + "$")) {

			Timeline timeline = Timeline.getInstance(repository, key.replace(Timeline.FilePostfix, ""));

			tweetsProcessed += timeline.getStatusList().size();

			processor.processTimeline(timeline);
		}

		System.out.println("Total tweets processed: " + tweetsProcessed);
	}

}
