/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.wizards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.plucknplay.csg.core.model.IntervalContainer;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.IImageKeys;
import com.plucknplay.csg.ui.util.DefaultCollectionContentProvider;
import com.plucknplay.csg.ui.util.IntervalTableViewerLabelProvider;
import com.plucknplay.csg.ui.util.IntervalViewerSorter;

public class CleanUpWizardPage extends WizardPage {

	private final Set<IntervalContainer> set;

	private ListViewer namesViewer;
	private Button removeNameButton;
	private TableViewer intervalViewer;

	public CleanUpWizardPage(final Set<IntervalContainer> set, final String pageName, final String title,
			final ImageDescriptor titleImage, final Object type) {
		super(pageName, title, titleImage);
		this.set = set;

		// descripton
		final String elementNames = determineNamesString();
		final String typeName = type == IntervalContainer.TYPE_CHORD ? WizardMessages.CleanUpWizardPage_chords
				: WizardMessages.CleanUpWizardPage_scales;
		setDescription(NLS.bind(WizardMessages.CleanUpWizardPage_description, typeName, elementNames));
	}

	@Override
	public void createControl(final Composite parent) {

		final Composite composite = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(2).margins(5, 5).spacing(5, 5).applyTo(composite);

		// names label
		final Label namesLabel = new Label(composite, SWT.LEFT);
		namesLabel.setText(WizardMessages.CleanUpWizardPage_names);
		GridDataFactory.fillDefaults().span(2, 1).applyTo(namesLabel);

		// names list
		namesViewer = new ListViewer(composite, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL);
		namesViewer.setContentProvider(new DefaultCollectionContentProvider());
		namesViewer.setLabelProvider(new LabelProvider());
		namesViewer.setSorter(new ViewerSorter());
		namesViewer.getList().setSize(-1, 100);
		final Set<String> names = determineAllNames();
		namesViewer.setInput(names);
		namesViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(final SelectionChangedEvent event) {
				setPageComplete(isPageComplete());
				updateRemoveButton();
			}
		});
		namesViewer.getList().select(0);
		namesViewer.getList().addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(final KeyEvent e) {
				if (e.keyCode == SWT.DEL) {
					removeSelectedName();
				}
			}
		});
		GridDataFactory.fillDefaults().grab(true, true).applyTo(namesViewer.getList());

		// delete button
		removeNameButton = new Button(composite, SWT.PUSH);
		removeNameButton.setImage(Activator.getDefault().getImage(IImageKeys.REMOVE));
		removeNameButton.setToolTipText(WizardMessages.CleanUpWizardPage_remove_name);
		removeNameButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				removeSelectedName();
			}
		});
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).grab(false, false).applyTo(removeNameButton);

		// intervals label
		final Label intervalsLabel = new Label(composite, SWT.LEFT);
		intervalsLabel.setText(WizardMessages.CleanUpWizardPage_intervals);
		GridDataFactory.fillDefaults().span(2, 1).applyTo(intervalsLabel);

		// intervals table
		intervalViewer = new TableViewer(composite, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER
				| SWT.HIDE_SELECTION);
		intervalViewer.setContentProvider(new IStructuredContentProvider() {
			@Override
			public Object[] getElements(final Object inputElement) {
				if (inputElement instanceof Set) {
					final IntervalContainer element = (IntervalContainer) ((Set<?>) inputElement).iterator().next();
					return element.getIntervals().toArray();
				}
				return null;
			}

			@Override
			public void dispose() {
			}

			@Override
			public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
			}
		});
		intervalViewer.setLabelProvider(new IntervalTableViewerLabelProvider(set));
		intervalViewer.setSorter(new IntervalViewerSorter(set));
		intervalViewer.setInput(set);
		GridDataFactory.fillDefaults().span(1, 1).grab(true, true).applyTo(intervalViewer.getTable());

		// last initialization
		updateRemoveButton();
		setControl(composite);
	}

	private Set<String> determineAllNames() {
		final Set<String> result = new HashSet<String>();
		for (final IntervalContainer element : set) {
			result.add(element.getName());
			result.addAll(element.getAlsoKnownAsNamesList());
		}
		return result;
	}

	private String determineNamesString() {
		final StringBuffer buf = new StringBuffer();
		for (final Iterator<IntervalContainer> iter = set.iterator(); iter.hasNext();) {
			final IntervalContainer element = iter.next();
			buf.append(element.getName());
			if (iter.hasNext()) {
				buf.append(", "); //$NON-NLS-1$
			}
		}
		final String result = buf.toString();
		final String theResult = result.substring(0, result.lastIndexOf(", ")) + WizardMessages.CleanUpWizardPage_and + result.substring(result.lastIndexOf(", ") + 2); //$NON-NLS-1$ //$NON-NLS-2$ 
		return theResult;
	}

	private void removeSelectedName() {
		if (!removeNameButton.isEnabled()) {
			return;
		}
		final String[] selection = namesViewer.getList().getSelection();
		if (selection.length > 0) {
			final String selectedName = selection[0];
			namesViewer.remove(selectedName);
			updateRemoveButton();
		}
	}

	private void updateRemoveButton() {
		removeNameButton.setEnabled(namesViewer.getList().getItemCount() > 1 && !namesViewer.getSelection().isEmpty());
	}

	@Override
	public boolean isPageComplete() {
		return !namesViewer.getSelection().isEmpty();
	}

	/**
	 * Returns the selected main name.
	 * 
	 * @return the selected main name.
	 */
	public String getSelectedName() {
		final String[] selection = namesViewer.getList().getSelection();
		return selection.length > 0 ? selection[0] : null;
	}

	/**
	 * Returns the remaining 'also known as' names.
	 * 
	 * @return the remaining 'also known as' names, never null
	 */
	public String getAkaNames() {
		// determine aka names list
		final List<String> list = new ArrayList<String>();
		list.addAll(Arrays.asList(namesViewer.getList().getItems()));
		list.remove(getSelectedName());

		// create string
		final StringBuffer buf = new StringBuffer();
		for (final Iterator<String> iter = list.iterator(); iter.hasNext();) {
			final String name = iter.next();
			buf.append(name);
			if (iter.hasNext()) {
				buf.append(", "); //$NON-NLS-1$
			}
		}
		return buf.toString();
	}

	/**
	 * Returns the interval containers which are associated with this page.
	 * 
	 * @return the interval containers which are associated with this page
	 */
	public Set<IntervalContainer> getIntervalContainers() {
		return set;
	}
}
