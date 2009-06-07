package edu.shu.nlt.twitter.analysis;

import edu.nlt.util.processor.LineProcessor;
import edu.shu.nlt.twitter.crawler.data.Status;
import edu.shu.nlt.twitter.crawler.data.Timeline;

public class LPTimelineProcessor implements TimelineProcessor {

	private LineProcessor lineProcessor;

	public LPTimelineProcessor(LineProcessor lineProcessor) {
		super();
		this.lineProcessor = lineProcessor;
	}

	@Override
	public void processTimeline(Timeline timeline) {
		for (Status status : timeline.getStatusList()) {
			lineProcessor.processLine(status.getText());

		}

	}

}
