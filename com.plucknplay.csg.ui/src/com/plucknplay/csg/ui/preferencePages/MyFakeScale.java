/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.preferencePages;

import com.plucknplay.csg.core.model.Note;
import com.plucknplay.csg.core.model.Scale;

class MyFakeScale extends Scale {

	private static final long serialVersionUID = 1341785360890090573L;

	public MyFakeScale(final Scale scale) {
		super(scale);
	}

	@Override
	public Note getRootNote() {
		return rootNote;
	}
}
