/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui;

/**
 * This class stores all necessary Constants of the project.
 */
public interface UIConstants {

	String[] LEVELS = { ConstantMessages.Constants_level_easy, ConstantMessages.Constants_level_medium,
			ConstantMessages.Constants_level_hard, ConstantMessages.Constants_level_very_hard,
			ConstantMessages.Constants_level_hell };

	String[] ROMAN_NUMERALS = { "0", "I", "II", "III", "IV", "V", "VI", "VII", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
			"VIII", "IX", "X", "XI", "XII", "XIII", "XIV", "XV", "XVI", "XVII", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$
			"XVIII", "XIX", "XX", "XXI", "XXII", "XXIII", "XXIV" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$

	/* --- min, maxs & defaults --- */

	int SUPPRESS_NOTIFICATION_NUMBER = 10;

	int MIN_GRIP_RANGE_IN_MM = 0;
	int DEFAULT_GRIP_RANGE_IN_MM = 80;
	int MAX_GRIP_RANGE_IN_MM = 200;

	double MIN_GRIP_RANGE_IN_INCH = 0.0d;
	double DEFAULT_GRIP_RANGE_IN_INCH = 3.0d;
	double MAX_GRIP_RANGE_IN_INCH = 8.0d;

	double MIN_SCALE_LENGTH = 100.0d;
	double MAX_SCALE_LENGTH = 1500.0d;

	int MIN_ENTRIES_PER_PAGE = 10;
	int MAX_ENTRIES_PER_PAGE = 500;

	int MIN_MAX_RESULTS_NUMBER = 100;
	int MAX_MAX_RESULTS_NUMBER = 10000;

	/* --- preferences values --- */

	String AUTOMATIC_VIEW_ORIENTATION = "automatic.view.orientation"; //$NON-NLS-1$
	String HORIZONTAL_VIEW_ORIENTATION = "horizontal.view.orientation"; //$NON-NLS-1$
	String VERTICAL_VIEW_ORIENTATION = "vertical.view.orientation"; //$NON-NLS-1$

	String NUMERALS_MODE_ARABIC = "numerals.mode.arabic"; //$NON-NLS-1$
	String NUMERALS_MODE_ROMAN = "numerals.mode.roman"; //$NON-NLS-1$

	String INLAYS_SHAPE_CIRCLE = "inlays.mode.circle"; //$NON-NLS-1$
	String INLAYS_SHAPE_TRIANGLE = "inlays.mode.triangle"; //$NON-NLS-1$

	String MODE_POINTS = "mode.points"; //$NON-NLS-1$
	String MODE_FINGERING = "mode.fingering"; //$NON-NLS-1$
	String MODE_NOTES = "mode.notes"; //$NON-NLS-1$
	String MODE_INTERVALS = "mode.intervals"; //$NON-NLS-1$

	String BLOCK_MODE_FRETS = "scale.block.mode.frets"; //$NON-NLS-1$
	String BLOCK_MODE_FRETS_ADVANCED = "scale.block.mode.frets.advanced"; //$NON-NLS-1$
	String BLOCK_MODE_OCTAVE = "scale.block.mode.octave"; //$NON-NLS-1$

	String NO_PERSPECTIVES_BINDING = "no.perspectives.binding"; //$NON-NLS-1$

	String DISPLAY_AS_BLOCK = "display.as.block"; //$NON-NLS-1$
	String DISPLAY_AS_ASC_ARPEGGIO = "display.as.asc.arpeggio"; //$NON-NLS-1$
	String DISPLAY_AS_DESC_ARPEGGIO = "display.as.desc.arpeggio"; //$NON-NLS-1$

	String EXPORT_BOX = "export.box"; //$NON-NLS-1$
	String EXPORT_TAB = "export.tab"; //$NON-NLS-1$
	String EXPORT_NOTES = "export.notes"; //$NON-NLS-1$
	String EXPORT_NONE = "export.none"; //$NON-NLS-1$

	String CHECK_NEVER = "check.never"; //$NON-NLS-1$
	String CHECK_ALWAYS = "check.always"; //$NON-NLS-1$
	String CHECK_DAILY = "check.daily"; //$NON-NLS-1$
	String CHECK_WEEKLY = "check.weekly"; //$NON-NLS-1$
	String CHECK_MONTHLY = "check.monthly"; //$NON-NLS-1$

	String NO_BACKGROUND = "no.background"; //$NON-NLS-1$
	String BACKGROUND_COLOR = "background.color"; //$NON-NLS-1$
	String BACKGROUND_IMAGE = "background.image"; //$NON-NLS-1$

	String FILE_EXTENSION_BMP = ".bmp"; //$NON-NLS-1$
	String FILE_EXTENSION_JPG = ".jpg"; //$NON-NLS-1$
	String FILE_EXTENSION_PNG = ".png"; //$NON-NLS-1$

	String BLOCK_OVERLAY = "block.overlay"; //$NON-NLS-1$
	String BLOCK_OUTLINE = "block.outline"; //$NON-NLS-1$
	String BLOCK_NO_OVERLAY_FRAME = "block.no.overlay.frame"; //$NON-NLS-1$
}
