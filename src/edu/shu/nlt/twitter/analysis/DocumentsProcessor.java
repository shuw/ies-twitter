package edu.shu.nlt.twitter.analysis;

import edu.shu.nlt.twitter.crawler.data.Timeline;
import edu.shu.nlt.twitter.crawler.repository.PersistentCache;

public class DocumentsProcessor {

	private TimelineProcessor processor;

	public DocumentsProcessor(TimelineProcessor processor) {
		super();
		this.processor = processor;
	}

	public void processRepository(PersistentCache repository) {
		int tweetsProcessed = 0;

		for (String key : repository.getKeysMatching(".*" + Timeline.FilePostfix + "$")) {

			Timeline timeline = Timeline.getInstance(repository, key.replace(Timeline.FilePostfix, ""));

			tweetsProcessed += timeline.getStatusList().size();

			processor.processTimeline(timeline);
		}

		System.out.println("Total tweets processed: " + tweetsProcessed);
	}

}
