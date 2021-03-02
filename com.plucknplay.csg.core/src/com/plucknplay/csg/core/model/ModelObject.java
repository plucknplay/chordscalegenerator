/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.core.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public abstract class ModelObject implements Serializable {

	private static final long serialVersionUID = -976154219555237185L;

	private Map<String, Object> dataMap;

	/**
	 * Saves some user data under a given key.
	 * 
	 * @param key
	 *            the key, must not be null
	 * @param data
	 *            the data
	 */
	public void setData(final String key, final Object data) {
		if (key == null) {
			throw new IllegalArgumentException();
		}
		if (dataMap == null) {
			dataMap = new HashMap<String, Object>();
		}
		if (data == null) {
			dataMap.remove(key);
		} else {
			dataMap.put(key, data);
		}
		if (dataMap.isEmpty()) {
			dataMap = null;
		}
	}

	/**
	 * Returns the user data for the given key.
	 * 
	 * @param key
	 *            the key, must not be null
	 * 
	 * @return the user data for the given key, or null if no data exists
	 */
	public Object getData(final String key) {
		if (key == null) {
			throw new IllegalArgumentException();
		}
		if (dataMap == null) {
			return null;
		}
		return dataMap.get(key);
	}

	public abstract String getName();
}
