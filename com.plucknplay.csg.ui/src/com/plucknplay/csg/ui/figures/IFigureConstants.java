/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.figures;

import org.eclipse.swt.graphics.Color;

public interface IFigureConstants {

	/* --- colors --- */

	Color DARK_GREY = new Color(null, 140, 140, 140);
	Color GREY = new Color(null, 180, 180, 180);
	Color LIGHT_GREY = new Color(null, 220, 220, 220);
	Color LIGHT_BLUE = new Color(null, 207, 224, 239);
	Color DARK_BLUE = new Color(null, 61, 116, 182);
	Color GREEN = new Color(null, 208, 217, 108);
	Color RED = new Color(null, 240, 147, 123);
	Color YELLOW = new Color(null, 248, 221, 114);
	Color GREY_ON_RED = new Color(null, 120, 120, 120);
	Color GREY_ON_GREEN = new Color(null, 140, 140, 140);
	Color GREY_ON_YELLOW = new Color(null, 160, 160, 160);
	Color TOOLTIP_YELLOW = new Color(null, 255, 255, 225);
	Color TOOLTIP_YELLOW_DARK = new Color(null, 255, 255, 200);
	Color SOUND_COLOR = new Color(null, 208, 164, 204);
	Color[] FRETBOARD_COLORS = { new Color(null, 225, 212, 179), new Color(null, 223, 176, 68),
			new Color(null, 197, 143, 79), new Color(null, 159, 126, 75), new Color(null, 109, 51, 21),
			new Color(null, 82, 56, 19) };

	/* --- fretboard sizes --- */

	int FRET_HEIGHT = 30;
	int FRET_WIDTH = 60;

	int FRETBOARD_OFFSET_X = 30;
	int FRETBOARD_OFFSET_Y = 30;
	int FRETBOARD_OFFSET_Y2 = 35;

	int CAPO_WIDTH = 50;
	int CAPO_OFFSET_X = (FRET_WIDTH - CAPO_WIDTH) / 2;
	int CAPO_OFFSET_Y = -1 * (FRET_HEIGHT / 2);

	int NOTES_HEIGHT = 20;
	int TWO_NOTES_WIDTH = NOTES_HEIGHT * 2;

	int DOUBLED_STRINGS_DISTANCE = 4;
	int INTARSIA_RADIUS = 5;

	int MAX_FRETBOARD_NOTE_TEXT_HEIGHT = 15;
	int MAX_FRETBOARD_NOTE_TEXT_HEIGHT_WITHOUT_FRAME = 20;
	int MAX_FRETBOARD_NOTE_TEXT_WIDTH = 30;
	int MAX_FRETBOARD_FRETNUMBER_TEXT_WIDTH = 18;

	/* --- keyboard sizes --- */

	double KEYBOARD_MIN_SCALE_FACTOR = 0.5;

	int KEYBOARD_OFFSET_X = 15;
	int KEYBOARD_OFFSET_Y = 15;

	int WHITE_KEY_WIDTH = 30;
	int BLACK_KEY_WIDTH = 20;

	int WHITE_KEY_HEIGHT_SMALL = 120;
	int BLACK_KEY_HEIGHT_SMALL = 78;
	int WHITE_KEY_HEIGHT_MEDIUM = 160;
	int BLACK_KEY_HEIGHT_MEDIUM = 104;
	int WHITE_KEY_HEIGHT_LARGE = 200;
	int BLACK_KEY_HEIGHT_LARGE = 130;

	int KEYBOARD_NOTES_HEIGHT = 27;
	int KEYBOARD_TWO_NOTES_HEIGHT = 38;

	int KEYBOARD_NOTES_OFFSET_Y = 2;
	int KEYBOARD_NOTES_OFFSET_X = 2;

	int NUMBER_OF_WHITE_KEYS = 57;

	int MAX_KEYBOARD_NOTE_TEXT_WIDTH = 16;

	/* --- tab sizes --- */

	int TAB_OFFSET_X = 30;
	int TAB_OFFSET_Y = 30;

	int TAB_LINE_DISTANCE = 30;
	int MAX_TAB_TEXT_HEIGHT = 30;

	int TAB_IMAGE_HEIGHT = 300;
	int TAB_IMAGE_WIDTH = 120;

	String TAB_IMAGE = "icons/tab.png"; //$NON-NLS-1$

	/* --- box sizes --- */

	int BOX_POINT_WIDTH_SMALL = 16;
	int BOX_POINT_WIDTH_MEDIUM = 21;
	int BOX_POINT_WIDTH_LARGE = 26;

	int BOX_ASSIGNMENT_SIZE_SMALL = BOX_POINT_WIDTH_LARGE;
	int BOX_ASSIGNMENT_SIZE_BIG = 46;

	int BOX_ASSIGNMENT_MAX_TEXT_HEIGHT = 24;
	int BOX_ASSIGNMENT_MAX_TEXT_HEIGHT_WITHOUT_FRAME = 34;

	int BOX_VIEW_HORIZONTAL_INFO_WIDTH_SMALL = 30;
	int BOX_VIEW_HORIZONTAL_INFO_WIDTH_MEDIUM = 45;
	int BOX_VIEW_HORIZONTAL_INFO_WIDTH_BIG = 60;

	int BOX_VIEW_VERTICAL_INFO_HEIGHT_SMALL = 30;
	int BOX_VIEW_VERTICAL_INFO_HEIGHT_MEDIUM = 40;
	int BOX_VIEW_VERTICAL_INFO_HEIGHT_BIG = 40;

	/* --- notes view --- */

	int MIN_NUMBER_OF_SPACES_ABOVE_OR_BELOW = 3;
	int NUMBER_OF_SPACES_INSIDE_STAFF = 4;
	int NUMBER_OF_STAFF_LINES = 5;

	double NOTES_MIN_SCALE_FACTOR = 0.14;

	int NOTE_LINE_DISTANCE = 60;
	int NOTE_WIDTH = 100;
	int LEDGER_LINE_OFFSET = 30;

	int MIN_NOTE_POSITION_OFFSET = 5;

	int NOTES_OFFSET_X = 60;
	int NOTES_OFFSET_Y = 120;

	int CLEF_IMAGE_HEIGHT = 600;
	int CLEF_IMAGE_WIDTH = 180;

	int ACCIDENTAL_IMAGE_HEIGHT = 190;
	int ACCIDENTAL_IMAGE_WIDTH = 60;

	int NOTE_ACCIDENTAL_SPACING = 50;
	int ACCIDENTAL_ACCIDENTAL_SPACING = 5;
	int COMMON_NOTE_SPACING = 60;

	int NOTE_START_X = NOTES_OFFSET_X + CLEF_IMAGE_WIDTH + 2 * ACCIDENTAL_IMAGE_WIDTH;

	String CLEF_IMAGE_C_1 = "icons/C_1_Sopran.png"; //$NON-NLS-1$
	String CLEF_IMAGE_C_2 = "icons/C_2_Mezzosopran.png"; //$NON-NLS-1$
	String CLEF_IMAGE_C_3 = "icons/C_3_Alt.png"; //$NON-NLS-1$
	String CLEF_IMAGE_C_4 = "icons/C_4_Tenor.png"; //$NON-NLS-1$
	String CLEF_IMAGE_C_5 = "icons/C_5_Bariton.png"; //$NON-NLS-1$

	String CLEF_IMAGE_F_1 = "icons/F_1_1tiefer.png"; //$NON-NLS-1$
	String CLEF_IMAGE_F_2 = "icons/F_2_Standard.png"; //$NON-NLS-1$
	String CLEF_IMAGE_F_3 = "icons/F_3_Chiavette.png"; //$NON-NLS-1$

	String CLEF_IMAGE_G_1 = "icons/G_1_1tiefer.png"; //$NON-NLS-1$
	String CLEF_IMAGE_G_2 = "icons/G_2_Standard.png"; //$NON-NLS-1$
	String CLEF_IMAGE_G_3 = "icons/G_3_Chiavette.png"; //$NON-NLS-1$
	String CLEF_IMAGE_G_4 = "icons/G_4_1hoeher.png"; //$NON-NLS-1$
	String CLEF_IMAGE_G_5 = "icons/G_5_2hoeher.png"; //$NON-NLS-1$

	String SHARP_SIGN_IMAGE = "icons/sharp_sign.png"; //$NON-NLS-1$
	String FLAT_SIGN_IMAGE = "icons/flat_sign.png"; //$NON-NLS-1$
	String SHARP_SIGN_IMAGE_BLUE = "icons/sharp_sign_blue.png"; //$NON-NLS-1$
	String FLAT_SIGN_IMAGE_BLUE = "icons/flat_sign_blue.png"; //$NON-NLS-1$
	String OPEN_NOTE_IMAGE = "icons/note.png"; //$NON-NLS-1$
	String OPEN_NOTE_IMAGE_BLUE = "icons/note_blue.png"; //$NON-NLS-1$
	String CLOSED_NOTE_IMAGE = "icons/note_closed.png"; //$NON-NLS-1$
	String CLOSED_NOTE_IMAGE_BLUE = "icons/note_closed_blue.png"; //$NON-NLS-1$
}
