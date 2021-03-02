/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.editParts;

import org.eclipse.draw2d.Label;
import org.eclipse.gef.tools.DirectEditManager;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;

import com.plucknplay.csg.core.model.FretboardPosition;
import com.plucknplay.csg.core.model.Instrument;
import com.plucknplay.csg.core.model.sets.InstrumentList;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.figures.IFigureConstants;
import com.plucknplay.csg.ui.listeners.ISimpleChangeListener;
import com.plucknplay.csg.ui.model.TabDraft;
import com.plucknplay.csg.ui.model.TabDraftFretboardPosition;
import com.plucknplay.csg.ui.util.FontManager;
import com.plucknplay.csg.ui.util.TabViewUtil;

public class TabLabelEditManager extends DirectEditManager {

	private boolean bringDown = true;

	private final double scale;
	private final Label label;
	private final TabDraft tabDraft;
	private final TabDraftFretboardPosition fbp;

	private Font font;

	public TabLabelEditManager(final TabDraftEditPart source, final Label label, final TabDraft tabDraft,
			final TabDraftFretboardPosition fbp, final double scale) {
		super(source, null, new LabelCellEditorLocator(label));

		if (tabDraft == null || fbp == null) {
			throw new IllegalArgumentException();
		}

		this.scale = scale;
		this.label = label;
		this.tabDraft = tabDraft;
		this.fbp = fbp;
	}

	private class MyTextCellEditor extends TextCellEditor implements ISimpleChangeListener {

		public MyTextCellEditor(final Composite composite, final int style) {
			super(composite, style);
			tabDraft.addSimpleChangeListener(this);
		}

		@Override
		protected void keyReleaseOccured(final KeyEvent e) {
			if (e.keyCode == SWT.CR
					&& Activator.getDefault().getPreferenceStore()
							.getBoolean(Preferences.VIEWS_SEARCH_MODE_ENABLE_FAST_EDITING)) {
				((TabDraftEditPart) getEditPart()).edit(tabDraft.getNextTabDraftFretboardPosition(fbp), null);
			} else if (e.character == SWT.ESC || e.character == SWT.TAB) {
				applyValueAndDeactivate();
			}
		}

		@Override
		public void notifyChange(final Object property, final Object value) {
			if (TabDraft.PROP_COLUMN_NUMBER_CHANGED.equals(property)) {
				fireCancelEditor();
			} else if (TabDraft.PROP_EDITABLE_STATE_CHANGED.equals(property)) {
				applyValueAndDeactivate();
			}
		}

		@Override
		public void dispose() {
			tabDraft.removeSimpleChangeListener(this);
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

				// determine new fret
				final String labelText = getValue();
				int newFret = "".equals(labelText) ? -1 : Integer.parseInt(labelText); //$NON-NLS-1$

				final Instrument currentInstrument = InstrumentList.getInstance().getCurrentInstrument();
				if (newFret != -1 && newFret < currentInstrument.getMinFret()) {
					newFret = currentInstrument.getMinFret();
				}

				// set new fretboard position
				final FretboardPosition newFbp = new FretboardPosition(fbp.getFretboardPosition().getString(), newFret);
				label.setText("".equals(labelText) ? "X" : labelText); //$NON-NLS-1$
				TabViewUtil.setTooltip(label, newFbp);
				TabViewUtil.updateRootNote(tabDraft, label, newFbp);
				tabDraft.setFretboardPostion(fbp.getColumn(), newFbp);
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
		getCellEditor().setValue("X".equals(label.getText()) ? "" : label.getText()); //$NON-NLS-1$ //$NON-NLS-2$
		final Font f = FontManager.getFont(null, label, "24", -1, IFigureConstants.MAX_TAB_TEXT_HEIGHT, SWT.NORMAL); //$NON-NLS-1$
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
