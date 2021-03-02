/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.views.dnd;

public final class GriptableTransfer extends AbstractTransfer {

	public static final String ROOT_NOTE = "ROOT_NOTE"; //$NON-NLS-1$

	private static final String TYPE_NAME = "chord-generator-griptable-transfer-format"; //$NON-NLS-1$

	private static GriptableTransfer instance;

	/**
	 * The private default constructor.
	 */
	private GriptableTransfer() {
	}

	/**
	 * Returns the singleton instance of this transfer.
	 * 
	 * @return the singleton instance of this transfer
	 */
	public static GriptableTransfer getInstance() {
		if (instance == null) {
			instance = new GriptableTransfer();
		}
		return instance;
	}

	@Override
	protected String getTypeName() {
		return TYPE_NAME;
	}
}
