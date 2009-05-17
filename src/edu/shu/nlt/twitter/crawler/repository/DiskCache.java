package edu.shu.nlt.twitter.crawler.repository;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;

import edu.nlt.util.FileProcessor;
import edu.nlt.util.InputUtil;
import edu.shu.nlt.twitter.crawler.repository.objectStore.MemoryObjectStore;
import edu.shu.nlt.twitter.crawler.repository.objectStore.ObjectStore;

public class DiskCache implements PersistentCache {

	private File basePath;
	private CacheMap cacheMap;
	private ObjectStore objectStore;

	public DiskCache(String basePath) {
		super();
		this.basePath = new File(basePath);

		initialize();
	}

	/**
	 *Generates a in-memory map of the disk cache
	 */
	private void initialize() {
		objectStore = new MemoryObjectStore();
		cacheMap = new CacheMap();

		// Parse files already processed
		//
		InputUtil.processFiles(basePath.getPath(), new FileProcessor() {

			@Override
			public void processFile(File file) {
				cacheMap.set(file.getName(), new Date(file.lastModified()));
			}
		});

	}

	@Override
	public CachedValue get(String key) {

		Cacheable cachedObject = objectStore.get(key);
		String cachedString = null;
		if (cachedObject != null) {

		} else {

			File file = new File(basePath, key);
			FileReader reader = null;

			try {
				reader = new FileReader(file);

				int fileLength = (int) file.length();

				char[] buffer = new char[(int) fileLength];

				try {
					reader.read(buffer, 0, fileLength);
					reader.close();

					cachedString = new String(buffer);

				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (FileNotFoundException e) {
				// do nothing
			}

		}
		if (cachedObject != null || cachedString != null) {
			return new CachedValue(cachedObject, cachedString);
		} else {
			return null;
		}
	}

	@Override
	public void put(Cacheable cacheable) {

		File file = new File(basePath, cacheable.getCacheKey());

		FileWriter fstream = null;
		try {
			fstream = new FileWriter(file);
			fstream.write(cacheable.getSerialized());

			cacheMap.set(cacheable.getCacheKey(), new Date(file.lastModified()));
			objectStore.put(cacheable);

			fstream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public boolean containsKey(String key) {
		return cacheMap.containsKey(key);
	}

	@Override
	public Date getLastUpdated(String key) {
		return cacheMap.getLastUpdated(key);
	}

	public CacheMap getCacheMap() {
		return cacheMap;
	}

	@Override
	public Collection<String> getKeysMatching(String regex) {
		return cacheMap.getKeysMatching(regex);

	}
}
