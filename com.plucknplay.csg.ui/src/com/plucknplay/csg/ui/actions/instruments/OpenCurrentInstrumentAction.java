/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions.instruments;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.WorkbenchException;

import com.plucknplay.csg.core.model.Instrument;
import com.plucknplay.csg.core.model.listeners.IChangeListener;
import com.plucknplay.csg.core.model.sets.Category;
import com.plucknplay.csg.core.model.sets.InstrumentList;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.IImageKeys;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.actions.ActionMessages;
import com.plucknplay.csg.ui.editors.InstrumentEditor;
import com.plucknplay.csg.ui.editors.input.InstrumentEditorInput;
import com.plucknplay.csg.ui.util.WorkbenchUtil;

/**
 * This contribution item shows the current instrument inside the statusbar. A
 * double click on this item opens the corresponding editor.
 */
public class OpenCurrentInstrumentAction extends ContributionItem implements IChangeListener {

	private Instrument currentInstrument;
	private final IWorkbenchWindow window;
	private Label imageLabel;
	private Label textLabel;

	public OpenCurrentInstrumentAction(final IWorkbenchWindow window) {
		this.window = window;
		InstrumentList.getInstance().addChangeListener(this);
		currentInstrument = InstrumentList.getInstance().getCurrentInstrument();
	}

	@Override
	public void fill(final Composite parent) {
		final Composite composite = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(2).margins(20, 5).applyTo(composite);
		imageLabel = createLabel(composite);
		imageLabel.setImage(Activator.getDefault().getImage(IImageKeys.CURRENT_INSTRUMENT));
		textLabel = createLabel(composite);
		setText();
	}

	private Label createLabel(final Composite parent) {
		final Label label = new Label(parent, SWT.RIGHT);
		label.setSize(label.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		label.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(final MouseEvent event) {
				try {
					InstrumentEditorInput instrumentEditorInput;
					if (currentInstrument == null) {
						final Category category = InstrumentList.getInstance().getRootCategory();
						final Instrument newInstrument = new Instrument();
						newInstrument.setMidiInstrumentNumber(Activator.getDefault().getPreferenceStore()
								.getInt(Preferences.SOUND_DEFAULT_MIDI_INSTRUMENT));
						instrumentEditorInput = new InstrumentEditorInput(newInstrument, category, true);
					} else {
						final Category category = InstrumentList.getInstance().getRootCategory()
								.getCategory(currentInstrument);
						instrumentEditorInput = new InstrumentEditorInput(currentInstrument, category, false);
					}
					WorkbenchUtil.showPerspective(window.getWorkbench(),
							Preferences.PERSPECTIVES_BINDING_ELEMENT_EDITING);
					window.getActivePage().openEditor(instrumentEditorInput, InstrumentEditor.ID);
				} catch (final PartInitException e) {
				} catch (final WorkbenchException e) {
				}
			}
		});
		label.setToolTipText(ActionMessages.OpenCurrentInstrumentAction_tooltip);
		return label;
	}

	/**
	 * Sets the text of this contribution item corresponding to the current
	 * instrument set in the instruments view.
	 */
	public void setText() {
		if (textLabel == null || textLabel.isDisposed()) {
			return;
		}

		// it's necessary to set the label invisible during the layout
		// since otherwise there would be flicker
		textLabel.setVisible(false);

		// set the new name of the label
		if (currentInstrument == null) {
			textLabel.setForeground(new Color(textLabel.getDisplay(), 255, 0, 0));
			textLabel.setText(ActionMessages.OpenCurrentInstrumentAction_no_active_instrument);
		} else {
			textLabel.setForeground(new Color(textLabel.getDisplay(), 0, 0, 0));
			textLabel.setText(currentInstrument.getName());
		}

		// layout parent composite - that is necessary due to a SWT layout bug
		if (textLabel.getParent() != null && textLabel.getParent().getParent() != null) {
			textLabel.getParent().getParent().layout(true);
		}

		// finally set the label visible again
		textLabel.setVisible(true);
	}

	@Override
	public void notifyChange(final Object source, final Object parentSource, final Object property) {
		currentInstrument = InstrumentList.getInstance().getCurrentInstrument();
		setText();
	}
}
