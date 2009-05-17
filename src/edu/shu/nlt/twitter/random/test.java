package edu.shu.nlt.twitter.random;

import java.util.List;

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.Twitter.Status;

public class test {
	static final String updateFormat = "%1$s\t%2$s\t%3$s\t%4$s\t%5$s";
	public static void main(String[] args) {
		
		
		String[] split = "1\nasd\n".split("\n");
		int i = 0;
		
	}

	public static void print(Object obj) {
		System.out.println("1");
	}

	public static void print(String sobj) {
		System.out.println("2");
	}

	private static void twitter() {
		// Make a Twitter object
		Twitter twitter = new Twitter("shuw", "direwolf");

		List<Status> timeline = twitter.getPublicTimeline();
		for (Status status : timeline) {

			String serialized = String.format(updateFormat, status.getId(), status.getUser(), status.getCreatedAt()
					.getTime(), status.getId(), status.getText());

			System.out.println(serialized);
		}
	}
}
