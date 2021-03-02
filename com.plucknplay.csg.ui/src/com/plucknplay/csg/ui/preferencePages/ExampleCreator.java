/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.preferencePages;

import java.util.List;
import java.util.Random;

import com.plucknplay.csg.core.Constants;
import com.plucknplay.csg.core.model.Categorizable;
import com.plucknplay.csg.core.model.Chord;
import com.plucknplay.csg.core.model.Factory;
import com.plucknplay.csg.core.model.FretBlock;
import com.plucknplay.csg.core.model.FretboardPosition;
import com.plucknplay.csg.core.model.Griptable;
import com.plucknplay.csg.core.model.OctaveBlock;
import com.plucknplay.csg.core.model.Scale;
import com.plucknplay.csg.core.model.sets.ChordList;
import com.plucknplay.csg.core.model.sets.ScaleList;

public final class ExampleCreator {

	private ExampleCreator() {
	}

	static Griptable createRandomGriptable() {
		final Chord chord = createRandomChord();
		return new MyFakeGriptable(new Chord(chord), getRandomBoolean(), getRandomBoolean(), getRandomBoolean());
	}

	static Griptable createRandomGriptable(final boolean no1, final boolean no3, final boolean no5) {
		final Chord chord = createRandomChord();
		return new MyFakeGriptable(new Chord(chord), no1, no3, no5);
	}

	static Object createRandomBlock(final int code) {
		final Random r = new Random();
		final List<Categorizable> allElements = ScaleList.getInstance().getRootCategory().getAllElements();
		final Scale scale = (Scale) allElements.get((int) (Math.random() * allElements.size()));
		scale.setRootNote(Factory.getInstance().getNote(r.nextInt(Constants.MAX_NOTES_VALUE)));

		if (code == 0) {
			return new MyFakeScale(scale);
		} else if (code == 1) {
			final FretBlock fretBlock = new FretBlock(new MyFakeScale(scale), r.nextInt(2) + 3, false);
			fretBlock.setMinFret(r.nextInt(9) + 1);
			return fretBlock;
		}
		return new OctaveBlock(new MyFakeScale(scale), new FretboardPosition(r.nextInt(6), r.nextInt(8) + 1));
	}

	static Scale createRandomScale() {
		final Random r = new Random();
		final List<Categorizable> allElements = ScaleList.getInstance().getRootCategory().getAllElements();
		final Scale scale = (Scale) allElements.get((int) (Math.random() * allElements.size()));
		scale.setRootNote(Factory.getInstance().getNote(r.nextInt(Constants.MAX_NOTES_VALUE)));
		return scale;
	}

	private static Chord createRandomChord() {
		final Random r = new Random();
		final List<Categorizable> allElements = ChordList.getInstance().getRootCategory().getAllElements();
		final Chord chord = (Chord) allElements.get((int) (Math.random() * allElements.size()));
		chord.setRootNote(Factory.getInstance().getNote(r.nextInt(Constants.MAX_NOTES_VALUE)));
		return chord;
	}

	private static boolean getRandomBoolean() {
		return Math.random() * 100 < 50;
	}
}
