package org.ihtsdo.changeanalyzer.utils;

/*
 * Copyright (c) 2008-2011 Simon Ritchie.
 * All rights reserved. 
 * 
 * This program is free software: you can redistribute it and/or modify 
 * it under the terms of the GNU Lesser General Public License as published 
 * by the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License 
 * along with this program.  If not, see http://www.gnu.org/licenses/>.
 */
//package org.rimudb.util;

import java.util.*;

public class LRUCache2<K, V> {
	private static final float hashTableLoadFactor = 0.75f;

	private LinkedHashMap<K, V> map;
	private int cacheSize;

	/**
	 * Creates a new LRU cache.
	 * 
	 * @param cacheSize
	 *            The maximum number of entries that will be kept in this cache.
	 */
	public LRUCache2(int cacheSize) {
		this.cacheSize = cacheSize;
		int hashTableCapacity = (int) Math.ceil(cacheSize / hashTableLoadFactor) + 1;

		map = new LinkedHashMap<K, V>(hashTableCapacity, hashTableLoadFactor, true) {
			private static final long serialVersionUID = 1;

			@Override
			protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
				return size() > LRUCache2.this.cacheSize;
			}
		};
	}

	/**
	 * Retrieves an entry from the cache.<br>
	 * The retrieved entry is move to the head of the list as it is the most
	 * recently used.
	 * 
	 * @param key
	 * @return The value associated to this key
	 */
	public synchronized V get(K key) {
		return map.get(key);
	}

	/**
	 * Adds an entry to this cache. If the cache is full, the LRU (least
	 * recently used) entry is dropped.
	 * 
	 * @param key
	 * @param value
	 */
	public synchronized void put(K key, V value) {
		map.put(key, value);
	}

	/**
	 * Remove an entry from the cache.
	 * 
	 * @param key
	 */
	public synchronized void remove(K key) {
		map.remove(key);
	}

	/**
	 * Clears the cache.
	 */
	public synchronized void clear() {
		map.clear();
	}

	/**
	 * Returns the number of used entries in the cache.
	 * 
	 * @return the number of entries currently in the cache.
	 */
	public synchronized int usedEntries() {
		return map.size();
	}

	/**
	 * Returns a <code>Collection</code> that contains a copy of all cache
	 * entries.
	 * 
	 * @return a <code>Collection</code> with a copy of the cache content.
	 */
	public synchronized Collection<Map.Entry<K, V>> getAll() {
		return new ArrayList<Map.Entry<K, V>>(map.entrySet());
	}

	public synchronized Set<K> keySet() {
		return map.keySet();
	}

	public int getMaximumSize() {
		return cacheSize;
	}
}
