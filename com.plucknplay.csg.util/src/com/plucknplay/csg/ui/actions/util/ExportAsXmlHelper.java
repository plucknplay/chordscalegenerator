/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;

import com.plucknplay.csg.core.model.Categorizable;
import com.plucknplay.csg.core.model.Chord;
import com.plucknplay.csg.core.model.Instrument;
import com.plucknplay.csg.core.model.sets.Category;
import com.plucknplay.csg.ui.actions.general.XMLExportImport;

public final class ExportAsXmlHelper {

	private ExportAsXmlHelper() {
	}

	public static void exportAsXml(final Shell shell, final List<Category> categories) {

		if (shell == null || categories == null || categories.isEmpty()) {
			return;
		}

		final boolean english = MessageDialog.openQuestion(shell, "Sprachauswahl",
				"Sollen die Dateinamen in ENGLISCH exportiert werden?");

		final DirectoryDialog dialog = new DirectoryDialog(shell);
		dialog.setText("Choose a directory");

		final String directory = dialog.open();
		if (directory != null) {

			final XMLExportImport exporter = new XMLExportImport();
			exporter.setIgnoreCurrentInstrument(true);
			final String type = getType(categories.get(0));

			int elementCount = 0;
			for (final Category category : categories) {
				elementCount += category.getAllElements().size();
			}

			try {

				exporter.storeToXML(type, new File(directory + "/" + getAllString(english).toUpperCase() + "_"
						+ getTypeString(type, english).toUpperCase() + "_(" + elementCount + ").xml"), categories);
				exportCategories(exporter, type, english, categories, directory);

			} catch (final ParserConfigurationException e) {
				e.printStackTrace();
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void exporAsXML(final Category category, final String directory, final boolean english)
			throws IOException, ParserConfigurationException {

		final XMLExportImport exporter = new XMLExportImport();
		exporter.setIgnoreCurrentInstrument(true);
		final String type = getType(category);

		for (final Categorizable element : category.getElements()) {
			exporter.storeToXML(type, new File(directory + "/" + updateFileName(element.getName()) + ".xml"),
					Collections.singletonList(element));
		}

		exportCategories(exporter, type, english, new ArrayList<Category>(category.getCategories()), directory);
	}

	private static void exportCategories(final XMLExportImport exporter, final String type, final boolean english,
			final List<Category> categories, final String directory) throws IOException, ParserConfigurationException {
		for (final Category category : categories) {
			final String filename = getAllString(english).toUpperCase() + "_"
					+ updateFileName(category.getName() + "_(" + category.getAllElements().size() + ").xml");
			exporter.storeToXML(type, new File(directory + "/" + filename), Collections.singletonList(category));

			final String newDirectory = directory + "/" + updateName(category.getName());
			if (new File(newDirectory).mkdir()) {
				exporAsXML(category, newDirectory, english);
			}
		}
	}

	private static String getType(final Category category) {
		final Categorizable firstElement = category.getFirstElement();
		return firstElement instanceof Instrument ? XMLExportImport.TYPE_INSTRUMENTS
				: firstElement instanceof Chord ? XMLExportImport.TYPE_CHORDS : XMLExportImport.TYPE_SCALES;
	}

	private static String updateName(final String name) {
		return name.replaceAll("Ä", "Ae").replaceAll("Ö", "Oe").replaceAll("Ü", "Ue").replaceAll("ä", "ae")
				.replaceAll("ö", "oe").replaceAll("ü", "ue").replaceAll("ß", "ss");
	}

	private static String updateFileName(final String name) {
		String result = updateName(name).replaceAll("-", " ").replaceAll("/", " ").replaceAll("!", "")
				.replaceAll(",", "").replaceAll("'", "").replaceAll("´", "").replaceAll("`", "").replaceAll("()", "")
				.replaceAll(" ", "_").trim();
		while (result.contains("__")) {
			result = result.replaceAll("__", "_");
		}
		return result;
	}

	private static String getAllString(final boolean english) {
		return english ? "All" : "Alle";
	}

	private static String getTypeString(final String type, final boolean english) {
		if (type.equals(XMLExportImport.TYPE_INSTRUMENTS)) {
			return english ? "Instruments" : "Instrumente";
		} else if (type.equals(XMLExportImport.TYPE_CHORDS)) {
			return english ? "Chords" : "Akkorde";
		}
		return english ? "Scales" : "Skalen";
	}
}
