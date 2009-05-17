package edu.shu.nlt.twitter.crawler.repository;

import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.LinkedList;

/**
 * Stores a list of crawled user updates and the date updated
 * 
 * @author shu
 * 
 */
class CacheMap {
	private Hashtable<String, Date> map = new Hashtable<String, Date>();

	public void set(String key, Date updateDate) {
		map.put(key, updateDate);
	}

	public Date getLastUpdated(String key) {
		return map.get(key);
	}

	public boolean containsKey(String key) {
		return map.containsKey(key);
	}

	
	public Collection<String> getKeysMatching(String regex) {

		LinkedList<String> keys = new LinkedList<String>();
		
		for (String key : map.keySet()) {
			if (key.matches(regex)) {
				keys.add(key);
			}
		}
		return keys;
		
	}
}


