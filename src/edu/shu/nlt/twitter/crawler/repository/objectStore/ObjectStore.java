package edu.shu.nlt.twitter.crawler.repository.objectStore;

import edu.shu.nlt.twitter.crawler.repository.Cacheable;

public interface ObjectStore {

	public Cacheable get(String key);

	public void put(Cacheable cacheable);

}
