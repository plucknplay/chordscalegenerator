/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.core.internal.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.plucknplay.csg.core.model.FretboardPosition;
import com.plucknplay.csg.core.model.IFingering;

/**
 * This class represents a fingering. Thus it simply defines the used finger
 * numbers for specified fretboard positions.
 */
public class Fingering implements IFingering {

	/* constants for the level determination */

	private static final int[][] RANGE_FACTORS = { { 0, 6, 5, 2 }, { 0, 0, 6, 6 }, { 0, 0, 0, 6 } };
	private static final int[][] SECOND_HIGHER_FACTORS = { { 0, 1, 1, 1 }, { 0, 0, 2, 2 }, { 0, 0, 0, 3 } };
	private static final int[][] SECOND_LOWER_FACTORS = { { 0, 1, 1, 1 }, { 0, 0, 1, 1 }, { 0, 0, 0, 1 } };

	private Map<FretboardPosition, Byte> fingering;

	private boolean isBarrePossible;
	private boolean hasBarre;

	private int level;
	private int maxDistance;
	private int totalDistance;

	private boolean determineLevel;
	private boolean determineHasBarre;

	/**
	 * The default constructor.
	 */
	public Fingering() {
		isBarrePossible = false;
		determineLevel = true;
		determineHasBarre = true;
	}

	@Override
	public Byte getFingerNumber(final FretboardPosition fretboardPosition) {
		if (fingering == null) {
			return null;
		}
		return fingering.get(fretboardPosition);
	}

	@Override
	public void setFingerNumber(final FretboardPosition fretboardPosition, final byte fingerNumber) {
		if (fretboardPosition == null || fingerNumber < 1 || fingerNumber > 4) {
			throw new IllegalArgumentException();
		}

		if (fingering == null) {
			fingering = new HashMap<FretboardPosition, Byte>();
		}
		fingering.put(fretboardPosition, fingerNumber);

		determineLevel = true;
		determineHasBarre = true;
	}

	@Override
	public List<FretboardPosition> getFretboardPositions(final byte fingerNumber) {
		if (fingerNumber < 1 || fingerNumber > 4) {
			throw new IllegalArgumentException();
		}

		final List<FretboardPosition> result = new ArrayList<FretboardPosition>();
		if (fingering == null) {
			return result;
		}
		for (final Entry<FretboardPosition, Byte> element : fingering.entrySet()) {
			if (element.getValue().byteValue() == fingerNumber) {
				result.add(element.getKey());
				if (fingerNumber > 1) {
					break;
				}
			}
		}
		return result;
	}

	@Override
	public void setIsBarrePossible(final boolean isBarrePossible) {
		this.isBarrePossible = isBarrePossible;
	}

	@Override
	public boolean isBarrePossible() {
		return isBarrePossible;
	}

	@Override
	public boolean hasBarre() {
		if (determineHasBarre) {
			determineHasBarre();
			determineHasBarre = false;
		}
		return hasBarre;
	}

	@Override
	public int getLevel() {
		if (determineLevel) {
			determineLevel();
			determineLevel = false;
		}
		return level;
	}

	/**
	 * Determines whether this fingering has a barre.
	 */
	private void determineHasBarre() {
		if (fingering == null || fingering.isEmpty()) {
			hasBarre = false;
			return;
		}
		hasBarre = getFretboardPositions((byte) 1).size() > 1;
	}

	/**
	 * Determines the level for this fingering.
	 */
	private void determineLevel() {

		// may happen if this fingering has no assignments (only empty strings)
		if (fingering == null) {
			level = 0;
			return;
		}

		// first of all get all necessary fretboard positions
		int maxString = 0;
		FretboardPosition fp1 = null, fp2 = null, fp3 = null, fp4 = null;
		for (final Entry<FretboardPosition, Byte> element : fingering.entrySet()) {
			switch (element.getValue().byteValue()) {
			case 2:
				fp2 = element.getKey();
				break;
			case 3:
				fp3 = element.getKey();
				break;
			case 4:
				fp4 = element.getKey();
				break;
			case 1:
				final FretboardPosition fp = element.getKey();
				if (fp.getString() >= maxString) {
					maxString = fp.getString();
					fp1 = fp;
				}
				break;
			default:
				break;
			}
		}

		// determine distance values between each single finger combination
		final int distance12 = fp1 != null && fp2 != null ? getWidth(fp1, fp2, 1, 2) + getHeight(fp1, fp2, 1, 2) : 0;
		final int distance23 = fp2 != null && fp3 != null ? getWidth(fp2, fp3, 2, 3) + getHeight(fp2, fp3, 2, 3) : 0;
		final int distance34 = fp3 != null && fp4 != null ? getWidth(fp3, fp4, 3, 4) + getHeight(fp3, fp4, 3, 4) : 0;
		final int distance13 = fp1 != null && fp2 == null && fp3 != null ? getWidth(fp1, fp3, 1, 3)
				+ getHeight(fp1, fp3, 1, 3) : 0;
		final int distance24 = fp2 != null && fp3 == null && fp4 != null ? getWidth(fp2, fp4, 2, 4)
				+ getHeight(fp2, fp4, 2, 4) : 0;
		final int distance14 = fp1 != null && fp2 == null && fp3 == null && fp4 != null ? getWidth(fp1, fp4, 1, 4)
				+ getHeight(fp1, fp4, 1, 4) : 0;

		maxDistance = Math.max(distance12,
				Math.max(distance23, Math.max(distance34, Math.max(distance13, Math.max(distance24, distance14)))));
		totalDistance = distance12 + distance23 + distance34 + distance13 + distance24 + distance14;
		if (hasBarre()) {
			totalDistance += fp1.getString() + 1;
		}

		final int totalDistanceLevel = getTotalDistanceLevel(totalDistance);
		final int maxDistanceLevel = getMaxDistanceLevel(maxDistance);
		level = Math.max(totalDistanceLevel, maxDistanceLevel);
		if (totalDistanceLevel >= maxDistanceLevel + 2) {
			level--;
		}

		if (level > 3) {
			level = 3;
		}
		if (level == 0 && hasBarre()) {
			level = 1;
		}
	}

	private int getTotalDistanceLevel(final int totalDistance) {
		if (totalDistance < 6) {
			return 0;
		}
		if (totalDistance == 6) {
			return 1;
		}
		return totalDistance / 7;
	}

	private int getMaxDistanceLevel(final int maxDistance) {
		if (maxDistance <= 5) {
			return 0;
		}
		if (maxDistance == 6) {
			return 1;
		}
		if (maxDistance >= 7 && maxDistance <= 8) {
			return 2;
		}
		return 3;
	}

	/**
	 * Determines the distance value for the width of the given fretboard
	 * positions and corresponding finger numbers.
	 * 
	 * @param firstFretboardPosition
	 *            the first fretboard position, the fret position must be
	 *            smaller or equal to the the second fretboard position
	 * @param secondFretboardPosition
	 *            the second fretboard position, the fret position must be
	 *            bigger or equal to the the first fretboard position
	 * @param firstFingerNumber
	 *            the finger number of the first fretboard position, must be a
	 *            value between 1 and 4, must be a smaller value than the finger
	 *            number of the second fretboard position
	 * @param secondFingerNumber
	 *            the finger number of the second fretboard position, must be a
	 *            value between 1 and 4, must be a bigger value than the finger
	 *            number of the first fretboard position
	 * 
	 * @return the distance value for the width of the given fretboard positions
	 *         and corresponding finger numbers
	 */
	private int getWidth(final FretboardPosition firstFretboardPosition,
			final FretboardPosition secondFretboardPosition, final int firstFingerNumber, final int secondFingerNumber) {

		if (firstFretboardPosition.getFret() > secondFretboardPosition.getFret()
				|| firstFingerNumber >= secondFingerNumber || firstFingerNumber < 0 || firstFingerNumber > 4
				|| secondFingerNumber < 0 || secondFingerNumber > 4) {
			throw new IllegalArgumentException();
		}

		int normRange = secondFingerNumber - firstFingerNumber;
		final int fretRange = secondFretboardPosition.getFret() - firstFretboardPosition.getFret();
		if (fretRange == 0) {
			return 1;
		}
		if (firstFingerNumber == 1 && secondFingerNumber == 4) {
			normRange = 2; // there's one exception
		}
		final int factor = RANGE_FACTORS[firstFingerNumber - 1][secondFingerNumber - 1];
		final int rangeDifference = factor * Math.abs(normRange - fretRange);
		return rangeDifference;
	}

	/**
	 * Determines the distance value for the height of the given fretboard
	 * positions and corresponding finger numbers.
	 * 
	 * @param firstFretboardPosition
	 *            the first fretboard position, the fret position must be
	 *            smaller or equal to the the second fretboard position
	 * @param secondFretboardPosition
	 *            the second fretboard position, the fret position must be
	 *            bigger or equal to the the first fretboard position
	 * @param firstFingerNumber
	 *            the finger number of the first fretboard position, must be a
	 *            value between 1 and 4, must be a smaller value than the finger
	 *            number of the second fretboard position
	 * @param secondFingerNumber
	 *            the finger number of the second fretboard position, must be a
	 *            value between 1 and 4, must be a bigger value than the finger
	 *            number of the first fretboard position
	 * 
	 * @return the distance value for the height of the given fretboard
	 *         positions and corresponding finger numbers
	 */
	private int getHeight(final FretboardPosition firstFretboardPosition,
			final FretboardPosition secondFretboardPosition, final int firstFingerNumber, final int secondFingerNumber) {

		if (firstFretboardPosition.getFret() > secondFretboardPosition.getFret()
				|| firstFingerNumber >= secondFingerNumber || firstFingerNumber < 0 || firstFingerNumber > 4
				|| secondFingerNumber < 0 || secondFingerNumber > 4) {
			throw new IllegalArgumentException();
		}

		int factor = secondFretboardPosition.getString() > firstFretboardPosition.getString() ? SECOND_HIGHER_FACTORS[firstFingerNumber - 1][secondFingerNumber - 1]
				: SECOND_LOWER_FACTORS[firstFingerNumber - 1][secondFingerNumber - 1];
		if (firstFretboardPosition.getFret() == secondFretboardPosition.getFret()) {
			factor = 1;
		}
		return factor * Math.abs(firstFretboardPosition.getString() - secondFretboardPosition.getString());
	}

	/**
	 * Returns the maximum distance between one single pair of fingers.
	 * 
	 * @return the maximum distance between one single pair of fingers
	 */
	@Override
	public int getMaxSingleDistance() {
		if (determineLevel) {
			determineLevel();
			determineLevel = false;
		}
		return maxDistance;
	}

	/**
	 * Returns the total distance value which is necessary for the determination
	 * of the level. The higher the value the harder the fingering.
	 * 
	 * @return the total distance value which is necessary for the determination
	 *         of the level
	 */
	@Override
	public int getTotalDistance() {
		if (determineLevel) {
			determineLevel();
			determineLevel = false;
		}
		return totalDistance;
	}
}
