/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.views.dnd;

import com.plucknplay.csg.core.model.sets.Category;
import com.plucknplay.csg.core.model.sets.ChordList;
import com.plucknplay.csg.core.model.sets.InstrumentList;
import com.plucknplay.csg.core.model.sets.ScaleList;

public final class ModelObjectTransfer extends AbstractTransfer {

	public static final String DATA_KEY_FQ_NAME = "FQ_NAME"; //$NON-NLS-1$

	public static final Object TYPE_INSTRUMENT = new Object();
	public static final Object TYPE_CHORD = new Object();
	public static final Object TYPE_SCALE = new Object();

	private static final String INSTRUMENT_TYPE_NAME = "chord-generator-instrument-transfer-format"; //$NON-NLS-1$
	private static final String CHORD_TYPE_NAME = "chord-generator-chord-transfer-format"; //$NON-NLS-1$
	private static final String SCALE_TYPE_NAME = "chord-generator-scale-transfer-format"; //$NON-NLS-1$

	private static ModelObjectTransfer instrumentInstance;
	private static ModelObjectTransfer chordInstance;
	private static ModelObjectTransfer scaleInstance;

	private final Object type;

	/**
	 * The private default constructor.
	 */
	private ModelObjectTransfer(final Object type) {
		this.type = type;
	}

	/**
	 * Returns the singleton instance of this transfer.
	 * 
	 * @param type
	 *            the type of transfer, SEE ModelObjectTransfer.TYPE_*
	 * 
	 * @return the singleton instance of this transfer
	 */
	public static ModelObjectTransfer getInstance(final Object type) {
		if (type == TYPE_INSTRUMENT) {
			if (instrumentInstance == null) {
				instrumentInstance = new ModelObjectTransfer(type);
			}
			return instrumentInstance;
		}
		if (type == ModelObjectTransfer.TYPE_CHORD) {
			if (chordInstance == null) {
				chordInstance = new ModelObjectTransfer(type);
			}
			return chordInstance;
		}
		if (type == ModelObjectTransfer.TYPE_SCALE) {
			if (scaleInstance == null) {
				scaleInstance = new ModelObjectTransfer(type);
			}
			return scaleInstance;
		}
		return null;
	}

	@Override
	protected String getTypeName() {
		return type == TYPE_INSTRUMENT ? INSTRUMENT_TYPE_NAME : type == TYPE_CHORD ? CHORD_TYPE_NAME : SCALE_TYPE_NAME;
	}

	public static Category getRootCategory(final Object type) {
		if (type == TYPE_INSTRUMENT) {
			return InstrumentList.getInstance().getRootCategory();
		}
		if (type == ModelObjectTransfer.TYPE_CHORD) {
			return ChordList.getInstance().getRootCategory();
		}
		if (type == ModelObjectTransfer.TYPE_SCALE) {
			return ScaleList.getInstance().getRootCategory();
		}
		return null;
	}
}
