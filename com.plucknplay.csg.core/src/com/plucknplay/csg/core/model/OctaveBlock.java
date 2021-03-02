/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.core.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.plucknplay.csg.core.Constants;

public class OctaveBlock extends Block {

	private FretboardPosition startPosition;

	private List<FretboardPosition> fretboardPositions;
	private int minStringNumber;
	private int maxStringNumber;
	private int minFret;
	private int maxFret;

	private Map<FretboardPosition, Byte> fingering;

	public OctaveBlock(final IntervalContainer intervalContainer, final FretboardPosition startPosition) {
		super(intervalContainer);
		if (startPosition == null) {
			throw new IllegalArgumentException();
		}
		this.startPosition = startPosition;
		determineFretboardPositions();
	}

	public OctaveBlock(final OctaveBlock other) {
		this(other.getIntervalContainer(), other.startPosition);
	}

	/**
	 * Sets the start fretboard position of this octave block.
	 * 
	 * @param startPosition
	 *            the new start fretboard position, must not be null
	 */
	public void setStartFretboardPosition(final FretboardPosition startPosition) {
		if (startPosition == null) {
			throw new IllegalArgumentException();
		}
		this.startPosition = startPosition;
		determineFretboardPositions();
	}

	private void determineFretboardPositions() {

		fretboardPositions = new ArrayList<FretboardPosition>();
		fretboardPositions.add(startPosition);

		final List<OctaveBlockDraft> drafts = new ArrayList<OctaveBlockDraft>();
		drafts.add(new OctaveBlockDraft(startPosition));

		while (!drafts.get(0).isClosed()) {
			final OctaveBlockDraft firstDraft = drafts.get(0);
			final List<FretboardPosition> foundFBPs = firstDraft.findNextFretboardPositions();

			if (!foundFBPs.isEmpty()) {
				drafts.remove(firstDraft);
			} else {
				firstDraft.setClosed(true);
			}
			for (final FretboardPosition fbp : foundFBPs) {
				final OctaveBlockDraft newDraft = new OctaveBlockDraft(firstDraft);
				newDraft.addFretboardPosition(fbp);
				drafts.add(newDraft);
			}

			Collections.sort(drafts);
		}

		final OctaveBlockDraft firstDraft = drafts.get(0);
		fretboardPositions = new ArrayList<FretboardPosition>(firstDraft.getFretboardPositions());
		minStringNumber = firstDraft.getMinString();
		maxStringNumber = firstDraft.getMaxString();
		minFret = firstDraft.getMinFret();
		maxFret = firstDraft.getMaxFret();
		fingering = null;

		determineMinMaxFrets();
	}

	public FretboardPosition getStartFretboardPosition() {
		return startPosition;
	}

	@Override
	public List<FretboardPosition> getFretboardPositions() {
		if (fretboardPositions == null) {
			return new ArrayList<FretboardPosition>();
		}
		return fretboardPositions;
	}

	@Override
	public int getMinString() {
		return minStringNumber;
	}

	@Override
	public int getMaxString() {
		return maxStringNumber;
	}

	@Override
	public int getMinFret(final boolean useCapoFret) {
		return minFret;
	}

	@Override
	public int getMaxFret() {
		return maxFret;
	}

	@Override
	public int getFretSpan() {
		return maxFret - minFret + 1;
	}

	@Override
	public boolean hasEmptyStringNotes() {
		return false;
	}

	/**
	 * Returns true if this octave block covers a complete octave, or false
	 * otherwise.
	 * 
	 * @return true if this octave block covers a complete octave, or false
	 *         otherwise
	 */
	public boolean isComplete() {
		final FretboardPosition startFBP = getStartFretboardPosition();
		final List<FretboardPosition> fbps = getFretboardPositions();
		final Note startNote = getCurrentInstrument().getNote(startFBP);
		for (final FretboardPosition current : fbps) {
			if (current.equals(startFBP)) {
				continue;
			}
			final Note currentNote = getCurrentInstrument().getNote(current);
			if (currentNote.getValue() == startNote.getValue()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean hasToneOnString(final int stringNumber) {
		return getMinFret(stringNumber) != -1 || getMaxFret(stringNumber) != -1;
	}

	/**
	 * Helper class used by the algorithm to determine the octave block.
	 */
	private class OctaveBlockDraft implements Comparable<OctaveBlockDraft> {

		private static final int MAX_RANGE = 6;

		private final List<FretboardPosition> foundFBP;
		private List<Note> openNotes;

		private int minFret;
		private int maxFret;
		private int minString;
		private int maxString;
		private boolean closed;
		private boolean ascendingNotes;

		public OctaveBlockDraft(final FretboardPosition startPosition) {
			foundFBP = new ArrayList<FretboardPosition>();
			foundFBP.add(startPosition);
			minFret = startPosition.getFret();
			maxFret = startPosition.getFret();
			minString = startPosition.getString();
			maxString = startPosition.getString();
			determineOpenNotes(startPosition);
			closed = openNotes.isEmpty();
		}

		/**
		 * Creates an octave block draft out of another one.
		 * 
		 * @param other
		 *            the other octave block draft, must not be null, must be
		 *            initialized
		 */
		public OctaveBlockDraft(final OctaveBlockDraft other) {
			if (other == null || other.foundFBP == null || other.openNotes == null) {
				throw new IllegalArgumentException();
			}

			foundFBP = new ArrayList<FretboardPosition>(other.foundFBP);
			openNotes = new ArrayList<Note>(other.openNotes);
			minFret = other.minFret;
			maxFret = other.maxFret;
			minString = other.minString;
			maxString = other.maxString;
			closed = other.closed;
			ascendingNotes = other.ascendingNotes;
		}

		/**
		 * Initializes the open notes list.
		 */
		private void determineOpenNotes(final FretboardPosition startPosition) {
			final Note startNote = getCurrentInstrument().getNote(startPosition);
			openNotes = new ArrayList<Note>();
			final Set<Integer> noteValues = getIntervalContainer().getNoteValues();
			boolean done = false;

			ascendingNotes = true;
			for (int s = startPosition.getString(); s > 0; s--) {
				final Note note = getCurrentInstrument().getNote(new FretboardPosition(s, 0));
				final Note note2 = getCurrentInstrument().getNote(new FretboardPosition(s - 1, 0));
				if (note.compareTo(note2) < 0) {
					break;
				}
				if (note.compareTo(note2) > 0) {
					ascendingNotes = false;
					break;
				}
			}

			if (ascendingNotes) {
				for (int l = startNote.getLevel(); l <= startNote.getLevel() + 1; l++) {
					for (int v = 0; v <= Constants.MAX_NOTES_VALUE; v++) {
						if (noteValues.contains(v)) {

							if (l == Constants.MAX_NOTES_LEVEL && v > 0) {
								done = true;
								break;
							}
							if (l == startNote.getLevel() && v <= startNote.getValue()) {
								continue;
							}

							if (l == startNote.getLevel() + 1 && v == startNote.getValue()) {
								openNotes.add(Factory.getInstance().getNote(v, l));
								done = true;
								break;
							}

							openNotes.add(Factory.getInstance().getNote(v, l));
						}
					}
					if (done) {
						break;
					}
				}
			}
			// descending notes
			else {
				for (int l = startNote.getLevel(); l >= startNote.getLevel() - 1; l--) {
					for (int v = Constants.MAX_NOTES_VALUE; v >= 0; v--) {
						if (noteValues.contains(v)) {

							if (l < 0) {
								done = true;
								break;
							}
							if (l == startNote.getLevel() && v >= startNote.getValue()) {
								continue;
							}

							if (l == startNote.getLevel() - 1 && v == startNote.getValue()) {
								openNotes.add(Factory.getInstance().getNote(v, l));
								done = true;
								break;
							}

							openNotes.add(Factory.getInstance().getNote(v, l));
						}
					}
					if (done) {
						break;
					}
				}
			}
		}

		/**
		 * Find all possible next fretboard positions.
		 * 
		 * <p>
		 * Note this method returns just an empty list if this octave block
		 * draft is already closed.
		 * </p>
		 * 
		 * @return all possible next fretboard positions
		 */
		public List<FretboardPosition> findNextFretboardPositions() {
			if (isClosed()) {
				return new ArrayList<FretboardPosition>();
			}

			final List<FretboardPosition> results = new ArrayList<FretboardPosition>();

			final FretboardPosition lastFBP = foundFBP.get(foundFBP.size() - 1);
			final Note openNote = openNotes.get(0);
			final int offset = MAX_RANGE - getFretRange();
			final int minF = Math.max(getCurrentInstrument().getMinFret() + 1, minFret - offset);
			final int maxF = Math.min(getCurrentInstrument().getFretCount(), maxFret + offset);

			for (int s = lastFBP.getString(); s >= 0; s--) {
				for (int f = minF; f <= maxF; f++) {
					if (ascendingNotes && s == lastFBP.getString() && f <= lastFBP.getFret()) {
						continue;
					}
					if (!ascendingNotes && s == lastFBP.getString() && f >= lastFBP.getFret()) {
						continue;
					}
					final FretboardPosition currentFBP = new FretboardPosition(s, f);
					if (getCurrentInstrument().getNote(currentFBP).equals(openNote)) {
						results.add(currentFBP);
					}
				}
			}

			return results;
		}

		/**
		 * Adds the next fretboard position to this octave block draft.
		 * 
		 * <p>
		 * Note this method has no effect if this octave block draft is closed.
		 * </p>
		 * 
		 * @param fbp
		 *            the next fretboard position, must not be null, must
		 *            correspond to the first open note
		 */
		public void addFretboardPosition(final FretboardPosition fbp) {
			if (closed) {
				return;
			}

			if (fbp == null || !getCurrentInstrument().getNote(fbp).equals(openNotes.get(0))) {
				throw new IllegalArgumentException();
			}

			if (Math.max(maxFret, fbp.getFret()) - Math.min(minFret, fbp.getFret()) > MAX_RANGE) {
				throw new IllegalArgumentException();
			}

			minFret = Math.min(minFret, fbp.getFret());
			maxFret = Math.max(maxFret, fbp.getFret());
			minString = Math.min(minString, fbp.getString());
			maxString = Math.max(maxString, fbp.getString());

			foundFBP.add(fbp);
			openNotes.remove(0);

			if (openNotes.isEmpty()) {
				closed = true;
			}
		}

		/**
		 * Returns the found fretboard positions of this octave block draft.
		 * 
		 * @return the found fretboard positions of this octave block draft
		 */
		public List<FretboardPosition> getFretboardPositions() {
			return foundFBP;
		}

		/**
		 * Returns the fret range of this octave block draft.
		 * 
		 * @return the fret range of this octave block draft
		 */
		public int getFretRange() {
			return maxFret - minFret + 1;
		}

		/**
		 * Returns the string range of this octave block draft.
		 * 
		 * @return the string range of this octave block draft
		 */
		public int getStringRange() {
			return maxString - minString + 1;
		}

		/**
		 * Returns the minimal string of this octave block draft.
		 * 
		 * @return the minimal string of this octave block draft
		 */
		public int getMinString() {
			return minString + 1;
		}

		/**
		 * Returns the maximum string of this octave block draft.
		 * 
		 * @return the maximum string of this octave block draft
		 */
		public int getMaxString() {
			return maxString + 1;
		}

		/**
		 * Returns the minimal fret of this octave block draft.
		 * 
		 * @return the minimal fret of this octave block draft
		 */
		public int getMinFret() {
			return minFret;
		}

		/**
		 * Returns the maximum fret of this octave block draft.
		 * 
		 * @return the maximum fret of this octave block draft
		 */
		public int getMaxFret() {
			return maxFret;
		}

		/**
		 * Sets the closed state of this octave block draft.
		 * 
		 * @param closed
		 *            the new closed state
		 */
		public void setClosed(final boolean closed) {
			this.closed = closed;
		}

		/**
		 * Returns the closed state of this octave block draft.
		 * 
		 * @return the closed state of this octave block draft
		 */
		public boolean isClosed() {
			return closed;
		}

		@Override
		public int compareTo(final OctaveBlockDraft other) {
			// (1) compare fret range
			int compare = Integer.valueOf(this.getFretRange()).compareTo(Integer.valueOf(other.getFretRange()));
			if (compare != 0) {
				return compare;
			}
			// (2) compare string range
			compare = Integer.valueOf(this.getStringRange()).compareTo(Integer.valueOf(other.getStringRange()));
			if (compare != 0) {
				return compare;
			}
			// (3) compare closed
			compare = Boolean.valueOf(this.isClosed()).compareTo(Boolean.valueOf(other.isClosed()));
			if (compare != 0) {
				return compare;
			}
			// (4) compare number of fretboard positions
			return Integer.valueOf(this.getFretboardPositions().size()).compareTo(
					Integer.valueOf(other.getFretboardPositions().size()));
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + (ascendingNotes ? 1231 : 1237);
			result = prime * result + (closed ? 1231 : 1237);
			result = prime * result + (foundFBP == null ? 0 : foundFBP.hashCode());
			result = prime * result + maxFret;
			result = prime * result + maxString;
			result = prime * result + minFret;
			result = prime * result + minString;
			result = prime * result + (openNotes == null ? 0 : openNotes.hashCode());
			return result;
		}

		@Override
		public boolean equals(final Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final OctaveBlockDraft other = (OctaveBlockDraft) obj;
			if (!getOuterType().equals(other.getOuterType())) {
				return false;
			}
			if (ascendingNotes != other.ascendingNotes) {
				return false;
			}
			if (closed != other.closed) {
				return false;
			}
			if (foundFBP == null) {
				if (other.foundFBP != null) {
					return false;
				}
			} else if (!foundFBP.equals(other.foundFBP)) {
				return false;
			}
			if (maxFret != other.maxFret) {
				return false;
			}
			if (maxString != other.maxString) {
				return false;
			}
			if (minFret != other.minFret) {
				return false;
			}
			if (minString != other.minString) {
				return false;
			}
			if (openNotes == null) {
				if (other.openNotes != null) {
					return false;
				}
			} else if (!openNotes.equals(other.openNotes)) {
				return false;
			}
			return true;
		}

		private OctaveBlock getOuterType() {
			return OctaveBlock.this;
		}
	}

	@Override
	public Byte getFingerNumber(final FretboardPosition fretboardPosition) {
		if (fingering == null) {
			determineFingering();
		}
		return fingering.get(fretboardPosition);
	}

	private void determineFingering() {
		fingering = new HashMap<FretboardPosition, Byte>();
		if (fretboardPositions == null) {
			return;
		}

		// (1) gather info
		Collections.sort(fretboardPositions, new Comparator<FretboardPosition>() {
			@Override
			public int compare(final FretboardPosition o1, final FretboardPosition o2) {
				final int compare = -1 * Integer.valueOf(o1.getString()).compareTo(Integer.valueOf(o2.getString()));
				return compare == 0 ? Integer.valueOf(o1.getFret()).compareTo(Integer.valueOf(o2.getFret())) : compare;
			}
		});
		final Map<Integer, List<FretboardPosition>> stringFBPMap = new HashMap<Integer, List<FretboardPosition>>();
		for (final FretboardPosition fbp : fretboardPositions) {
			List<FretboardPosition> fbps = stringFBPMap.get(fbp.getString());
			if (fbps == null) {
				fbps = new ArrayList<FretboardPosition>();
				stringFBPMap.put(fbp.getString(), fbps);
			}
			fbps.add(fbp);
		}
		final Map<Integer, Integer> minFrets = new HashMap<Integer, Integer>();
		final Map<Integer, Integer> fretRanges = new HashMap<Integer, Integer>();

		for (final Entry<Integer, List<FretboardPosition>> entry : stringFBPMap.entrySet()) {
			final Integer currentString = entry.getKey();
			final List<FretboardPosition> fbps = stringFBPMap.get(currentString);
			final FretboardPosition first = fbps != null && fbps.size() > 0 ? fbps.get(0) : null;
			final FretboardPosition last = fbps != null && fbps.size() > 0 ? fbps.get(fbps.size() - 1) : null;
			minFrets.put(currentString, first != null ? first.getFret() : null);
			fretRanges.put(currentString, first != null && last != null ? last.getFret() - first.getFret() + 1 : null);
		}

		// (2) determine the fingering
		final Iterator<FretboardPosition> iter = fretboardPositions.iterator();
		FretboardPosition lastFBP = null;
		FretboardPosition currentFBP = iter.next();
		FretboardPosition nextFBP = null;

		while (iter.hasNext()) {
			nextFBP = iter.next();
			determineCurrentFingering(lastFBP, currentFBP, nextFBP, stringFBPMap, minFrets, fretRanges);
			lastFBP = currentFBP;
			currentFBP = nextFBP;
		}
		determineCurrentFingering(lastFBP, currentFBP, null, stringFBPMap, minFrets, fretRanges);

		// (3) finally finetune the fingering
		finetune();
	}

	private void finetune() {
		// first finetune a possible first fingernumber
		if (fretboardPositions.size() >= 2) {
			final FretboardPosition firstFBP = fretboardPositions.get(0);
			final FretboardPosition secondFBP = fretboardPositions.get(1);
			if (firstFBP.getString() > secondFBP.getString() && firstFBP.getFret() > secondFBP.getFret()) {
				final byte firstFingerNumber = fingering.get(firstFBP);
				final byte secondFingerNumber = fingering.get(secondFBP);
				final int distance1 = firstFBP.getFret() - secondFBP.getFret();
				final int distance2 = firstFingerNumber - secondFingerNumber;
				if (firstFingerNumber != 4 && distance1 > distance2) {
					final int newFirstFingerNumber = Math.min(secondFingerNumber + distance1, 4);
					fingering.put(firstFBP, (byte) newFirstFingerNumber);
				}
			}
		}

		// create reverse fretboard positions
		final List<FretboardPosition> reverseFretboardPositions = new ArrayList<FretboardPosition>(fretboardPositions);
		Collections.reverse(reverseFretboardPositions);
		final List<FretboardPosition> tempFBPs = new ArrayList<FretboardPosition>(reverseFretboardPositions);

		// store old fingering for the case that an optinal problem cause a
		// critical problem
		final Map<FretboardPosition, Byte> tempFingering = new HashMap<FretboardPosition, Byte>(fingering);
		final boolean hasOptionalProblem = hasProblem(false);
		final boolean hasCriticalProblem = hasProblem(true);

		FretboardPosition lastFBP = null;
		FretboardPosition nextFBPofInterest = null;
		for (final FretboardPosition currentFBP : reverseFretboardPositions) {
			if (nextFBPofInterest == null || currentFBP.equals(nextFBPofInterest)) {
				nextFBPofInterest = null;
				if (hasOptionalProblem(currentFBP, lastFBP) || hasCriticalProblem(currentFBP, lastFBP)) {
					int nextString = -1;
					for (final Iterator<FretboardPosition> iterator = tempFBPs.listIterator(tempFBPs
							.indexOf(currentFBP)); iterator.hasNext();) {
						final FretboardPosition fbp = iterator.next();
						if (fbp.getFret() > currentFBP.getFret()) {
							break;
						}
						if (nextString == -1 && fbp.getString() != currentFBP.getString()) {
							nextString = fbp.getString();
						}
						if (nextString != -1 && fbp.getString() != nextString) {
							break;
						}
						final Byte fingerNumber = fingering.get(fbp);
						if (fingerNumber == null) {
							break;
						}
						Byte newFingerNumber = (byte) (fingerNumber - 1);
						if (newFingerNumber < 1) {
							newFingerNumber = 1;
						}
						fingering.put(fbp, newFingerNumber);
						nextFBPofInterest = fbp;
					}
				}
			}
			lastFBP = currentFBP;
		}

		// reset the fingering if the finetuning was not successful
		if (hasOptionalProblem && !hasCriticalProblem && hasProblem(true)) {
			fingering = new HashMap<FretboardPosition, Byte>(tempFingering);
		}
	}

	private boolean hasProblem(final boolean critical) {
		final List<FretboardPosition> reverseFretboardPositions = new ArrayList<FretboardPosition>(fretboardPositions);
		Collections.reverse(reverseFretboardPositions);
		FretboardPosition lastFBP = null;
		for (final FretboardPosition currentFBP : reverseFretboardPositions) {
			if (critical) {
				if (hasCriticalProblem(currentFBP, lastFBP)) {
					return true;
				}
			} else {
				if (hasOptionalProblem(currentFBP, lastFBP)) {
					return true;
				}
			}
			lastFBP = currentFBP;
		}
		return false;
	}

	private boolean hasCriticalProblem(final FretboardPosition currentFBP, final FretboardPosition lastFBP) {
		if (lastFBP == null) {
			return false;
		}
		final Byte fingerNumber = fingering.get(currentFBP);
		final Byte lastFingerNumber = fingering.get(lastFBP);
		if (fingerNumber == null || lastFingerNumber == null) {
			return false;
		}
		return fingerNumber.equals(lastFingerNumber) && currentFBP.getString() != lastFBP.getString();
	}

	private boolean hasOptionalProblem(final FretboardPosition currentFBP, final FretboardPosition lastFBP) {
		if (lastFBP == null) {
			return false;
		}
		final Byte fingerNumber = fingering.get(currentFBP);
		final Byte lastFingerNumber = fingering.get(lastFBP);
		if (fingerNumber == null || lastFingerNumber == null) {
			return false;
		}
		return fingerNumber.equals(lastFingerNumber) && currentFBP.getString() == lastFBP.getString()
				&& fingerNumber != 1 || fingerNumber == 3 && lastFingerNumber == 4
				&& lastFBP.getFret() - currentFBP.getFret() > 1;
	}

	private void determineCurrentFingering(final FretboardPosition lastFBP, final FretboardPosition currentFBP,
			final FretboardPosition nextFBP, final Map<Integer, List<FretboardPosition>> stringFBPMap,
			final Map<Integer, Integer> minFrets, final Map<Integer, Integer> fretRanges) {

		// (1) gather info
		final Byte lastFingerNumber = lastFBP != null ? fingering.get(lastFBP) : null;
		final List<FretboardPosition> currentFBPs = stringFBPMap.get(currentFBP.getString());
		final int remainingFBPsInCurrentRow = currentFBPs.size() - currentFBPs.indexOf(currentFBP) - 1;
		final List<FretboardPosition> lastFBPs = lastFBP != null ? stringFBPMap.get(lastFBP.getString())
				: new ArrayList<FretboardPosition>();
		final List<Integer> currentFrets = new ArrayList<Integer>();
		final List<Integer> lastFrets = new ArrayList<Integer>();
		for (final FretboardPosition fretboardPosition : currentFBPs) {
			currentFrets.add(fretboardPosition.getFret());
		}
		for (final FretboardPosition fretboardPosition : lastFBPs) {
			lastFrets.add(fretboardPosition.getFret());
		}
		final int blockRange = maxFret - minFret + 1;
		final int currentMinFret = minFrets.get(currentFBP.getString());
		final int currentFretRange = fretRanges.get(currentFBP.getString());
		final int currentMaxFret = currentMinFret + currentFretRange - 1;
		final List<Integer> usedStrings = new ArrayList<Integer>(stringFBPMap.keySet());
		Collections.sort(usedStrings);
		final int indexOfCurrentString = usedStrings.indexOf(currentFBP.getString());
		final int lastString = indexOfCurrentString != usedStrings.size() - 1 ? usedStrings
				.get(indexOfCurrentString + 1) : -1;
		final int nextString = indexOfCurrentString != 0 ? usedStrings.get(indexOfCurrentString - 1) : -1;
		final int lastMinFret = lastString != -1 ? minFrets.get(lastString) : -1;
		final int nextMinFret = nextString != -1 ? minFrets.get(nextString) : -1;

		// (2) compute finger number
		Byte fingerNumber = null;

		// (2.1) adopt finger numbers of last row (if possible)
		if (lastFBP != null && lastFBP.getString() != currentFBP.getString() && lastFrets.containsAll(currentFrets)
				&& lastFBP.getFret() != currentFBP.getFret()) {
			fingerNumber = fingering.get(new FretboardPosition(lastFBP.getString(), currentFBP.getFret()));
		}

		// (2.2) avoid jumps with one finger (if necessary)
		else if (lastFBP != null && lastFBP.getFret() == currentFBP.getFret()) {
			fingerNumber = (byte) (lastFingerNumber + 1);
			if (fingerNumber > 4) {
				fingerNumber = (byte) 1;
			}

			if (nextFBP != null && nextFBP.getFret() < currentFBP.getFret()) {
				final int distance = (byte) (currentFBP.getFret() - nextFBP.getFret());
				if (fingerNumber - distance <= 0) {
					fingerNumber = (byte) (fingerNumber + distance);
					if (fingerNumber > lastFingerNumber) {
						fingerNumber = 4;
					}
				}
			}
		}

		// (2.3) normal approach
		else {
			if (lastFBP != null && lastFBP.getString() == currentFBP.getString()) {
				if (lastFingerNumber == 1
						&& currentFretRange > 4
						&& (currentMaxFret - currentFBP.getFret() > 2 || currentMaxFret - currentFBP.getFret() == 2
								&& currentFBP.getFret() - lastFBP.getFret() >= 2)) {
					fingerNumber = 1;
				} else {
					fingerNumber = (byte) (lastFingerNumber + currentFBP.getFret() - lastFBP.getFret());
					if (fingerNumber > 4) {
						fingerNumber = 4;
					}
					if (fingerNumber + remainingFBPsInCurrentRow > 4) {
						fingerNumber = (byte) (fingerNumber - remainingFBPsInCurrentRow);
					}
					if (nextFBP != null && nextFBP.getString() != currentFBP.getString()
							&& nextFBP.getFret() < currentFBP.getFret()) {
						final int distanceToNext = currentFBP.getFret() - nextFBP.getFret();
						if (distanceToNext >= fingerNumber) {
							fingerNumber = (byte) distanceToNext;
							if (fingerNumber > 4) {
								fingerNumber = 4;
							}
						}
					}
				}
			} else {
				Integer theMinFret = minFret;
				if (blockRange > 4) {
					theMinFret = currentMinFret;
					if (currentFretRange < 4) {
						if (lastString == -1) {
							theMinFret = currentMaxFret - Math.max(currentFretRange, 4) + 1;
							if (theMinFret < minFret) {
								theMinFret = minFret;
							}

						} else if (nextString != -1 && nextMinFret < currentMinFret) {
							theMinFret = nextMinFret;
						} else if (lastString != -1 && lastMinFret < currentMinFret) {
							final Byte fingeringOfInerest = fingering.get(new FretboardPosition(lastString,
									currentMinFret));
							theMinFret = fingeringOfInerest != null && fingeringOfInerest.byteValue() == 1 ? currentMinFret
									: lastMinFret;
						}
					}
				}
				if (theMinFret == null) {
					theMinFret = minFret;
				}
				fingerNumber = (byte) (currentFBP.getFret() - theMinFret + 1);
				if (lastFingerNumber != null && currentFBP.getFret() < lastFBP.getFret()
						&& fingerNumber > lastFingerNumber) {
					fingerNumber = (byte) (lastFingerNumber + currentFBP.getFret() - lastFBP.getFret());
				}
				if (fingerNumber > 4) {
					fingerNumber = 4;
				}

				if (lastFBP != null && lastFBP.getFret() > currentFBP.getFret()) {
					final int distanceToLast = lastFBP.getFret() - currentFBP.getFret();
					final int distanceToLast2 = lastFingerNumber - fingerNumber;
					if (distanceToLast > distanceToLast2) {
						fingerNumber = (byte) (4 - distanceToLast);
						if (fingerNumber <= 0) {
							fingerNumber = (byte) 1;
						}
					}
				}
			}
		}
		fingering.put(currentFBP, fingerNumber);
	}

	@Override
	public String getBeautifiedName(final String notesMode) {
		final StringBuffer buf = new StringBuffer(getIntervalContainer().getBeautifiedName(notesMode));

		buf.append(" ("); //$NON-NLS-1$
		buf.append(ModelMessages.OctaveBlock_fret);
		buf.append(": "); //$NON-NLS-1$
		buf.append(getStartFretboardPosition().getFret());
		buf.append(", "); //$NON-NLS-1$
		buf.append(ModelMessages.OctaveBlock_string);
		buf.append(": "); //$NON-NLS-1$
		buf.append(getStartFretboardPosition().getString() + 1);
		buf.append(")"); //$NON-NLS-1$

		return buf.toString();
	}
}
