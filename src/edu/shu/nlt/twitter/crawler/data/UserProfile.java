package edu.shu.nlt.twitter.crawler.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

import edu.shu.nlt.twitter.crawler.repository.Cacheable;
import edu.shu.nlt.twitter.crawler.repository.PersistentCache;
import edu.shu.nlt.twitter.crawler.repository.PersistentCache.CachedValue;

/**
 * UserList relationships represent a directional social graph
 * 
 * @author shu
 * 
 */
public class UserProfile implements Cacheable {
	public static final String FilePostfix = "_friends";

	public static UserProfile getInstance(User user, Collection<User> friends, Date lastUpdated) {
		return new UserProfile(user, friends, lastUpdated);
	}

	public static UserProfile getInstance(PersistentCache fromCache, String userScreenName) {
		String userKey = getCacheKey(userScreenName);

		CachedValue cachedValue = fromCache.get(userKey);
		if (cachedValue != null) {
			Cacheable objectValue = cachedValue.getCachedObject();

			if (objectValue != null) {
				return (UserProfile) objectValue;
			} else {
				return UserProfile.getInstance(cachedValue.getCachedString());
			}

		} else {
			return null;
		}
	}

	public static String getCacheKey(String userScreenName) {
		return userScreenName + FilePostfix;
	}

	public String getCacheKey() {
		return getCacheKey(user.getScreenName());
	}

	public static UserProfile getInstance(String value) {
		BufferedReader reader = new BufferedReader(new StringReader(value));

		User owner = null;
		LinkedList<User> friends = new LinkedList<User>();
		StringBuilder userValue = new StringBuilder();

		try {
			String version = reader.readLine(); // user screen name
			if (version.equals(Version2)) {

				Date lastUpdated = new Date(Long.parseLong(reader.readLine()));

				while (true) {
					String line;
					line = reader.readLine();

					if (line == null) {
						break;
					} else {
						if (line.equals(Util.Delimiter)) {
							User user = User.getInstance(userValue.toString());
							userValue = new StringBuilder();

							if (owner == null) {
								owner = user;
							} else {
								friends.add(user);
							}
						} else {
							userValue.append(line + "\n");
						}
					}
				}
				return new UserProfile(owner, friends, lastUpdated);
			} else {
				throw new RuntimeException("Invalid version found");
			}

		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	private static final String Version2 = "FormatVersion2";

	public String getSerialized() {
		String delimiterWithSpace = "\n" + Util.Delimiter + "\n";

		StringBuilder builder = new StringBuilder();
		builder.append(Version2 + "\n");
		builder.append(getLastUpdated().getTime() + "\n");
		builder.append(user.serialize());
		builder.append(delimiterWithSpace);

		for (User friend : friends) {
			builder.append(friend.serialize());
			builder.append(delimiterWithSpace);
		}

		return builder.toString();

	}

	private Collection<User> friends;
	private Date lastUpdated;
	private User user;

	private UserProfile(User user, Collection<User> friends, Date lastUpdated) {
		super();
		this.user = user;
		this.lastUpdated = lastUpdated;

		this.friends = friends != null ? friends : new ArrayList<User>();

	}

	public Collection<User> getFriends() {
		return friends;
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public User getUser() {
		return user;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
