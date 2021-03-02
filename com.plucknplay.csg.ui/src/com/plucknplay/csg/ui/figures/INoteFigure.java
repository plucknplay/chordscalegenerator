/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.figures;

import com.plucknplay.csg.core.model.Note;

public interface INoteFigure {

	/**
	 * Returns the corresponding note of this figure.
	 * 
	 * @return the corresponding note of this figure
	 */
	Note getNote();
}
