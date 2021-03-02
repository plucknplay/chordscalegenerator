/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.preferencePages;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.plucknplay.csg.core.Constants;
import com.plucknplay.csg.core.model.Factory;
import com.plucknplay.csg.core.model.Note;
import com.plucknplay.csg.ui.util.WidgetFactory;

public class ToneRangeComposite extends Composite {

	private boolean checkToneRange = true;

	private ComboViewer relativeStartToneComboViewer;
	private ComboViewer absoluteStartToneComboViewer;
	private ComboViewer relativeEndToneComboViewer;
	private ComboViewer absoluteEndToneComboViewer;

	public ToneRangeComposite(final Composite parent, final int style, final boolean addSignedNoteFilter) {
		super(parent, style);

		GridLayoutFactory.fillDefaults().equalWidth(false).numColumns(6).spacing(10, 5).applyTo(this);

		final ViewerFilter filter = new ViewerFilter() {
			@Override
			public boolean select(final Viewer viewer, final Object parentElement, final Object element) {
				return element instanceof Note && !((Note) element).hasAccidental();
			}
		};

		new Label(this, SWT.NONE).setText(PreferenceMessages.ToneRangeComposite_from + " "); //$NON-NLS-1$

		relativeStartToneComboViewer = WidgetFactory.createRelativeNotesComboViewer(this);
		if (addSignedNoteFilter) {
			relativeStartToneComboViewer.addFilter(filter);
		}
		relativeStartToneComboViewer.addSelectionChangedListener(new RelativeToneSelectionChangedListener(true));
		GridDataFactory.fillDefaults().hint(80, SWT.DEFAULT).applyTo(relativeStartToneComboViewer.getCombo());

		absoluteStartToneComboViewer = WidgetFactory.createAbsoluteNotesComboViewer(this);
		absoluteStartToneComboViewer.addSelectionChangedListener(new AbsoluteToneSelectionChangedListener(true));
		GridDataFactory.fillDefaults().hint(80, SWT.DEFAULT).applyTo(absoluteStartToneComboViewer.getCombo());

		new Label(this, SWT.NONE).setText("  " + PreferenceMessages.ToneRangeComposite_to + "  "); //$NON-NLS-1$ //$NON-NLS-2$

		relativeEndToneComboViewer = WidgetFactory.createRelativeNotesComboViewer(this);
		if (addSignedNoteFilter) {
			relativeEndToneComboViewer.addFilter(filter);
		}
		relativeEndToneComboViewer.addSelectionChangedListener(new RelativeToneSelectionChangedListener(false));
		GridDataFactory.fillDefaults().hint(80, SWT.DEFAULT).applyTo(relativeEndToneComboViewer.getCombo());

		absoluteEndToneComboViewer = WidgetFactory.createAbsoluteNotesComboViewer(this);
		absoluteEndToneComboViewer.addSelectionChangedListener(new AbsoluteToneSelectionChangedListener(false));
		GridDataFactory.fillDefaults().hint(80, SWT.DEFAULT).applyTo(absoluteEndToneComboViewer.getCombo());
	}

	private class RelativeToneSelectionChangedListener implements ISelectionChangedListener {

		private final boolean startToneMode;

		public RelativeToneSelectionChangedListener(final boolean startTone) {
			startToneMode = startTone;
		}

		@Override
		public void selectionChanged(final SelectionChangedEvent event) {

			// (1) update absolute notes combo
			final Note note = getNoteSelection(event.getSelection());
			final ComboViewer absoluteComboViewer = startToneMode ? absoluteStartToneComboViewer
					: absoluteEndToneComboViewer;

			final Factory factory = Factory.getInstance();
			final Note minNote = startToneMode ? factory.getNote(0, 0) : factory.getNote(Constants.MAX_NOTES_VALUE, 0);
			final Note maxNote = startToneMode ? factory.getNote(1, Constants.MAX_NOTES_LEVEL - 1) : factory.getNote(0,
					Constants.MAX_NOTES_LEVEL);

			final Note absoluteNote = getNoteSelection(absoluteComboViewer.getSelection());
			final List<Note> input = new ArrayList<Note>();
			for (int l = 0; l <= Constants.MAX_NOTES_LEVEL; l++) {
				if (l < Constants.MAX_NOTES_LEVEL || l == Constants.MAX_NOTES_LEVEL && note.getValue() == 0) {
					final Note theNote = factory.getNote(note.getValue(), l);
					if (theNote.compareTo(minNote) >= 0 && theNote.compareTo(maxNote) <= 0) {
						input.add(theNote);
					}
				}
			}
			absoluteComboViewer.setInput(input);
			if (absoluteNote != null) {
				Note newAbsoluteNote = absoluteNote.getLevel() == Constants.MAX_NOTES_LEVEL && note.getValue() > 0 ? factory
						.getNote(note.getValue(), Constants.MAX_NOTES_LEVEL - 1) : factory.getNote(note.getValue(),
						absoluteNote.getLevel());
				if (newAbsoluteNote.compareTo(maxNote) > 0) {
					newAbsoluteNote = factory.getNote(newAbsoluteNote.getValue(), newAbsoluteNote.getLevel() - 1);
				}
				if (newAbsoluteNote.compareTo(minNote) < 0) {
					newAbsoluteNote = factory.getNote(newAbsoluteNote.getValue(), newAbsoluteNote.getLevel() + 1);
				}
				absoluteComboViewer.setSelection(new StructuredSelection(newAbsoluteNote));
			}

			// (2) check tone range
			checkToneRange(startToneMode);
		}
	}

	private class AbsoluteToneSelectionChangedListener implements ISelectionChangedListener {

		private final boolean startToneMode;

		public AbsoluteToneSelectionChangedListener(final boolean startToneMode) {
			this.startToneMode = startToneMode;
		}

		@Override
		public void selectionChanged(final SelectionChangedEvent event) {
			checkToneRange(startToneMode);
		}
	}

	private void checkToneRange(final boolean startToneMode) {
		if (!checkToneRange) {
			return;
		}

		final Factory factory = Factory.getInstance();
		Note startNote = getNoteSelection(absoluteStartToneComboViewer.getSelection());
		Note endNote = getNoteSelection(absoluteEndToneComboViewer.getSelection());
		if (startNote == null || endNote == null) {
			return;
		}

		final int startNoteValue = startNote.getLevel() * 12 + startNote.getValue();
		final int endNoteValue = endNote.getLevel() * 12 + endNote.getValue();
		if (endNoteValue - 11 < startNoteValue) {
			checkToneRange = false;
			if (startToneMode) {
				final int newEndNoteValue = startNoteValue + 11;
				endNote = factory.getNoteByIndex(newEndNoteValue);
				if (endNote.hasAccidental()) {
					endNote = endNote.getNextHigherNote();
				}

				relativeEndToneComboViewer.setSelection(new StructuredSelection(factory.getNote(endNote.getValue())));
				absoluteEndToneComboViewer.setSelection(new StructuredSelection(endNote));
			} else {
				final int newStartNoteValue = endNoteValue - 11;
				startNote = factory.getNoteByIndex(newStartNoteValue);
				if (startNote.hasAccidental()) {
					startNote = startNote.getNextDeeperNote();
				}

				relativeStartToneComboViewer
						.setSelection(new StructuredSelection(factory.getNote(startNote.getValue())));
				absoluteStartToneComboViewer.setSelection(new StructuredSelection(startNote));
			}
			checkToneRange = true;
		}
	}

	private Note getNoteSelection(final ISelection selection) {
		if (selection != null && selection instanceof IStructuredSelection && !selection.isEmpty()) {
			final IStructuredSelection structured = (IStructuredSelection) selection;
			final Object first = structured.getFirstElement();
			if (first != null && first instanceof Note) {
				return (Note) first;
			}
		}
		return null;
	}

	@Override
	public void setEnabled(final boolean enabled) {
		relativeStartToneComboViewer.getCombo().setEnabled(enabled);
		absoluteStartToneComboViewer.getCombo().setEnabled(enabled);
		relativeEndToneComboViewer.getCombo().setEnabled(enabled);
		absoluteEndToneComboViewer.getCombo().setEnabled(enabled);
	}

	public Note getStartTone() {
		return getNoteSelection(absoluteStartToneComboViewer.getSelection());
	}

	public void setStartTone(final Note startNote) {
		relativeStartToneComboViewer.setSelection(new StructuredSelection(Factory.getInstance().getNote(
				startNote.getValue())));
		absoluteStartToneComboViewer.setSelection(new StructuredSelection(startNote));
	}

	public Note getEndTone() {
		return getNoteSelection(absoluteEndToneComboViewer.getSelection());
	}

	public void setEndTone(final Note endNote) {
		relativeEndToneComboViewer.setSelection(new StructuredSelection(Factory.getInstance().getNote(
				endNote.getValue())));
		absoluteEndToneComboViewer.setSelection(new StructuredSelection(endNote));
	}

	public boolean isEmptySelection() {
		return "".equals(relativeStartToneComboViewer.getCombo().getText()); //$NON-NLS-1$
	}

	public void setAbsoluteToneCombosVisible(final boolean visible) {
		absoluteStartToneComboViewer.getControl().setVisible(visible);
		absoluteEndToneComboViewer.getControl().setVisible(visible);
	}
}
