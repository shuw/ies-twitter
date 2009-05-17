package edu.shu.nlt.twitter.crawler.repository.objectStore;

import java.util.Hashtable;

import edu.shu.nlt.twitter.crawler.repository.Cacheable;

public class MemoryObjectStore implements ObjectStore {
	private Hashtable<String, Cacheable> objectTable = new Hashtable<String, Cacheable>();

	@Override
	public Cacheable get(String key) {
		return objectTable.get(key);
	}

	@Override
	public void put(Cacheable cacheable) {
		objectTable.put(cacheable.getCacheKey(), cacheable);

	}
}
