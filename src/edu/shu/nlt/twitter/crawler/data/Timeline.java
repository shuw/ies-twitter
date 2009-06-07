package edu.shu.nlt.twitter.crawler.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

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

	public static Timeline getInstance(String userScreenName, List<Status> statusList, Date lastUpdated) {
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

	/**
	 * @param timeline
	 *            Additional timeline data to append
	 */
	public void append(Timeline timeline) {

		if (!timeline.getUserScreenName().equals(timeline.getUserScreenName())) {
			throw new IllegalArgumentException("timelines do not match");
		}
		Hashtable<Integer, Status> statusTable = new Hashtable<Integer, Status>(statusList.size());

		for (Status status : statusList) {
			statusTable.put(status.getId(), status);
		}

		for (Status status : timeline.getStatusList()) {
			if (!statusTable.containsKey((Integer) status.getId())) {
				// only add status that are not already in the list
				statusList.add(status);
			}
		}

	}

	public String getCacheKey() {
		return getCacheKey(userScreenName);
	}

	public Date getLastUpdated() {
		return lastUpdated;
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

	public Collection<Status> getStatusList() {
		return statusList;
	}

	/**
	 * @return The update frequency / day for the latest 10 tweets
	 */
	public double getUpdateFrequency() {

		ArrayList<Status> sortedList = new ArrayList<Status>(statusList);

		// Reverse sort so that latest tweets appear first
		Collections.sort(sortedList, new Comparator<Status>() {

			@Override
			public int compare(Status o1, Status o2) {
				return o2.getCreatedAt().compareTo(o1.getCreatedAt());
			}

		});

		int depthToSeek = (sortedList.size() < 10 ? sortedList.size() : 10);

		if (depthToSeek == 0) {
			// no updates from user
			return 0;
		}

		Date lastUpdatedTime = getLastUpdated();
		Date updatedTimeOfLastElement = sortedList.get(depthToSeek - 1).getCreatedAt();

		long timeBetweenUpdatesMillisecs = (lastUpdatedTime.getTime() - updatedTimeOfLastElement.getTime())
				/ depthToSeek;

		return (double) (3600 * 24 * 1000) / (double) timeBetweenUpdatesMillisecs;
	}

	public String getUserScreenName() {
		return userScreenName;
	}

}
