/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.core.calculation;

import java.util.Collection;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;

import com.plucknplay.csg.core.model.Chord;
import com.plucknplay.csg.core.model.Griptable;
import com.plucknplay.csg.core.model.IFingering;
import com.plucknplay.csg.core.model.Note;

/**
 * A calculator is responsible for all important calculations concerning chords
 * and griptables.
 */
public interface ICalculator {

	/**
	 * Calculates the corresponding chord of a given griptable.
	 * 
	 * <p>
	 * Note, it can happen that one griptable can correspond to more than one
	 * chord even though these chords contain completely different intervals.
	 * </p>
	 * 
	 * @param griptable
	 *            the griptable, must not be null
	 * @param monitor
	 *            the monitor, must not be null
	 * @param work
	 *            the number of work units
	 * 
	 * @return a set of chords which belong to the given griptable
	 */
	Set<Chord> calculateCorrespondingChordsOfGriptable(Griptable griptable, IProgressMonitor monitor, int work)
			throws InterruptedException;

	/**
	 * Calculates the corresponding griptables of a given chord.
	 * 
	 * @param descriptor
	 *            the calculation description, must not be null
	 * @param monitor
	 *            the monitor, must not be null
	 * @param work
	 *            the number of work units
	 * 
	 * @return a set of griptables which belong to the given chord and fulfil
	 *         the specified criterias
	 * 
	 * @throws InterruptedException
	 */
	Set<Griptable> calculateCorrespondingGriptablesOfChord(CalculationDescriptor descriptor, IProgressMonitor monitor,
			int work) throws InterruptedException;

	/**
	 * Calculates a suggested fingering for the given griptable.
	 * 
	 * @param griptable
	 *            the griptable, must not be null
	 * 
	 * @return a suggested fingering for the given griptable
	 */
	IFingering calculateFingeringOfGriptable(Griptable griptable);

	/**
	 * Finds all possible fingerings for a give set of absolute notes.
	 * 
	 * @param absoluteNotes
	 *            the set of absolute notes, must not be null
	 * 
	 * @return all possible fingerings for a give set of absolute notes
	 */
	Set<Griptable> calculateCorrespondingGriptablesOfSetOfAbsoluteNotes(Collection<Note> absoluteNotes);

	/**
	 * This method is responsible for building up the list of possible
	 * griptables.
	 * 
	 * @param griptables
	 *            the list of possible griptables already build up, must not be
	 *            null
	 * @param positions
	 *            the positions to add on, must not be null
	 * @param stringNumber
	 *            the string number the positions are associated with
	 * @param monitor
	 *            the monitor, must not be null
	 * 
	 * @throws InterruptedException
	 */
	Set<Griptable> buildUpGriptables(Set<Griptable> griptables, Set<Byte> positions, int stringNumber,
			IProgressMonitor monitor) throws InterruptedException;
}
