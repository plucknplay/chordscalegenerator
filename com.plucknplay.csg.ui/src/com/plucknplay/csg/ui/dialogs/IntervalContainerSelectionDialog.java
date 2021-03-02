/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import com.plucknplay.csg.core.model.Categorizable;
import com.plucknplay.csg.core.model.IntervalContainer;
import com.plucknplay.csg.core.model.sets.Category;
import com.plucknplay.csg.core.model.sets.CategoryList;
import com.plucknplay.csg.core.model.sets.ChordList;
import com.plucknplay.csg.core.model.sets.ScaleList;
import com.plucknplay.csg.core.model.workingCopies.IntervalContainerWorkingCopy;
import com.plucknplay.csg.core.model.workingCopies.WorkingCopyManager;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.IImageKeys;

public class IntervalContainerSelectionDialog extends ElementListSelectionDialog {

	private static final String COMMA = ", ";
	private Object type;
	private Button button;

	private int tooLessIntervals;
	private int tooManyIntervals;

	public IntervalContainerSelectionDialog(final Shell parent, final Object type, final List<Categorizable> input) {
		this(parent, type, input, 0, 0);
	}

	public IntervalContainerSelectionDialog(final Shell parent, final Object type, final List<Categorizable> input,
			final int numberOfFilteredChordsWithTooLessIntervals, final int numberOfFilteredChordsWithTooManyIntervals) {

		super(parent, new LabelProvider() {
			@Override
			public String getText(final Object element) {
				if (element instanceof IntervalContainerWrapper) {
					return ((IntervalContainerWrapper) element).getFullName();
				}
				return super.getText(element);
			}

			@Override
			public Image getImage(final Object element) {
				if (element instanceof IntervalContainerWrapper) {
					final Object icType = ((IntervalContainerWrapper) element).getIntervalContainer().getType();
					if (icType == IntervalContainer.TYPE_CHORD) {
						return Activator.getDefault().getImage(IImageKeys.CHORD);
					}
					if (icType == IntervalContainer.TYPE_SCALE) {
						return Activator.getDefault().getImage(IImageKeys.SCALE);
					}
				}
				return super.getImage(element);
			}
		});

		tooLessIntervals = numberOfFilteredChordsWithTooLessIntervals;
		tooManyIntervals = numberOfFilteredChordsWithTooManyIntervals;

		String title;
		String msg;
		if (type == IntervalContainer.TYPE_CHORD) {
			title = DialogMessages.IntervalContainerSelectionDialog_open_chord;
			msg = DialogMessages.IntervalContainerSelectionDialog_select_a_chord;
		} else if (type == IntervalContainer.TYPE_SCALE) {
			title = DialogMessages.IntervalContainerSelectionDialog_open_scale;
			msg = DialogMessages.IntervalContainerSelectionDialog_select_a_scale;
		} else {
			throw new IllegalArgumentException();
		}
		this.type = type;

		setTitle(title);
		setMessage(msg + " " + DialogMessages.IntervalContainerSelectionDialog_any_character_any_string); //$NON-NLS-1$
		setMultipleSelection(false);
		setSize(50, 20);

		// set elements
		final List<IntervalContainerWrapper> wrappedList = new ArrayList<IntervalContainerWrapper>();
		for (final Categorizable categorizable : input) {
			if (categorizable instanceof IntervalContainer) {
				final IntervalContainer container = (IntervalContainer) categorizable;
				wrappedList.add(new IntervalContainerWrapper(container, container.getName()));
				for (final String string : container.getAlsoKnownAsNamesList()) {
					wrappedList.add(new IntervalContainerWrapper(container, string));
				}
			}
		}
		setElements(wrappedList.toArray());

		// Hide help button
		setHelpAvailable(false);
	}

	@Override
	protected Control createDialogArea(final Composite parent) {

		final Composite mainComposite = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().applyTo(mainComposite);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(mainComposite);

		// default dialog area (type-a-head list)
		final Control dialogArea = super.createDialogArea(mainComposite);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(dialogArea);

		// check box
		button = new Button(mainComposite, SWT.CHECK);
		button.setText(DialogMessages.IntervalContainerSelectionDialog_take_selected_name_as_main_name);
		GridDataFactory.fillDefaults().grab(true, false).indent(10, -10).applyTo(button);

		// warning messages
		if (tooLessIntervals > 0 || tooManyIntervals > 0) {
			final Composite warningComposite = new Composite(mainComposite, SWT.NONE);
			warningComposite.setBackground(warningComposite.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
			GridLayoutFactory.fillDefaults().numColumns(2).extendedMargins(10, 15, 10, 10).applyTo(warningComposite);
			GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(warningComposite);

			if (tooLessIntervals > 0) {
				final Label tooLessImageLabel = new Label(warningComposite, SWT.LEFT);
				tooLessImageLabel.setImage(Activator.getDefault().getImage(IImageKeys.WARNING));
				final Label tooLessLabel = new Label(warningComposite, SWT.LEFT | SWT.WRAP);
				tooLessLabel
						.setText(tooLessIntervals == 1 ? DialogMessages.IntervalContainerSelectionDialog_too_less_intervals_singular
								: NLS.bind(DialogMessages.IntervalContainerSelectionDialog_too_less_intervals_plural,
										tooLessIntervals));
				GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false)
						.hint(button.getSize().x, SWT.DEFAULT).applyTo(tooLessLabel);
			}

			if (tooManyIntervals > 0) {
				final Label tooManyImageLabel = new Label(warningComposite, SWT.LEFT);
				tooManyImageLabel.setImage(Activator.getDefault().getImage(IImageKeys.WARNING));
				final Label tooManyLabel = new Label(warningComposite, SWT.LEFT | SWT.WRAP);
				tooManyLabel
						.setText(tooManyIntervals == 1 ? DialogMessages.IntervalContainerSelectionDialog_too_many_intervals_singular
								: NLS.bind(DialogMessages.IntervalContainerSelectionDialog_too_many_intervals_plural,
										tooManyIntervals));
				GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false)
						.hint(button.getSize().x, SWT.DEFAULT).applyTo(tooManyLabel);
			}
		}

		return mainComposite;
	}

	@Override
	protected Label createMessageArea(final Composite composite) {
		final Label label = new Label(composite, SWT.WRAP);
		if (getMessage() != null) {
			label.setText(getMessage());
		}
		label.setFont(composite.getFont());
		GridDataFactory.fillDefaults().grab(true, false).applyTo(label);
		return label;
	}

	@Override
	protected void handleSelectionChanged() {
		super.handleSelectionChanged();

		if (button == null || button.isDisposed()) {
			return;
		}

		final Object[] selectedElements = getSelectedElements();
		if (selectedElements.length > 0) {
			final CategoryList categoryList = getCategoryList();
			final IntervalContainerWrapper wrapper = (IntervalContainerWrapper) getSelectedElements()[0];
			final IntervalContainer element = wrapper.getIntervalContainer();
			button.setEnabled(!wrapper.getName().equals(element.getName())
					&& categoryList.getElement(wrapper.getName()) == null);
		} else {
			button.setEnabled(true);
		}
	}

	private CategoryList getCategoryList() {
		return type == IntervalContainer.TYPE_CHORD ? ChordList.getInstance() : ScaleList.getInstance();
	}

	@Override
	public Object[] getResult() {
		final Object[] result = super.getResult();

		if (result == null) {
			return null;
		}
		for (int i = 0; i < result.length; i++) {
			final Object object = result[i];
			if (object instanceof IntervalContainerWrapper) {
				result[i] = ((IntervalContainerWrapper) object).getIntervalContainer();
			}
		}

		return result;
	}

	@Override
	protected void okPressed() {
		final Object[] selectedElements = getSelectedElements();

		// rename selected interval container if wanted
		if (button.isEnabled() && button.getSelection() && selectedElements.length > 0) {

			final IntervalContainerWrapper wrapper = (IntervalContainerWrapper) selectedElements[0];
			final IntervalContainer theElement = wrapper.getIntervalContainer();
			final Category category = getCategoryList().getRootCategory().getCategory(theElement);

			// modify working copy and save
			final IntervalContainerWorkingCopy workingCopy = (IntervalContainerWorkingCopy) WorkingCopyManager
					.getInstance().getWorkingCopy(theElement, category, false);
			workingCopy.setAlsoKnownAsString(theElement.getAlsoKnownAsString() + COMMA + theElement.getName(), false);
			workingCopy.setName(wrapper.getName(), true);
			workingCopy.saveName();
			WorkingCopyManager.getInstance().disposeWorkingCopy(theElement);
		}

		// close dialog
		super.okPressed();
	}

	/**
	 * This class wraps interval containers to enable more than one entry for
	 * each interval container.
	 */
	private static class IntervalContainerWrapper {

		private final IntervalContainer intervalContainer;
		private final String name;
		private final List<String> names;
		private final String fullName;

		/**
		 * The constructor.
		 * 
		 * @param intervalContainer
		 *            the interval container to wrap, must not be null
		 * @param name
		 *            the main name which should be displayed, must not be null
		 *            and must be one of the known names of the given interval
		 *            container
		 */
		public IntervalContainerWrapper(final IntervalContainer intervalContainer, final String name) {
			if (intervalContainer == null || name == null) {
				throw new IllegalArgumentException();
			}

			this.intervalContainer = intervalContainer;
			this.name = name;

			// fill list with all names
			names = new ArrayList<String>();
			names.add(intervalContainer.getName());
			for (final String currentName : intervalContainer.getAlsoKnownAsNamesList()) {
				if (!names.contains(currentName)) {
					names.add(currentName);
				}
			}

			if (!names.contains(name)) {
				throw new IllegalArgumentException();
			}

			fullName = createFullName();
		}

		/**
		 * Returns the wrapped interval container.
		 * 
		 * @return the wrapped interval container
		 */
		public IntervalContainer getIntervalContainer() {
			return intervalContainer;
		}

		/**
		 * Creates the full name of this wrapper.
		 * 
		 * @return the full name of this wrapper
		 */
		private String createFullName() {
			final StringBuffer buf = new StringBuffer(name);

			if (names.size() > 1) {
				buf.append("  ....  "); //$NON-NLS-1$
			}

			for (final String currentName : names) {
				if (!currentName.equals(name)) {
					buf.append(currentName + COMMA);
				}
			}

			// cut last ", "
			String result = buf.toString();
			if (result.endsWith(COMMA)) {
				result = result.substring(0, result.length() - 2);
			}

			return result;
		}

		/**
		 * Returns the full name of this wrapper.
		 * 
		 * @return the full name of this wrapper
		 */
		public String getFullName() {
			return fullName;
		}

		/**
		 * Returns the main name of this wrapper. The main name is the first
		 * displayed name of this wrapper.
		 * 
		 * @return the main name of this wrapper
		 */
		public String getName() {
			return name;
		}
	}
}
