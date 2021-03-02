/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.editParts;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.gef.tools.DirectEditManager;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;

import com.plucknplay.csg.core.model.FretboardPosition;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.figures.IFigureWithLabel;
import com.plucknplay.csg.ui.listeners.ISimpleChangeListener;
import com.plucknplay.csg.ui.model.BoxDraft;
import com.plucknplay.csg.ui.model.Draft;
import com.plucknplay.csg.ui.model.FretDraft;
import com.plucknplay.csg.ui.util.FontManager;
import com.plucknplay.csg.ui.util.enums.FingeringMode;

public class FingerNumberEditManager extends DirectEditManager {

	private static final String REMOVE_ASSIGNMENT = "X"; //$NON-NLS-1$

	private boolean bringDown = true;

	private final double scale;
	private final IFigureWithLabel figure;
	private final FretDraft fretDraft;
	private final FretboardPosition relativeFbp;

	private Font font;

	private final List<String> itemList;

	public FingerNumberEditManager(final AbstractDraftEditPart source, final IFigureWithLabel figure,
			final FretDraft fretDraft, final FretboardPosition relativeFbp, final double scale) {

		super(source, null, new LabelCellEditorLocator(figure.getMainLabel(), new Dimension((int) (35 * scale) + 20,
				(int) (35 * scale)), SWT.LEFT));

		if (fretDraft == null || relativeFbp == null) {
			throw new IllegalArgumentException();
		}

		this.scale = scale;
		this.figure = figure;
		this.fretDraft = fretDraft;
		this.relativeFbp = relativeFbp;

		// determine combo box items
		final FingeringMode fingeringMode = FingeringMode.valueOf(Activator.getDefault().getPreferenceStore()
				.getString(Preferences.GENERAL_FINGERING_MODE));
		itemList = new ArrayList<String>();
		for (int i = 0; i < 5; i++) {
			itemList.add(Character.toString(fingeringMode.getValue(i)));
		}
		itemList.add(REMOVE_ASSIGNMENT);
	}

	private class MyComboBoxCellEditor extends ComboBoxCellEditor implements ISimpleChangeListener {

		public MyComboBoxCellEditor(final Composite composite, final String[] items, final int style) {
			super(composite, items, style);
			fretDraft.addSimpleChangeListener(this);
		}

		@Override
		protected void keyReleaseOccured(final KeyEvent e) {
			final String upperCase = Character.toString(e.character).toUpperCase();
			final String lowerCase = Character.toString(e.character).toLowerCase();
			if (itemList.contains(upperCase)) {
				setValue(itemList.indexOf(upperCase));
			} else if (itemList.contains(lowerCase)) {
				setValue(itemList.indexOf(lowerCase));
			} else if (e.keyCode == SWT.DEL || e.keyCode == SWT.BS) {
				setValue(itemList.indexOf(REMOVE_ASSIGNMENT));
			} else if (e.keyCode == SWT.CR
					&& Activator.getDefault().getPreferenceStore()
							.getBoolean(Preferences.VIEWS_SEARCH_MODE_ENABLE_FAST_EDITING)) {
				if (getEditPart() instanceof FretDraftEditPart) {
					((FretDraftEditPart) getEditPart()).editFingering(fretDraft
							.getNextRelativeFretboardPosition(relativeFbp));
				}
			} else if (e.character == SWT.ESC || e.character == SWT.TAB) {
				applyValueAndDeactivate();
			}
		}

		@Override
		public void notifyChange(final Object property, final Object value) {
			if (BoxDraft.PROP_FRET_WIDTH_CHANGED.equals(property)) {
				fireCancelEditor();
			} else if (Draft.PROP_EDITABLE_STATE_CHANGED.equals(property)) {
				applyValueAndDeactivate();
			}
		}

		@Override
		public void dispose() {
			fretDraft.removeSimpleChangeListener(this);
			super.dispose();
		}

		private void applyValueAndDeactivate() {
			fireApplyEditorValue();
			deactivate();
		}
	}

	@Override
	protected CellEditor createCellEditorOn(final Composite composite) {

		// create cell editor
		final ComboBoxCellEditor comboBoxCellEditor = new MyComboBoxCellEditor(composite,
				itemList.toArray(new String[itemList.size()]), SWT.READ_ONLY | SWT.DROP_DOWN);

		comboBoxCellEditor.addListener(new ICellEditorListener() {

			@Override
			public void applyEditorValue() {
				Integer value = (Integer) ((ComboBoxCellEditor) getCellEditor()).getValue();
				if (value == null || value < 0 || value > 5) {
					value = FretDraft.UNKNOWN_FINGER;
				}
				fretDraft.setAssignment(relativeFbp.getFret(), relativeFbp.getString() + 1, value);
			}

			@Override
			public void cancelEditor() {
			}

			@Override
			public void editorValueChanged(final boolean oldValidState, final boolean newValidState) {
			}
		});

		return comboBoxCellEditor;
	}

	@Override
	protected void initCellEditor() {

		final String text = figure.getText();
		if (text != null && itemList.contains(text)) {
			getCellEditor().setValue(itemList.indexOf(text));
		} else {
			getCellEditor().setValue(1);
		}
		final Font f = FontManager.getFont(null, figure.getMainLabel(), "M", 24, -1, SWT.NORMAL); //$NON-NLS-1$
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
