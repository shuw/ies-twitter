package edu.shu.nlt.twitter.crawler.repository.objectStore;

import edu.shu.nlt.twitter.crawler.repository.Cacheable;

public interface ObjectStore {

	public void put(Cacheable cacheable);

	public Cacheable get(String key);

}
