/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import com.plucknplay.csg.core.model.Instrument;
import com.plucknplay.csg.core.model.sets.InstrumentList;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.IImageKeys;
import com.plucknplay.csg.ui.util.CategoryTreeContentProvider;

public class InstrumentSelectionDialog extends TrayDialog implements IDoubleClickListener {

	private final boolean activateMode;
	private TreeViewer viewer;
	private Instrument selectedInstrument;
	private Button okButton;

	public InstrumentSelectionDialog(final Shell shell, final boolean activateMode) {
		super(shell);
		this.activateMode = activateMode;
		setHelpAvailable(false);
	}

	@Override
	protected Control createDialogArea(final Composite parent) {
		final Composite composite = (Composite) super.createDialogArea(parent);

		final Label label = new Label(composite, SWT.WRAP);
		label.setText(DialogMessages.InstrumentSelectionDialog_select_instrument
				+ DialogMessages.IntervalContainerSelectionDialog_any_character_any_string);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(label);

		final PatternFilter filter = new PatternFilter();
		final FilteredTree filteredTree = new FilteredTree(composite, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.BORDER, filter, false);

		viewer = filteredTree.getViewer();
		viewer.setLabelProvider(new WorkbenchLabelProvider());
		viewer.setContentProvider(new CategoryTreeContentProvider(InstrumentList.getInstance()));
		viewer.setComparator(new ViewerComparator());
		viewer.addDoubleClickListener(this);
		viewer.setInput(InstrumentList.getInstance().getRootCategory());
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(final SelectionChangedEvent event) {
				updateButtons();
			}
		});

		/*
		 * if (activateMode) { viewer.addFilter(new ViewerFilter() { public
		 * boolean select(Viewer viewer, Object parentElement, Object element) {
		 * if (element == InstrumentList.getInstance().getCurrentInstrument())
		 * return false; return true; } }); }
		 */

		GridDataFactory.fillDefaults().grab(true, true).hint(300, 300).applyTo(viewer.getControl());

		return composite;
	}

	@Override
	public void doubleClick(final DoubleClickEvent event) {
		final IStructuredSelection s = (IStructuredSelection) event.getSelection();
		final Object element = s.getFirstElement();
		if (viewer.isExpandable(element)) {
			viewer.setExpandedState(element, !viewer.getExpandedState(element));
		} else {
			okPressed();
		}
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	@Override
	protected void createButtonsForButtonBar(final Composite parent) {
		okButton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
		updateButtons();
	}

	protected void updateButtons() {
		selectedInstrument = null;
		final ISelection selection = viewer.getSelection();
		if (selection instanceof IStructuredSelection && !selection.isEmpty()) {
			final Object first = ((IStructuredSelection) selection).getFirstElement();
			if (first instanceof Instrument) {
				selectedInstrument = (Instrument) first;
			}
		}
		if (okButton != null) {
			okButton.setEnabled(selectedInstrument != null);
		}
	}

	@Override
	protected void configureShell(final Shell shell) {
		super.configureShell(shell);
		final String imagePath = activateMode ? IImageKeys.CURRENT_INSTRUMENT : IImageKeys.INSTRUMENT;
		shell.setText(activateMode ? DialogMessages.InstrumentSelectionDialog_activate_instrument
				: DialogMessages.InstrumentSelectionDialog_open_instrument);
		shell.setImage(Activator.getDefault().getImage(imagePath));
	}

	public Instrument getSelectedInstrument() {
		return selectedInstrument;
	}
}
