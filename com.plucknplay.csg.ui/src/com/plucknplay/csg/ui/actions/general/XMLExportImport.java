/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions.general;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import com.plucknplay.csg.core.model.Categorizable;
import com.plucknplay.csg.core.model.Chord;
import com.plucknplay.csg.core.model.Factory;
import com.plucknplay.csg.core.model.Instrument;
import com.plucknplay.csg.core.model.Interval;
import com.plucknplay.csg.core.model.IntervalContainer;
import com.plucknplay.csg.core.model.Note;
import com.plucknplay.csg.core.model.Scale;
import com.plucknplay.csg.core.model.Unit;
import com.plucknplay.csg.core.model.enums.Clef;
import com.plucknplay.csg.core.model.enums.IntervalNamesMode;
import com.plucknplay.csg.core.model.sets.Category;
import com.plucknplay.csg.core.model.sets.CategoryList;
import com.plucknplay.csg.core.model.sets.ChordList;
import com.plucknplay.csg.core.model.sets.InstrumentList;
import com.plucknplay.csg.core.model.sets.ScaleList;
import com.plucknplay.csg.core.util.NamesUtil;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.listeners.CategoryViewFilter;
import com.plucknplay.csg.ui.util.XMLUtil;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

/**
 * This class is responsible for the XML import and export.
 */
public class XMLExportImport {

	public static final String TYPE_SCALES = "scales"; //$NON-NLS-1$
	public static final String TYPE_CHORDS = "chords"; //$NON-NLS-1$
	public static final String TYPE_INSTRUMENTS = "instruments"; //$NON-NLS-1$

	private static final String CLEF = "clef"; //$NON-NLS-1$
	private static final String MIDI_INSTRUMENT_NUMBER = "midiInstrumentNumber"; //$NON-NLS-1$
	private static final String OCTAVE_JUMP = "octaveJump"; //$NON-NLS-1$
	private static final String STRING_NUMBER = "stringNumber"; //$NON-NLS-1$
	private static final String LEVEL = "level"; //$NON-NLS-1$
	private static final String NOTE2 = "note"; //$NON-NLS-1$
	private static final String PITCH = "pitch"; //$NON-NLS-1$
	private static final String DOUBLED_STRINGS = "doubled_strings"; //$NON-NLS-1$
	private static final String FRETLESS = "fretless"; //$NON-NLS-1$
	private static final String LEFTY_RIGHTY = "lefty_righty"; //$NON-NLS-1$
	private static final String UNIT = "unit"; //$NON-NLS-1$
	private static final String SCALE_LENGTH = "scaleLength"; //$NON-NLS-1$
	private static final String STRING_COUNT = "stringCount"; //$NON-NLS-1$
	private static final String IS_CURRENT = "isCurrent"; //$NON-NLS-1$
	private static final String VALUE = "value"; //$NON-NLS-1$
	private static final String INTERVAL = "interval"; //$NON-NLS-1$
	private static final String INTERVALS = "intervals"; //$NON-NLS-1$
	private static final String AKA_NAME = "akaName"; //$NON-NLS-1$
	private static final String DESCRIPTION = "description"; //$NON-NLS-1$
	private static final String NAME = "name"; //$NON-NLS-1$
	private static final String INSTRUMENT = "instrument"; //$NON-NLS-1$
	private static final String INTERVAL_CONTAINER = "interval_container"; //$NON-NLS-1$
	private static final String FOLDER = "folder"; //$NON-NLS-1$
	private static final String TYPE = "type"; //$NON-NLS-1$

	private static final String ENCODING = "UTF-8"; //$NON-NLS-1$

	private boolean ignoreCurrentInstrument;

	public XMLExportImport() {
		ignoreCurrentInstrument = false;
	}

	/* --- LOADING --- */

	/**
	 * Loads the given xml file into the specified category.
	 * 
	 * @param type
	 *            the type of element to be loaded, use XMLExportImport.TYPE_*
	 * @param input
	 *            the input, either a {@link File} or an {@link InputStream},
	 *            must not be null
	 * @param category
	 *            the category, must not be null
	 * 
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public void loadFromXML(final String type, final Object input, final Category category)
			throws ParserConfigurationException, SAXException, IOException {
		if (category == null || input == null || !(input instanceof File || input instanceof InputStream)) {
			throw new IllegalArgumentException();
		}

		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setNamespaceAware(false);
		factory.setIgnoringComments(true);

		final DocumentBuilder builder = factory.newDocumentBuilder();
		final Document document = input instanceof File ? builder.parse((File) input) : builder
				.parse((InputStream) input);
		final Element root = document.getDocumentElement();
		if (!type.equals(TYPE_INSTRUMENTS) && !root.getAttribute(TYPE).equals(type)) {
			throw new IllegalArgumentException();
		}

		// parse root element
		final CategoryList categoryList = getCategoryList(type);
		final CategoryViewFilter filter = new CategoryViewFilter();
		categoryList.addChangeListenerFilter(filter);

		final NodeList children = root.getChildNodes();
		for (int c = 0; c < children.getLength(); c++) {
			final Node node = children.item(c);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				final Element element = (Element) node;
				loadElement(category, categoryList, element);
			}
		}

		categoryList.removeChangeListenerFilter(filter);
		categoryList.changedWholeList();
	}

	/**
	 * Helper method to forward the given element to its corresponding method.
	 * 
	 * @param parentCategory
	 *            the category the element shall be added, must not be null
	 * @param categoryList
	 *            the category list the category belongs to, must not be null
	 * @param element
	 *            the element ot be added, must not be null
	 */
	private void loadElement(final Category parentCategory, final CategoryList categoryList, final Element element) {
		if (parentCategory == null || categoryList == null || element == null) {
			throw new IllegalArgumentException();
		}

		if (element.getTagName().equals(FOLDER)) {
			loadFolder(parentCategory, categoryList, element);
		} else if (element.getTagName().equals(INTERVAL_CONTAINER)) {
			loadIntervalContainer(parentCategory, categoryList, element);
		} else if (element.getTagName().equals(INSTRUMENT)) {
			loadInstrument(parentCategory, element);
		}
	}

	/**
	 * Loads the given folder element into the specified category.
	 * 
	 * @param parentCategory
	 *            the category, must not be null
	 * @param categoryList
	 *            the category list the category belongs to, must not be null
	 * @param element
	 *            the folder element to be added, must not be null
	 */
	private void loadFolder(final Category parentCategory, final CategoryList categoryList, final Element element) {
		if (parentCategory == null || categoryList == null || element == null) {
			throw new IllegalArgumentException();
		}

		final String name = element.getAttribute(NAME);
		Category newCategory = parentCategory.getCategory(name);
		if (newCategory == null) {
			newCategory = new Category(name);
			categoryList.addCategory(newCategory, parentCategory);
		}
		final NodeList folderChildren = element.getChildNodes();
		for (int f = 0; f < folderChildren.getLength(); f++) {
			final Node currentChildNode = folderChildren.item(f);
			if (currentChildNode.getNodeType() == Node.ELEMENT_NODE) {
				loadElement(newCategory, categoryList, (Element) currentChildNode);
			}
		}
	}

	/**
	 * Loads the given interval container element into the specified category.
	 * 
	 * @param parentCategory
	 *            the category, must not be null
	 * @param categoryList
	 *            the category list the category belongs to, must not be null
	 * @param element
	 *            the interval container element to be added, must not be null
	 */
	private void loadIntervalContainer(final Category parentCategory, final CategoryList categoryList,
			final Element element) {
		if (parentCategory == null || categoryList == null || element == null) {
			throw new IllegalArgumentException();
		}

		final IntervalNamesMode intervalNameMode = IntervalNamesMode.valueOf(Activator.getDefault()
				.getPreferenceStore().getString(Preferences.INTERVAL_NAMES_MODE));

		// create new interval container to be filled up with new values
		final IntervalContainer newIntervalContainer = categoryList instanceof ChordList ? new Chord() : new Scale();

		// parse the values for the new instrument
		final NodeList children = element.getChildNodes();
		for (int c = 0; c < children.getLength(); c++) {
			final Node currentNode = children.item(c);
			if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
				final Element currentElement = (Element) currentNode;

				// common values
				final String elementValue = XMLUtil.getElementValue(currentElement);
				if (currentElement.getNodeName().equals(NAME)) {
					newIntervalContainer.setName(elementValue);
				} else if (currentElement.getNodeName().equals(DESCRIPTION)) {
					newIntervalContainer.setComment(elementValue);
				} else if (currentElement.getNodeName().equals(AKA_NAME)) {
					newIntervalContainer.addAkaName(elementValue);
				} else if (currentElement.getNodeName().equals(INTERVALS)) {
					final NodeList intervalsChildren = currentElement.getChildNodes();
					for (int p = 0; p < intervalsChildren.getLength(); p++) {
						final Node currentIntervalNode = intervalsChildren.item(p);
						if (currentIntervalNode.getNodeType() == Node.ELEMENT_NODE
								&& currentIntervalNode.getNodeName().equals(INTERVAL)) {
							// empty string notes
							final Element currentIntervalElement = (Element) currentIntervalNode;
							final Interval interval = Factory.getInstance().getInterval(
									new Integer(currentIntervalElement.getAttribute(VALUE)).intValue());
							final String name = intervalNameMode.update(currentIntervalElement.getAttribute(NAME));
							newIntervalContainer
									.addInterval(
											interval,
											name == null || "".equals(name) ? NamesUtil.getDefaultIntervalName(interval) : NamesUtil.translateIntervalName(name)); //$NON-NLS-1$
						}
					}
				}
			}
		}

		// add the new interval container to the corresponding category list
		categoryList.addElement(newIntervalContainer, parentCategory);
	}

	/**
	 * Loads the given instrument element into the specified category.
	 * 
	 * @param parentCategory
	 *            the category, must not be null
	 * @param element
	 *            the interval container element to be added, must not be null
	 */
	private void loadInstrument(final Category parentCategory, final Element element) {
		if (parentCategory == null || element == null) {
			throw new IllegalArgumentException();
		}

		// create new instrument to be filled up with new values
		final Instrument newInstrument = new Instrument();
		boolean isCurrent = false;

		// parse the values for the new instrument
		final NodeList children = element.getChildNodes();
		for (int c = 0; c < children.getLength(); c++) {
			final Node currentNode = children.item(c);
			if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
				final Element currentElement = (Element) currentNode;

				// common values
				final String elementValue = XMLUtil.getElementValue(currentElement);
				if (currentElement.getNodeName().equals(NAME)) {
					newInstrument.setName(elementValue);
				} else if (currentElement.getNodeName().equals(DESCRIPTION)) {
					newInstrument.setComment(elementValue);
				} else if (currentElement.getNodeName().equals(IS_CURRENT)) {
					isCurrent = Boolean.valueOf(elementValue);
				} else if (currentElement.getNodeName().equals(STRING_COUNT)) {
					newInstrument.setStringCount(new Integer(elementValue).intValue());
				} else if (currentElement.getNodeName().equals(SCALE_LENGTH)) {
					newInstrument.setScaleLengthUnit(Unit.valueOf(currentElement.getAttribute(UNIT)));
					newInstrument.setScaleLength(new Double(currentElement.getAttribute(VALUE)).doubleValue());
				}

				// advanced values
				else if (currentElement.getNodeName().equals(LEFTY_RIGHTY)) {
					newInstrument.setLefty(Boolean.valueOf(elementValue));
				} else if (currentElement.getNodeName().equals(FRETLESS)) {
					newInstrument.setFretless(Boolean.valueOf(elementValue));
				} else if (currentElement.getNodeName().equals(DOUBLED_STRINGS)) {
					newInstrument.setDoubledStrings(Boolean.valueOf(elementValue));
				} else if (currentElement.getNodeName().equals(PITCH)) {
					final NodeList pitchChildren = currentElement.getChildNodes();
					for (int p = 0; p < pitchChildren.getLength(); p++) {
						final Node currentPitchNode = pitchChildren.item(p);
						if (currentPitchNode.getNodeType() == Node.ELEMENT_NODE
								&& currentPitchNode.getNodeName().equals(NOTE2)) {
							// empty string notes
							final Element currentPitchElement = (Element) currentPitchNode;
							final Note note = Factory.getInstance().getNote(
									new Integer(currentPitchElement.getAttribute(VALUE)).intValue(),
									new Integer(currentPitchElement.getAttribute(LEVEL)).intValue());
							final int stringNumber = new Integer(currentPitchElement.getAttribute(STRING_NUMBER))
									.intValue();
							newInstrument.setNoteOfEmptyString(note, stringNumber);

							// doubled strings with octave jump
							final boolean octaveJump = Boolean.valueOf(currentPitchElement.getAttribute(OCTAVE_JUMP));
							newInstrument.setDoubledStringWithOctaveJump(octaveJump, stringNumber);
						}
					}
				}

				// sound values
				else if (currentElement.getNodeName().equals(MIDI_INSTRUMENT_NUMBER)) {
					newInstrument.setMidiInstrumentNumber(new Integer(elementValue).intValue());
				} else if (currentElement.getNodeName().equals(CLEF)) {
					final int intValue = new Integer(elementValue).intValue();
					if (intValue >= 0 && intValue < Clef.values().length) {
						newInstrument.setClef(Clef.values()[intValue]);
					}
				}
			}
		}

		// add the new element to the instrument list
		InstrumentList.getInstance().addElement(newInstrument, parentCategory);
		if (isCurrent && !ignoreCurrentInstrument) {
			InstrumentList.getInstance().setCurrentInstrument(newInstrument);
		}
	}

	/* --- STORING --- */

	/**
	 * Stores the given elements to the specified xml file.
	 * 
	 * @param type
	 *            the type of element to be stored, use XMLExportImport.TYPE_*
	 * @param file
	 *            the file, must not be null
	 * 
	 * @throws ParserConfigurationException
	 * @throws IOException
	 */
	@SuppressWarnings("restriction")
	public void storeToXML(final String type, final File file, final List<?> selectedElements) throws IOException,
			ParserConfigurationException {
		if (file == null || selectedElements == null) {
			throw new IllegalArgumentException();
		}

		// create empty document
		final Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

		// create root element
		final Element rootElement = document.createElement(TYPE_INSTRUMENTS.equals(type) ? TYPE_INSTRUMENTS
				: "interval_containers"); //$NON-NLS-1$
		if (TYPE_CHORDS.equals(type)) {
			rootElement.setAttribute(TYPE, TYPE_CHORDS);
		} else if (TYPE_SCALES.equals(type)) {
			rootElement.setAttribute(TYPE, TYPE_SCALES);
		}
		document.appendChild(rootElement);

		// fill document
		for (final Object name : selectedElements) {
			storeElement(document, rootElement, name);
		}

		// serialize document
		final FileWriter fileWriter = new FileWriter(file);
		final XMLSerializer xmlSerializer = new XMLSerializer(fileWriter, null);
		xmlSerializer.serialize(document);
		fileWriter.close();
	}

	/**
	 * Helper method to forward the given element to corresponding storing
	 * method.
	 * 
	 * @param document
	 *            the document, must not be null
	 * @param parentNode
	 *            the parent node, must not be null
	 * @param category
	 *            the element to store, must not be null
	 * 
	 * @throws UnsupportedEncodingException
	 */
	private void storeElement(final Document document, final Node parentNode, final Object element)
			throws UnsupportedEncodingException {
		if (document == null || parentNode == null || element == null) {
			throw new IllegalArgumentException();
		}

		if (element instanceof Category) {
			storeCategory(document, parentNode, (Category) element);
		} else if (element instanceof Instrument) {
			storeInstrument(document, parentNode, (Instrument) element);
		} else if (element instanceof IntervalContainer) {
			storeIntervalContainer(document, parentNode, (IntervalContainer) element);
		}
	}

	/**
	 * Stores the given folder element into the specified parent node.
	 * 
	 * @param document
	 *            the document, must not be null
	 * @param parentNode
	 *            the parent node, must not be null
	 * @param category
	 *            the category to store, must not be null
	 * 
	 * @throws UnsupportedEncodingException
	 */
	private void storeCategory(final Document document, final Node parentNode, final Category category)
			throws UnsupportedEncodingException {
		if (document == null || parentNode == null || category == null) {
			throw new IllegalArgumentException();
		}

		// create category element
		final Element categoryElement = document.createElement(FOLDER);
		parentNode.appendChild(categoryElement);
		categoryElement.setAttribute(NAME, new String(category.getName().getBytes(ENCODING)));

		for (final Category category2 : category.getCategories()) {
			storeCategory(document, categoryElement, category2);
		}
		for (final Categorizable next : category.getElements()) {
			if (next instanceof Instrument) {
				storeInstrument(document, categoryElement, (Instrument) next);
			} else if (next instanceof IntervalContainer) {
				storeIntervalContainer(document, categoryElement, (IntervalContainer) next);
			}
		}
	}

	/**
	 * Stores the given instrument into the specified parent node.
	 * 
	 * @param document
	 *            the document, must not be null
	 * @param parentNode
	 *            the parent node, must not be null
	 * @param instrument
	 *            the instrument to store, must not be null
	 * 
	 * @throws UnsupportedEncodingException
	 */
	private void storeInstrument(final Document document, final Node parentNode, final Instrument instrument)
			throws UnsupportedEncodingException {
		if (document == null || parentNode == null || instrument == null) {
			throw new IllegalArgumentException();
		}

		// create instrument element
		final Element instrumentElement = document.createElement(INSTRUMENT);
		parentNode.appendChild(instrumentElement);

		// create name element
		final Element nameElement = document.createElement(NAME);
		instrumentElement.appendChild(nameElement);
		final Text nameValue = document.createTextNode(new String(instrument.getName().getBytes(ENCODING)));
		nameElement.appendChild(nameValue);

		// create description element
		final Element descriptionElement = document.createElement(DESCRIPTION);
		instrumentElement.appendChild(descriptionElement);
		final Text descriptionValue = document.createTextNode(processCommentString(instrument.getComment()));
		descriptionElement.appendChild(descriptionValue);

		// create isCurrent element
		if (InstrumentList.getInstance().getCurrentInstrument() == instrument && !ignoreCurrentInstrument) {
			final Element isCurrentElement = document.createElement(IS_CURRENT);
			instrumentElement.appendChild(isCurrentElement);
			final Text isCurrentValue = document.createTextNode("true"); //$NON-NLS-1$
			isCurrentElement.appendChild(isCurrentValue);
		}

		// create stringCount element
		final Element stringCountElement = document.createElement(STRING_COUNT);
		instrumentElement.appendChild(stringCountElement);
		final Text stringCountValue = document.createTextNode("" + instrument.getStringCount()); //$NON-NLS-1$
		stringCountElement.appendChild(stringCountValue);

		// create scaleLength element
		final Element scaleLengthElement = document.createElement(SCALE_LENGTH);
		instrumentElement.appendChild(scaleLengthElement);
		scaleLengthElement.setAttribute(VALUE, "" + instrument.getScaleLength()); //$NON-NLS-1$
		scaleLengthElement.setAttribute(UNIT, "" + instrument.getScaleLengthUnit()); //$NON-NLS-1$

		// create lefty element
		final Element leftyRightyElement = document.createElement(LEFTY_RIGHTY);
		instrumentElement.appendChild(leftyRightyElement);
		final Text leftyRightyValue = document.createTextNode(("" + instrument.isLefty()).toLowerCase()); //$NON-NLS-1$
		leftyRightyElement.appendChild(leftyRightyValue);

		// create fretless element
		final Element fretlessElement = document.createElement(FRETLESS);
		instrumentElement.appendChild(fretlessElement);
		final Text fretlessValue = document.createTextNode(("" + instrument.isFretless()).toLowerCase()); //$NON-NLS-1$
		fretlessElement.appendChild(fretlessValue);

		// create doubled strings element
		final Element doubledStringsElement = document.createElement(DOUBLED_STRINGS);
		instrumentElement.appendChild(doubledStringsElement);
		final Text doubledStringsValue = document.createTextNode(("" + instrument.hasDoubledStrings()).toLowerCase()); //$NON-NLS-1$
		doubledStringsElement.appendChild(doubledStringsValue);

		// create pitch element
		final Element pitchElement = document.createElement(PITCH);
		instrumentElement.appendChild(pitchElement);
		for (int i = 1; i <= instrument.getStringCount(); i++) {
			final Element noteElement = document.createElement(NOTE2);
			pitchElement.appendChild(noteElement);
			noteElement.setAttribute(STRING_NUMBER, "" + i); //$NON-NLS-1$
			final Note note = instrument.getNoteOfEmptyString(i);
			noteElement.setAttribute(VALUE, "" + note.getValue()); //$NON-NLS-1$
			noteElement.setAttribute(LEVEL, "" + note.getLevel()); //$NON-NLS-1$
			noteElement.setAttribute(OCTAVE_JUMP, ("" + instrument.isDoubledStringWithOctaveJump(i)).toLowerCase()); //$NON-NLS-1$
		}

		// create midi instrument number element
		final Element midiInstrumentNumberElement = document.createElement(MIDI_INSTRUMENT_NUMBER);
		instrumentElement.appendChild(midiInstrumentNumberElement);
		final Text midiInstrumentNumber = document.createTextNode("" + instrument.getMidiInstrumentNumber()); //$NON-NLS-1$
		midiInstrumentNumberElement.appendChild(midiInstrumentNumber);

		// create clef element
		final Element clefElement = document.createElement(CLEF);
		instrumentElement.appendChild(clefElement);
		final Text clefValue = document.createTextNode("" + instrument.getClef().ordinal()); //$NON-NLS-1$
		clefElement.appendChild(clefValue);
	}

	/**
	 * Stores the given interval container into the specified parent node.
	 * 
	 * @param document
	 *            the document, must not be null
	 * @param parentNode
	 *            the parent node, must not be null
	 * @param container
	 *            the interval container to store, must not be null
	 * 
	 * @throws UnsupportedEncodingException
	 */
	private void storeIntervalContainer(final Document document, final Node parentNode,
			final IntervalContainer container) throws UnsupportedEncodingException {
		if (document == null || parentNode == null || container == null) {
			throw new IllegalArgumentException();
		}

		// create interval container element
		final Element containerElement = document.createElement(INTERVAL_CONTAINER);
		containerElement.setAttribute(TYPE, container.getType() == IntervalContainer.TYPE_CHORD ? TYPE_CHORDS
				: TYPE_SCALES);
		parentNode.appendChild(containerElement);

		// create name element
		final Element nameElement = document.createElement(NAME);
		containerElement.appendChild(nameElement);
		final Text nameValue = document.createTextNode(new String(container.getName().getBytes(ENCODING)));
		nameElement.appendChild(nameValue);

		// create aka name elements
		for (final String akaName : container.getAlsoKnownAsNamesList()) {
			final Element akaNameElement = document.createElement(AKA_NAME);
			containerElement.appendChild(akaNameElement);
			final Text akaNameValue = document.createTextNode(new String(akaName.getBytes(ENCODING)));
			akaNameElement.appendChild(akaNameValue);
		}

		// create description element
		final Element descriptionElement = document.createElement(DESCRIPTION);
		containerElement.appendChild(descriptionElement);
		final Text descriptionValue = document.createTextNode(processCommentString(container.getComment()));
		descriptionElement.appendChild(descriptionValue);

		// create interval elements
		final Element intervalsElement = document.createElement(INTERVALS);
		containerElement.appendChild(intervalsElement);
		for (final Interval interval : container.getIntervals()) {
			final Element intervalElement = document.createElement(INTERVAL);
			intervalsElement.appendChild(intervalElement);
			intervalElement.setAttribute(VALUE, "" + interval.getHalfsteps()); //$NON-NLS-1$
			intervalElement.setAttribute(NAME, NamesUtil.translateIntervalName(container.getIntervalName(interval),
					IntervalNamesMode.DEFAULT, false));
		}
	}

	/* --- helper methods --- */

	/**
	 * Returns the category list to the given type.
	 * 
	 * @param type
	 *            the type, use XMLExportImport.TYPE_*
	 * 
	 * @return the category list to the given type
	 */
	private CategoryList getCategoryList(final String type) {
		return TYPE_INSTRUMENTS.equals(type) ? InstrumentList.getInstance() : TYPE_CHORDS.equals(type) ? ChordList
				.getInstance() : ScaleList.getInstance();
	}

	private String processCommentString(final String comment) throws UnsupportedEncodingException {
		String description = comment;
		description = description.replaceAll("\n", "\r\n");
		final String crCr = "\r\r";
		while (description.contains(crCr)) {
			description = description.replaceAll(crCr, "\r");
		}
		return new String(description.getBytes(ENCODING));
	}

	/* --- getter & setter --- */

	/**
	 * Returns true if the current instrument setting should be ignored, or
	 * false otherwise.
	 * 
	 * @return true if the current instrument setting should be ignored, or
	 *         false otherwise
	 */
	public boolean isIgnoreCurrentInstrument() {
		return ignoreCurrentInstrument;
	}

	/**
	 * Sets the current instrument ignore state.
	 * 
	 * @param ignoreCurrentInstrument
	 *            true if the current instrument setting should be ignored, or
	 *            false otherwise.
	 */
	public void setIgnoreCurrentInstrument(final boolean ignoreCurrentInstrument) {
		this.ignoreCurrentInstrument = ignoreCurrentInstrument;
	}
}
