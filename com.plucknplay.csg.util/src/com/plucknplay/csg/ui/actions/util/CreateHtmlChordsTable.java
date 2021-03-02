/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import com.plucknplay.csg.core.calculation.CalculationDescriptor;
import com.plucknplay.csg.core.calculation.ICalculator;
import com.plucknplay.csg.core.model.Chord;
import com.plucknplay.csg.core.model.Factory;
import com.plucknplay.csg.core.model.Griptable;
import com.plucknplay.csg.core.model.Instrument;
import com.plucknplay.csg.core.model.Note;
import com.plucknplay.csg.core.model.sets.InstrumentList;
import com.plucknplay.csg.ui.util.CalculatorUtil;

public class CreateHtmlChordsTable implements IWorkbenchWindowActionDelegate {

	private static List<Note> notes;
	private static List<Chord> chords;

	private IWorkbenchWindow window;

	static {
		// create fundamtenal tone list
		notes = new ArrayList<Note>();
		notes.add(Factory.getInstance().getNote(0));
		notes.add(Factory.getInstance().getNote(2));
		notes.add(Factory.getInstance().getNote(4));
		notes.add(Factory.getInstance().getNote(5));
		notes.add(Factory.getInstance().getNote(7));
		notes.add(Factory.getInstance().getNote(9));
		notes.add(Factory.getInstance().getNote(11));

		// create chord list
		chords = new ArrayList<Chord>();
		final Chord maj = new Chord("maj");
		maj.addInterval(Factory.getInstance().getInterval(0), "1");
		maj.addInterval(Factory.getInstance().getInterval(4), "3");
		maj.addInterval(Factory.getInstance().getInterval(7), "5");
		chords.add(maj);

		final Chord min = new Chord("min");
		min.addInterval(Factory.getInstance().getInterval(0), "1");
		min.addInterval(Factory.getInstance().getInterval(3), "b3");
		min.addInterval(Factory.getInstance().getInterval(7), "5");
		chords.add(min);

		// Chord sus2 = new Chord("sus2");
		// sus2.addInterval(Factory.getInstance().getInterval(0), "1");
		// sus2.addInterval(Factory.getInstance().getInterval(2), "2");
		// sus2.addInterval(Factory.getInstance().getInterval(7), "5");
		// chords.add(sus2);

		final Chord maj7 = new Chord(maj);
		maj7.setName("7");
		maj7.addInterval(Factory.getInstance().getInterval(11), "7");
		chords.add(maj7);
	}

	@Override
	public void init(final IWorkbenchWindow window) {
		this.window = window;
	}

	@Override
	public void run(final IAction action) {

		final Instrument currentInstrument = InstrumentList.getInstance().getCurrentInstrument();
		if (currentInstrument == null) {
			return;
		}

		final boolean english = MessageDialog.openQuestion(window.getShell(), "Sprache",
				"Soll die Tabelle in ENGLISCH erstellt werden?");

		// open dialog
		final FileDialog dialog = new FileDialog(window.getShell(), SWT.SAVE);
		dialog.setText("Choose a .txt. file");
		dialog.setFilterExtensions(new String[] { "*.txt" }); //$NON-NLS-1$
		dialog.setFilterNames(new String[] { "TXT" }); //$NON-NLS-1$

		final String result = dialog.open();
		if (result != null) {

			try {
				final File file = new File(result);
				if (file.createNewFile()) {
					final PrintWriter printWriter = new PrintWriter(new FileWriter(file, false), false);

					final String firstLine = "";
					printWriter.println(firstLine);
					printWriter.flush(); // needed for IOExceptions to be
											// thrown.

					printWriter.println("<div id=\"table\">");
					printWriter.println("\n\t<table>");
					printWriter.println("\t\t<tr>");
					printWriter.println("\t\t\t<th></th>");
					printWriter.println("\t\t\t<th>" + (english ? "maj" : "Dur") + "</th>");
					printWriter.println("\t\t\t<th>" + (english ? "min" : "moll") + "</th>");
					printWriter.println("\t\t\t<th>7</th>");
					printWriter.println("\t\t</tr>");

					// create calculator
					final ICalculator calculator = CalculatorUtil.getCalculator();

					int fretRange = 3;
					StringBuffer resultBuffer = new StringBuffer();
					for (int f = 3; f <= 4; f++) {
						resultBuffer = new StringBuffer();
						int minResult = Integer.MAX_VALUE;
						for (final Note note : notes) {
							resultBuffer.append("\t\t<tr>\n");
							resultBuffer.append("\t\t\t<td><b>" + note.getRelativeName() + "</b></td>\n");
							for (final Chord chord : chords) {
								chord.setRootNote(note);
								final CalculationDescriptor desc = createCalculationDescriptor(chord,
										currentInstrument, f);
								Set<Griptable> griptables = new HashSet<Griptable>();
								try {
									griptables = calculator.calculateCorrespondingGriptablesOfChord(desc,
											new NullProgressMonitor(), 1000);
								} catch (final InterruptedException e) {
								}
								final int numberOfGriptables = griptables.size();
								resultBuffer.append("\t\t\t<td>" + numberOfGriptables + "</td>\n");
								if (numberOfGriptables < minResult) {
									minResult = numberOfGriptables;
								}
							}
							resultBuffer.append("\t\t</tr>\n");
						}
						if (minResult >= 20 || f == 4) {
							fretRange = f;
							break;
						}
					}

					printWriter.println(resultBuffer.toString());
					printWriter.println("\t</table>");

					final StringBuffer computationBuffer = new StringBuffer("<u>");

					computationBuffer.append(english ? "Basis of computation:" : "Berechnungsgrundlage:");
					computationBuffer.append("</u><br/>");

					final String instrumentName = currentInstrument.getName();
					final int indexOf = instrumentName.indexOf(" (");
					final String formattedInstrumentName = instrumentName.substring(0, indexOf > 0 ? indexOf
							: instrumentName.length());

					computationBuffer.append(formattedInstrumentName);
					computationBuffer.append(", ");
					computationBuffer.append(currentInstrument.getFretCount());
					computationBuffer.append(english ? " Frets, Tuning " : " B&#252;nde, Stimmung ");
					for (int i = currentInstrument.getStringCount() - 1; i >= 0; i--) {
						final Note note = currentInstrument.getNotesOfEmptyStrings()[i];
						computationBuffer.append(note.getAbsoluteNameAug());
						computationBuffer.append(i != 0 ? "-" : ", ");
					}
					computationBuffer.append(english ? "Max. Fret Span " : "max. Bundbreite ");
					computationBuffer.append(fretRange);

					printWriter.println("\n\t<p id=\"chords_table_caption\">");
					printWriter.println("\t" + computationBuffer.toString());
					printWriter.println("\t</p>");

					printWriter.println("\n</div>");
					printWriter.close();
				}

			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
	}

	private CalculationDescriptor createCalculationDescriptor(final Chord chord, final Instrument instrument,
			final int fretRange) {

		final CalculationDescriptor descriptor = new CalculationDescriptor();

		descriptor.setChord(chord);
		descriptor.setBassTone(null);
		descriptor.setLeadTone(null);
		descriptor.setMinLevel(0);
		descriptor.setMaxLevel(3);
		descriptor.setMinString(0);
		descriptor.setMaxString(instrument.getStringCount());
		descriptor.setMinFret(0);
		descriptor.setMaxFret(instrument.getFretCount());
		descriptor.setToneNumber(null);
		descriptor.setFretGripRange(fretRange);
		descriptor.setEmptyStrings(true);
		descriptor.setMutedStrings(true);
		descriptor.setOnlyPacked(false);
		descriptor.setOnlySingleMutedStrings(false);
		descriptor.setDoubledTones(true);
		descriptor.setAscendingDescending(false);
		descriptor.setWithout1st(false);
		descriptor.setWithout3rd(false);
		descriptor.setWithout5th(false);

		return descriptor;
	}

	@Override
	public void selectionChanged(final IAction action, final ISelection selection) {
		// do nothing
	}

	@Override
	public void dispose() {
		// do nothing
	}
}
