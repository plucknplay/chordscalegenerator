/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.util;

import org.w3c.dom.Element;

public final class XMLUtil {

	private XMLUtil() {
	}

	/**
	 * Returns the text value of the given element.
	 * 
	 * @param element
	 *            the element to parse, must not be null
	 * 
	 * @return the text value of this given element, never null
	 */
	public static String getElementValue(final Element element) {
		if (element == null) {
			throw new IllegalArgumentException();
		}

		return element.hasChildNodes() ? element.getFirstChild().getNodeValue() : ""; //$NON-NLS-1$
	}
}
