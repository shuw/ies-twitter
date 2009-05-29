package edu.shu.nlt.twitter.crawler;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import winterwell.jtwitter.Twitter;
import edu.shu.nlt.twitter.crawler.data.Status;
import edu.shu.nlt.twitter.crawler.data.Timeline;
import edu.shu.nlt.twitter.crawler.data.User;
import edu.shu.nlt.twitter.crawler.data.UserProfile;
import edu.shu.nlt.twitter.crawler.repository.DiskCache;
import edu.shu.nlt.twitter.crawler.repository.PersistentCache;

/**
 * Crawls in the current thread.
 * 
 * @author shu
 * 
 */
public class BasicTimelineCrawler implements Runnable {

	private static List<Status> adaptStatusList(List<winterwell.jtwitter.Twitter.Status> statusList) {
		List<Status> adaptedStatus = new ArrayList<Status>(statusList.size());

		for (winterwell.jtwitter.Twitter.Status status : statusList) {
			adaptedStatus.add(Status.getInstance(status));
		}

		return adaptedStatus;
	}

	public static void main(String[] args) {

		Twitter twitter = new Twitter("shuw", "direwolf");
		DiskCache cache = new DiskCache("cache");

		BasicTimelineCrawler crawler = new BasicTimelineCrawler(twitter, cache, "shanselman");
		crawler.run();
	}

	private PersistentCache repository;

	private String rootUser;

	private Twitter twitter;

	public BasicTimelineCrawler(Twitter twitter, PersistentCache repository, String rootUser) {
		super();
		this.twitter = twitter;
		this.repository = repository;
		this.rootUser = rootUser;
	}

	/**
	 * Depth first algorithm for crawling the timeline.
	 * 
	 * Assume that all relevant social network data is already cached
	 * 
	 * @param rootUser
	 */
	private synchronized void crawlBreadthFirst(User rootUser) {
		LinkedList<User> queue = new LinkedList<User>();
		queue.add(rootUser);

		while (!queue.isEmpty()) {
			User user = queue.remove();
			// UserProfile userProfile = UserProfile.getInstance(repository,
			// userScreenName);
			UserProfile userProfile = BasicFriendGraphCrawler.ensureUserProfile(repository, twitter, user);

			// Only perform timeline loading and tree expansion for cached user
			// profiles
			if (userProfile != null) {
				ensureTimeline(repository, twitter, userProfile.getUser(), true);

				for (User friend : userProfile.getFriends()) {
					queue.add(friend);
				}
			}
		}
	}

	public static boolean needsUpdate(Timeline timeline, int numOfUpdatesToRetrieve) {
		double daysSinceLastUpdate = (double) (System.currentTimeMillis() - timeline.getLastUpdated().getTime())
				/ (double) (3600 * 24 * 1000);

		double updateFrequency = timeline.getUpdateFrequency();

		// return true if estimated number of new updates exceeds the number of
		// updates we can retrieve per call
		return (daysSinceLastUpdate * updateFrequency) > numOfUpdatesToRetrieve;

	}

	static final int NUM_OF_UPDATES_TO_RETRIEVE = 20;

	/**
	 * Ensures that user friend data is loaded for user
	 * 
	 * @param user
	 * @return
	 */
	public static void ensureTimeline(PersistentCache repository, Twitter twitter, User user, boolean appendTimelines) {

		Timeline cachedTimeline = Timeline.getInstance(repository, user.getScreenName());

		if (cachedTimeline != null && !(appendTimelines && needsUpdate(cachedTimeline, NUM_OF_UPDATES_TO_RETRIEVE))) {

			System.out.println("Cached timeline " + cachedTimeline.getStatusList().size() + ": " + user.getScreenName()
					+ "\t" + repository.getLastUpdated(Timeline.getCacheKey(user.getScreenName())));
			return;
		}

		try {
			List<winterwell.jtwitter.Twitter.Status> statusList = twitter.getUserTimeline(user.getScreenName(),
					NUM_OF_UPDATES_TO_RETRIEVE, null);

			Timeline timeline = Timeline.getInstance(user.getScreenName(), adaptStatusList(statusList), new Date());

			if (cachedTimeline != null) {
				timeline.append(cachedTimeline);
				System.out.println("Updated timeline +"
						+ (timeline.getStatusList().size() - cachedTimeline.getStatusList().size()) + ": "
						+ user.getScreenName());
			} else {
				System.out.println("New timeline " + timeline.getStatusList().size() + ": " + user.getScreenName());
			}

			// update cache
			repository.put(timeline);

		} catch (Exception ex) {
			if (ex.getMessage().contains("401 Unauthorized")) {
				System.out.println("New empty (private) timeline: " + user.getScreenName());

				Timeline timeline = Timeline.getInstance(user.getScreenName(), null, new Date());
				// update cache
				repository.put(timeline);
			} else {

				System.out.println("Error / Rate limit reached, sleeping for pre-set time." + new Date() + " "
						+ ex.getMessage());
				try {
					Thread.sleep(1000 * 60 * Util.ThrottlerWaitTimeMinutes);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}

				ensureTimeline(repository, twitter, user, appendTimelines);
				return;
			}
		}

	}

	@Override
	public void run() {
		UserProfile cachedValue = UserProfile.getInstance(repository, rootUser);
		User rootUserData = (cachedValue != null) ? cachedValue.getUser() : User.getInstance(twitter.show(rootUser));

		crawlBreadthFirst(rootUserData);

	}
}
