/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.internal.WorkbenchPage;
import org.eclipse.ui.progress.UIJob;

import com.plucknplay.csg.core.Constants;
import com.plucknplay.csg.core.model.Factory;
import com.plucknplay.csg.core.model.Instrument;
import com.plucknplay.csg.core.model.Note;
import com.plucknplay.csg.core.model.Unit;
import com.plucknplay.csg.core.model.enums.Clef;
import com.plucknplay.csg.core.model.listeners.IChangeListener;
import com.plucknplay.csg.core.model.sets.Category;
import com.plucknplay.csg.core.model.sets.InstrumentList;
import com.plucknplay.csg.core.model.workingCopies.InstrumentWorkingCopy;
import com.plucknplay.csg.core.model.workingCopies.WorkingCopyManager;
import com.plucknplay.csg.sound.SoundConstants;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.UIConstants;
import com.plucknplay.csg.ui.editors.input.InstrumentEditorInput;
import com.plucknplay.csg.ui.util.OnlyNumbersKeyListener;
import com.plucknplay.csg.ui.util.ViewUtil;
import com.plucknplay.csg.ui.util.WidgetFactory;
import com.plucknplay.csg.ui.util.WorkbenchUtil;
import com.plucknplay.csg.ui.views.ChordResultsView;
import com.plucknplay.csg.ui.views.InstrumentsView;

/**
 * Editor for editing instruments.
 */
public class InstrumentEditor extends CategorizableEditor implements IChangeListener, IPropertyChangeListener {

	public static final String ID = "com.plucknplay.csg.ui.editors.instrumentEditor"; //$NON-NLS-1$
	public static final String HELP_ID = "instrument_editor_context"; //$NON-NLS-1$

	private Instrument instrument;
	private InstrumentWorkingCopy workingCopy;
	private List<Integer> midiNumbersList;

	private int lastStringCount;

	private Combo stringCombo;
	private ExpandableComposite pitchSection;
	private Composite pitchComposite;
	private ComboViewer[] relativePitchComboViewer;
	private ComboViewer[] absolutePitchComboViewer;
	private Text scaleLengthText;
	private Combo scaleLengthUnitCombo;
	private Section advancedSection;
	private Composite advancedComposite;
	private Button doubledStringsCheckBox;
	private Label octaveJumpHeaderLabel;
	private Button[] octaveJumpButtons;
	private Button leftyCheckBox;
	private Button fretlessCheckBox;
	private Composite soundComposite;
	private Combo midiInstrumentCombo;
	private ComboViewer clefComboViewer;

	private final KeyListener keyListener = new OnlyNumbersKeyListener();

	@Override
	public void doSave(final IProgressMonitor monitor) {

		if (getErrorMessage() != null) {
			return;
		}

		// show error
		if (!workingCopy.canBeSaved()) {
			MessageDialog.openError(getShell(), EditorMessages.InstrumentEditor_error_title,
					EditorMessages.InstrumentEditor_error_msg);
		}
		// save
		else if (workingCopy.getInstrument() == InstrumentList.getInstance().getCurrentInstrument()) {
			final ChordResultsView chordResultsView = (ChordResultsView) WorkbenchUtil.findView(getSite()
					.getWorkbenchWindow(), ChordResultsView.ID);

			boolean performSave = true;
			MessageDialogWithToggle dialog = null;

			final IPreferenceStore prefs = Activator.getDefault().getPreferenceStore();
			final boolean hidePrompt = prefs.getBoolean(Preferences.WARNINGS_HIDE_PROMPT_FLUSH_RESULTS_VIEW);

			if (chordResultsView != null && !chordResultsView.isEmpty() && !hidePrompt) {
				dialog = MessageDialogWithToggle.openOkCancelConfirm(getSite().getShell(),
						EditorMessages.InstrumentEditor_warning_title, EditorMessages.InstrumentEditor_warning_msg,
						EditorMessages.InstrumentEditor_warning_prompt, hidePrompt, null, null);
				performSave = dialog.getReturnCode() == Dialog.OK;
			}

			if (performSave) {
				if (dialog != null) {
					prefs.setValue(Preferences.WARNINGS_HIDE_PROMPT_FLUSH_RESULTS_VIEW, dialog.getToggleState());
				}
				if (chordResultsView != null) {
					chordResultsView.flush();
				}
				workingCopy.save();
			}
		} else {
			workingCopy.save();
		}
	}

	@Override
	public void init(final IEditorSite site, final IEditorInput input) throws PartInitException {
		if (!(input instanceof InstrumentEditorInput)) {
			throw new PartInitException(EditorMessages.InstrumentEditor_invalid_input);
		}

		final InstrumentEditorInput editorInput = (InstrumentEditorInput) input;

		instrument = editorInput.getInstrument();
		workingCopy = (InstrumentWorkingCopy) WorkingCopyManager.getInstance().getWorkingCopy(
				editorInput.getInstrument(), editorInput.getCategory(), editorInput.isNewElement());
		workingCopy.addListener(this);

		Activator.getDefault().getPreferenceStore().addPropertyChangeListener(this);

		super.init(site, workingCopy, editorInput);
	}

	@Override
	protected void createHeadClient(final FormToolkit toolkit, final Composite headComposite) {
		createNameComposite(toolkit, headComposite);
	}

	@Override
	protected void createMainArea(final FormToolkit toolkit, final Composite composite) {

		// dimension section
		final Section dimensionSection = toolkit.createSection(composite, Section.EXPANDED);
		dimensionSection.setText(EditorMessages.InstrumentEditor_dimensions);
		dimensionSection.marginWidth = 10;
		GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.BEGINNING).grab(true, false).applyTo(dimensionSection);
		toolkit.createCompositeSeparator(dimensionSection);

		// dimension composite
		final Composite dimensionComposite = toolkit.createComposite(dimensionSection);
		GridLayoutFactory.fillDefaults().numColumns(3).margins(5, 5).spacing(10, 5).applyTo(dimensionComposite);
		dimensionSection.setClient(dimensionComposite);

		// create string count label
		final Label stringLabel = toolkit.createLabel(dimensionComposite,
				EditorMessages.InstrumentEditor_number_of_strings);
		GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).grab(false, false).applyTo(stringLabel);

		// create string count text
		stringCombo = new Combo(dimensionComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
		stringCombo.setItems(getStringNumbers());
		stringCombo.setText("" + workingCopy.getStringCount()); //$NON-NLS-1$
		lastStringCount = Integer.parseInt(stringCombo.getText());
		stringCombo.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(final ModifyEvent e) {
				workingCopy.setStringCount(Integer.parseInt(((Combo) e.widget).getText()));
			}
		});
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(false, false).hint(60, SWT.DEFAULT)
				.applyTo(stringCombo);

		// create string count comment label
		final Label stringCommentLabel = toolkit.createLabel(dimensionComposite,
				"(" + Constants.MIN_STRING_SIZE + ".." + Constants.MAX_STRING_SIZE + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).grab(true, false).applyTo(stringCommentLabel);

		// create scale length label
		final Label scaleLengthLabel = toolkit.createLabel(dimensionComposite,
				EditorMessages.InstrumentEditor_scale_length);
		GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).grab(false, false).applyTo(scaleLengthLabel);

		// create scale length count text
		scaleLengthText = toolkit.createText(dimensionComposite,
				"" + workingCopy.getScaleLength(), SWT.SINGLE | SWT.BORDER); //$NON-NLS-1$
		scaleLengthText.addKeyListener(keyListener);
		scaleLengthText.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(final FocusEvent arg0) {
				if ("".equals(scaleLengthText.getText())) {
					workingCopy.setScaleLength(UIConstants.MIN_SCALE_LENGTH);
				}
				checkScaleLength();
			}
		});
		scaleLengthText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(final ModifyEvent e) {
				if (!"".equals(scaleLengthText.getText())) { //$NON-NLS-1$
					final double currentValue = ViewUtil.getDoubleValue(scaleLengthText);
					workingCopy.setScaleLength(currentValue);
				}
			}
		});
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(false, false).hint(45, SWT.DEFAULT)
				.applyTo(scaleLengthText);

		// create fret count comment label
		scaleLengthUnitCombo = new Combo(dimensionComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
		scaleLengthUnitCombo.setItems(Unit.printableValues());
		scaleLengthUnitCombo.setVisibleItemCount(Unit.values().length);
		scaleLengthUnitCombo.setText(workingCopy.getScaleLengthUnit().toString());
		scaleLengthUnitCombo.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(final ModifyEvent e) {
				workingCopy.setScaleLengthUnit(Unit.valueOf(scaleLengthUnitCombo.getText()));
				checkScaleLength();
			}
		});
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(false, false).applyTo(scaleLengthUnitCombo);

		// advanced section
		advancedSection = toolkit.createSection(composite, Section.EXPANDED);
		advancedSection.setText(EditorMessages.InstrumentEditor_advanced);
		advancedSection.marginWidth = 10;
		GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.BEGINNING).grab(true, false).applyTo(advancedSection);
		toolkit.createCompositeSeparator(advancedSection);

		// advanced composite
		advancedComposite = toolkit.createComposite(advancedSection);
		GridLayoutFactory.fillDefaults().margins(5, 5).spacing(10, 5).applyTo(advancedComposite);
		advancedSection.setClient(advancedComposite);

		// doubled strings check box
		doubledStringsCheckBox = toolkit.createButton(advancedComposite,
				EditorMessages.InstrumentEditor_doubled_strings, SWT.CHECK);
		doubledStringsCheckBox.setSelection(workingCopy.hasDoubledStrings());
		doubledStringsCheckBox.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				workingCopy.setDoubledStrings(doubledStringsCheckBox.getSelection());
			}
		});

		// lefty check box
		leftyCheckBox = toolkit
				.createButton(advancedComposite, EditorMessages.InstrumentEditor_lefty_righty, SWT.CHECK);
		leftyCheckBox.setSelection(workingCopy.isLefty());
		leftyCheckBox.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				workingCopy.setLefty(leftyCheckBox.getSelection());
			}
		});

		fretlessCheckBox = toolkit.createButton(advancedComposite, EditorMessages.InstrumentEditor_fretless, SWT.CHECK);
		fretlessCheckBox.setSelection(workingCopy.isFretless());
		fretlessCheckBox.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				workingCopy.setFretless(fretlessCheckBox.getSelection());
			}
		});

		// pitch section
		pitchSection = toolkit.createSection(composite, Section.EXPANDED);
		pitchSection.setText(EditorMessages.InstrumentEditor_pitch);
		pitchSection.marginWidth = 10;
		GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.BEGINNING).grab(true, false).applyTo(pitchSection);
		toolkit.createCompositeSeparator(pitchSection);
		createPitchCombos();

		// sound section
		final Section soundSection = toolkit.createSection(composite, Section.EXPANDED);
		soundSection.setText(EditorMessages.InstrumentEditor_miscellaneous);
		soundSection.marginWidth = 10;
		GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.BEGINNING).grab(true, true).applyTo(soundSection);
		toolkit.createCompositeSeparator(soundSection);

		// sound composite
		soundComposite = toolkit.createComposite(soundSection);
		GridLayoutFactory.fillDefaults().numColumns(2).margins(5, 5).spacing(10, 5).applyTo(soundComposite);
		soundSection.setClient(soundComposite);

		// create clef label
		final Label clefLabel = toolkit.createLabel(soundComposite, EditorMessages.InstrumentEditor_default_clef);
		GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).grab(false, false).applyTo(clefLabel);

		// create clef combo
		clefComboViewer = new ComboViewer(soundComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
		clefComboViewer.setContentProvider(new ArrayContentProvider());
		clefComboViewer.setInput(Clef.values());
		clefComboViewer.setSelection(new StructuredSelection(workingCopy.getClef()));
		clefComboViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(final Object element) {
				return element instanceof Clef ? ((Clef) element).getName() : super.getText(element);
			}
		});
		clefComboViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(final SelectionChangedEvent event) {
				final IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				if (selection != null && !selection.isEmpty()) {
					final Object first = selection.getFirstElement();
					if (first != null && first instanceof Clef) {
						final Clef clef = (Clef) first;
						final Clef oldClef = workingCopy.getClef();
						if (clef != oldClef) {
							workingCopy.setClef(clef);
						}
					}
				}
			}
		});
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(clefComboViewer.getCombo());

		// create MIDI instrument label
		final Label midiInstrumentLabel = toolkit.createLabel(soundComposite,
				EditorMessages.InstrumentEditor_sounds_like);
		GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).grab(false, false).applyTo(midiInstrumentLabel);

		// create MIDI instrument text
		midiInstrumentCombo = new Combo(soundComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
		midiInstrumentCombo.setItems(SoundConstants.MIDI_INSTRUMENTS);
		final int index = getMidiNumbersList().indexOf(Integer.valueOf(workingCopy.getMidiInstrumentNumber()));
		midiInstrumentCombo.setText(SoundConstants.MIDI_INSTRUMENTS[index]);
		midiInstrumentCombo.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(final ModifyEvent e) {
				workingCopy.setMidiInstrumentNumber(SoundConstants.MIDI_INSTRUMENT_NUMBERS[midiInstrumentCombo
						.getSelectionIndex()]);
			}
		});
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(midiInstrumentCombo);

		// create test button
		toolkit.createLabel(soundComposite, ""); //$NON-NLS-1$
		final Button midiInstrumentTestButton = toolkit.createButton(soundComposite,
				EditorMessages.InstrumentEditor_test, SWT.PUSH);
		midiInstrumentTestButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {

				final Instrument tempInstrument = new Instrument();
				tempInstrument.setStringCount(workingCopy.getStringCount());
				tempInstrument.setNotesOfEmptyStrings(workingCopy.getNotesOfEmptyStrings());
				tempInstrument.setDoubledStrings(workingCopy.hasDoubledStrings());
				tempInstrument.setDoubledStringsWithOctaveJump(workingCopy.getDoubledStringsWithOctaveJump());
				tempInstrument.setMidiInstrumentNumber(workingCopy.getMidiInstrumentNumber());
				tempInstrument.setSimpleCapotasto(workingCopy.isSimpleCapotasto());
				tempInstrument.setCapoFrets(workingCopy.getCapoFrets());

				Activator.getDefault().getSoundMachine().play(tempInstrument);
			}
		});
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(midiInstrumentTestButton);

		// comment section
		createCommmentSection(toolkit, composite);

		// necessary to ensure that the multi text field has always the same
		// height
		workingCopy.setComment(workingCopy.getComment());

		// final initializations
		InstrumentList.getInstance().addChangeListener(this);
	}

	private List<Integer> getMidiNumbersList() {
		if (midiNumbersList == null) {
			midiNumbersList = new ArrayList<Integer>();
			for (final int element : SoundConstants.MIDI_INSTRUMENT_NUMBERS) {
				midiNumbersList.add(element);
			}
		}
		return midiNumbersList;
	}

	private String[] getStringNumbers() {
		final String[] result = new String[Constants.MAX_STRING_SIZE - Constants.MIN_STRING_SIZE + 1];
		for (int i = Constants.MIN_STRING_SIZE; i <= Constants.MAX_STRING_SIZE; i++) {
			result[i - Constants.MIN_STRING_SIZE] = "" + i; //$NON-NLS-1$
		}
		return result;
	}

	private void createPitchCombos() {
		if (pitchComposite != null) {
			pitchComposite.dispose();
		}

		pitchComposite = getToolkit().createComposite(pitchSection);
		GridLayoutFactory.fillDefaults().numColumns(4).margins(5, 5).spacing(10, 5).applyTo(pitchComposite);
		pitchSection.setClient(pitchComposite);

		// dummy label
		getToolkit().createLabel(pitchComposite, "", SWT.NONE); //$NON-NLS-1$

		// relative note header label
		final Label relativeHeaderLabel = getToolkit().createLabel(pitchComposite,
				EditorMessages.InstrumentEditor_relative, SWT.NONE);
		relativeHeaderLabel.setToolTipText(EditorMessages.InstrumentEditor_relative_note);

		// absolute note header label
		final Label absoluteHeaderLabel = getToolkit().createLabel(pitchComposite,
				EditorMessages.InstrumentEditor_absolute, SWT.NONE);
		absoluteHeaderLabel.setToolTipText(EditorMessages.InstrumentEditor_absolute_note);

		// octave jump label
		octaveJumpHeaderLabel = getToolkit().createLabel(pitchComposite, EditorMessages.InstrumentEditor_octave_jump,
				SWT.NONE);
		octaveJumpHeaderLabel.setToolTipText(EditorMessages.InstrumentEditor_doubled_string_with_octave_jump);

		final int stringCount = Integer.parseInt(stringCombo.getText());
		relativePitchComboViewer = new ComboViewer[stringCount];
		absolutePitchComboViewer = new ComboViewer[stringCount];
		octaveJumpButtons = new Button[stringCount];
		for (int i = 0; i < stringCount; i++) {
			final int index = i;

			// pitch label
			String text = ""; //$NON-NLS-1$
			if (i == 0) {
				text += EditorMessages.InstrumentEditor_highest;
				text += " "; //$NON-NLS-1$
			}
			if (i == stringCount - 1) {
				text += EditorMessages.InstrumentEditor_deepest;
				text += " "; //$NON-NLS-1$
			}
			text += "(" + (i + 1) + ")  "; //$NON-NLS-1$ //$NON-NLS-2$
			final Label pitchLabel = getToolkit().createLabel(pitchComposite, text, SWT.NONE);
			GridDataFactory.fillDefaults().align(SWT.END, SWT.CENTER).grab(false, false).applyTo(pitchLabel);

			// relative pitch combo viewer
			final ComboViewer relativeComboViewer = WidgetFactory.createRelativeNotesComboViewer(pitchComposite,
					Constants.NOTES_MODE_ONLY_CROSS);
			relativePitchComboViewer[i] = relativeComboViewer;
			relativeComboViewer.addSelectionChangedListener(new ISelectionChangedListener() {
				@Override
				public void selectionChanged(final SelectionChangedEvent event) {
					final IStructuredSelection selection = (IStructuredSelection) event.getSelection();
					if (selection != null && !selection.isEmpty()) {
						final Object first = selection.getFirstElement();
						if (first != null && first instanceof Note) {
							final Note note = (Note) first;
							final Note oldNote = workingCopy.getNoteOfEmptyString(index + 1);
							if (!note.hasSameValue(oldNote)) {
								workingCopy.setNoteOfEmptyString(
										Factory.getInstance().getNote(note.getValue(), oldNote.getLevel()), index + 1);
							}
						}
					}
				}
			});
			GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false)
					.applyTo(relativeComboViewer.getCombo());

			// absolute pitch combo viewer
			final ComboViewer absoluteComboViewer = WidgetFactory.createAbsoluteNotesComboViewer(pitchComposite,
					Constants.NOTES_MODE_ONLY_CROSS);
			absolutePitchComboViewer[i] = absoluteComboViewer;
			absoluteComboViewer.addSelectionChangedListener(new ISelectionChangedListener() {
				@Override
				public void selectionChanged(final SelectionChangedEvent event) {
					final IStructuredSelection selection = (IStructuredSelection) event.getSelection();
					if (selection != null && !selection.isEmpty()) {
						final Object first = selection.getFirstElement();
						if (first != null && first instanceof Note) {
							final Note note = (Note) first;
							if (!note.hasSameLevel(workingCopy.getNoteOfEmptyString(index + 1))) {
								workingCopy.setNoteOfEmptyString(note, index + 1);
							}
						}
					}
				}
			});
			GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false)
					.applyTo(absoluteComboViewer.getCombo());

			// octave jump check boxes
			final Button octaveJumpButton = getToolkit().createButton(pitchComposite, "", SWT.CHECK); //$NON-NLS-1$
			octaveJumpButtons[i] = octaveJumpButton;
			octaveJumpButton.setSelection(workingCopy.isDoubledStringWithOctaveJump(index + 1));
			octaveJumpButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(final SelectionEvent e) {
					workingCopy.setDoubledStringWithOctaveJump(octaveJumpButtons[index].getSelection(), index + 1);
				}
			});
			GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(octaveJumpButton);
		}
		updatePitch();
		updateOctaveJump();
		if (pitchSection != null && !pitchSection.isDisposed() && pitchSection.getParent() != null
				&& !pitchSection.getParent().isDisposed()) {
			pitchSection.getParent().layout(true);
		}
		reflow();
	}

	private void updateOctaveJump() {
		// set visibility and enablement
		final boolean visible = doubledStringsCheckBox.getSelection();
		octaveJumpHeaderLabel.setVisible(visible);
		for (int i = 0; i < octaveJumpButtons.length; i++) {
			octaveJumpButtons[i].setVisible(visible);

			final Note note1 = workingCopy.getNoteOfEmptyString(i + 1);
			boolean enabled = false;
			if (Factory.getInstance().isValidNote(note1.getValue(), note1.getLevel() + 1)) {
				final Note note2 = Factory.getInstance().getNote(note1.getValue(), note1.getLevel() + 1);
				enabled = workingCopy.isValidNoteOfEmptyString(note2);
			}
			octaveJumpButtons[i].setEnabled(enabled);
			if (!enabled && workingCopy.isDoubledStringWithOctaveJump(i + 1)) {
				workingCopy.setDoubledStringWithOctaveJump(false, i + 1);
			}
		}
	}

	@Override
	public void notifyChange(final Object source, final Object parentSource, final Object property) {
		if (property == InstrumentList.PROP_REMOVED && source != null) {
			final Category category = InstrumentList.getInstance().getRootCategory().getCategory(instrument);
			if (source instanceof Instrument && instrument != null && instrument.equals(source)
					|| source instanceof Category && category != null && category.equals(source)) {
				this.getEditorSite().getPage().closeEditor(this, false);
			}
		} else if (property == InstrumentList.PROP_ADDED && source == instrument) {
			if (getEditorInput() instanceof InstrumentEditorInput) {
				((InstrumentEditorInput) getEditorInput()).setNewElement(false);
			}

			setFocus();
			updateToolTip();
		} else if (property == InstrumentList.PROP_CHANGED_ELEMENT
				&& (source == instrument || source instanceof Category)) {
			if (source == instrument) {
				setFocus();
			}
			updateToolTip();
		} else if (property == InstrumentList.PROP_MOVED) {
			updateToolTip();
		}
	}

	@Override
	public void notifyChange(final Object value, final Object property) {
		super.notifyChange(value, property);

		// string count
		if (property == InstrumentWorkingCopy.PROP_STRING_COUNT_CHANGED) {
			final int currentStringCount = ((Integer) value).intValue();
			if (!stringCombo.isDisposed() && !stringCombo.getText().equals("" + currentStringCount)) {
				stringCombo.setText("" + currentStringCount); //$NON-NLS-1$
			}
			if (currentStringCount != lastStringCount) {
				lastStringCount = currentStringCount;
				createPitchCombos();
			}
		}
		// pitch
		else if (property == InstrumentWorkingCopy.PROP_PITCH_CHANGED) {
			updatePitch();
			updateOctaveJump();
		}
		// scale length count
		else if (property == InstrumentWorkingCopy.PROP_SCALE_LENGTH_CHANGED) {
			final double currentScaleLength = value != null ? ((Double) value).doubleValue() : workingCopy
					.getScaleLength();
			if (!scaleLengthText.isDisposed() && !scaleLengthText.getText().equals("" + currentScaleLength)) {
				scaleLengthText.setText("" + currentScaleLength); //$NON-NLS-1$
			}
		}
		// scale length unit
		else if (property == InstrumentWorkingCopy.PROP_SCALE_LENGTH_UNIT_CHANGED) {
			final String currentScaleLengthUnit = value.toString();
			if (!scaleLengthUnitCombo.isDisposed()
					&& !scaleLengthUnitCombo.getText().equals("" + currentScaleLengthUnit)) {
				scaleLengthUnitCombo.setText("" + currentScaleLengthUnit); //$NON-NLS-1$
			}
		}
		// doubled strings
		else if (property == InstrumentWorkingCopy.PROP_DOUBLED_STRINGS_CHANGED) {
			final boolean currentValue = ((Boolean) value).booleanValue();
			if (!doubledStringsCheckBox.isDisposed() && doubledStringsCheckBox.getSelection() != currentValue) {
				doubledStringsCheckBox.setSelection(currentValue);
			}
			updateOctaveJump();
		}
		// doubled strings with octave jump
		else if (property == InstrumentWorkingCopy.PROP_DOUBLED_STRINGS_WITH_OCTAVE_JUMP_CHANGED) {
			if (doubledStringsCheckBox.getSelection()) {
				for (int i = 0; i < octaveJumpButtons.length; i++) {
					final Button button = octaveJumpButtons[i];
					final boolean octaveJump = workingCopy.isDoubledStringWithOctaveJump(i + 1);
					if (!button.isDisposed() && button.getSelection() != octaveJump) {
						button.setSelection(octaveJump);
					}
				}
			}
		}
		// lefty
		else if (property == InstrumentWorkingCopy.PROP_LEFTY_CHANGED) {
			final boolean currentValue = ((Boolean) value).booleanValue();
			if (!leftyCheckBox.isDisposed() && leftyCheckBox.getSelection() != currentValue) {
				leftyCheckBox.setSelection(currentValue);
			}
		}
		// fretless
		else if (property == InstrumentWorkingCopy.PROP_FRETLESS_CHANGED) {
			final boolean currentValue = ((Boolean) value).booleanValue();
			if (!fretlessCheckBox.isDisposed() && fretlessCheckBox.getSelection() != currentValue) {
				fretlessCheckBox.setSelection(currentValue);
			}
		}
		// midi instrument number
		else if (property == InstrumentWorkingCopy.PROP_MIDI_INSTRUMENT_NUMBER_CHANGED) {
			final int currentValue = ((Integer) value).intValue();
			if (!midiInstrumentCombo.isDisposed()
					&& SoundConstants.MIDI_INSTRUMENT_NUMBERS[midiInstrumentCombo.getSelectionIndex()] != currentValue) {
				final int index = getMidiNumbersList().indexOf(currentValue);
				midiInstrumentCombo.select(index);
			}
		}
		// clef
		else if (property == InstrumentWorkingCopy.PROP_CLEF_CHANGED) {
			if (clefComboViewer != null && clefComboViewer.getControl() != null
					&& !clefComboViewer.getControl().isDisposed()) {
				clefComboViewer.setSelection(new StructuredSelection(value));
			}
		}
	}

	private void updatePitch() {
		for (int i = 0; i < relativePitchComboViewer.length; i++) {
			final Note note = workingCopy.getNoteOfEmptyString(i + 1);

			// update relative note combo viewer
			final ComboViewer relativeComboViewer = relativePitchComboViewer[i];
			if (!relativeComboViewer.getCombo().isDisposed()) {
				relativeComboViewer
						.setSelection(new StructuredSelection(Factory.getInstance().getNote(note.getValue())));
			}

			// update absolute note combo viewer
			final ComboViewer absoluteComboViewer = absolutePitchComboViewer[i];
			final List<Note> input = new ArrayList<Note>();
			for (int l = 0; l <= Constants.MAX_NOTES_LEVEL; l++) {
				if (l < Constants.MAX_NOTES_LEVEL || l == Constants.MAX_NOTES_LEVEL && note.getValue() == 0) {
					final Note theNote = Factory.getInstance().getNote(note.getValue(), l);
					if (workingCopy.isValidNoteOfEmptyString(theNote)) {
						input.add(theNote);
					}
				}
			}
			absoluteComboViewer.setInput(input);
			if (!absoluteComboViewer.getCombo().isDisposed()) {
				absoluteComboViewer.setSelection(new StructuredSelection(note));
			}
		}
	}

	@Override
	public void propertyChange(final PropertyChangeEvent event) {

		// H/B names
		if (event.getProperty().equals(Preferences.GENERAL_H_NOTE_NAME)
				|| event.getProperty().equals(Preferences.GENERAL_B_NOTE_NAME)) {
			refreshComboViewer(relativePitchComboViewer);
			refreshComboViewer(absolutePitchComboViewer);
		}

		// absolute note names
		if (event.getProperty().equals(Preferences.ABSOLUTE_NOTE_NAMES_MODE)) {
			refreshComboViewer(absolutePitchComboViewer);
		}
	}

	private void refreshComboViewer(final ComboViewer[] viewers) {
		for (final ComboViewer viewer : viewers) {
			viewer.refresh(true);
		}
	}

	@Override
	public void dispose() {
		workingCopy.removeListener(this);
		WorkingCopyManager.getInstance().disposeWorkingCopy(workingCopy.getInstrument());
		InstrumentList.getInstance().removeChangeListener(this);
		Activator.getDefault().getPreferenceStore().removePropertyChangeListener(this);
		super.dispose();
	}

	@Override
	public void setFocus() {
		super.setFocus();

		final UIJob job = new UIJob(EditorMessages.InstrumentEditor_link_with_editor_job) {
			@Override
			public IStatus runInUIThread(final IProgressMonitor monitor) {

				final IWorkbenchPage page = getSite().getPage();
				final IViewPart view = page.findView(InstrumentsView.ID);
				final IViewReference viewReference = page.findViewReference(InstrumentsView.ID);

				if (view == null) {
					return Status.OK_STATUS;
				}

				try {
					if (page instanceof WorkbenchPage && !((WorkbenchPage) page).isFastView(viewReference)) {
						page.showView(InstrumentsView.ID);
					}

				} catch (final PartInitException e) {
				}
				if (view instanceof InstrumentsView && instrument != null) {
					((InstrumentsView) view).expandElements(InstrumentList.getInstance().getRootCategory()
							.getCategoryPath(instrument, true));
				}

				InstrumentEditor.super.setFocus();

				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}

	private void checkScaleLength() {
		double currentValueInMM = Unit.convert(workingCopy.getScaleLengthUnit(),
				ViewUtil.getDoubleValue(scaleLengthText), Unit.mm);
		if (currentValueInMM < UIConstants.MIN_SCALE_LENGTH) {
			currentValueInMM = UIConstants.MIN_SCALE_LENGTH;
		}
		if (currentValueInMM > UIConstants.MAX_SCALE_LENGTH) {
			currentValueInMM = UIConstants.MAX_SCALE_LENGTH;
		}
		workingCopy.setScaleLength(Unit.convert(Unit.mm, currentValueInMM, workingCopy.getScaleLengthUnit()));
	}

	@Override
	protected String getTypeName() {
		return EditorMessages.InstrumentEditor_instrument;
	}

	@Override
	protected String getHelpId() {
		return HELP_ID;
	}

	@Override
	protected int getMinWidth() {
		return 483;
	}

	@Override
	protected String getErrorMessage() {
		return workingCopy.isValidName() ? null : getNameErrorMessage();
	}

	@Override
	protected String getNameErrorMessage() {
		return EditorMessages.InstrumentEditor_instrument_must_have_a_name;
	}
}
