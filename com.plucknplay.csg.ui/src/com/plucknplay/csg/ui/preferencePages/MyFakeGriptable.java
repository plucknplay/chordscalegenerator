/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.preferencePages;

import com.plucknplay.csg.core.model.Chord;
import com.plucknplay.csg.core.model.Griptable;

class MyFakeGriptable extends Griptable {

	private final boolean first;
	private final boolean third;
	private final boolean fifth;

	public MyFakeGriptable(final Chord chord, final boolean first, final boolean third, final boolean fifth) {
		super(chord);
		this.first = first;
		this.third = third;
		this.fifth = fifth;
	}

	@Override
	public boolean isWithout1st() {
		return first;
	}

	@Override
	public boolean isWithout3rd() {
		return third;
	}

	@Override
	public boolean isWithout5th() {
		return fifth;
	}
}
