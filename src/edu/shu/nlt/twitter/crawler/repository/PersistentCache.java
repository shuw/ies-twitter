package edu.shu.nlt.twitter.crawler.repository;

import java.util.Collection;
import java.util.Date;

public interface PersistentCache {

	public boolean containsKey(String key);

	public CachedValue get(String key);

	public Collection<String> getKeysMatching(String regex);

	public Date getLastUpdated(String key);

	public void put(Cacheable cacheable);

	public class CachedValue {

		private Cacheable objectValue;
		private String value;

		public CachedValue(Cacheable objectValue, String value) {
			super();
			this.objectValue = objectValue;
			this.value = value;

		}

		/**
		 * Object cache if available
		 * 
		 * @return
		 */
		public Cacheable getCachedObject() {
			return objectValue;
		}

		/**
		 * String cache value if avilable
		 * 
		 * @return
		 */
		public String getCachedString() {
			return value;
		}

	}

}
