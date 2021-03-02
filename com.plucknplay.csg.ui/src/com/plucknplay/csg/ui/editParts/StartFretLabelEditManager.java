/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.editParts;

import org.eclipse.draw2d.Label;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.tools.DirectEditManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;

import com.plucknplay.csg.core.model.Instrument;
import com.plucknplay.csg.core.model.sets.InstrumentList;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.listeners.ISimpleChangeListener;
import com.plucknplay.csg.ui.model.BoxDraft;
import com.plucknplay.csg.ui.util.FontManager;
import com.plucknplay.csg.ui.util.enums.BoxViewPresentationMode;

public class StartFretLabelEditManager extends DirectEditManager {

	private boolean bringDown = true;

	private final double scale;
	private final Label label;
	private final BoxDraft boxDraft;
	private Font font;

	public StartFretLabelEditManager(final GraphicalEditPart source, final Label label, final BoxDraft boxDraft,
			final double scale) {

		super(source, null, new LabelCellEditorLocator(label));

		if (label == null || boxDraft == null) {
			throw new IllegalArgumentException();
		}

		this.scale = scale;
		this.label = label;
		this.boxDraft = boxDraft;
	}

	private class MyTextCellEditor extends TextCellEditor implements ISimpleChangeListener {

		public MyTextCellEditor(final Composite composite, final int style) {
			super(composite, style);
			boxDraft.addSimpleChangeListener(this);
		}

		@Override
		public void notifyChange(final Object property, final Object value) {
			if (BoxDraft.PROP_FRET_WIDTH_CHANGED.equals(property)) {
				fireCancelEditor();
			} else if (BoxDraft.PROP_EDITABLE_STATE_CHANGED.equals(property)) {
				applyValueAndDeactivate();
			}
		}

		@Override
		protected void keyReleaseOccured(final KeyEvent e) {
			if (e.character == SWT.ESC || e.character == SWT.TAB) {
				applyValueAndDeactivate();
			}
		}

		@Override
		public void dispose() {
			boxDraft.removeSimpleChangeListener(this);
			super.dispose();
		}

		private void applyValueAndDeactivate() {
			fireApplyEditorValue();
			deactivate();
		}
	}

	@Override
	protected CellEditor createCellEditorOn(final Composite composite) {
		final TextCellEditor textCellEditor = new MyTextCellEditor(composite, SWT.SINGLE);
		textCellEditor.addListener(new ICellEditorListener() {

			private String oldValue = ""; //$NON-NLS-1$

			@Override
			public void applyEditorValue() {
				int newFret = "".equals(getValue()) ? 0 : Integer.parseInt(getValue()); //$NON-NLS-1$

				final Instrument currentInstrument = InstrumentList.getInstance().getCurrentInstrument();
				if (newFret <= currentInstrument.getMinFret()) {
					newFret = currentInstrument.getMinFret() + 1;
				}
				if (newFret > currentInstrument.getFretCount() - boxDraft.getFretWidth() + 1) {
					newFret = currentInstrument.getFretCount() - boxDraft.getFretWidth() + 1;
				}

				if (newFret > 0) {
					boxDraft.setStartFret(newFret);
				}
			}

			@Override
			public void cancelEditor() {
			}

			@Override
			public void editorValueChanged(final boolean oldValidState, final boolean newValidState) {
				final String value = getValue();
				if (isValidValue(value)) {
					oldValue = value;
					return;
				}
				setValue(oldValue);
			}

			private boolean isValidValue(final String value) {
				if ("".equals(value)) {
					return true;
				}
				if (value.length() > 2) {
					return false;
				}
				for (int i = 0; i < value.length(); i++) {
					if (!Character.isDigit(value.charAt(i))) {
						return false;
					}
				}
				final int theValue = Integer.parseInt(value);
				if (theValue > InstrumentList.getInstance().getCurrentInstrument().getFretCount()) {
					return false;
				}
				return true;
			}

			private String getValue() {
				return (String) ((TextCellEditor) getCellEditor()).getValue();
			}

			private void setValue(final String value) {
				getCellEditor().setValue(value);
			}
		});

		return textCellEditor;
	}

	@Override
	protected void initCellEditor() {

		final IPreferenceStore prefs = Activator.getDefault().getPreferenceStore();
		final BoxViewPresentationMode presentationMode = BoxViewPresentationMode.valueOf(prefs
				.getString(Preferences.BOX_VIEW_PRESENTATION_MODE));
		final boolean showNotesInside = prefs.getBoolean(Preferences.BOX_VIEW_SHOW_NOTES)
				&& !prefs.getBoolean(Preferences.BOX_VIEW_SHOW_NOTES_OUTSIDE_BOX);
		final boolean showIntervalsInside = prefs.getBoolean(Preferences.BOX_VIEW_SHOW_INTERVALS)
				&& !prefs.getBoolean(Preferences.BOX_VIEW_SHOW_INTERVALS_OUTSIDE_BOX);

		getCellEditor().setValue("X".equals(label.getText()) ? "" : label.getText()); //$NON-NLS-1$ //$NON-NLS-2$
		final Font f = FontManager.getFont(null, label, "24",
				presentationMode.getFretNumberWidth(showNotesInside, showIntervalsInside) - 15, -1, SWT.BOLD);
		font = new Font(f.getDevice(), f.getFontData()[0].getName(), (int) (scale * f.getFontData()[0].getHeight()),
				f.getFontData()[0].getStyle());
		getCellEditor().getControl().setFont(font);
	}

	@Override
	protected void bringDown() {
		if (bringDown) {
			bringDown = false;
			font.dispose();
			super.bringDown();
		}
	}
}
