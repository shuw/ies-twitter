package edu.shu.nlt.twitter.crawler.repository;

import java.util.Date;

public interface Cacheable {
	public String getCacheKey();

	public Date getLastUpdated();

	public String getSerialized();
}
