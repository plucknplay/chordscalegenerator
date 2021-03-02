/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.core.model.sets;

import com.plucknplay.csg.core.model.Categorizable;
import com.plucknplay.csg.core.model.Instrument;

/**
 * This container class stores all known instruments for the application and
 * handles also the currently set instrument which is necessary for
 * calculations.
 * 
 * <p>
 * Note this class is a singleton and can't be initiate using a constructor.
 * Invoke getInstance() to retrieve the shared instance of this class.
 * </p>
 */
public final class InstrumentList extends CategoryList {

	public static final Object PROP_CURRENT_INSTRUMENT_CHANGED = new Object();

	private static InstrumentList instance;

	private Instrument currentInstrument;

	/**
	 * The private default constructor.
	 */
	private InstrumentList() {
		super();
	}

	/**
	 * Returns the singleton instance of the instrument list.
	 * 
	 * @return the singleton instance of the instrument list, never null
	 */
	public static InstrumentList getInstance() {
		if (instance == null) {
			instance = new InstrumentList();
		}
		return instance;
	}

	@Override
	public void addElement(final Categorizable element, final Category category) {
		super.addElement(element, category);

		if (currentInstrument == null) {
			setDefaultCurrentInstrument();
		}
	}

	@Override
	public void removeElement(final Categorizable element) {
		super.removeElement(element);

		if (currentInstrument == null || currentInstrument.equals(element)) {
			setDefaultCurrentInstrument();
		}
	}

	@Override
	public void addCategory(final Category categoryToAdd, final Category category) {
		super.addCategory(categoryToAdd, category);

		if (currentInstrument == null) {
			setDefaultCurrentInstrument();
		}
	};

	/**
	 * Sets the default current instrument. Here will be taken the first
	 * instrument which will be found.
	 */
	private void setDefaultCurrentInstrument() {
		setCurrentInstrument((Instrument) getRootCategory().getFirstElement());
	}

	/**
	 * Sets the current instrument with which it will be work in the
	 * application.
	 * 
	 * <p>
	 * Note the given instrument must be known to the instrument list.
	 * </p>
	 * 
	 * @param instrument
	 *            the instrument, must not be null
	 */
	public void setCurrentInstrument(final Instrument instrument) {
		currentInstrument = instrument;
		notifyListeners(instrument, null, PROP_CURRENT_INSTRUMENT_CHANGED);
	}

	/**
	 * Returns the currently set instrument which will be used in the whole
	 * application. If no instrument was set explicitly the first instrument of
	 * the list will be chosen.
	 * 
	 * @return the currently set instrument, may be null if the instrument list
	 *         is empty
	 */
	public Instrument getCurrentInstrument() {
		return currentInstrument;
	}

	@Override
	protected void checkCategorizableType(final Categorizable element) {
		if (!(element instanceof Instrument)) {
			throw new IllegalArgumentException();
		}
	}

	@Override
	protected Categorizable createNewElement(final Categorizable element) {
		if (!(element instanceof Instrument)) {
			throw new IllegalArgumentException();
		}
		return new Instrument((Instrument) element);
	}
}
