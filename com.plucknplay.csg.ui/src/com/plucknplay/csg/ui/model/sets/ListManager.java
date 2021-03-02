/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.model.sets;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.xml.sax.SAXException;

import com.plucknplay.csg.core.Constants;
import com.plucknplay.csg.core.model.Chord;
import com.plucknplay.csg.core.model.Factory;
import com.plucknplay.csg.core.model.Instrument;
import com.plucknplay.csg.core.model.Interval;
import com.plucknplay.csg.core.model.Scale;
import com.plucknplay.csg.core.model.Unit;
import com.plucknplay.csg.core.model.enums.Clef;
import com.plucknplay.csg.core.model.sets.Category;
import com.plucknplay.csg.core.model.sets.CategoryList;
import com.plucknplay.csg.core.model.sets.ChordList;
import com.plucknplay.csg.core.model.sets.InstrumentList;
import com.plucknplay.csg.core.model.sets.ScaleList;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.actions.general.XMLExportImport;
import com.plucknplay.csg.ui.activation.NlsUtil;
import com.plucknplay.csg.ui.listeners.CategoryViewFilter;
import com.plucknplay.csg.ui.util.LoginUtil;

public final class ListManager {

	private static final String TYPE_SCALES = "scales"; //$NON-NLS-1$
	private static final String TYPE_CHORDS = "chords"; //$NON-NLS-1$
	private static final String TYPE_INSTRUMENTS = "instruments"; //$NON-NLS-1$

	private ListManager() {
	}

	public static void loadLists() {
		loadInstruments();
		loadChords();
		loadScales();
	}

	private static void loadInstruments() {
		final XMLExportImport importer = new XMLExportImport();
		try {
			importer.setIgnoreCurrentInstrument(false);
			importer.loadFromXML(TYPE_INSTRUMENTS, getFile(TYPE_INSTRUMENTS, false), InstrumentList.getInstance()
					.getRootCategory());
		} catch (final ParserConfigurationException e) {
			createDefaultInstruments();
		} catch (final SAXException e) {
			createDefaultInstruments();
		} catch (final IOException e) {
			createDefaultInstruments();
		}
	}

	public static void loadChords() {

		ChordList.getInstance().clear();

		final XMLExportImport importer = new XMLExportImport();
		final boolean isActivated = LoginUtil.isActivated();
		if (isActivated) {
			try {
				importer.loadFromXML(TYPE_CHORDS, getFile(TYPE_CHORDS, false), ChordList.getInstance()
						.getRootCategory());
			} catch (final ParserConfigurationException e) {
				createDefaultChords(isActivated);
			} catch (final SAXException e) {
				createDefaultChords(isActivated);
			} catch (final IOException e) {
				createDefaultChords(isActivated);
			}
		} else {
			createDefaultChords(isActivated);
		}
	}

	public static void loadScales() {

		ScaleList.getInstance().clear();

		final XMLExportImport importer = new XMLExportImport();
		final boolean isActivated = LoginUtil.isActivated();
		if (isActivated) {
			try {
				importer.loadFromXML(TYPE_SCALES, getFile(TYPE_SCALES, false), ScaleList.getInstance()
						.getRootCategory());
			} catch (final ParserConfigurationException e) {
				createDefaultScales(isActivated);
			} catch (final SAXException e) {
				createDefaultScales(isActivated);
			} catch (final IOException e) {
				createDefaultScales(isActivated);
			}
		} else {
			createDefaultScales(isActivated);
		}
	}

	public static void storeLists() {

		final XMLExportImport exporter = new XMLExportImport();

		// store instruments
		try {
			exporter.setIgnoreCurrentInstrument(false);
			exporter.storeToXML(TYPE_INSTRUMENTS, getFile(TYPE_INSTRUMENTS, true), getAllElements(TYPE_INSTRUMENTS));
		} catch (final IOException e) {
		} catch (final ParserConfigurationException e) {
		}

		if (LoginUtil.isActivated()) {
			// store chords
			try {
				exporter.storeToXML(TYPE_CHORDS, getFile(TYPE_CHORDS, true), getAllElements(TYPE_CHORDS));
			} catch (final IOException e) {
			} catch (final ParserConfigurationException e) {
			}

			// store scales
			try {
				exporter.storeToXML(TYPE_SCALES, getFile(TYPE_SCALES, true), getAllElements(TYPE_SCALES));
			} catch (final IOException e) {
			} catch (final ParserConfigurationException e) {
			}
		}

		// finally delete all temp files
		final IPath tempPath = Platform.getLocation().append("temp"); //$NON-NLS-1$
		final File file = tempPath.toFile();
		if (file == null) {
			return;
		}
		final File[] listFiles = file.listFiles();
		if (listFiles == null) {
			return;
		}
		final File[] tempListFiles = new File[listFiles.length];
		System.arraycopy(listFiles, 0, tempListFiles, 0, listFiles.length);
		for (final File currentTempFile : tempListFiles) {
			currentTempFile.delete();
		}
	}

	/* --- helper methods --- */

	private static File getFile(final String type, final boolean createFile) {
		final String filename = TYPE_INSTRUMENTS.equals(type) ? "instruments.xml" : TYPE_CHORDS.equals(type) ? "chords.xml" : "scales.xml"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		final IPath path = Platform.getLocation().append("data/" + filename); //$NON-NLS-1$
		final File dataFile = path.toFile();

		if (createFile && !dataFile.exists()) {
			try {
				final File parentDirectory = dataFile.getParentFile();
				if (!parentDirectory.exists()) {
					parentDirectory.mkdirs();
				}
				if (!dataFile.exists()) {
					dataFile.createNewFile();
				}
			} catch (final IOException e) {
			}
		}

		return dataFile;
	}

	private static CategoryList getCategoryList(final String type) {
		return TYPE_INSTRUMENTS.equals(type) ? InstrumentList.getInstance() : TYPE_CHORDS.equals(type) ? ChordList
				.getInstance() : ScaleList.getInstance();
	}

	private static List<?> getAllElements(final String type) {
		final List<Object> result = new ArrayList<Object>();
		final CategoryList categoryList = getCategoryList(type);
		result.addAll(categoryList.getRootCategory().getCategories());
		result.addAll(categoryList.getRootCategory().getElements());
		return result;
	}

	/* --- default lists creation --- */

	private static void createDefaultInstruments() {

		final InstrumentList instrumentList = InstrumentList.getInstance();

		// (1) first try to load instruments from file inside plugin
		try {
			final XMLExportImport importer = new XMLExportImport();
			final InputStream inputStream = FileLocator.openStream(Activator.getDefault().getBundle(), new Path(
					"lists/" + NlsUtil.getDataLanguagePath() + "/instruments.xml"), false); //$NON-NLS-1$ //$NON-NLS-2$
			importer.loadFromXML(TYPE_INSTRUMENTS, inputStream, instrumentList.getRootCategory());
			// TODO ggf. muss hier noch das aktuelle Instrument gesetzt werden
			return;
		} catch (final IOException e) {
		} catch (final ParserConfigurationException e) {
		} catch (final SAXException e) {
		}

		// (2) in case of an exception add instruments programmatically

		final String scaleLength = ListMessages.ListManager_scale_length;

		// plucked instruments
		final Category pluckedInstruments = new Category(ListMessages.ListManager_plucked_instruments);
		instrumentList.getRootCategory().addCategory(pluckedInstruments);

		final Instrument guitar = new Instrument();
		guitar.setName(ListMessages.ListManager_guitar);
		guitar.setStringCount(6);
		guitar.setFretCount(12);
		guitar.setScaleLengthUnit(Unit.mm);
		guitar.setScaleLength(625.0d);
		guitar.setNoteOfEmptyString(Factory.getInstance().getNote("E", 4), 1); //$NON-NLS-1$
		guitar.setNoteOfEmptyString(Factory.getInstance().getNote("H", 3), 2); //$NON-NLS-1$
		guitar.setNoteOfEmptyString(Factory.getInstance().getNote("G", 3), 3); //$NON-NLS-1$
		guitar.setNoteOfEmptyString(Factory.getInstance().getNote("D", 3), 4); //$NON-NLS-1$
		guitar.setNoteOfEmptyString(Factory.getInstance().getNote("A", 2), 5); //$NON-NLS-1$
		guitar.setNoteOfEmptyString(Factory.getInstance().getNote("E", 2), 6); //$NON-NLS-1$
		guitar.setClef(Clef.G_ONE_OCTAVE_DEEPER);
		guitar.setMidiInstrumentNumber(24);
		pluckedInstruments.addElement(guitar);

		final Instrument bass = new Instrument();
		bass.setName(ListMessages.ListManager_bass);
		bass.setStringCount(4);
		bass.setFretCount(12);
		bass.setScaleLengthUnit(Unit.mm);
		bass.setScaleLength(865.0d);
		bass.setNoteOfEmptyString(Factory.getInstance().getNote("G", 3), 1); //$NON-NLS-1$
		bass.setNoteOfEmptyString(Factory.getInstance().getNote("D", 3), 2); //$NON-NLS-1$
		bass.setNoteOfEmptyString(Factory.getInstance().getNote("A", 2), 3); //$NON-NLS-1$
		bass.setNoteOfEmptyString(Factory.getInstance().getNote("E", 2), 4); //$NON-NLS-1$
		bass.setClef(Clef.F_STANDARD);
		bass.setMidiInstrumentNumber(32);
		pluckedInstruments.addElement(bass);

		final Instrument banjo = new Instrument();
		banjo.setName(ListMessages.ListManager_banjo);
		banjo.setStringCount(4);
		banjo.setFretCount(12);
		banjo.setScaleLengthUnit(Unit.mm);
		banjo.setScaleLength(620.0d);
		banjo.setNoteOfEmptyString(Factory.getInstance().getNote("D", 4), 1); //$NON-NLS-1$
		banjo.setNoteOfEmptyString(Factory.getInstance().getNote("H", 3), 2); //$NON-NLS-1$
		banjo.setNoteOfEmptyString(Factory.getInstance().getNote("G", 3), 3); //$NON-NLS-1$
		banjo.setNoteOfEmptyString(Factory.getInstance().getNote("C", 3), 4); //$NON-NLS-1$
		banjo.setClef(Clef.G_STANDARD);
		banjo.setMidiInstrumentNumber(105);
		pluckedInstruments.addElement(banjo);

		final Instrument mandolin = new Instrument();
		mandolin.setName(ListMessages.ListManager_mandolin);
		mandolin.setStringCount(4);
		mandolin.setFretCount(12);
		mandolin.setScaleLengthUnit(Unit.mm);
		mandolin.setScaleLength(340.0d);
		mandolin.setNoteOfEmptyString(Factory.getInstance().getNote("E", 5), 1); //$NON-NLS-1$
		mandolin.setNoteOfEmptyString(Factory.getInstance().getNote("A", 4), 2); //$NON-NLS-1$
		mandolin.setNoteOfEmptyString(Factory.getInstance().getNote("D", 4), 3); //$NON-NLS-1$
		mandolin.setNoteOfEmptyString(Factory.getInstance().getNote("G", 3), 4); //$NON-NLS-1$
		mandolin.setClef(Clef.G_STANDARD);
		mandolin.setMidiInstrumentNumber(24);
		mandolin.setDoubledStrings(true);
		mandolin.setComment(ListMessages.ListManager_aka_mandolin_banjo);
		pluckedInstruments.addElement(mandolin);

		final Instrument ukulele = new Instrument();
		ukulele.setName(ListMessages.ListManager_ukulele);
		ukulele.setStringCount(4);
		ukulele.setFretCount(12);
		ukulele.setScaleLengthUnit(Unit.mm);
		ukulele.setScaleLength(345.0d);
		ukulele.setNoteOfEmptyString(Factory.getInstance().getNote("H", 4), 1); //$NON-NLS-1$
		ukulele.setNoteOfEmptyString(Factory.getInstance().getNote("F#", 4), 2); //$NON-NLS-1$
		ukulele.setNoteOfEmptyString(Factory.getInstance().getNote("D", 4), 3); //$NON-NLS-1$
		ukulele.setNoteOfEmptyString(Factory.getInstance().getNote("A", 4), 4); //$NON-NLS-1$
		ukulele.setClef(Clef.G_STANDARD);
		ukulele.setMidiInstrumentNumber(24);
		pluckedInstruments.addElement(ukulele);

		// bowed instruments
		final Category bowedInstruments = new Category(ListMessages.ListManager_bowed_instruments);
		instrumentList.getRootCategory().addCategory(bowedInstruments);

		final Instrument cello = new Instrument();
		cello.setName(ListMessages.ListManager_violoncello);
		cello.setStringCount(4);
		cello.setFretCount(12);
		cello.setScaleLengthUnit(Unit.mm);
		cello.setScaleLength(690.0d);
		cello.setNoteOfEmptyString(Factory.getInstance().getNote("A", 3), 1); //$NON-NLS-1$
		cello.setNoteOfEmptyString(Factory.getInstance().getNote("D", 3), 2); //$NON-NLS-1$
		cello.setNoteOfEmptyString(Factory.getInstance().getNote("G", 2), 3); //$NON-NLS-1$
		cello.setNoteOfEmptyString(Factory.getInstance().getNote("C", 2), 4); //$NON-NLS-1$
		cello.setClef(Clef.F_STANDARD);
		cello.setMidiInstrumentNumber(42);
		cello.setFretless(true);
		cello.setComment(scaleLength + "640-760"); //$NON-NLS-1$
		bowedInstruments.addElement(cello);

		final Instrument contrabass = new Instrument();
		contrabass.setName(ListMessages.ListManager_contrabass);
		contrabass.setStringCount(4);
		contrabass.setFretCount(12);
		contrabass.setScaleLengthUnit(Unit.mm);
		contrabass.setScaleLength(1070.0d);
		contrabass.setNoteOfEmptyString(Factory.getInstance().getNote("G", 2), 1); //$NON-NLS-1$
		contrabass.setNoteOfEmptyString(Factory.getInstance().getNote("D", 2), 2); //$NON-NLS-1$
		contrabass.setNoteOfEmptyString(Factory.getInstance().getNote("A", 1), 3); //$NON-NLS-1$
		contrabass.setNoteOfEmptyString(Factory.getInstance().getNote("E", 1), 4); //$NON-NLS-1$
		contrabass.setClef(Clef.F_ONE_OCTAVE_DEEPER);
		contrabass.setMidiInstrumentNumber(43);
		contrabass.setFretless(true);
		contrabass.setComment(ListMessages.ListManager_aka_contrabass
				+ "\r\n" + scaleLength + "1/16=700, 1/10=750, 1/8=800, 1/4=900, 1/2=1000, 3/4=1050, 7/8=1060"); //$NON-NLS-1$ //$NON-NLS-2$
		bowedInstruments.addElement(contrabass);

		final Instrument viola = new Instrument();
		viola.setName(ListMessages.ListManager_viola);
		viola.setStringCount(4);
		viola.setFretCount(12);
		viola.setScaleLengthUnit(Unit.mm);
		viola.setScaleLength(370.0d);
		viola.setNoteOfEmptyString(Factory.getInstance().getNote("A", 4), 1); //$NON-NLS-1$
		viola.setNoteOfEmptyString(Factory.getInstance().getNote("D", 4), 2); //$NON-NLS-1$
		viola.setNoteOfEmptyString(Factory.getInstance().getNote("G", 3), 3); //$NON-NLS-1$
		viola.setNoteOfEmptyString(Factory.getInstance().getNote("C", 3), 4); //$NON-NLS-1$
		viola.setClef(Clef.C_ALTO);
		viola.setMidiInstrumentNumber(41);
		viola.setFretless(true);
		viola.setComment(ListMessages.ListManager_comment_viola);
		bowedInstruments.addElement(viola);

		final Instrument violin = new Instrument();
		violin.setName(ListMessages.ListManager_violin);
		violin.setStringCount(4);
		violin.setFretCount(12);
		violin.setScaleLengthUnit(Unit.mm);
		violin.setScaleLength(325.0d);
		violin.setNoteOfEmptyString(Factory.getInstance().getNote("E", 5), 1); //$NON-NLS-1$
		violin.setNoteOfEmptyString(Factory.getInstance().getNote("A", 4), 2); //$NON-NLS-1$
		violin.setNoteOfEmptyString(Factory.getInstance().getNote("D", 4), 3); //$NON-NLS-1$
		violin.setNoteOfEmptyString(Factory.getInstance().getNote("G", 3), 4); //$NON-NLS-1$
		violin.setClef(Clef.G_STANDARD);
		violin.setMidiInstrumentNumber(40);
		violin.setFretless(true);
		violin.setComment(scaleLength + "1/16=215, 1/10=222, 1/8=240, 1/4=260, 1/2=283, 3/4=305, 7/8=314"); //$NON-NLS-1$
		bowedInstruments.addElement(violin);

		instrumentList.setCurrentInstrument(guitar);
	}

	private static void createDefaultChords(final boolean isActivated) {

		final ChordList chordList = ChordList.getInstance();
		final CategoryViewFilter filter = new CategoryViewFilter();
		chordList.addChangeListenerFilter(filter);

		try {

			// (1) load chords from file inside plugin
			if (isActivated) {
				final XMLExportImport importer = new XMLExportImport();
				final InputStream inputStream = FileLocator.openStream(Activator.getDefault().getBundle(), new Path(
						"lists/" + NlsUtil.getDataLanguagePath() + "/chords.xml"), false); //$NON-NLS-1$ //$NON-NLS-2$
				importer.loadFromXML(TYPE_CHORDS, inputStream, chordList.getRootCategory());
				return;
			}

			// (2) add chords programmatically

			// intervals
			final Interval t1 = Factory.getInstance().getInterval(1);
			final Interval t2 = Factory.getInstance().getInterval(2);
			final Interval t3 = Factory.getInstance().getInterval(3);
			final Interval t4 = Factory.getInstance().getInterval(4);
			final Interval t5 = Factory.getInstance().getInterval(5);
			final Interval t6 = Factory.getInstance().getInterval(6);
			final Interval t7 = Factory.getInstance().getInterval(7);
			final Interval t8 = Factory.getInstance().getInterval(8);
			final Interval t9 = Factory.getInstance().getInterval(9);
			final Interval t10 = Factory.getInstance().getInterval(10);
			final Interval t11 = Factory.getInstance().getInterval(11);

			// first add major chord --> will be selected after activation
			final Category major = new Category(ListMessages.ListManager_major);
			if (isActivated) {
				chordList.addCategory(major, chordList.getRootCategory());
			}

			final Chord maj = new Chord(Constants.BLANK_CHORD_NAME);
			maj.setAlsoKnownAsString(ListMessages.ListManager_aka_maj, false);
			maj.addInterval(t4, "3"); //$NON-NLS-1$
			maj.addInterval(t7, "5"); //$NON-NLS-1$
			if (isActivated) {
				chordList.addElement(maj, major);
			}

			// diminished
			final Category diminished = new Category(ListMessages.ListManager_diminished);
			if (isActivated) {
				chordList.addCategory(diminished, chordList.getRootCategory());
			}

			final Chord dim = new Chord("\u00b0"); //$NON-NLS-1$
			dim.setAlsoKnownAsString(ListMessages.ListManager_aka_dim, false);
			dim.addInterval(t3, "b3"); //$NON-NLS-1$
			dim.addInterval(t6, "b5"); //$NON-NLS-1$
			if (isActivated) {
				chordList.addElement(dim, diminished);
			}

			final Chord dim7 = new Chord("\u00b07"); //$NON-NLS-1$
			dim7.addInterval(t3, "b3"); //$NON-NLS-1$
			dim7.addInterval(t6, "b5"); //$NON-NLS-1$
			dim7.addInterval(t9, "bb7"); //$NON-NLS-1$
			if (isActivated) {
				chordList.addElement(dim7, diminished);
			}

			final Chord dim7_13 = new Chord("\u00b07/b13"); //$NON-NLS-1$
			dim7_13.addInterval(t3, "b3"); //$NON-NLS-1$
			dim7_13.addInterval(t6, "b5"); //$NON-NLS-1$
			dim7_13.addInterval(t9, "bb7"); //$NON-NLS-1$
			dim7_13.addInterval(t8, "b13"); //$NON-NLS-1$
			if (isActivated) {
				chordList.addElement(dim7_13, diminished);
			}

			// half diminished
			final Category halfDiminished = new Category(ListMessages.ListManager_half_diminished);
			if (isActivated) {
				chordList.addCategory(halfDiminished, chordList.getRootCategory());
			}

			final Chord m7b5 = new Chord("m7(b5)"); //$NON-NLS-1$
			m7b5.setAlsoKnownAsString("m\u00d8, dim7, min7(b5)", false); //$NON-NLS-1$
			m7b5.addInterval(t3, "b3"); //$NON-NLS-1$
			m7b5.addInterval(t6, "b5"); //$NON-NLS-1$
			m7b5.addInterval(t10, "b7"); //$NON-NLS-1$
			if (isActivated) {
				chordList.addElement(m7b5, halfDiminished);
			}

			// minor
			final Category minor = new Category(ListMessages.ListManager_minor);
			if (isActivated) {
				chordList.addCategory(minor, chordList.getRootCategory());
			}

			final Chord m = new Chord("m"); //$NON-NLS-1$
			m.setAlsoKnownAsString(ListMessages.ListManager_aka_min, false);
			m.addInterval(t3, "b3"); //$NON-NLS-1$
			m.addInterval(t7, "5"); //$NON-NLS-1$
			if (isActivated) {
				chordList.addElement(m, minor);
			}

			final Chord m_add11 = new Chord("m(add11)"); //$NON-NLS-1$
			m_add11.setAlsoKnownAsString("m(add4)", false); //$NON-NLS-1$
			m_add11.addInterval(t3, "b3"); //$NON-NLS-1$
			m_add11.addInterval(t7, "5"); //$NON-NLS-1$
			m_add11.addInterval(t5, "11"); //$NON-NLS-1$
			if (isActivated) {
				chordList.addElement(m_add11, minor);
			}

			final Chord m_add9 = new Chord("m(add9)"); //$NON-NLS-1$
			m_add9.setAlsoKnownAsString("m(add2)", false); //$NON-NLS-1$
			m_add9.addInterval(t3, "b3"); //$NON-NLS-1$
			m_add9.addInterval(t7, "5"); //$NON-NLS-1$
			m_add9.addInterval(t2, "9"); //$NON-NLS-1$
			if (isActivated) {
				chordList.addElement(m_add9, minor);
			}

			final Chord mmaj7 = new Chord("m(maj7)"); //$NON-NLS-1$
			mmaj7.setAlsoKnownAsString("m(j7)", false); //$NON-NLS-1$
			mmaj7.addInterval(t3, "b3"); //$NON-NLS-1$
			mmaj7.addInterval(t7, "5"); //$NON-NLS-1$
			mmaj7.addInterval(t11, "7"); //$NON-NLS-1$
			if (isActivated) {
				chordList.addElement(mmaj7, minor);
			}

			final Chord mmaj9 = new Chord("m(maj9)"); //$NON-NLS-1$
			mmaj9.setAlsoKnownAsString("m(j9)", false); //$NON-NLS-1$
			mmaj9.addInterval(t3, "b3"); //$NON-NLS-1$
			mmaj9.addInterval(t7, "5"); //$NON-NLS-1$
			mmaj9.addInterval(t11, "7"); //$NON-NLS-1$
			mmaj9.addInterval(t2, "9"); //$NON-NLS-1$
			if (isActivated) {
				chordList.addElement(mmaj9, minor);
			}

			final Chord m9 = new Chord("m9"); //$NON-NLS-1$
			m9.setAlsoKnownAsString("min9", false); //$NON-NLS-1$
			m9.addInterval(t3, "b3"); //$NON-NLS-1$
			m9.addInterval(t7, "5"); //$NON-NLS-1$
			m9.addInterval(t10, "b7"); //$NON-NLS-1$
			m9.addInterval(t2, "9"); //$NON-NLS-1$
			if (isActivated) {
				chordList.addElement(m9, minor);
			}

			final Chord m11 = new Chord("m11"); //$NON-NLS-1$
			m11.setAlsoKnownAsString("m7/9/11, m9/11, min11", false); //$NON-NLS-1$
			m11.addInterval(t3, "b3"); //$NON-NLS-1$
			m11.addInterval(t7, "5"); //$NON-NLS-1$
			m11.addInterval(t10, "b7"); //$NON-NLS-1$
			m11.addInterval(t2, "9"); //$NON-NLS-1$
			m11.addInterval(t5, "11"); //$NON-NLS-1$
			if (isActivated) {
				chordList.addElement(m11, minor);
			}

			final Chord m13 = new Chord("m13"); //$NON-NLS-1$
			m13.setAlsoKnownAsString("min13", false); //$NON-NLS-1$
			m13.addInterval(t3, "b3"); //$NON-NLS-1$
			m13.addInterval(t7, "5"); //$NON-NLS-1$
			m13.addInterval(t10, "b7"); //$NON-NLS-1$
			m13.addInterval(t2, "9"); //$NON-NLS-1$
			m13.addInterval(t5, "11"); //$NON-NLS-1$
			m13.addInterval(t9, "13"); //$NON-NLS-1$
			if (isActivated) {
				chordList.addElement(m13, minor);
			}

			final Chord m6 = new Chord("m6"); //$NON-NLS-1$
			m6.setAlsoKnownAsString("min6", false); //$NON-NLS-1$
			m6.addInterval(t3, "b3"); //$NON-NLS-1$
			m6.addInterval(t7, "5"); //$NON-NLS-1$
			m6.addInterval(t9, "6"); //$NON-NLS-1$
			if (isActivated) {
				chordList.addElement(m6, minor);
			}

			final Chord m69 = new Chord("m6/9"); //$NON-NLS-1$
			m69.setAlsoKnownAsString("min6/9", false); //$NON-NLS-1$
			m69.addInterval(t3, "b3"); //$NON-NLS-1$
			m69.addInterval(t7, "5"); //$NON-NLS-1$
			m69.addInterval(t9, "6"); //$NON-NLS-1$
			m69.addInterval(t2, "9"); //$NON-NLS-1$
			if (isActivated) {
				chordList.addElement(m69, minor);
			}

			final Chord m6_9_11 = new Chord("m6/9/11"); //$NON-NLS-1$
			m6_9_11.setAlsoKnownAsString("min6/9/11", false); //$NON-NLS-1$
			m6_9_11.addInterval(t3, "b3"); //$NON-NLS-1$
			m6_9_11.addInterval(t7, "5"); //$NON-NLS-1$
			m6_9_11.addInterval(t9, "6"); //$NON-NLS-1$
			m6_9_11.addInterval(t2, "9"); //$NON-NLS-1$
			m6_9_11.addInterval(t5, "11"); //$NON-NLS-1$
			if (isActivated) {
				chordList.addElement(m6_9_11, minor);
			}

			final Chord m7 = new Chord("m7"); //$NON-NLS-1$
			m7.setAlsoKnownAsString("-7, mi7, min7", false); //$NON-NLS-1$
			m7.addInterval(t3, "b3"); //$NON-NLS-1$
			m7.addInterval(t7, "5"); //$NON-NLS-1$
			m7.addInterval(t10, "b7"); //$NON-NLS-1$
			if (isActivated) {
				chordList.addElement(m7, minor);
			}

			final Chord m7_11 = new Chord("m7/11"); //$NON-NLS-1$
			m7_11.setAlsoKnownAsString("min7/11", false); //$NON-NLS-1$
			m7_11.addInterval(t3, "b3"); //$NON-NLS-1$
			m7_11.addInterval(t7, "5"); //$NON-NLS-1$
			m7_11.addInterval(t10, "b7"); //$NON-NLS-1$
			m7_11.addInterval(t5, "11"); //$NON-NLS-1$
			if (isActivated) {
				chordList.addElement(m7_11, minor);
			}

			final Chord m7_11_b5 = new Chord("m7/11(b5)"); //$NON-NLS-1$
			m7_11_b5.setAlsoKnownAsString("min7/11(b5)", false); //$NON-NLS-1$
			m7_11_b5.addInterval(t3, "b3"); //$NON-NLS-1$
			m7_11_b5.addInterval(t6, "b5"); //$NON-NLS-1$
			m7_11_b5.addInterval(t10, "b7"); //$NON-NLS-1$
			m7_11_b5.addInterval(t5, "11"); //$NON-NLS-1$
			if (isActivated) {
				chordList.addElement(m7_11_b5, minor);
			}

			final Chord m7_13 = new Chord("m7/13"); //$NON-NLS-1$
			m7_13.setAlsoKnownAsString("min7/13", false); //$NON-NLS-1$
			m7_13.addInterval(t3, "b3"); //$NON-NLS-1$
			m7_13.addInterval(t7, "5"); //$NON-NLS-1$
			m7_13.addInterval(t10, "b7"); //$NON-NLS-1$
			m7_13.addInterval(t9, "13"); //$NON-NLS-1$
			if (isActivated) {
				chordList.addElement(m7_13, minor);
			}

			final Chord m7_b13 = new Chord("m7/b13"); //$NON-NLS-1$
			m7_b13.setAlsoKnownAsString("min7/b13", false); //$NON-NLS-1$
			m7_b13.addInterval(t3, "b3"); //$NON-NLS-1$
			m7_b13.addInterval(t7, "5"); //$NON-NLS-1$
			m7_b13.addInterval(t10, "b7"); //$NON-NLS-1$
			m7_b13.addInterval(t8, "b13"); //$NON-NLS-1$
			if (isActivated) {
				chordList.addElement(m7_b13, minor);
			}

			// major
			final Chord b5 = new Chord("(b5)"); //$NON-NLS-1$
			b5.addInterval(t4, "3"); //$NON-NLS-1$
			b5.addInterval(t6, "b5"); //$NON-NLS-1$
			if (isActivated) {
				chordList.addElement(b5, major);
			}

			final Chord sus2 = new Chord("2"); //$NON-NLS-1$
			sus2.setAlsoKnownAsString("sus2", false); //$NON-NLS-1$
			sus2.addInterval(t2, "2"); //$NON-NLS-1$
			sus2.addInterval(t7, "5"); //$NON-NLS-1$
			if (isActivated) {
				chordList.addElement(sus2, major);
			}

			final Chord sus4 = new Chord("4"); //$NON-NLS-1$
			sus4.setAlsoKnownAsString("sus4, m(sus4)", false); //$NON-NLS-1$
			sus4.addInterval(t5, "4"); //$NON-NLS-1$
			sus4.addInterval(t7, "5"); //$NON-NLS-1$
			if (isActivated) {
				chordList.addElement(sus4, major);
			}

			final Chord add6 = new Chord("6"); //$NON-NLS-1$
			add6.setAlsoKnownAsString("add13, add6, maj6", false); //$NON-NLS-1$
			add6.addInterval(t4, "3"); //$NON-NLS-1$
			add6.addInterval(t7, "5"); //$NON-NLS-1$
			add6.addInterval(t9, "6"); //$NON-NLS-1$
			if (isActivated) {
				chordList.addElement(add6, major);
			}

			final Chord maj69 = new Chord("6/9"); //$NON-NLS-1$
			maj69.setAlsoKnownAsString("maj6/9", false); //$NON-NLS-1$
			maj69.addInterval(t4, "3"); //$NON-NLS-1$
			maj69.addInterval(t7, "5"); //$NON-NLS-1$
			maj69.addInterval(t9, "6"); //$NON-NLS-1$
			maj69.addInterval(t2, "9"); //$NON-NLS-1$
			if (isActivated) {
				chordList.addElement(maj69, major);
			}

			final Chord maj6_9_11 = new Chord("6/9/#11"); //$NON-NLS-1$
			maj6_9_11.addInterval(t4, "3"); //$NON-NLS-1$
			maj6_9_11.addInterval(t7, "5"); //$NON-NLS-1$
			maj6_9_11.addInterval(t9, "6"); //$NON-NLS-1$
			maj6_9_11.addInterval(t2, "9"); //$NON-NLS-1$
			maj6_9_11.addInterval(t6, "#11"); //$NON-NLS-1$
			if (isActivated) {
				chordList.addElement(maj6_9_11, major);
			}

			final Chord add11 = new Chord("add11"); //$NON-NLS-1$
			add11.setAlsoKnownAsString("add4", false); //$NON-NLS-1$
			add11.addInterval(t4, "3"); //$NON-NLS-1$
			add11.addInterval(t7, "5"); //$NON-NLS-1$
			add11.addInterval(t5, "11"); //$NON-NLS-1$
			if (isActivated) {
				chordList.addElement(add11, major);
			}

			final Chord add9 = new Chord("add9"); //$NON-NLS-1$
			add9.setAlsoKnownAsString("add2", false); //$NON-NLS-1$
			add9.addInterval(t4, "3"); //$NON-NLS-1$
			add9.addInterval(t7, "5"); //$NON-NLS-1$
			add9.addInterval(t2, "9"); //$NON-NLS-1$
			if (isActivated) {
				chordList.addElement(add9, major);
			}

			final Chord maj11 = new Chord("maj11"); //$NON-NLS-1$
			maj11.setAlsoKnownAsString("maj7/9/11, maj9/11", false); //$NON-NLS-1$
			maj11.addInterval(t4, "3"); //$NON-NLS-1$
			maj11.addInterval(t7, "5"); //$NON-NLS-1$
			maj11.addInterval(t11, "7"); //$NON-NLS-1$
			maj11.addInterval(t2, "9"); //$NON-NLS-1$
			maj11.addInterval(t5, "11"); //$NON-NLS-1$
			if (isActivated) {
				chordList.addElement(maj11, major);
			}

			final Chord maj7 = new Chord("maj7"); //$NON-NLS-1$
			maj7.setAlsoKnownAsString("j7, M7, \u0394", false); //$NON-NLS-1$
			maj7.addInterval(t4, "3"); //$NON-NLS-1$
			maj7.addInterval(t7, "5"); //$NON-NLS-1$
			maj7.addInterval(t11, "7"); //$NON-NLS-1$
			if (isActivated) {
				chordList.addElement(maj7, major);
			}

			final Chord maj7_11 = new Chord("maj7/#11"); //$NON-NLS-1$
			maj7_11.addInterval(t4, "3"); //$NON-NLS-1$
			maj7_11.addInterval(t7, "5"); //$NON-NLS-1$
			maj7_11.addInterval(t11, "7"); //$NON-NLS-1$
			maj7_11.addInterval(t6, "#11"); //$NON-NLS-1$
			if (isActivated) {
				chordList.addElement(maj7_11, major);
			}

			final Chord maj7_13 = new Chord("maj7/13"); //$NON-NLS-1$
			maj7_13.addInterval(t4, "3"); //$NON-NLS-1$
			maj7_13.addInterval(t7, "5"); //$NON-NLS-1$
			maj7_13.addInterval(t11, "7"); //$NON-NLS-1$
			maj7_13.addInterval(t9, "13"); //$NON-NLS-1$
			if (isActivated) {
				chordList.addElement(maj7_13, major);
			}

			final Chord maj7_5 = new Chord("maj7(#5)"); //$NON-NLS-1$
			maj7_5.addInterval(t4, "3"); //$NON-NLS-1$
			maj7_5.addInterval(t8, "#5"); //$NON-NLS-1$
			maj7_5.addInterval(t11, "7"); //$NON-NLS-1$
			if (isActivated) {
				chordList.addElement(maj7_5, major);
			}

			final Chord maj7_9 = new Chord("maj7(#9)"); //$NON-NLS-1$
			maj7_9.addInterval(t4, "3"); //$NON-NLS-1$
			maj7_9.addInterval(t7, "5"); //$NON-NLS-1$
			maj7_9.addInterval(t11, "7"); //$NON-NLS-1$
			maj7_9.addInterval(t3, "#9"); //$NON-NLS-1$
			if (isActivated) {
				chordList.addElement(maj7_9, major);
			}

			final Chord maj7_9_11 = new Chord("maj7(#9)11"); //$NON-NLS-1$
			maj7_9_11.addInterval(t4, "3"); //$NON-NLS-1$
			maj7_9_11.addInterval(t7, "5"); //$NON-NLS-1$
			maj7_9_11.addInterval(t11, "7"); //$NON-NLS-1$
			maj7_9_11.addInterval(t3, "#9"); //$NON-NLS-1$
			maj7_9_11.addInterval(t5, "11"); //$NON-NLS-1$
			if (isActivated) {
				chordList.addElement(maj7_9_11, major);
			}

			final Chord maj7_b5 = new Chord("maj7(b5)"); //$NON-NLS-1$
			maj7_b5.addInterval(t4, "3"); //$NON-NLS-1$
			maj7_b5.addInterval(t6, "b5"); //$NON-NLS-1$
			maj7_b5.addInterval(t11, "7"); //$NON-NLS-1$
			if (isActivated) {
				chordList.addElement(maj7_b5, major);
			}

			final Chord maj7_b9 = new Chord("maj7(b9)"); //$NON-NLS-1$
			maj7_b9.addInterval(t4, "3"); //$NON-NLS-1$
			maj7_b9.addInterval(t7, "5"); //$NON-NLS-1$
			maj7_b9.addInterval(t11, "7"); //$NON-NLS-1$
			maj7_b9.addInterval(t1, "b9"); //$NON-NLS-1$
			if (isActivated) {
				chordList.addElement(maj7_b9, major);
			}

			final Chord maj9 = new Chord("maj9"); //$NON-NLS-1$
			maj9.setAlsoKnownAsString("maj7/9", false); //$NON-NLS-1$
			maj9.addInterval(t4, "3"); //$NON-NLS-1$
			maj9.addInterval(t7, "5"); //$NON-NLS-1$
			maj9.addInterval(t11, "7"); //$NON-NLS-1$
			maj9.addInterval(t2, "9"); //$NON-NLS-1$
			if (isActivated) {
				chordList.addElement(maj9, major);
			}

			final Chord maj9_13 = new Chord("maj9/13"); //$NON-NLS-1$
			maj9_13.setAlsoKnownAsString("maj7/9/13", false); //$NON-NLS-1$
			maj9_13.addInterval(t4, "3"); //$NON-NLS-1$
			maj9_13.addInterval(t7, "5"); //$NON-NLS-1$
			maj9_13.addInterval(t11, "7"); //$NON-NLS-1$
			maj9_13.addInterval(t2, "9"); //$NON-NLS-1$
			maj9_13.addInterval(t9, "13"); //$NON-NLS-1$
			if (isActivated) {
				chordList.addElement(maj9_13, major);
			}

			// (dominant-) seventh
			final Category seventh = new Category(ListMessages.ListManager_dominant_seventh);
			if (isActivated) {
				chordList.addCategory(seventh, chordList.getRootCategory());
			}

			final Chord dom11 = new Chord("11"); //$NON-NLS-1$
			dom11.setAlsoKnownAsString("dom11", false); //$NON-NLS-1$
			dom11.addInterval(t4, "3"); //$NON-NLS-1$
			dom11.addInterval(t7, "5"); //$NON-NLS-1$
			dom11.addInterval(t10, "b7"); //$NON-NLS-1$
			dom11.addInterval(t2, "9"); //$NON-NLS-1$
			dom11.addInterval(t5, "11"); //$NON-NLS-1$
			if (isActivated) {
				chordList.addElement(dom11, seventh);
			}

			final Chord dom11_9 = new Chord("11(#9)"); //$NON-NLS-1$
			dom11_9.addInterval(t4, "3"); //$NON-NLS-1$
			dom11_9.addInterval(t7, "5"); //$NON-NLS-1$
			dom11_9.addInterval(t10, "b7"); //$NON-NLS-1$
			dom11_9.addInterval(t3, "#9"); //$NON-NLS-1$
			dom11_9.addInterval(t5, "11"); //$NON-NLS-1$
			if (isActivated) {
				chordList.addElement(dom11_9, seventh);
			}

			final Chord dom11_b9 = new Chord("11(b9)"); //$NON-NLS-1$
			dom11_b9.addInterval(t4, "3"); //$NON-NLS-1$
			dom11_b9.addInterval(t7, "5"); //$NON-NLS-1$
			dom11_b9.addInterval(t10, "b7"); //$NON-NLS-1$
			dom11_b9.addInterval(t1, "b9"); //$NON-NLS-1$
			dom11_b9.addInterval(t5, "11"); //$NON-NLS-1$
			if (isActivated) {
				chordList.addElement(dom11_b9, seventh);
			}

			final Chord dom13 = new Chord("13"); //$NON-NLS-1$
			dom13.setAlsoKnownAsString("dom13", false); //$NON-NLS-1$
			dom13.addInterval(t4, "3"); //$NON-NLS-1$
			dom13.addInterval(t7, "5"); //$NON-NLS-1$
			dom13.addInterval(t10, "b7"); //$NON-NLS-1$
			dom13.addInterval(t2, "9"); //$NON-NLS-1$
			dom13.addInterval(t5, "11"); //$NON-NLS-1$
			dom13.addInterval(t9, "13"); //$NON-NLS-1$
			if (isActivated) {
				chordList.addElement(dom13, seventh);
			}

			final Chord dom13_4 = new Chord("13/4"); //$NON-NLS-1$
			dom13_4.setAlsoKnownAsString("13sus4, 9/13sus4", false); //$NON-NLS-1$
			dom13_4.addInterval(t5, "4"); //$NON-NLS-1$
			dom13_4.addInterval(t7, "5"); //$NON-NLS-1$
			dom13_4.addInterval(t10, "b7"); //$NON-NLS-1$
			dom13_4.addInterval(t2, "9"); //$NON-NLS-1$
			dom13_4.addInterval(t9, "13"); //$NON-NLS-1$
			if (isActivated) {
				chordList.addElement(dom13_4, seventh);
			}

			final Chord dom13_9 = new Chord("13(#9)"); //$NON-NLS-1$
			dom13_9.addInterval(t4, "3"); //$NON-NLS-1$
			dom13_9.addInterval(t7, "5"); //$NON-NLS-1$
			dom13_9.addInterval(t10, "b7"); //$NON-NLS-1$
			dom13_9.addInterval(t3, "#9"); //$NON-NLS-1$
			dom13_9.addInterval(t5, "11"); //$NON-NLS-1$
			dom13_9.addInterval(t9, "13"); //$NON-NLS-1$
			if (isActivated) {
				chordList.addElement(dom13_9, seventh);
			}

			final Chord dom13_b9 = new Chord("13(b9)"); //$NON-NLS-1$
			dom13_b9.addInterval(t4, "3"); //$NON-NLS-1$
			dom13_b9.addInterval(t7, "5"); //$NON-NLS-1$
			dom13_b9.addInterval(t10, "b7"); //$NON-NLS-1$
			dom13_b9.addInterval(t1, "b9"); //$NON-NLS-1$
			dom13_b9.addInterval(t5, "11"); //$NON-NLS-1$
			dom13_b9.addInterval(t9, "13"); //$NON-NLS-1$
			if (isActivated) {
				chordList.addElement(dom13_b9, seventh);
			}

			final Chord dom7 = new Chord("7"); //$NON-NLS-1$
			dom7.setAlsoKnownAsString("dom7", false); //$NON-NLS-1$
			dom7.addInterval(t4, "3"); //$NON-NLS-1$
			dom7.addInterval(t7, "5"); //$NON-NLS-1$
			dom7.addInterval(t10, "b7"); //$NON-NLS-1$
			if (isActivated) {
				chordList.addElement(dom7, seventh);
			}

			final Chord dom7_a11 = new Chord("7/#11"); //$NON-NLS-1$
			dom7_a11.addInterval(t4, "3"); //$NON-NLS-1$
			dom7_a11.addInterval(t7, "5"); //$NON-NLS-1$
			dom7_a11.addInterval(t10, "b7"); //$NON-NLS-1$
			dom7_a11.addInterval(t6, "#11"); //$NON-NLS-1$
			if (isActivated) {
				chordList.addElement(dom7_a11, seventh);
			}

			final Chord dom7_11 = new Chord("7/11"); //$NON-NLS-1$
			dom7_11.addInterval(t4, "3"); //$NON-NLS-1$
			dom7_11.addInterval(t7, "5"); //$NON-NLS-1$
			dom7_11.addInterval(t10, "b7"); //$NON-NLS-1$
			dom7_11.addInterval(t5, "11"); //$NON-NLS-1$
			if (isActivated) {
				chordList.addElement(dom7_11, seventh);
			}

			final Chord dom7_13 = new Chord("7/13"); //$NON-NLS-1$
			dom7_13.addInterval(t4, "3"); //$NON-NLS-1$
			dom7_13.addInterval(t7, "5"); //$NON-NLS-1$
			dom7_13.addInterval(t10, "b7"); //$NON-NLS-1$
			dom7_13.addInterval(t9, "13"); //$NON-NLS-1$
			if (isActivated) {
				chordList.addElement(dom7_13, seventh);
			}

			final Chord dom7_4 = new Chord("7/4"); //$NON-NLS-1$
			dom7_4.setAlsoKnownAsString("7sus4", false); //$NON-NLS-1$
			dom7_4.addInterval(t5, "4"); //$NON-NLS-1$
			dom7_4.addInterval(t7, "5"); //$NON-NLS-1$
			dom7_4.addInterval(t10, "b7"); //$NON-NLS-1$
			if (isActivated) {
				chordList.addElement(dom7_4, seventh);
			}

			final Chord dom7_b13 = new Chord("7/b13"); //$NON-NLS-1$
			dom7_b13.addInterval(t4, "3"); //$NON-NLS-1$
			dom7_b13.addInterval(t7, "5"); //$NON-NLS-1$
			dom7_b13.addInterval(t10, "b7"); //$NON-NLS-1$
			dom7_b13.addInterval(t8, "b13"); //$NON-NLS-1$
			if (isActivated) {
				chordList.addElement(dom7_b13, seventh);
			}

			final Chord dom7_5 = new Chord("7(#5)"); //$NON-NLS-1$
			dom7_5.addInterval(t4, "3"); //$NON-NLS-1$
			dom7_5.addInterval(t8, "#5"); //$NON-NLS-1$
			dom7_5.addInterval(t10, "b7"); //$NON-NLS-1$
			if (isActivated) {
				chordList.addElement(dom7_5, seventh);
			}

			final Chord dom_9 = new Chord("7(#9)"); //$NON-NLS-1$
			dom_9.setAlsoKnownAsString("dom(#9)", false); //$NON-NLS-1$
			dom_9.addInterval(t4, "3"); //$NON-NLS-1$
			dom_9.addInterval(t7, "5"); //$NON-NLS-1$
			dom_9.addInterval(t10, "b7"); //$NON-NLS-1$
			dom_9.addInterval(t3, "#9"); //$NON-NLS-1$
			if (isActivated) {
				chordList.addElement(dom_9, seventh);
			}

			final Chord dom_9_11 = new Chord("7(#9)#11"); //$NON-NLS-1$
			dom_9_11.addInterval(t4, "3"); //$NON-NLS-1$
			dom_9_11.addInterval(t7, "5"); //$NON-NLS-1$
			dom_9_11.addInterval(t10, "b7"); //$NON-NLS-1$
			dom_9_11.addInterval(t3, "#9"); //$NON-NLS-1$
			dom_9_11.addInterval(t6, "#11"); //$NON-NLS-1$
			if (isActivated) {
				chordList.addElement(dom_9_11, seventh);
			}

			final Chord dom_9_b13 = new Chord("7(#9)b13"); //$NON-NLS-1$
			dom_9_b13.addInterval(t4, "3"); //$NON-NLS-1$
			dom_9_b13.addInterval(t7, "5"); //$NON-NLS-1$
			dom_9_b13.addInterval(t10, "b7"); //$NON-NLS-1$
			dom_9_b13.addInterval(t3, "#9"); //$NON-NLS-1$
			dom_9_b13.addInterval(t8, "b13"); //$NON-NLS-1$
			if (isActivated) {
				chordList.addElement(dom_9_b13, seventh);
			}

			final Chord dom7_b5 = new Chord("7(b5)"); //$NON-NLS-1$
			dom7_b5.setAlsoKnownAsString("\u00d8", false); //$NON-NLS-1$
			dom7_b5.addInterval(t4, "3"); //$NON-NLS-1$
			dom7_b5.addInterval(t6, "b5"); //$NON-NLS-1$
			dom7_b5.addInterval(t10, "b7"); //$NON-NLS-1$
			if (isActivated) {
				chordList.addElement(dom7_b5, seventh);
			}

			final Chord dom_b9 = new Chord("7(b9)"); //$NON-NLS-1$
			dom_b9.setAlsoKnownAsString("dom(b9)", false); //$NON-NLS-1$
			dom_b9.addInterval(t4, "3"); //$NON-NLS-1$
			dom_b9.addInterval(t7, "5"); //$NON-NLS-1$
			dom_b9.addInterval(t10, "b7"); //$NON-NLS-1$
			dom_b9.addInterval(t1, "b9"); //$NON-NLS-1$
			if (isActivated) {
				chordList.addElement(dom_b9, seventh);
			}

			final Chord dom_b9_11 = new Chord("7(b9)#11"); //$NON-NLS-1$
			dom_b9_11.addInterval(t4, "3"); //$NON-NLS-1$
			dom_b9_11.addInterval(t7, "5"); //$NON-NLS-1$
			dom_b9_11.addInterval(t10, "b7"); //$NON-NLS-1$
			dom_b9_11.addInterval(t1, "b9"); //$NON-NLS-1$
			dom_b9_11.addInterval(t6, "#11"); //$NON-NLS-1$
			if (isActivated) {
				chordList.addElement(dom_b9_11, seventh);
			}

			final Chord dom_b9_13 = new Chord("7(b9)13"); //$NON-NLS-1$
			dom_b9_13.addInterval(t4, "3"); //$NON-NLS-1$
			dom_b9_13.addInterval(t7, "5"); //$NON-NLS-1$
			dom_b9_13.addInterval(t10, "b7"); //$NON-NLS-1$
			dom_b9_13.addInterval(t1, "b9"); //$NON-NLS-1$
			dom_b9_13.addInterval(t9, "13"); //$NON-NLS-1$
			if (isActivated) {
				chordList.addElement(dom_b9_13, seventh);
			}

			final Chord dom_b9_4 = new Chord("7(b9)4"); //$NON-NLS-1$
			dom_b9_4.addInterval(t5, "4"); //$NON-NLS-1$
			dom_b9_4.addInterval(t7, "5"); //$NON-NLS-1$
			dom_b9_4.addInterval(t10, "b7"); //$NON-NLS-1$
			dom_b9_4.addInterval(t1, "b9"); //$NON-NLS-1$
			if (isActivated) {
				chordList.addElement(dom_b9_4, seventh);
			}

			final Chord dom_b9_b13 = new Chord("7(b9)b13"); //$NON-NLS-1$
			dom_b9_b13.addInterval(t4, "3"); //$NON-NLS-1$
			dom_b9_b13.addInterval(t7, "5"); //$NON-NLS-1$
			dom_b9_b13.addInterval(t10, "b7"); //$NON-NLS-1$
			dom_b9_b13.addInterval(t1, "b9"); //$NON-NLS-1$
			dom_b9_b13.addInterval(t8, "b13"); //$NON-NLS-1$
			if (isActivated) {
				chordList.addElement(dom_b9_b13, seventh);
			}

			final Chord dom9 = new Chord("9"); //$NON-NLS-1$
			dom9.setAlsoKnownAsString("dom9, 7/9", false); //$NON-NLS-1$
			dom9.addInterval(t4, "3"); //$NON-NLS-1$
			dom9.addInterval(t7, "5"); //$NON-NLS-1$
			dom9.addInterval(t10, "b7"); //$NON-NLS-1$
			dom9.addInterval(t2, "9"); //$NON-NLS-1$
			if (isActivated) {
				chordList.addElement(dom9, seventh);
			}

			final Chord dom9_4 = new Chord("9/4"); //$NON-NLS-1$
			dom9_4.setAlsoKnownAsString("9sus4, 11/4, 11sus4", false); //$NON-NLS-1$
			dom9_4.addInterval(t5, "4"); //$NON-NLS-1$
			dom9_4.addInterval(t7, "5"); //$NON-NLS-1$
			dom9_4.addInterval(t10, "b7"); //$NON-NLS-1$
			dom9_4.addInterval(t2, "9"); //$NON-NLS-1$
			if (isActivated) {
				chordList.addElement(dom9_4, seventh);
			}

			final Chord dom9_11 = new Chord("9/#11"); //$NON-NLS-1$
			dom9_11.addInterval(t4, "3"); //$NON-NLS-1$
			dom9_11.addInterval(t7, "5"); //$NON-NLS-1$
			dom9_11.addInterval(t10, "b7"); //$NON-NLS-1$
			dom9_11.addInterval(t2, "9"); //$NON-NLS-1$
			dom9_11.addInterval(t6, "#11"); //$NON-NLS-1$
			if (isActivated) {
				chordList.addElement(dom9_11, seventh);
			}

			final Chord dom9_11_13 = new Chord("9/#11/13"); //$NON-NLS-1$
			dom9_11_13.addInterval(t4, "3"); //$NON-NLS-1$
			dom9_11_13.addInterval(t7, "5"); //$NON-NLS-1$
			dom9_11_13.addInterval(t10, "b7"); //$NON-NLS-1$
			dom9_11_13.addInterval(t2, "9"); //$NON-NLS-1$
			dom9_11_13.addInterval(t6, "#11"); //$NON-NLS-1$
			dom9_11_13.addInterval(t9, "13"); //$NON-NLS-1$
			if (isActivated) {
				chordList.addElement(dom9_11_13, seventh);
			}

			final Chord dom9_13 = new Chord("9/13"); //$NON-NLS-1$
			dom9_13.setAlsoKnownAsString("9/6, 7/9/13", false); //$NON-NLS-1$
			dom9_13.addInterval(t4, "3"); //$NON-NLS-1$
			dom9_13.addInterval(t7, "5"); //$NON-NLS-1$
			dom9_13.addInterval(t10, "b7"); //$NON-NLS-1$
			dom9_13.addInterval(t2, "9"); //$NON-NLS-1$
			dom9_13.addInterval(t9, "13"); //$NON-NLS-1$
			if (isActivated) {
				chordList.addElement(dom9_13, seventh);
			}

			final Chord dom9_b13 = new Chord("9/b13"); //$NON-NLS-1$
			dom9_b13.addInterval(t4, "3"); //$NON-NLS-1$
			dom9_b13.addInterval(t7, "5"); //$NON-NLS-1$
			dom9_b13.addInterval(t10, "b7"); //$NON-NLS-1$
			dom9_b13.addInterval(t2, "9"); //$NON-NLS-1$
			dom9_b13.addInterval(t8, "b13"); //$NON-NLS-1$
			if (isActivated) {
				chordList.addElement(dom9_b13, seventh);
			}

			final Chord dom9_5 = new Chord("9(#5)"); //$NON-NLS-1$
			dom9_5.setAlsoKnownAsString("7/9(#5)", false); //$NON-NLS-1$
			dom9_5.addInterval(t4, "3"); //$NON-NLS-1$
			dom9_5.addInterval(t8, "#5"); //$NON-NLS-1$
			dom9_5.addInterval(t10, "b7"); //$NON-NLS-1$
			dom9_5.addInterval(t2, "9"); //$NON-NLS-1$
			if (isActivated) {
				chordList.addElement(dom9_5, seventh);
			}

			final Chord dom9_b5 = new Chord("9(b5)"); //$NON-NLS-1$
			dom9_b5.setAlsoKnownAsString("7/9(b5), 9(#11)", false); //$NON-NLS-1$
			dom9_b5.addInterval(t4, "3"); //$NON-NLS-1$
			dom9_b5.addInterval(t6, "b5"); //$NON-NLS-1$
			dom9_b5.addInterval(t10, "b7"); //$NON-NLS-1$
			dom9_b5.addInterval(t2, "9"); //$NON-NLS-1$
			if (isActivated) {
				chordList.addElement(dom9_b5, seventh);
			}

			// augmented
			final Category augmented = new Category(ListMessages.ListManager_augmented);
			if (isActivated) {
				chordList.addCategory(augmented, chordList.getRootCategory());
			}

			final Chord aug = new Chord("+"); //$NON-NLS-1$
			aug.setAlsoKnownAsString(ListMessages.ListManager_aka_aug, false);
			aug.addInterval(t4, "3"); //$NON-NLS-1$
			aug.addInterval(t8, "#5"); //$NON-NLS-1$
			if (isActivated) {
				chordList.addElement(aug, augmented);
			}

			// powerchords
			final Category power = new Category(ListMessages.ListManager_powerchords);
			if (isActivated) {
				chordList.addCategory(power, chordList.getRootCategory());
			}

			final String usageMsg = ListMessages.ListManager_usage;

			final Chord p3 = new Chord("1-3-x"); //$NON-NLS-1$
			p3.addInterval(t4, "3"); //$NON-NLS-1$
			p3.setComment(usageMsg + "maj, 7"); //$NON-NLS-1$
			if (isActivated) {
				chordList.addElement(p3, power);
			}

			final Chord p4 = new Chord("1-4-x"); //$NON-NLS-1$
			p4.addInterval(t5, "4"); //$NON-NLS-1$
			p4.setComment(usageMsg + "4, 7/4"); //$NON-NLS-1$
			if (isActivated) {
				chordList.addElement(p4, power);
			}

			final Chord pb3 = new Chord("1-b3-x"); //$NON-NLS-1$
			pb3.addInterval(t3, "b3"); //$NON-NLS-1$
			pb3.setComment(usageMsg + "m, m7(b5), \u00b07"); //$NON-NLS-1$
			if (isActivated) {
				chordList.addElement(pb3, power);
			}

			final Chord p6 = new Chord("1-x-6"); //$NON-NLS-1$
			p6.addInterval(t9, "6"); //$NON-NLS-1$
			p6.setComment(usageMsg + "6"); //$NON-NLS-1$
			if (isActivated) {
				chordList.addElement(p6, power);
			}

			final Chord p7 = new Chord("1-x-7"); //$NON-NLS-1$
			p7.addInterval(t11, "7"); //$NON-NLS-1$
			p7.setComment(usageMsg + "maj7"); //$NON-NLS-1$
			if (isActivated) {
				chordList.addElement(p7, power);
			}

			final Chord pb5 = new Chord("1-x-b5"); //$NON-NLS-1$
			pb5.addInterval(t6, "b5"); //$NON-NLS-1$
			pb5.setComment(usageMsg + "\u00b07, m7(b5)"); //$NON-NLS-1$
			if (isActivated) {
				chordList.addElement(pb5, power);
			}

			final Chord pb7 = new Chord("1-x-b7"); //$NON-NLS-1$
			pb7.addInterval(t10, "b7"); //$NON-NLS-1$
			pb7.setComment(usageMsg + ListMessages.ListManager_usage_pb7);
			if (isActivated) {
				chordList.addElement(pb7, power);
			}

			final Chord p5 = new Chord("5"); //$NON-NLS-1$
			p5.addInterval(t7, "5"); //$NON-NLS-1$
			p5.setComment(usageMsg + "maj, m"); //$NON-NLS-1$
			if (isActivated) {
				chordList.addElement(p5, power);
			}

			// load default list if full version is not activated
			if (!isActivated) {
				final Category root = chordList.getRootCategory();
				chordList.addElement(maj, root);
				chordList.addElement(aug, root);
				chordList.addElement(p5, root);
				chordList.addElement(sus2, root);
				chordList.addElement(sus4, root);
				chordList.addElement(add6, root);
				chordList.addElement(m7b5, root);
				chordList.addElement(dom9_13, root);
				chordList.addElement(dom9_5, root);
			}

		} catch (final Exception e) {
			// in case of an exception the chords are added programmatically
		} finally {
			chordList.removeChangeListenerFilter(filter);
			chordList.changedWholeList();
		}
	}

	private static void createDefaultScales(final boolean isActivated) {

		final ScaleList scaleList = ScaleList.getInstance();
		final CategoryViewFilter filter = new CategoryViewFilter();
		scaleList.addChangeListenerFilter(filter);

		try {

			// (1) load scales from file inside plugin
			if (isActivated) {
				final XMLExportImport importer = new XMLExportImport();
				final InputStream inputStream = FileLocator.openStream(Activator.getDefault().getBundle(), new Path(
						"lists/" + NlsUtil.getDataLanguagePath() + "/scales.xml"), false); //$NON-NLS-1$ //$NON-NLS-2$
				importer.loadFromXML(TYPE_SCALES, inputStream, scaleList.getRootCategory());
				return;
			}

			// (2) add scales programmatically
			final String orderMsg = ListMessages.ListManager_order;
			final String keyMsg = ListMessages.ListManager_key;
			final String typicalMsg = ListMessages.ListManager_typical;
			final String commentMsg = ListMessages.ListManager_comment;
			final String commentsMsg = ListMessages.ListManager_comments;
			final String minorMsg = ListMessages.ListManager_key_minor;
			final String majorMsg = ListMessages.ListManager_key_major;

			// intervals
			final Interval t1 = Factory.getInstance().getInterval(1);
			final Interval t2 = Factory.getInstance().getInterval(2);
			final Interval t3 = Factory.getInstance().getInterval(3);
			final Interval t4 = Factory.getInstance().getInterval(4);
			final Interval t5 = Factory.getInstance().getInterval(5);
			final Interval t6 = Factory.getInstance().getInterval(6);
			final Interval t7 = Factory.getInstance().getInterval(7);
			final Interval t8 = Factory.getInstance().getInterval(8);
			final Interval t9 = Factory.getInstance().getInterval(9);
			final Interval t10 = Factory.getInstance().getInterval(10);
			final Interval t11 = Factory.getInstance().getInterval(11);

			// (ecclesiastical-) modes
			final Category modes = new Category(ListMessages.ListManager_ecclesiastical_modes);
			if (isActivated) {
				scaleList.addCategory(modes, scaleList.getRootCategory());
			}

			// major & natural minor (N.M.)
			final Category major = new Category(ListMessages.ListManager_major_and_natural_minor);
			if (isActivated) {
				scaleList.addCategory(major, modes);
			}

			final Scale aeolian = new Scale(ListMessages.ListManager_aeolian);
			aeolian.setAlsoKnownAsString(ListMessages.ListManager_aka_aeolian, false);
			aeolian.addInterval(t2, "2"); //$NON-NLS-1$
			aeolian.addInterval(t3, "b3"); //$NON-NLS-1$
			aeolian.addInterval(t5, "4"); //$NON-NLS-1$
			aeolian.addInterval(t7, "5"); //$NON-NLS-1$
			aeolian.addInterval(t8, "b6"); //$NON-NLS-1$
			aeolian.addInterval(t10, "b7"); //$NON-NLS-1$
			aeolian.setComment(orderMsg
					+ ListMessages.ListManager_aeolian_order
					+ "\r\n" + keyMsg + minorMsg + "\r\n" + typicalMsg + "b6, b7" + "\r\n" + "\r\n" + commentsMsg + "\r\n" + ListMessages.ListManager_aeolian_comment1 + "\r\n" + ListMessages.ListManager_aeolian_comment2); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
			if (isActivated) {
				scaleList.addElement(aeolian, major);
			}

			final Scale dorian = new Scale(ListMessages.ListManager_dorian);
			dorian.setAlsoKnownAsString("", false); //$NON-NLS-1$
			dorian.addInterval(t2, "2"); //$NON-NLS-1$
			dorian.addInterval(t3, "b3"); //$NON-NLS-1$
			dorian.addInterval(t5, "4"); //$NON-NLS-1$
			dorian.addInterval(t7, "5"); //$NON-NLS-1$
			dorian.addInterval(t9, "6"); //$NON-NLS-1$
			dorian.addInterval(t10, "b7"); //$NON-NLS-1$
			dorian.setComment(orderMsg
					+ ListMessages.ListManager_dorian_order
					+ "\r\n" + keyMsg + minorMsg + "\r\n" + typicalMsg + "6" + "\r\n" + "\r\n" + commentMsg + "\r\n" + ListMessages.ListManager_dorian_comment); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
			if (isActivated) {
				scaleList.addElement(dorian, major);
			}

			final Scale ionian = new Scale(ListMessages.ListManager_ionian);
			ionian.setAlsoKnownAsString(ListMessages.ListManager_aka_ionian, false);
			ionian.addInterval(t2, "2"); //$NON-NLS-1$
			ionian.addInterval(t4, "3"); //$NON-NLS-1$
			ionian.addInterval(t5, "4"); //$NON-NLS-1$
			ionian.addInterval(t7, "5"); //$NON-NLS-1$
			ionian.addInterval(t9, "6"); //$NON-NLS-1$
			ionian.addInterval(t11, "7"); //$NON-NLS-1$
			ionian.setComment(orderMsg + ListMessages.ListManager_ionian_order + "\r\n" + keyMsg + majorMsg); //$NON-NLS-1$
			if (isActivated) {
				scaleList.addElement(ionian, major);
			}

			final Scale locrian = new Scale(ListMessages.ListManager_locrian);
			locrian.setAlsoKnownAsString("", false); //$NON-NLS-1$
			locrian.addInterval(t1, "b2"); //$NON-NLS-1$
			locrian.addInterval(t3, "b3"); //$NON-NLS-1$
			locrian.addInterval(t5, "4"); //$NON-NLS-1$
			locrian.addInterval(t6, "b5"); //$NON-NLS-1$
			locrian.addInterval(t8, "b6"); //$NON-NLS-1$
			locrian.addInterval(t10, "b7"); //$NON-NLS-1$
			locrian.setComment(orderMsg
					+ ListMessages.ListManager_locrian_order
					+ "\r\n" + keyMsg + minorMsg + "\r\n" + typicalMsg + "b2, b5" + "\r\n" + "\r\n" + commentMsg + "\r\n" + ListMessages.ListManager_locrian_comment); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
			if (isActivated) {
				scaleList.addElement(locrian, major);
			}

			final Scale lydian = new Scale(ListMessages.ListManager_lydian);
			lydian.setAlsoKnownAsString("", false); //$NON-NLS-1$
			lydian.addInterval(t2, "2"); //$NON-NLS-1$
			lydian.addInterval(t4, "3"); //$NON-NLS-1$
			lydian.addInterval(t6, "#4"); //$NON-NLS-1$
			lydian.addInterval(t7, "5"); //$NON-NLS-1$
			lydian.addInterval(t9, "6"); //$NON-NLS-1$
			lydian.addInterval(t11, "7"); //$NON-NLS-1$
			lydian.setComment(orderMsg + ListMessages.ListManager_lydian_order
					+ "\r\n" + keyMsg + majorMsg + "\r\n" + typicalMsg + "#4"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			if (isActivated) {
				scaleList.addElement(lydian, major);
			}

			final Scale mixolydian = new Scale(ListMessages.ListManager_mixolydian);
			mixolydian.setAlsoKnownAsString("", false); //$NON-NLS-1$
			mixolydian.addInterval(t2, "2"); //$NON-NLS-1$
			mixolydian.addInterval(t4, "3"); //$NON-NLS-1$
			mixolydian.addInterval(t5, "4"); //$NON-NLS-1$
			mixolydian.addInterval(t7, "5"); //$NON-NLS-1$
			mixolydian.addInterval(t9, "6"); //$NON-NLS-1$
			mixolydian.addInterval(t10, "b7"); //$NON-NLS-1$
			mixolydian.setComment(orderMsg + ListMessages.ListManager_mixolydian_order
					+ "\r\n" + keyMsg + majorMsg + "\r\n" + typicalMsg + "b7"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			if (isActivated) {
				scaleList.addElement(mixolydian, major);
			}

			final Scale phrygian = new Scale(ListMessages.ListManager_phrygian);
			phrygian.setAlsoKnownAsString("", false); //$NON-NLS-1$
			phrygian.addInterval(t1, "b2"); //$NON-NLS-1$
			phrygian.addInterval(t3, "b3"); //$NON-NLS-1$
			phrygian.addInterval(t5, "4"); //$NON-NLS-1$
			phrygian.addInterval(t7, "5"); //$NON-NLS-1$
			phrygian.addInterval(t8, "b6"); //$NON-NLS-1$
			phrygian.addInterval(t10, "b7"); //$NON-NLS-1$
			phrygian.setComment(orderMsg + ListMessages.ListManager_phrygian_order
					+ "\r\n" + keyMsg + minorMsg + "\r\n" + typicalMsg + "b2"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			if (isActivated) {
				scaleList.addElement(phrygian, major);
			}

			// harmonic minor (H.M.)
			final Category harmonic = new Category(ListMessages.ListManager_harmonic_minor);
			if (isActivated) {
				scaleList.addCategory(harmonic, modes);
			}

			final Scale aeoloan7 = new Scale(ListMessages.ListManager_aeolian_7);
			aeoloan7.setAlsoKnownAsString(ListMessages.ListManager_aka_aeolian_7, false);
			aeoloan7.addInterval(t2, "2"); //$NON-NLS-1$
			aeoloan7.addInterval(t3, "b3"); //$NON-NLS-1$
			aeoloan7.addInterval(t5, "4"); //$NON-NLS-1$
			aeoloan7.addInterval(t7, "5"); //$NON-NLS-1$
			aeoloan7.addInterval(t8, "b6"); //$NON-NLS-1$
			aeoloan7.addInterval(t11, "7"); //$NON-NLS-1$
			aeoloan7.setComment(orderMsg
					+ ListMessages.ListManager_aeolian7_order
					+ "\r\n" + keyMsg + minorMsg + "\r\n" + "\r\n" + commentsMsg + "\r\n" + ListMessages.ListManager_aeolian7_comment1 + "\r\n" + ListMessages.ListManager_aeolian7_comment2 + "\r\n" + ListMessages.ListManager_aeolian7_comment3); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
			if (isActivated) {
				scaleList.addElement(aeoloan7, harmonic);
			}

			final Scale dorian4 = new Scale(ListMessages.ListManager_dorian_4);
			dorian4.setAlsoKnownAsString(ListMessages.ListManager_aka_dorian_4, false);
			dorian4.addInterval(t2, "2"); //$NON-NLS-1$
			dorian4.addInterval(t3, "b3"); //$NON-NLS-1$
			dorian4.addInterval(t6, "#4"); //$NON-NLS-1$
			dorian4.addInterval(t7, "5"); //$NON-NLS-1$
			dorian4.addInterval(t9, "6"); //$NON-NLS-1$
			dorian4.addInterval(t10, "b7"); //$NON-NLS-1$
			dorian4.setComment(orderMsg + ListMessages.ListManager_dorian4_order
					+ "\r\n" + "\r\n" + commentMsg + "\r\n" + ListMessages.ListManager_dorian4_comment); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			if (isActivated) {
				scaleList.addElement(dorian4, harmonic);
			}

			final Scale ionian5 = new Scale(ListMessages.ListManager_ionian_5);
			ionian5.setAlsoKnownAsString(ListMessages.ListManager_aka_ionian_5, false);
			ionian5.addInterval(t2, "2"); //$NON-NLS-1$
			ionian5.addInterval(t4, "3"); //$NON-NLS-1$
			ionian5.addInterval(t5, "4"); //$NON-NLS-1$
			ionian5.addInterval(t8, "#5"); //$NON-NLS-1$
			ionian5.addInterval(t9, "6"); //$NON-NLS-1$
			ionian5.addInterval(t11, "7"); //$NON-NLS-1$
			ionian5.setComment(orderMsg + ListMessages.ListManager_ionian5_order
					+ "\r\n" + "\r\n" + commentMsg + "\r\n" + ListMessages.ListManager_ionian5_comment); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			if (isActivated) {
				scaleList.addElement(ionian5, harmonic);
			}

			final Scale locrian6 = new Scale(ListMessages.ListManager_locrian_6);
			locrian6.setAlsoKnownAsString(ListMessages.ListManager_aka_locrian_6, false);
			locrian6.addInterval(t1, "b2"); //$NON-NLS-1$
			locrian6.addInterval(t3, "b3"); //$NON-NLS-1$
			locrian6.addInterval(t5, "4"); //$NON-NLS-1$
			locrian6.addInterval(t6, "b5"); //$NON-NLS-1$
			locrian6.addInterval(t9, "6"); //$NON-NLS-1$
			locrian6.addInterval(t10, "b7"); //$NON-NLS-1$
			locrian6.setComment(orderMsg
					+ ListMessages.ListManager_locrian6_order
					+ "\r\n" + "\r\n" + commentsMsg + "\r\n" + ListMessages.ListManager_locrian6_comment1 + "\r\n" + ListMessages.ListManager_locrian6_comment2); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			if (isActivated) {
				scaleList.addElement(locrian6, harmonic);
			}

			final Scale locrian4 = new Scale(ListMessages.ListManager_locrian_4);
			locrian4.setAlsoKnownAsString(ListMessages.ListManager_aka_locrian_4, false);
			locrian4.addInterval(t1, "b2"); //$NON-NLS-1$
			locrian4.addInterval(t3, "b3"); //$NON-NLS-1$
			locrian4.addInterval(t4, "b4"); //$NON-NLS-1$
			locrian4.addInterval(t6, "b5"); //$NON-NLS-1$
			locrian4.addInterval(t8, "b6"); //$NON-NLS-1$
			locrian4.addInterval(t9, "bb7"); //$NON-NLS-1$
			locrian4.setComment(orderMsg + ListMessages.ListManager_locrian4_order
					+ "\r\n" + "\r\n" + commentMsg + "\r\n" + ListMessages.ListManager_locrian5_comment); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			if (isActivated) {
				scaleList.addElement(locrian4, harmonic);
			}

			final Scale lydian2 = new Scale(ListMessages.ListManager_lydian_2);
			lydian2.setAlsoKnownAsString(ListMessages.ListManager_aka_lydian_2, false);
			lydian2.addInterval(t3, "#2"); //$NON-NLS-1$
			lydian2.addInterval(t4, "3"); //$NON-NLS-1$
			lydian2.addInterval(t6, "#4"); //$NON-NLS-1$
			lydian2.addInterval(t7, "5"); //$NON-NLS-1$
			lydian2.addInterval(t9, "6"); //$NON-NLS-1$
			lydian2.addInterval(t11, "7"); //$NON-NLS-1$
			lydian2.setComment(orderMsg + ListMessages.ListManager_lydian2_order
					+ "\r\n" + "\r\n" + commentMsg + "\r\n" + ListMessages.ListManager_lydian2_comment); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			if (isActivated) {
				scaleList.addElement(lydian2, harmonic);
			}

			final Scale phrygian3 = new Scale(ListMessages.ListManager_phrygian_3);
			phrygian3.setAlsoKnownAsString(ListMessages.ListManager_aka_phrygian_3, false);
			phrygian3.addInterval(t1, "b2"); //$NON-NLS-1$
			phrygian3.addInterval(t4, "3"); //$NON-NLS-1$
			phrygian3.addInterval(t5, "4"); //$NON-NLS-1$
			phrygian3.addInterval(t7, "5"); //$NON-NLS-1$
			phrygian3.addInterval(t8, "b6"); //$NON-NLS-1$
			phrygian3.addInterval(t10, "b7"); //$NON-NLS-1$
			phrygian3.setComment(orderMsg + ListMessages.ListManager_phrygian3_order
					+ "\r\n" + "\r\n" + commentMsg + "\r\n" + ListMessages.ListManager_phrygian3_comment); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			if (isActivated) {
				scaleList.addElement(phrygian3, harmonic);
			}

			// harmonic minor (H.M.)
			final Category melodic = new Category(ListMessages.ListManager_melodic_minor);
			if (isActivated) {
				scaleList.addCategory(melodic, modes);
			}

			final Scale aeolian3 = new Scale(ListMessages.ListManager_aeolian_3);
			aeolian3.setAlsoKnownAsString(ListMessages.ListManager_aka_aeolian_3, false);
			aeolian3.addInterval(t2, "2"); //$NON-NLS-1$
			aeolian3.addInterval(t4, "3"); //$NON-NLS-1$
			aeolian3.addInterval(t5, "4"); //$NON-NLS-1$
			aeolian3.addInterval(t7, "5"); //$NON-NLS-1$
			aeolian3.addInterval(t8, "b6"); //$NON-NLS-1$
			aeolian3.addInterval(t10, "b7"); //$NON-NLS-1$
			aeolian3.setComment(orderMsg
					+ ListMessages.ListManager_aeolian3_order
					+ "\r\n" + "\r\n" + commentsMsg + "\r\n" + ListMessages.ListManager_aeolian3_comment1 + "\r\n" + ListMessages.ListManager_aeolian3_comment2 + "\r\n" + ListMessages.ListManager_aeolian3_comment3); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
			if (isActivated) {
				scaleList.addElement(aeolian3, melodic);
			}

			final Scale aeolian6 = new Scale(ListMessages.ListManager_aeolian_6);
			aeolian6.setAlsoKnownAsString(ListMessages.ListManager_aka_aeolian_6, false);
			aeolian6.addInterval(t2, "2"); //$NON-NLS-1$
			aeolian6.addInterval(t3, "b3"); //$NON-NLS-1$
			aeolian6.addInterval(t5, "4"); //$NON-NLS-1$
			aeolian6.addInterval(t7, "5"); //$NON-NLS-1$
			aeolian6.addInterval(t9, "6"); //$NON-NLS-1$
			aeolian6.addInterval(t11, "7"); //$NON-NLS-1$
			aeolian6.setComment(orderMsg
					+ ListMessages.ListManager_aeolian6_order
					+ "\r\n" + "\r\n" + commentsMsg + "\r\n" + ListMessages.ListManager_aeolian6_comment1 + "\r\n" + ListMessages.ListManager_aeolian6_comment2 + "\r\n" + ListMessages.ListManager_aeolian6_comment3 + "\r\n" + ListMessages.ListManager_aeolian6_comment4); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
			if (isActivated) {
				scaleList.addElement(aeolian6, melodic);
			}

			final Scale locrian2 = new Scale(ListMessages.ListManager_locrian_2);
			locrian2.setAlsoKnownAsString(ListMessages.ListManager_aka_locrian_2, false);
			locrian2.addInterval(t2, "2"); //$NON-NLS-1$
			locrian2.addInterval(t3, "b3"); //$NON-NLS-1$
			locrian2.addInterval(t5, "4"); //$NON-NLS-1$
			locrian2.addInterval(t6, "b5"); //$NON-NLS-1$
			locrian2.addInterval(t8, "b6"); //$NON-NLS-1$
			locrian2.addInterval(t10, "b7"); //$NON-NLS-1$
			locrian2.setComment(orderMsg
					+ ListMessages.ListManager_locrian2_order
					+ "\r\n" + "\r\n" + commentsMsg + "\r\n" + ListMessages.ListManager_locrian2_comment1 + "\r\n" + ListMessages.ListManager_locrian2_comment2); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			if (isActivated) {
				scaleList.addElement(locrian2, melodic);
			}

			final Scale locrianb4 = new Scale(ListMessages.ListManager_locrian_b4);
			locrianb4.setAlsoKnownAsString(ListMessages.ListManager_aka_locrian_b4, false);
			locrianb4.addInterval(t1, "b2"); //$NON-NLS-1$
			locrianb4.addInterval(t3, "b3"); //$NON-NLS-1$
			locrianb4.addInterval(t4, "b4"); //$NON-NLS-1$
			locrianb4.addInterval(t6, "b5"); //$NON-NLS-1$
			locrianb4.addInterval(t8, "b6"); //$NON-NLS-1$
			locrianb4.addInterval(t10, "b7"); //$NON-NLS-1$
			locrianb4.setComment(orderMsg + ListMessages.ListManager_locrianb4_order
					+ "\r\n" + "\r\n" + commentMsg + "\r\n" + ListMessages.ListManager_locrianb4_comment); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			if (isActivated) {
				scaleList.addElement(locrianb4, melodic);
			}

			final Scale lydian5 = new Scale(ListMessages.ListManager_lydian_5);
			lydian5.setAlsoKnownAsString(ListMessages.ListManager_aka_lydian_5, false);
			lydian5.addInterval(t2, "2"); //$NON-NLS-1$
			lydian5.addInterval(t4, "3"); //$NON-NLS-1$
			lydian5.addInterval(t6, "#4"); //$NON-NLS-1$
			lydian5.addInterval(t8, "#5"); //$NON-NLS-1$
			lydian5.addInterval(t9, "6"); //$NON-NLS-1$
			lydian5.addInterval(t11, "7"); //$NON-NLS-1$
			lydian5.setComment(orderMsg + ListMessages.ListManager_lydian5_order
					+ "\r\n" + "\r\n" + commentMsg + "\r\n" + ListMessages.ListManager_lydian5_comment); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			if (isActivated) {
				scaleList.addElement(lydian5, melodic);
			}

			final Scale lydian7 = new Scale(ListMessages.ListManager_lydian_7);
			lydian7.setAlsoKnownAsString(ListMessages.ListManager_aka_lydian_7, false);
			lydian7.addInterval(t2, "2"); //$NON-NLS-1$
			lydian7.addInterval(t4, "3"); //$NON-NLS-1$
			lydian7.addInterval(t6, "#4"); //$NON-NLS-1$
			lydian7.addInterval(t7, "5"); //$NON-NLS-1$
			lydian7.addInterval(t9, "6"); //$NON-NLS-1$
			lydian7.addInterval(t10, "b7"); //$NON-NLS-1$
			lydian7.setComment(orderMsg
					+ ListMessages.ListManager_lydian7_order
					+ "\r\n" + "\r\n" + commentsMsg + "\r\n" + ListMessages.ListManager_lydian7_comment1 + "\r\n" + ListMessages.ListManager_lydian7_comment2 + "\r\n" + ListMessages.ListManager_lydian7_comment3); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
			if (isActivated) {
				scaleList.addElement(lydian7, melodic);
			}

			final Scale phrygian6 = new Scale(ListMessages.ListManager_phrygian_6);
			phrygian6.setAlsoKnownAsString(ListMessages.ListManager_aka_phrygian_6, false);
			phrygian6.addInterval(t1, "b2"); //$NON-NLS-1$
			phrygian6.addInterval(t3, "b3"); //$NON-NLS-1$
			phrygian6.addInterval(t5, "4"); //$NON-NLS-1$
			phrygian6.addInterval(t7, "5"); //$NON-NLS-1$
			phrygian6.addInterval(t9, "6"); //$NON-NLS-1$
			phrygian6.addInterval(t10, "b7"); //$NON-NLS-1$
			phrygian6
					.setComment(orderMsg
							+ ListMessages.ListManager_phrygian6_order
							+ "\r\n" + "\r\n" + commentsMsg + "\r\n" + ListMessages.ListManager_phrygian6_comment1 + "\r\n" + ListMessages.ListManager_phrygian6_comment2 + "\r\n" + ListMessages.ListManager_phrygian6_comment3); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
			if (isActivated) {
				scaleList.addElement(phrygian6, melodic);
			}

			// pentatonic
			final Category pentatonic = new Category(ListMessages.ListManager_pentatonic);
			if (isActivated) {
				scaleList.addCategory(pentatonic, scaleList.getRootCategory());
			}

			final Scale majorP = new Scale(ListMessages.ListManager_major_pentatonic);
			majorP.setAlsoKnownAsString(ListMessages.ListManager_aka_major_pentatonic, false);
			majorP.addInterval(t2, "2"); //$NON-NLS-1$
			majorP.addInterval(t4, "3"); //$NON-NLS-1$
			majorP.addInterval(t7, "5"); //$NON-NLS-1$
			majorP.addInterval(t9, "6"); //$NON-NLS-1$
			majorP.setComment(orderMsg + ListMessages.ListManager_majorP_order);
			if (isActivated) {
				scaleList.addElement(majorP, pentatonic);
			}

			final Scale minorP = new Scale(ListMessages.ListManager_minor_pentatonic);
			minorP.setAlsoKnownAsString(ListMessages.ListManager_aka_minor_pentatonic, false);
			minorP.addInterval(t3, "b3"); //$NON-NLS-1$
			minorP.addInterval(t5, "4"); //$NON-NLS-1$
			minorP.addInterval(t7, "5"); //$NON-NLS-1$
			minorP.addInterval(t10, "b7"); //$NON-NLS-1$
			minorP.setComment(orderMsg + ListMessages.ListManager_minorP_order
					+ "\r\n" + "\r\n" + commentMsg + "\r\n" + ListMessages.ListManager_minorP_comment); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			if (isActivated) {
				scaleList.addElement(minorP, pentatonic);
			}

			final Scale neutralP = new Scale(ListMessages.ListManager_neutral_pentatonic);
			neutralP.setAlsoKnownAsString("", false); //$NON-NLS-1$
			neutralP.addInterval(t2, "2"); //$NON-NLS-1$
			neutralP.addInterval(t5, "4"); //$NON-NLS-1$
			neutralP.addInterval(t7, "5"); //$NON-NLS-1$
			neutralP.addInterval(t10, "b7"); //$NON-NLS-1$
			neutralP.setComment(orderMsg + ListMessages.ListManager_neutralP_order);
			if (isActivated) {
				scaleList.addElement(neutralP, pentatonic);
			}

			final Scale rockP = new Scale(ListMessages.ListManager_rock_pentatonic);
			rockP.setAlsoKnownAsString("", false); //$NON-NLS-1$
			rockP.addInterval(t3, "b3"); //$NON-NLS-1$
			rockP.addInterval(t5, "4"); //$NON-NLS-1$
			rockP.addInterval(t8, "#5"); //$NON-NLS-1$
			rockP.addInterval(t10, "b7"); //$NON-NLS-1$
			rockP.setComment(orderMsg + ListMessages.ListManager_rockP_order);
			if (isActivated) {
				scaleList.addElement(rockP, pentatonic);
			}

			final Scale scottishP = new Scale(ListMessages.ListManager_scottish_pentatonic);
			scottishP.setAlsoKnownAsString(ListMessages.ListManager_aka_scottish_pentatonic, false);
			scottishP.addInterval(t2, "2"); //$NON-NLS-1$
			scottishP.addInterval(t5, "4"); //$NON-NLS-1$
			scottishP.addInterval(t7, "5"); //$NON-NLS-1$
			scottishP.addInterval(t9, "6"); //$NON-NLS-1$
			scottishP.setComment(orderMsg + ListMessages.ListManager_scottishP_order);
			if (isActivated) {
				scaleList.addElement(scottishP, pentatonic);
			}

			// blues
			final Category blues = new Category(ListMessages.ListManager_blues);
			if (isActivated) {
				scaleList.addCategory(blues, scaleList.getRootCategory());
			}

			final Scale bluesS = new Scale(ListMessages.ListManager_blues_scale);
			bluesS.setAlsoKnownAsString("", false); //$NON-NLS-1$
			bluesS.addInterval(t3, "b3"); //$NON-NLS-1$
			bluesS.addInterval(t5, "4"); //$NON-NLS-1$
			bluesS.addInterval(t6, "b5"); //$NON-NLS-1$
			bluesS.addInterval(t7, "5"); //$NON-NLS-1$
			bluesS.addInterval(t10, "b7"); //$NON-NLS-1$
			bluesS.setComment(commentMsg + "\r\n" + ListMessages.ListManager_bluesS_comment); //$NON-NLS-1$
			if (isActivated) {
				scaleList.addElement(bluesS, blues);
			}

			if (!isActivated) {
				final Category root = scaleList.getRootCategory();
				scaleList.addElement(locrian, root);
				scaleList.addElement(aeolian, root);
				scaleList.addElement(dorian, root);
				scaleList.addElement(mixolydian, root);
				scaleList.addElement(scottishP, root);
				scaleList.addElement(bluesS, root);
			}

		} catch (final Exception e) {
			// in case of an exception the scales are added programmatically
		} finally {
			scaleList.removeChangeListenerFilter(filter);
			scaleList.changedWholeList();
		}
	}
}
