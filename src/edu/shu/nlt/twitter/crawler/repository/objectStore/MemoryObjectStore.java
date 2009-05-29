package edu.shu.nlt.twitter.crawler.repository.objectStore;

import java.lang.ref.WeakReference;
import java.util.Hashtable;

import edu.shu.nlt.twitter.crawler.repository.Cacheable;

public class MemoryObjectStore implements ObjectStore {
	private Hashtable<String, WeakReference<Cacheable>> objectTable = new Hashtable<String, WeakReference<Cacheable>>();

	@Override
	public Cacheable get(String key) {
		WeakReference<Cacheable> cacheableReference = objectTable.get(key);
		Cacheable rtnValue = null;

		if (cacheableReference != null) {
			rtnValue = cacheableReference.get();

			if (rtnValue == null) {
				// remove the key if it's no longer referenced
				objectTable.remove(key);
			}
		}

		return rtnValue;
	}

	@Override
	public void put(Cacheable cacheable) {
		objectTable.put(cacheable.getCacheKey(), new WeakReference<Cacheable>(cacheable));

	}
}
