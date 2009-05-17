package edu.shu.nlt.twitter.crawler.repository;

import java.util.Date;

public interface Cacheable {
	public String getCacheKey();

	public String getSerialized();

	public Date getLastUpdated();
}
