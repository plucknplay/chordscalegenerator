/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.core.model;

public interface IFingerNumberProvider {

	/**
	 * Returns the number of the used finger for the given fretboard position.
	 * 
	 * @return the number of the used finger for the given fretboard position,
	 *         or null if there is none
	 */
	Byte getFingerNumber(FretboardPosition fretboardPosition);

}
