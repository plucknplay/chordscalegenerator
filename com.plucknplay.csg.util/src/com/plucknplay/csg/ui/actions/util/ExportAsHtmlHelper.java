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
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import com.plucknplay.csg.core.model.Categorizable;
import com.plucknplay.csg.core.model.Factory;
import com.plucknplay.csg.core.model.Instrument;
import com.plucknplay.csg.core.model.Interval;
import com.plucknplay.csg.core.model.IntervalContainer;
import com.plucknplay.csg.core.model.Note;
import com.plucknplay.csg.core.model.sets.Category;

public final class ExportAsHtmlHelper {

	private ExportAsHtmlHelper() {
	}

	public static void exportAsHtml(final Shell shell, final List<Category> categories) {

		if (shell == null || categories == null || categories.isEmpty()) {
			return;
		}

		final boolean addCategoryColumn = MessageDialog.openQuestion(shell, "Tabellentyp",
				"Soll eine erste Spalte mit den Kategorie-Informationen eingefügt werden?");

		final FileDialog dialog = new FileDialog(shell, SWT.SAVE);
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
					// needed for IOExceptions to be thrown
					printWriter.flush(); 

					for (final Category category : categories) {
						printCategory(printWriter, category, addCategoryColumn);
					}

					printWriter.close();
				}

			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void printCategory(final PrintWriter printWriter, final Category category,
			final boolean addCategoryColumn) {

		printWriter.println();
		printWriter.println("<!-- " + category.getName() + "-->");
		printWriter.println("<tr>");

		final List<Categorizable> elements = new ArrayList<Categorizable>(category.getElements());
		if (addCategoryColumn) {
			if (!elements.isEmpty()) {
				printWriter.println("\t<td rowspan=\"" + elements.size() + "\"><b>" + category.getName() + "</b></td>");
			} else {
				printWriter.println("\t<td colspan=\"4\"><b>" + category.getName() + "</b></td></tr>");
			}
		}

		Collections.sort(elements, new Comparator<Categorizable>() {
			@Override
			public int compare(final Categorizable o1, final Categorizable o2) {
				if (o1 instanceof Instrument && o2 instanceof Instrument) {
					return Integer.valueOf(((Instrument) o1).getStringCount()).compareTo(
							Integer.valueOf(((Instrument) o2).getStringCount()));
				}
				if (o1 instanceof IntervalContainer && o2 instanceof IntervalContainer) {
					return Integer.valueOf(((IntervalContainer) o1).getIntervals().size()).compareTo(
							Integer.valueOf(((IntervalContainer) o2).getIntervals().size()));
				}
				return 0;
			}
		});

		boolean first = true;
		for (final Categorizable categorizable : elements) {

			if (!first) {
				printWriter.println("<tr>");
			}

			if (categorizable instanceof IntervalContainer) {
				printElement(printWriter, (IntervalContainer) categorizable, category, addCategoryColumn);
			} else {
				printElement(printWriter, (Instrument) categorizable, category, addCategoryColumn);
			}

			printWriter.println("</tr>");
			first = false;
		}
	}

	private static void printElement(final PrintWriter printWriter, final IntervalContainer element,
			final Category parentCategory, final boolean addCategoryColumn) {

		// name
		printWriter.println("\t<td>" + element.getName() + "</td>");

		// intervals
		final StringBuffer buf = new StringBuffer();
		buf.append("\t<td>");
		for (final Iterator<Interval> iter = element.getIntervals().iterator(); iter.hasNext();) {
			final Interval interval = iter.next();
			buf.append(element.getIntervalName(interval));
			if (iter.hasNext()) {
				buf.append("-");
			}
		}
		buf.append("</td>");
		printWriter.println(buf.toString());

		// aka
		String aka = element.getAlsoKnownAsString();
		if (aka == null || "".equals(aka.trim())) {
			aka = "-";
		}
		printWriter.println("\t<td>" + aka + "</td>");

	}

	private static void printElement(final PrintWriter printWriter, final Instrument element,
			final Category parentCategory, final boolean addCategoryColumn) {

		// name
		String name = element.getName().replaceAll(parentCategory.getName(), "").trim();
		name = name.substring(0, name.indexOf('(')) + name.substring(name.indexOf(')') + 1);
		name = name.replace("-", "").trim();
		while (name.contains("  ")) {
			name = name.replace("  ", " ");
		}
		if ("".equals(name)) {
			name = "-";
		}
		printWriter.println("\t<td>" + name + "</td>");

		// strings
		printWriter.println("\t<td>" + element.getStringCount() + (element.hasDoubledStrings() ? "x2" : "") + "</td>");

		// tuning
		final StringBuffer buf = new StringBuffer();
		buf.append("\t<td>");
		for (int i = element.getStringCount(); i > 0; i--) {
			final Note note = element.getNoteOfEmptyString(i);
			buf.append(note.getAbsoluteNameAug());

			if (element.hasDoubledStrings()) {
				if (element.isDoubledStringWithOctaveJump(i)) {
					final Note note2 = Factory.getInstance().getNote(note.getValue(), note.getLevel() + 1);
					buf.append(note2.getAbsoluteNameAug());
				} else {
					buf.append(note.getAbsoluteNameAug());
				}
			}

			if (i > 1) {
				buf.append(" - ");
			}
		}
		buf.append("</td>");
		printWriter.println(buf.toString());
	}
}
