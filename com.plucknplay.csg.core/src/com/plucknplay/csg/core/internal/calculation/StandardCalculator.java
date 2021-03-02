/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.core.internal.calculation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osgi.util.NLS;

import com.plucknplay.csg.core.Constants;
import com.plucknplay.csg.core.calculation.CalculationDescriptor;
import com.plucknplay.csg.core.calculation.CalculationMessages;
import com.plucknplay.csg.core.calculation.ICalculator;
import com.plucknplay.csg.core.internal.calculation.filters.BassToneFilter;
import com.plucknplay.csg.core.internal.calculation.filters.DoubledTonesFilter;
import com.plucknplay.csg.core.internal.calculation.filters.Filterer;
import com.plucknplay.csg.core.internal.calculation.filters.LeadToneFilter;
import com.plucknplay.csg.core.internal.calculation.filters.LevelFilter;
import com.plucknplay.csg.core.internal.calculation.filters.OnlyAscendingDescendingToneSequenceFilter;
import com.plucknplay.csg.core.internal.calculation.filters.OnlyPackedFilter;
import com.plucknplay.csg.core.internal.calculation.filters.OnlySingleMutedStringsFilter;
import com.plucknplay.csg.core.internal.calculation.filters.ToneNumberFilter;
import com.plucknplay.csg.core.internal.model.Fingering;
import com.plucknplay.csg.core.internal.model.HellFingering;
import com.plucknplay.csg.core.model.Chord;
import com.plucknplay.csg.core.model.Factory;
import com.plucknplay.csg.core.model.FretboardPosition;
import com.plucknplay.csg.core.model.Griptable;
import com.plucknplay.csg.core.model.IFingering;
import com.plucknplay.csg.core.model.Instrument;
import com.plucknplay.csg.core.model.Interval;
import com.plucknplay.csg.core.model.Note;
import com.plucknplay.csg.core.model.sets.ChordList;
import com.plucknplay.csg.core.model.sets.InstrumentList;

/**
 * This class represents the standard implementation of a calculator.
 */
public class StandardCalculator implements ICalculator {

	private final Instrument currentInstrument;
	private final List<?> chords;

	private final boolean preferBarrees;
	private final int maxResultsNumber;

	private final Map<Interval, Boolean> allowExcludedIntervalsMap;
	private final int findChordsRestriction;

	public StandardCalculator(final boolean findChordsWithout1st, final boolean findChordsWithout3rd,
			final boolean findChordsWithout5th, final boolean preferBarrees, final int maxResultsNumber,
			final int findChordsRestriction) {

		currentInstrument = InstrumentList.getInstance().getCurrentInstrument();
		chords = ChordList.getInstance().getRootCategory().getAllElements();

		allowExcludedIntervalsMap = new HashMap<Interval, Boolean>();
		allowExcludedIntervalsMap.put(Factory.getInstance().getInterval(0), findChordsWithout1st);
		allowExcludedIntervalsMap.put(Factory.getInstance().getInterval(3), findChordsWithout3rd);
		allowExcludedIntervalsMap.put(Factory.getInstance().getInterval(4), findChordsWithout3rd);
		allowExcludedIntervalsMap.put(Factory.getInstance().getInterval(7), findChordsWithout5th);
		this.findChordsRestriction = findChordsRestriction;

		this.preferBarrees = preferBarrees;
		this.maxResultsNumber = maxResultsNumber;
	}

	@Override
	public Set<Chord> calculateCorrespondingChordsOfGriptable(final Griptable griptable,
			final IProgressMonitor monitor, final int work) throws InterruptedException {
		if (griptable == null) {
			throw new IllegalArgumentException();
		}

		// get necessary preference values
		final Set<Chord> results = new HashSet<Chord>();

		final int workPerNote = work / Constants.MAX_NOTES_VALUE + 1;
		final int workPerChord = workPerNote / chords.size();

		final Interval i0 = Factory.getInstance().getInterval(0);
		final Interval i3 = Factory.getInstance().getInterval(3);
		final Interval i4 = Factory.getInstance().getInterval(4);
		final Interval i7 = Factory.getInstance().getInterval(7);

		for (int value = 0; value <= Constants.MAX_NOTES_VALUE; value++) {
			final Note currentNote = Factory.getInstance().getNote(value);

			for (final Object name : chords) {
				if (monitor.isCanceled()) {
					throw new InterruptedException();
				}

				final Chord currentChord = (Chord) name;
				currentChord.setRootNote(currentNote);

				// check all variations of excluded intervals
				if (check(currentChord, griptable, new Interval[] {})
						|| check(currentChord, griptable, new Interval[] { i0 })
						|| check(currentChord, griptable, new Interval[] { i3 })
						|| check(currentChord, griptable, new Interval[] { i4 })
						|| check(currentChord, griptable, new Interval[] { i7 })
						|| check(currentChord, griptable, new Interval[] { i0, i3 })
						|| check(currentChord, griptable, new Interval[] { i0, i4 })
						|| check(currentChord, griptable, new Interval[] { i0, i7 })
						|| check(currentChord, griptable, new Interval[] { i3, i4 })
						|| check(currentChord, griptable, new Interval[] { i3, i7 })
						|| check(currentChord, griptable, new Interval[] { i4, i7 })
						|| check(currentChord, griptable, new Interval[] { i0, i3, i4 })
						|| check(currentChord, griptable, new Interval[] { i0, i3, i7 })
						|| check(currentChord, griptable, new Interval[] { i0, i4, i7 })
						|| check(currentChord, griptable, new Interval[] { i3, i4, i7 })
						|| check(currentChord, griptable, new Interval[] { i0, i3, i4, i7 })) {

					results.add(new Chord(currentChord));
					monitor.worked(workPerChord);
					continue;
				}
			}
		}
		return results;
	}

	private boolean check(final Chord currentChord, final Griptable griptable, final Interval[] excludedIntervals) {
		if (currentChord.getIntervals().size() - excludedIntervals.length < 2) {
			return false;
		}

		if (findChordsRestriction == Constants.CALCULATOR_RESTRICTION_MAX_1_EXCLUDED_INTERVAL
				&& excludedIntervals.length > 1) {
			return false;
		}
		if (findChordsRestriction == Constants.CALCULATOR_RESTRICTION_MAX_2_EXCLUDED_INTERVALS
				&& excludedIntervals.length > 2) {
			return false;
		}

		for (final Interval excludedInterval : excludedIntervals) {
			final Interval interval = excludedInterval;
			if (!allowExcludedIntervalsMap.get(interval) || !currentChord.getIntervals().contains(interval)) {
				return false;
			}
		}
		return isChordCorrespondingOneOfTheGriptable(currentChord, griptable, Arrays.asList(excludedIntervals));
	}

	/**
	 * Returns true if the given chord matches to the given griptable under
	 * consideration of a list of excluded intervals, or false otherwise.
	 * 
	 * This method merely checks whether the griptable contains all necessary
	 * intervals of the chord (without the excluded intervals).
	 * 
	 * @param chord
	 *            the chord, must not be null
	 * @param griptable
	 *            the griptable, must not be null
	 * @param excludedIntervals
	 *            the excluded intervals, must not be null
	 * 
	 * @return true if the given chord matches to the given griptable, or false
	 *         otherwise
	 */
	private boolean isChordCorrespondingOneOfTheGriptable(final Chord chord, final Griptable griptable,
			final List<Interval> excludedIntervals) {
		if (chord == null || griptable == null || excludedIntervals == null) {
			throw new IllegalArgumentException();
		}

		final Set<Integer> noteValuesOfGriptable = new HashSet<Integer>();
		for (final FretboardPosition fretboardPosition : griptable.getFretboardPositions()) {
			noteValuesOfGriptable.add(currentInstrument.getNote(fretboardPosition).getValue());
		}

		final Set<Integer> necessaryNoteValues = chord.getNoteValues(excludedIntervals);

		for (final Iterator<Integer> i = noteValuesOfGriptable.iterator(); i.hasNext();) {
			final int currentValue = i.next();
			if (necessaryNoteValues.contains(currentValue)) {
				i.remove();
				necessaryNoteValues.remove(currentValue);
			}
		}
		if (!necessaryNoteValues.isEmpty()) {
			return false;
		}
		if (noteValuesOfGriptable.isEmpty()) {
			return true;
		}

		return false;
	}

	/**
	 * Returns true if the given chord might match in the future to the given
	 * potential griptable under consideration of a list of excluded intervals,
	 * or false otherwise.
	 * 
	 * This method merely checks which necessary intervals the griptable already
	 * contains and whether the number of unset (muted) assignments is enough to
	 * place the remaining necessary intervals.
	 * 
	 * @param chord
	 *            the chord, must not be null
	 * @param griptable
	 *            the griptable, must not be null
	 * @param excludedIntervals
	 *            the excluded intervals, must not be null
	 * @param remainingStrings
	 *            the number of remaining strings where notes can be assigned to
	 *            the potential griptables
	 * @return true if the given chord might match in the future to the given
	 *         potential griptable under consideration of a list of excluded
	 *         intervals, or false otherwise
	 */
	private boolean mightChordBeCorrespondingOneOfThePotentialGriptable(final Chord chord, final Griptable griptable,
			final List<Interval> excludedIntervals, final int remainingStrings) {
		if (chord == null || griptable == null || excludedIntervals == null) {
			throw new IllegalArgumentException();
		}

		final Set<Integer> noteValuesOfGriptable = new HashSet<Integer>();
		for (final FretboardPosition fretboardPosition : griptable.getFretboardPositions()) {
			noteValuesOfGriptable.add(currentInstrument.getNote(fretboardPosition).getValue());
		}

		final Set<Integer> necessaryNoteValues = chord.getNoteValues(excludedIntervals);
		for (final Integer currentValue : noteValuesOfGriptable) {
			if (necessaryNoteValues.contains(currentValue)) {
				necessaryNoteValues.remove(currentValue);
			}
		}

		return necessaryNoteValues.size() <= remainingStrings;
	}

	@Override
	public Set<Griptable> calculateCorrespondingGriptablesOfChord(final CalculationDescriptor descriptor,
			final IProgressMonitor monitor, final int work) throws InterruptedException {
		if (descriptor == null || monitor == null) {
			throw new IllegalArgumentException();
		}

		final Set<Griptable> result = new HashSet<Griptable>();

		// determine relevant notes
		final Set<Integer> relevantNotes = descriptor.getChord().getNoteValues(descriptor.getExcludedIntervals());

		final int numberOfLoops = descriptor.getNumberOfCalculationLoops();
		final int workPerLoop = work / numberOfLoops;
		final int remainingWork = work - numberOfLoops * workPerLoop;
		final int workPerString = workPerLoop / currentInstrument.getStringCount();
		final int innerRemainingWork = workPerLoop - workPerString * currentInstrument.getStringCount();

		for (int startFret = descriptor.getMinFret(); startFret <= descriptor.getMaxStartFret(); startFret++) {

			monitor.subTask(NLS.bind(CalculationMessages.StandardCalculator_scanning_frets, startFret, startFret
					+ descriptor.getFretGripRange(startFret) - 1)
					+ NLS.bind(CalculationMessages.StandardCalculator_found_chords, result.size()));

			if (result.size() >= maxResultsNumber) {
				return result;
			}

			// create list for possible griptables
			Set<Griptable> possibleGriptables = new HashSet<Griptable>();
			possibleGriptables.add(new Griptable(descriptor.getChord())); // that's
																			// the
																			// first
																			// seed
																			// point

			boolean griptablesFound = false;
			for (int string = descriptor.getMaxString() - 1; string >= descriptor.getMinString() - 1; string--) {

				monitor.subTask(NLS.bind(CalculationMessages.StandardCalculator_scanning_frets, startFret, startFret
						+ descriptor.getFretGripRange(startFret) - 1)
						+ NLS.bind(CalculationMessages.StandardCalculator_found_chords, result.size()));

				final Set<Byte> positions = new HashSet<Byte>();

				if (monitor.isCanceled()) {
					throw new InterruptedException();
				}

				// damp tones if allowed
				if (descriptor.allowMutedStrings()) {
					positions.add(Byte.valueOf((byte) -1));
				}

				// empty string notes are only allowed if chosen
				final int capoFret = currentInstrument.getCapoFret(string + 1);
				if (capoFret > 0 || descriptor.allowEmptyStrings()) {
					final int currentNoteValue = currentInstrument.getNote(new FretboardPosition(string, capoFret))
							.getValue();
					if (relevantNotes.contains(currentNoteValue)) {
						positions.add(Byte.valueOf((byte) capoFret));
					}
				}

				// check notes in the current range for relevance
				for (int inRange = 0; inRange < descriptor.getFretGripRange(startFret); inRange++) {
					final int currentNoteValue = currentInstrument.getNote(
							new FretboardPosition(string, startFret + inRange)).getValue();
					if (relevantNotes.contains(currentNoteValue)) {
						positions.add(Byte.valueOf((byte) (startFret + inRange)));
					}
				}

				griptablesFound = !positions.isEmpty();
				if (positions.isEmpty()) {
					break;
				}

				possibleGriptables = buildUpGriptables(possibleGriptables, positions, string, monitor);

				final boolean onlyDrafts = string != 0;
				if (onlyDrafts) {
					validateGriptables(possibleGriptables, onlyDrafts, string, descriptor, monitor);
					filterGriptables(possibleGriptables, onlyDrafts, descriptor, monitor);
				}

				monitor.worked(workPerString);
			}

			if (griptablesFound) {

				final Set<Griptable> results = new HashSet<Griptable>();

				int currentSubset = 0;
				final int lastSubset = (possibleGriptables.size() - 1) / maxResultsNumber;

				while (results.size() < maxResultsNumber && currentSubset <= lastSubset) {

					final int from = maxResultsNumber * currentSubset;
					final int to = currentSubset == lastSubset ? possibleGriptables.size() : from + maxResultsNumber;

					final List<Griptable> possibleGriptablesList = new ArrayList<Griptable>(possibleGriptables);
					final List<Griptable> subList = new ArrayList<Griptable>(possibleGriptablesList.subList(from, to));

					validateGriptables(subList, false, 0, descriptor, monitor);
					filterGriptables(subList, false, descriptor, monitor);

					results.addAll(subList);
					currentSubset++;
				}

				result.addAll(results);
			}

			monitor.worked(innerRemainingWork);
		}

		monitor.subTask(NLS.bind(CalculationMessages.StandardCalculator_found_chords, result.size()));
		monitor.worked(remainingWork);

		return result;
	}

	@Override
	public Set<Griptable> buildUpGriptables(final Set<Griptable> griptables, final Set<Byte> positions,
			final int stringNumber, final IProgressMonitor monitor) throws InterruptedException {
		if (griptables == null || positions == null || monitor == null) {
			throw new IllegalArgumentException();
		}

		final Set<Griptable> result = new HashSet<Griptable>();

		for (final Griptable griptable : griptables) {
			for (final Byte currentPosition : positions) {
				if (monitor.isCanceled()) {
					throw new InterruptedException();
				}
				final Griptable newGriptable = new Griptable(griptable);
				newGriptable.setValue(currentPosition, stringNumber);
				result.add(newGriptable);
			}
		}

		return result;
	}

	/**
	 * Validates a set of potential griptables. It will be checked whether the
	 * given griptables contain all necessary intervals.
	 * 
	 * @param possibleGriptables
	 *            the set of potential griptables, must not be null
	 * @param onlyDrafts
	 *            true if the passed griptables are only drafts (that means not
	 *            completed yet), or false otherwise
	 * @param currentStringNumber
	 *            the current string number
	 * @param descriptor
	 *            the calculation descriptor, must not be null
	 * @param monitor
	 *            the monitor, must not be null
	 * 
	 * @throws InterruptedException
	 */
	private void validateGriptables(final Collection<Griptable> possibleGriptables, final boolean onlyDrafts,
			final int currentStringNumber, final CalculationDescriptor descriptor, final IProgressMonitor monitor)
			throws InterruptedException {
		if (possibleGriptables == null || descriptor == null || monitor == null) {
			throw new IllegalArgumentException();
		}

		for (final Iterator<Griptable> iter = possibleGriptables.iterator(); iter.hasNext();) {
			if (monitor.isCanceled()) {
				throw new InterruptedException();
			}
			final Griptable currentGriptable = iter.next();

			// drafts
			if (currentStringNumber != 0) {
				if (!mightChordBeCorrespondingOneOfThePotentialGriptable(descriptor.getChord(), currentGriptable,
						descriptor.getExcludedIntervals(), currentStringNumber + 1)) {
					iter.remove();
				}
			}
			// completed griptables
			else {
				if (!isChordCorrespondingOneOfTheGriptable(descriptor.getChord(), currentGriptable,
						descriptor.getExcludedIntervals())) {
					iter.remove();
				}
			}
		}
	}

	private void filterGriptables(final Collection<Griptable> possibleGriptables, final boolean onlyDrafts,
			final CalculationDescriptor descriptor, final IProgressMonitor monitor) throws InterruptedException {

		final Filterer filterer = new Filterer();

		// a) bass tone
		final Note bassTone = descriptor.getBassTone();
		if (bassTone != null && !onlyDrafts) {
			filterer.addFilter(new BassToneFilter(bassTone));
		}

		// b) lead tone
		final Note leadTone = descriptor.getLeadTone();
		if (leadTone != null && !onlyDrafts) {
			filterer.addFilter(new LeadToneFilter(leadTone));
		}

		// c) only packed
		if (descriptor.isOnlyPacked()) {
			filterer.addFilter(new OnlyPackedFilter());
		}

		// d) only single muted strings
		if (descriptor.isOnlySingleMutedStrings() && !onlyDrafts) {
			filterer.addFilter(new OnlySingleMutedStringsFilter());
		}

		// e) tone numbers
		final Integer toneNumber = descriptor.getToneNumber();
		if (toneNumber != null) {
			filterer.addFilter(new ToneNumberFilter(toneNumber.intValue()));
		}

		// f) level
		if (!onlyDrafts
				&& !(descriptor.getMinLevel() == Constants.MIN_LEVEL && descriptor.getMaxLevel() == Constants.MAX_LEVEL)) {
			filterer.addFilter(new LevelFilter(descriptor.getMinLevel(), descriptor.getMaxLevel(), preferBarrees));
		}

		// g) doubled tones
		if (!onlyDrafts && !descriptor.areDoubledTonesAllowed()) {
			filterer.addFilter(new DoubledTonesFilter());
		}

		// h) only ascending/descending tone sequence
		if (!onlyDrafts && descriptor.isOnlyAscendingDescendingToneSequenceAllowed()) {
			filterer.addFilter(new OnlyAscendingDescendingToneSequenceFilter());
		}

		filterer.startFiltering(possibleGriptables, monitor);
	}

	@Override
	public IFingering calculateFingeringOfGriptable(final Griptable griptable) {
		final IFingering fingering = new Fingering();

		// determine whether a barre is possible
		boolean isBarrePossible = true;
		boolean foundBlock = false;

		final List<FretboardPosition> fbps = griptable.getFretboardPositions(true);
		Collections.reverse(fbps);

		for (final FretboardPosition fbp : fbps) {
			final int fret = fbp.getFret();
			final int string = fbp.getString();
			if (!foundBlock && fret != -1 && fret != currentInstrument.getCapoFret(string + 1)) {
				foundBlock = true;
			} else if (foundBlock && fret == currentInstrument.getCapoFret(string + 1)) {
				isBarrePossible = false;
				break;
			}
		}
		fingering.setIsBarrePossible(isBarrePossible);

		// determine all gripped fretboard positions
		final List<FretboardPosition> fretboardPositions = new ArrayList<FretboardPosition>();
		for (final FretboardPosition current : griptable.getFretboardPositions()) {
			if (current.getFret() > currentInstrument.getCapoFret(current.getString() + 1)) {
				fretboardPositions.add(current);
			}
		}

		// sort fretboard positions
		Collections.sort(fretboardPositions, new Comparator<FretboardPosition>() {
			@Override
			public int compare(final FretboardPosition fp1, final FretboardPosition fp2) {
				int result = Integer.valueOf(fp1.getFret()).compareTo(Integer.valueOf(fp2.getFret()));
				if (result == 0) {
					result = -1 * Integer.valueOf(fp1.getString()).compareTo(Integer.valueOf(fp2.getString()));
				}
				return result;
			}
		});

		// assign first finger numbers to the fretboard positions
		final int firstFret = griptable.getMinFret();
		final boolean barrePossible = (fretboardPositions.size() > 4 || preferBarrees) && fingering.isBarrePossible();
		boolean usedBarre = false;
		byte fingerNumber = 0;
		for (final FretboardPosition fp : fretboardPositions) {
			if (fingerNumber == 1 && firstFret == fp.getFret() && barrePossible) {
				fingerNumber = 1;
				usedBarre = true;
			} else {
				fingerNumber = (byte) (fingerNumber + 1);
			}
			if (fingerNumber > 4) {
				return new HellFingering();
			}
			fingering.setFingerNumber(fp, fingerNumber);
		}

		// tune and validate fingering - if the 3rd or 4th finger are unused
		// it is sometimes better to use these ones instead of the 2nd finger
		final List<FretboardPosition> list1 = fingering.getFretboardPositions((byte) 1);
		final FretboardPosition fp1 = list1.isEmpty() ? null : list1.get(0);
		final List<FretboardPosition> list2 = fingering.getFretboardPositions((byte) 2);
		final FretboardPosition fp2 = list2.isEmpty() ? null : list2.get(0);
		final List<FretboardPosition> list3 = fingering.getFretboardPositions((byte) 3);
		final FretboardPosition fp3 = list3.isEmpty() ? null : list3.get(0);
		final List<FretboardPosition> list4 = fingering.getFretboardPositions((byte) 4);
		final FretboardPosition fp4 = list4.isEmpty() ? null : list4.get(0);

		// there are just 2 possible constellations according to our algortihm
		// for
		// building

		// 2 !3 !4 --> !2 3 !4 || !2 !3 4
		if (fp2 != null && fp3 == null && fp4 == null) {
			final int fretRange = fp2.getFret() - fp1.getFret();
			final int stringRange = fp1.getString() - fp2.getString();
			if (fretRange == 2 && stringRange < 3 || !usedBarre && fretRange > 0 && stringRange == 2) {
				fingering.setFingerNumber(fp2, (byte) 3);
			} else if (fretRange > 2 || !usedBarre && fretRange > 0 && stringRange > 2) {
				fingering.setFingerNumber(fp2, (byte) 4);
			}
		}

		// 2 3 !4 --> !2 3 4 || 2 !3 4
		else if (fp2 != null && fp3 != null && fp4 == null) {
			final int fretRange12 = fp2.getFret() - fp1.getFret();
			final int fretRange23 = fp3.getFret() - fp2.getFret();
			final int stringRange12 = fp1.getString() - fp2.getString();
			final int stringRange23 = fp2.getString() - fp3.getString();

			// 2 3 !4 --> 2 !3 4
			if (fretRange23 >= 2 && fretRange23 >= fretRange12 || !usedBarre && fretRange23 > 0 && stringRange23 >= 2
					&& stringRange23 >= stringRange12) {
				fingering.setFingerNumber(fp3, (byte) 4);
			}

			// 2 3 !4 --> !2 3 4
			else if (fretRange12 >= 2 && fretRange12 > fretRange23 || !usedBarre && fretRange12 > 0
					&& stringRange12 >= 2 && stringRange12 > Math.abs(stringRange23) + 2) {
				fingering.setFingerNumber(fp2, (byte) 3);
				fingering.setFingerNumber(fp3, (byte) 4);
			}
		}

		return fingering;
	}

	@Override
	public Set<Griptable> calculateCorrespondingGriptablesOfSetOfAbsoluteNotes(final Collection<Note> notes) {

		if (notes == null) {
			throw new IllegalArgumentException();
		}

		final Set<Griptable> results = new HashSet<Griptable>();
		final Chord firstChord = (Chord) ChordList.getInstance().getRootCategory().getFirstElement();
		if (currentInstrument == null || firstChord == null || notes.size() > currentInstrument.getStringCount()) {
			return results;
		}

		// determine all valid fretboard positions
		final Set<FretboardPosition> fbps = new HashSet<FretboardPosition>();
		for (final Note note : notes) {
			final Set<FretboardPosition> currentFbps = currentInstrument.getFretboardPositions(note,
					currentInstrument.getMinFret() + 1, currentInstrument.getFretCount(), false);
			for (int s = 1; s <= currentInstrument.getStringCount(); s++) {
				final FretboardPosition currentCapoFbp = new FretboardPosition(s - 1, currentInstrument.getCapoFret(s));
				if (note.equals(currentInstrument.getNote(currentCapoFbp))) {
					currentFbps.add(currentCapoFbp);
				}
			}
			if (currentFbps.isEmpty()) {
				return results;
			}
			fbps.addAll(currentFbps);
		}

		// determine potential griptables
		final Set<Griptable> allThePotentialGriptables = new HashSet<Griptable>();

		boolean visitedAtLeastOnce = false;
		for (int startFret = currentInstrument.getMinFret(); startFret < currentInstrument.getFretCount(); startFret++) {

			if (startFret + 5 > currentInstrument.getFretCount() && visitedAtLeastOnce) {
				break;
			}

			Set<Griptable> potentialGriptables = new HashSet<Griptable>();
			potentialGriptables.add(new Griptable(firstChord));

			for (int s = 0; s < currentInstrument.getStringCount(); s++) {

				// add muted position
				final Set<Byte> positions = new HashSet<Byte>();
				if (notes.size() < currentInstrument.getStringCount()) {
					positions.add((byte) -1);
				}

				// add empty position
				final int capoFret = currentInstrument.getCapoFret(s + 1);
				if (fbps.contains(new FretboardPosition(s, 0))) {
					positions.add((byte) capoFret);
				}

				// add positions in range
				for (int f = 0; f < 6; f++) {
					final int fret = startFret + f;
					if (fret > currentInstrument.getFretCount()) {
						continue;
					}
					final FretboardPosition currentFbp = new FretboardPosition(s, fret);
					if (fbps.contains(currentFbp)) {
						positions.add((byte) currentFbp.getFret());
					}
				}

				// build up griptables
				try {
					potentialGriptables = buildUpGriptables(potentialGriptables, positions, s,
							new NullProgressMonitor());
				} catch (final InterruptedException e) {
					return results;
				}
			}

			allThePotentialGriptables.addAll(potentialGriptables);
			visitedAtLeastOnce = true;
		}

		// determine valid griptables
		for (final Griptable griptable : allThePotentialGriptables) {
			final Set<Note> theNotes = griptable.getNotes(false);
			if (theNotes.containsAll(notes)) {
				results.add(griptable);
			}
		}

		return results;
	}
}
