package edu.shu.nlt.twitter.crawler.strategy;

import java.util.Date;

/**
 * @author shu
 * 
 */
public interface Strategy {

	/**
	 * @return Next user ID to crawl. Is null is no more to crawl.
	 */

	public UserInfoRequest getNext();

	public class UserInfoRequest {
		private Date lastUpdated;
		private String userID;

		public UserInfoRequest(String userID, Date lastUpdated) {
			super();
			this.userID = userID;
			this.lastUpdated = lastUpdated;
		}

		public String getUserID() {
			return userID;
		}

		public Date lastUpdated() {
			return lastUpdated;
		}

	}
}
