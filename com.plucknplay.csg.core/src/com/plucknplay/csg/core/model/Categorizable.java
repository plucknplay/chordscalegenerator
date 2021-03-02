/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.core.model;

public abstract class Categorizable extends ModelObject implements Comparable<Categorizable> {

	private static final long serialVersionUID = -1131939417009319524L;

	private String name;
	private String comment;

	/**
	 * The constructor.
	 * 
	 * @param name
	 *            the name, must not be null
	 */
	public Categorizable(final String name) {
		if (name == null) {
			throw new IllegalArgumentException();
		}
		this.name = name;
		comment = ""; //$NON-NLS-1$
	}

	/**
	 * The constructor.
	 * 
	 * @param element
	 *            the element to clone, must not be null
	 */
	public Categorizable(final Categorizable element) {
		if (element == null) {
			throw new IllegalArgumentException();
		}
		name = element.getName();
		comment = element.getComment();
	}

	/**
	 * Returns the name of this element.
	 * 
	 * @return the name of this element
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of this element.
	 * 
	 * @param name
	 *            the name, must not be null
	 */
	public void setName(final String name) {
		if (name == null) {
			throw new IllegalArgumentException();
		}
		this.name = name;
	}

	/**
	 * Returns the comment for this element.
	 * 
	 * @return the comment for this element
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * Sets the comment for this element.
	 * 
	 * @param comment
	 *            the comment, must not be null
	 */
	public void setComment(final String comment) {
		if (comment == null) {
			throw new IllegalArgumentException();
		}
		this.comment = comment;
	}

	@Override
	public int compareTo(final Categorizable other) {
		if (other == null) {
			return 0;
		}
		return this.getName().compareTo(other.getName());
	}
}
