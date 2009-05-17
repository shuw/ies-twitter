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

public class Timeline implements Cacheable {
	public static final String FilePostfix = "_timeline";

	public static String getCacheKey(String userScreenName) {
		return userScreenName + FilePostfix;
	}

	public static Timeline getInstance(PersistentCache fromCache, String userScreenName) {
		String userKey = getCacheKey(userScreenName);

		CachedValue cachedValue = fromCache.get(userKey);
		if (cachedValue != null) {
			Cacheable objectValue = cachedValue.getCachedObject();

			if (objectValue != null) {
				return (Timeline) objectValue;
			} else {
				return Timeline.getInstance(cachedValue.getCachedString());
			}

		} else {
			return null;
		}
	}

	public static Timeline getInstance(String value) {
		BufferedReader reader = new BufferedReader(new StringReader(value));

		LinkedList<Status> statusList = new LinkedList<Status>();
		StringBuilder timelineValue = new StringBuilder();

		try {
			Date lastUpdatedTime = new Date(Long.parseLong(reader.readLine()));
			String userScreenName = reader.readLine();

			while (true) {
				String line;
				line = reader.readLine();

				if (line == null) {
					break;
				} else {
					if (line.equals(Util.Delimiter)) {
						Status status = Status.getInstance(timelineValue.toString());
						timelineValue = new StringBuilder();

						statusList.add(status);

					} else {
						timelineValue.append(line + "\n");
					}
				}
			}

			return getInstance(userScreenName, statusList, lastUpdatedTime);

		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	public static Timeline getInstance(String userScreenName, Collection<Status> statusList, Date lastUpdated) {
		return new Timeline(userScreenName, statusList, lastUpdated);
	}

	private Date lastUpdated;

	private Collection<Status> statusList;

	private String userScreenName;

	private Timeline(String userScreenName, Collection<Status> statusList, Date lastUpdated) {
		super();
		this.userScreenName = userScreenName;
		this.statusList = statusList != null ? statusList : new ArrayList<Status>(0);
		this.lastUpdated = lastUpdated;
	}

	public String getCacheKey() {
		return getCacheKey(userScreenName);
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public Collection<Status> getStatusList() {
		return statusList;
	}

	public String getUserScreenName() {
		return userScreenName;
	}

	public String getSerialized() {
		StringBuilder builder = new StringBuilder();
		builder.append(lastUpdated.getTime() + "\n");
		builder.append(userScreenName + "\n");

		String delimiterWithSpace = "\n" + Util.Delimiter + "\n";
		for (Status status : statusList) {
			builder.append(status.serialize() + delimiterWithSpace);
		}

		return builder.toString();

	}

}
