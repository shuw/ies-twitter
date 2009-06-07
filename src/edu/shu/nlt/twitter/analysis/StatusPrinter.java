package edu.shu.nlt.twitter.analysis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import edu.shu.nlt.twitter.analysis.readers.CacheReader;
import edu.shu.nlt.twitter.crawler.data.Status;
import edu.shu.nlt.twitter.crawler.data.Timeline;
import edu.shu.nlt.twitter.crawler.repository.DiskCache;

public class StatusPrinter implements TimelineProcessor {

	private PrintStream stream;

	public StatusPrinter(PrintStream stream) {
		super();
		this.stream = stream;
	}

	@Override
	public void processTimeline(Timeline timeline) {
		for (Status status : timeline.getStatusList()) {

			String statusText = status.getText();

			stream.println(statusText);
			
			if (stream != System.out)
				System.out.println(statusText);
		}
	}

	static boolean printToFile = true;

	public static void main(String[] args) throws FileNotFoundException {
		PrintStream printStream;

		if (printToFile) {

			File file = new File("output", "tweets_all2.txt");

			printStream = new PrintStream(new FileOutputStream(file));
		} else {
			printStream = System.out;
		}

		StatusPrinter statusPrinter = new StatusPrinter(printStream);
		(new CacheReader(new DiskCache("cache"), statusPrinter)).run();
	}
}