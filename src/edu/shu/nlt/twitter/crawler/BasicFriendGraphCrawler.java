package edu.shu.nlt.twitter.crawler;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONException;

import winterwell.jtwitter.Twitter;
import edu.shu.nlt.twitter.crawler.data.User;
import edu.shu.nlt.twitter.crawler.data.UserProfile;
import edu.shu.nlt.twitter.crawler.repository.DiskCache;
import edu.shu.nlt.twitter.crawler.repository.PersistentCache;

/**
 * Crawls friends depth-first
 * 
 * @author shu
 * 
 */
public class BasicFriendGraphCrawler implements Runnable {

	static final int maxDepth = 2;

	private static List<User> adaptUsers(List<winterwell.jtwitter.Twitter.User> users) {
		List<User> adaptedUsers = new ArrayList<User>(users.size());

		for (winterwell.jtwitter.Twitter.User user : users) {
			adaptedUsers.add(User.getInstance(user));
		}
		return adaptedUsers;
	}

	/**
	 * Ensures that user friend data is loaded for user
	 * 
	 * @param user
	 * @return
	 */
	public static UserProfile ensureUserProfile(PersistentCache repository, Twitter twitter, User user) {
		UserProfile userWrapper = UserProfile.getInstance(repository, user.getScreenName());

		if (userWrapper != null) {
			System.out.println("Got cached data: " + user.getScreenName() + "\t" + userWrapper.getLastUpdated());
		} else {
			try {
				List<winterwell.jtwitter.Twitter.User> friends = twitter.getFriends(user.getScreenName());
				userWrapper = UserProfile.getInstance(user, adaptUsers(friends), new Date());

				// update cache
				repository.put(userWrapper);
				System.out.println("New data: " + user.getScreenName());

			} catch (Exception ex) {
				if (ex.getMessage().contains("404")) {
					// do nothing
				} else if (ex.getCause() instanceof JSONException) {
					System.out.println("User JSON error: " + user.getScreenName());

					userWrapper = UserProfile.getInstance(user, null, new Date());

				} else {
					ex.printStackTrace();

					System.out.println("Error / Rate limit reached, sleeping for preset time.65" + new Date() + " "
							+ ex.getMessage());
					try {
						Thread.sleep(1000 * 60 * Util.ThrottlerWaitTimeMinutes);
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}

					return ensureUserProfile(repository, twitter, user);
				}
			}
		}
		return userWrapper;
	}

	public static void main(String[] args) {

		Twitter twitter = new Twitter("shuw", "direwolf");
		DiskCache cache = DiskCache.getInstance();

		BasicFriendGraphCrawler crawler = new BasicFriendGraphCrawler(twitter, cache, "shanselman");
		crawler.run();
	}

	private PersistentCache repository;

	private String rootUser;

	private Twitter twitter;

	public BasicFriendGraphCrawler(Twitter twitter, PersistentCache repository, String rootScreenName) {
		super();
		this.twitter = twitter;
		this.repository = repository;
		this.rootUser = rootScreenName;
	}

	/**
	 * Depth first algorithm for crawling the social network
	 * 
	 * @param rootUser
	 */
	private synchronized void getFriendsBreadthFirst(User rootUser) {
		LinkedList<User> queue = new LinkedList<User>();
		queue.add(rootUser);

		while (!queue.isEmpty()) {
			User user = queue.remove();
			UserProfile userWrapper = ensureUserProfile(repository, twitter, user);

			for (User friend : userWrapper.getFriends()) {
				queue.add(friend);
			}
		}
	}

	/**
	 * Depth first method for crawling friends
	 * 
	 * @param user
	 * @param depth
	 */
	@SuppressWarnings("unused")
	private synchronized void getFriendsDepthFirst(User user, int depth) {
		UserProfile userWrapper = ensureUserProfile(repository, twitter, user);

		if (depth < maxDepth) {
			for (User friend : userWrapper.getFriends()) {
				System.out.println("recurring @ depth: " + ((int) depth + 1) + " under " + user.getScreenName());
				getFriendsDepthFirst(friend, depth + 1);
			}
		}

	}

	public void run() {
		UserProfile cachedValue = UserProfile.getInstance(repository, rootUser);
		User rootUserData = (cachedValue != null) ? cachedValue.getUser() : User.getInstance(twitter.show(rootUser));

		// getFriendsDepthFirst(rootUserData, 0);
		getFriendsBreadthFirst(rootUserData);

		System.out.println("Finished crawling @" + rootUser + " up to depth " + (maxDepth + 1));
	}
}
