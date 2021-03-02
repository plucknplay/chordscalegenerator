/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.util;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class MyIntegerFieldEditor extends FieldEditor {

	private final int textLimit;
	private final int minValue;
	private int maxValue = Integer.MAX_VALUE;

	/**
	 * The text field, or <code>null</code> if none.
	 */
	private Text textField;

	/**
	 * Creates an integer field editor.
	 * 
	 * @param name
	 *            the name of the preference this field editor works on
	 * @param labelText
	 *            the label text of the field editor
	 * @param parent
	 *            the parent of the field editor's control
	 * @param textLimit
	 *            the maximum number of characters in the text.
	 */
	public MyIntegerFieldEditor(final String name, final String labelText, final Composite parent, final int textLimit,
			final int minValue, final int maxValue) {

		init(name, labelText);
		this.textLimit = textLimit;
		this.minValue = minValue;
		this.maxValue = maxValue;
		createControl(parent);
	}

	@Override
	protected void adjustForNumColumns(final int numColumns) {
		final GridData gd = (GridData) textField.getLayoutData();
		gd.horizontalSpan = numColumns - 2;
		gd.grabExcessHorizontalSpace = gd.horizontalSpan == 1;
	}

	/**
	 * Fills this field editor's basic controls into the given parent.
	 * <p>
	 * The string field implementation of this <code>FieldEditor</code>
	 * framework method contributes the text field. Subclasses may override but
	 * must call <code>super.doFillIntoGrid</code>.
	 * </p>
	 */
	@Override
	protected void doFillIntoGrid(final Composite parent, final int numColumns) {
		getLabelControl(parent);

		textField = getTextControl(parent);
		GridDataFactory.fillDefaults().align(GridData.FILL, GridData.FILL).grab(true, false).applyTo(textField);

		final Label commentLabel = new Label(parent, SWT.NONE);
		commentLabel.setText("(" + minValue + ".." + maxValue + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Override
	protected void doLoad() {
		final Text text = getTextControl();
		if (text != null) {
			final int value = getPreferenceStore().getInt(getPreferenceName());
			text.setText("" + value); //$NON-NLS-1$
		}
	}

	@Override
	protected void doLoadDefault() {
		final Text text = getTextControl();
		if (text != null) {
			final int value = getPreferenceStore().getDefaultInt(getPreferenceName());
			text.setText("" + value); //$NON-NLS-1$
		}
	}

	/**
	 * Returns this field editor's text control.
	 * <p>
	 * The control is created if it does not yet exist
	 * </p>
	 * 
	 * @param parent
	 *            the parent
	 * @return the text control
	 */
	public Text getTextControl(final Composite parent) {
		if (textField == null) {
			textField = new Text(parent, SWT.SINGLE | SWT.BORDER);
			textField.setFont(parent.getFont());
			textField.setTextLimit(textLimit);
			textField.addKeyListener(new OnlyNumbersKeyListener());
			textField.addFocusListener(new FocusAdapter() {
				@Override
				public void focusLost(final FocusEvent e) {
					if ("".equals(textField.getText())) {
						textField.setText("0"); //$NON-NLS-1$
					}
					final int currentValue = Integer.parseInt(textField.getText());
					if (currentValue < minValue) {
						textField.setText("" + minValue); //$NON-NLS-1$
					}
					if (currentValue > maxValue) {
						textField.setText("" + maxValue); //$NON-NLS-1$
					}
				}
			});
		} else {
			checkParent(textField, parent);
		}
		return textField;
	}

	@Override
	protected void doStore() {
		final Text text = getTextControl();
		if (text != null) {
			final Integer i = new Integer(text.getText());
			getPreferenceStore().setValue(getPreferenceName(), i.intValue());
		}
	}

	@Override
	public int getNumberOfControls() {
		return 3;
	}

	/**
	 * Returns the field editor's value.
	 * 
	 * @return the current value
	 */
	public String getStringValue() {
		if (textField != null) {
			return textField.getText();
		}
		return getPreferenceStore().getString(getPreferenceName());
	}

	/**
	 * Returns this field editor's text control.
	 * 
	 * @return the text control, or <code>null</code> if no text field is
	 *         created yet
	 */
	protected Text getTextControl() {
		return textField;
	}

	/**
	 * Returns this field editor's current value as an integer.
	 * 
	 * @return the value
	 */
	public int getIntValue() {
		return new Integer(getStringValue()).intValue();
	}
}
