/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.core.model;

import java.util.List;

public interface IFingering extends IFingerNumberProvider {

	/**
	 * Returns the fretboard positions where the finger with the given finger
	 * number is placed, or an empty list if there is none.
	 * 
	 * @param fingerNumber
	 *            the number of the finger, must be a value between 1 and 4
	 * 
	 * @return the fretboard positions where the finger with the given finger
	 *         number is placed, or an empty list if there is none
	 */
	List<FretboardPosition> getFretboardPositions(byte fingerNumber);

	/**
	 * Sets the number of the used finger for the given fretboard position.
	 * 
	 * @param fretboardPosition
	 *            the fretboard position, must not be null
	 * @param fingerNumber
	 *            the number of the used finger, must be a value between 1 and 4
	 */
	void setFingerNumber(FretboardPosition fretboardPosition, byte fingerNumber);

	/**
	 * Set true if a barre is possible for this fingering. A barre is possible
	 * if there are no empty or muted strings inside the corresponding
	 * griptable.
	 * 
	 * @param isBarrePossible
	 *            true if a barre is possible, or false otherwise
	 */
	void setIsBarrePossible(boolean isBarrePossible);

	/**
	 * Returns true if a barre is possible for this fingering, or false
	 * otherwise.
	 * 
	 * @return true if a barre is possible for this fingering, or false
	 *         otherwise
	 */
	boolean isBarrePossible();

	/**
	 * Returns true if this fingering has a barre, or false otherwise.
	 * 
	 * @return true if this fingering has a barre, or false otherwise
	 */
	boolean hasBarre();

	/**
	 * Returns the level of this fingering.
	 * 
	 * @return the level of this fingering
	 */
	int getLevel();

	/**
	 * Returns the maximum distance between one single pair of fingers.
	 * 
	 * @return the maximum distance between one single pair of fingers
	 */
	int getMaxSingleDistance();

	/**
	 * Returns the total distance value which is necessary for the determination
	 * of the level. The higher the value the harder the fingering.
	 * 
	 * @return the total distance value which is necessary for the determination
	 *         of the level
	 */
	int getTotalDistance();
}
