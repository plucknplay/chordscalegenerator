/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.editors;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.internal.WorkbenchPage;
import org.eclipse.ui.progress.UIJob;

import com.plucknplay.csg.core.Constants;
import com.plucknplay.csg.core.model.Chord;
import com.plucknplay.csg.core.model.Factory;
import com.plucknplay.csg.core.model.Interval;
import com.plucknplay.csg.core.model.IntervalContainer;
import com.plucknplay.csg.core.model.listeners.IChangeListener;
import com.plucknplay.csg.core.model.sets.Category;
import com.plucknplay.csg.core.model.sets.CategoryList;
import com.plucknplay.csg.core.model.sets.ChordList;
import com.plucknplay.csg.core.model.sets.IntervalContainerList;
import com.plucknplay.csg.core.model.sets.ScaleList;
import com.plucknplay.csg.core.model.workingCopies.IntervalContainerWorkingCopy;
import com.plucknplay.csg.core.model.workingCopies.WorkingCopyManager;
import com.plucknplay.csg.core.util.NamesUtil;
import com.plucknplay.csg.ui.editors.input.IntervalContainerEditorInput;
import com.plucknplay.csg.ui.util.LoginUtil;
import com.plucknplay.csg.ui.util.StatusLineUtil;
import com.plucknplay.csg.ui.views.AbstractCategoryView;
import com.plucknplay.csg.ui.views.ChordsView;

/**
 * Editor for editing interval container definitions, i.e. chord or scales.
 */
public class IntervalContainerEditor extends CategorizableEditor implements IChangeListener {

	public static final String ID = "com.plucknplay.csg.ui.editors.intervalContainerEditor"; //$NON-NLS-1$

	public static final String CHORD_EDITOR_HELP_ID = "chord_editor_context"; //$NON-NLS-1$
	public static final String SCALE_EDITOR_HELP_ID = "scale_editor_context"; //$NON-NLS-1$

	private static final Color BLACK = new Color(null, 0, 0, 0);
	private static final Color GRAY = new Color(null, 192, 192, 192);
	private static final Color LIGHT_GRAY = new Color(null, 223, 223, 223);
	private static final Color WHITE = new Color(null, 255, 255, 255);

	private static final String KEY_INTERVAL = "key.interval"; //$NON-NLS-1$
	private static final String KEY_NAME = "key.name"; //$NON-NLS-1$
	private static final String KEY_ITEM = "key.item";

	private IntervalContainer intervalContainer;
	private IntervalContainerWorkingCopy workingCopy;
	private CategoryList categoryList;
	private String linkedViewID;

	private Text akaText;

	private Button[][] buttons;
	private Composite intervalsComposite;

	@Override
	public void doSave(final IProgressMonitor monitor) {
		if (LoginUtil.isActivated()) {
			if (!workingCopy.save()) {

				final String thisElementName = intervalContainer.getType() == IntervalContainer.TYPE_CHORD ? EditorMessages.IntervalContainerEditor_this_chord
						: EditorMessages.IntervalContainerEditor_this_scale;
				final String aElementName = intervalContainer.getType() == IntervalContainer.TYPE_CHORD ? EditorMessages.IntervalContainerEditor_a_chord
						: EditorMessages.IntervalContainerEditor_a_scale;
				MessageDialog.openError(getShell(), EditorMessages.IntervalContainerEditor_error_title,
						NLS.bind(EditorMessages.IntervalContainerEditor_error_msg, thisElementName, aElementName));
			}
		} else {
			LoginUtil.showUnsupportedFeatureInformation(getSite().getShell());
		}
	}

	@Override
	public void init(final IEditorSite site, final IEditorInput input) throws PartInitException {
		if (!(input instanceof IntervalContainerEditorInput)) {
			throw new PartInitException(EditorMessages.IntervalContainerEditor_invalid_input);
		}

		final IntervalContainerEditorInput editorInput = (IntervalContainerEditorInput) input;

		intervalContainer = editorInput.getIntervalContainer();
		workingCopy = (IntervalContainerWorkingCopy) WorkingCopyManager.getInstance().getWorkingCopy(
				editorInput.getIntervalContainer(), editorInput.getCategory(), editorInput.isNewElement());
		workingCopy.addListener(this);
		categoryList = editorInput.getCategoryList();
		linkedViewID = editorInput.getViewID();

		super.init(site, workingCopy, editorInput);
	}

	@Override
	protected void createHeadClient(final FormToolkit toolkit, final Composite headComposite) {
		// name
		createNameComposite(toolkit, headComposite);
		getNameText().addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(final org.eclipse.swt.events.FocusEvent e) {
				workingCopy.setName(getNameText().getText(), true);
			}
		});
		getNameText().addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(final KeyEvent e) {
				if (e.character == ',' || e.character == ';') {
					e.doit = false;
				}
			}
		});

		// aka
		final Label akaLabel = toolkit.createLabel(headComposite, "aka:   "); //$NON-NLS-1$
		akaLabel.setBackground(null);

		akaText = toolkit.createText(headComposite, workingCopy.getAlsoKnownAsString(), SWT.BORDER);
		akaText.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(final KeyEvent e) {
				if (e.character == ';') {
					e.doit = false;
				}
			}
		});
		akaText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(final ModifyEvent e) {
				workingCopy.setAlsoKnownAsString(akaText.getText(), false);
			}
		});
		akaText.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(final org.eclipse.swt.events.FocusEvent e) {
				workingCopy.setAlsoKnownAsString(akaText.getText(), true);
			}
		});
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).indent(3, 0).grab(true, false).applyTo(akaText);

		final Label akaCommentLabel = toolkit.createLabel(headComposite, "(abc, xyz, ...)"); //$NON-NLS-1$
		akaCommentLabel.setBackground(null);
	}

	@Override
	protected void createMainArea(final FormToolkit toolkit, final Composite composite) {

		// interval section
		final Section intervalsSection = toolkit.createSection(composite, Section.EXPANDED);
		intervalsSection.setText(EditorMessages.IntervalContainerEditor_intervals_section);
		intervalsSection.marginWidth = 10;
		GridDataFactory.fillDefaults().grab(false, true).span(2, 1).applyTo(intervalsSection);
		toolkit.createCompositeSeparator(intervalsSection);

		// intervals composite
		intervalsComposite = toolkit.createComposite(intervalsSection);
		GridLayoutFactory.fillDefaults().margins(5, 0).extendedMargins(0, 0, 5, 0).spacing(5, 5)
				.applyTo(intervalsComposite);
		intervalsSection.setClient(intervalsComposite);

		final Table table = new Table(intervalsComposite, SWT.MULTI | SWT.HIDE_SELECTION);
		table.setLinesVisible(false);
		table.setHeaderVisible(false);
		GridDataFactory.fillDefaults().grab(false, true).applyTo(table);

		for (int i = 0; i < getColumnNumber() + 1; i++) {
			final TableColumn column = new TableColumn(table, SWT.RIGHT);
			column.setWidth(i == 0 ? 0 : 70);
		}

		buttons = new Button[Constants.INTERVALS_NUMBER][getColumnNumber()];
		for (int i = 0; i < Constants.INTERVALS_NUMBER; i++) {
			buttons[i] = createTableItem(table, i);
		}

		// comment section
		createCommmentSection(toolkit, composite);
		workingCopy.setComment(workingCopy.getComment());

		// final initializations
		categoryList.addChangeListener(this);
	}

	private Button[] createTableItem(final Table table, final int index) {
		final TableItem item = new TableItem(table, SWT.NONE);

		// create names array (little hack since the first column doesn't like
		// the column allignment)
		final String[] theNames = setTableItemText(index, item);

		// create table editors
		final List<String> mostImportantNames = NamesUtil.getMostImportantIntervalNames();
		final List<String> minorImportantNames = NamesUtil.getMinorImportantIntervalNames();
		final Interval interval = Factory.getInstance().getInterval(index);
		final Button[] result = new Button[getColumnNumber()];
		for (int i = 0; i < getColumnNumber() + 1; i++) {
			final String name = theNames[i];
			if (i == 0) {
				continue;
			}
			final Color foreground = mostImportantNames.contains(name) || minorImportantNames.contains(name) ? BLACK
					: GRAY;
			final Color background = mostImportantNames.contains(name) ? GRAY
					: minorImportantNames.contains(name) ? LIGHT_GRAY : WHITE;
			final TableEditor editor = new TableEditor(table);
			final Button button = new Button(table, SWT.CHECK);
			item.setForeground(i, foreground);
			item.setBackground(i, background);
			button.setBackground(background);
			button.setEnabled(index != 0 && !"".equals(name)); //$NON-NLS-1$
			button.setData(KEY_INTERVAL, interval);
			button.setData(KEY_NAME, name);
			button.setData(KEY_ITEM, item);
			button.setSelection(workingCopy.getIntervals().contains(interval)
					&& workingCopy.getIntervalName(interval).equals(name));
			button.pack();
			result[i - 1] = button;
			editor.minimumWidth = button.getSize().x;
			editor.horizontalAlignment = SWT.LEFT;
			editor.setEditor(button, item, i);
		}

		// add selection listeners
		if (index == 0) {
			return result;
		}
		for (final Button button : result) {
			button.addSelectionListener(new ButtonSelectionListener(result));
		}

		return result;
	}

	private String[] setTableItemText(final int index, final TableItem item) {
		final List<String> names = NamesUtil.getIntervalNames(Factory.getInstance().getInterval(index));
		final String[] theNames = new String[getColumnNumber() + 1];
		theNames[0] = ""; //$NON-NLS-1$
		for (int i = 0; i < getColumnNumber(); i++) {
			theNames[i + 1] = names.get(i);
		}
		item.setText(theNames);
		return theNames;
	}

	/**
	 * Selection listener for the interval name check boxes.
	 */
	private class ButtonSelectionListener extends SelectionAdapter {

		private final Button[] buttons;

		public ButtonSelectionListener(final Button[] buttons) {
			this.buttons = buttons;
		}

		@Override
		public void widgetSelected(final SelectionEvent e) {
			if (!(e.widget instanceof Button)) {
				return;
			}
			final Button button = (Button) e.widget;
			// add interval / change interval name
			if (button.getSelection()) {
				workingCopy.addInterval((Interval) button.getData(KEY_INTERVAL), (String) button.getData(KEY_NAME));
				for (final Button current : buttons) {
					if (button == current) {
						continue;
					}
					current.setSelection(false);
				}
			}
			// remove interval
			else {
				workingCopy.removeInterval((Interval) button.getData(KEY_INTERVAL));
			}
			updateErrorMessage();
		}
	}

	private int getColumnNumber() {
		return intervalContainer instanceof Chord ? 6 : 3;
	}

	@Override
	public void dispose() {
		workingCopy.removeListener(this);
		WorkingCopyManager.getInstance().disposeWorkingCopy(workingCopy.getIntervalContainer());
		categoryList.removeChangeListener(this);
		super.dispose();
	}

	@Override
	public void setFocus() {
		super.setFocus();
		
		final UIJob job = new UIJob(EditorMessages.IntervalContainerEditor_link_with_editor_job) {
			@Override
			public IStatus runInUIThread(final IProgressMonitor monitor) {

				final IWorkbenchPage page = getSite().getPage();
				final IViewPart view = page.findView(linkedViewID);
				final IViewReference viewReference = page.findViewReference(linkedViewID);

				if (view == null) {
					return Status.OK_STATUS;
				}

				try {
					if (page instanceof WorkbenchPage && !((WorkbenchPage) page).isFastView(viewReference)) {
						page.showView(linkedViewID);
					}
				} catch (final PartInitException e) {
				}
				if (view instanceof AbstractCategoryView && intervalContainer != null) {
					final CategoryList activeCategoryList = linkedViewID.equals(ChordsView.ID) ? ChordList
							.getInstance() : ScaleList.getInstance();
					((AbstractCategoryView) view).expandElements(activeCategoryList.getRootCategory().getCategoryPath(
							intervalContainer, true));
				}

				IntervalContainerEditor.super.setFocus();

				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}

	@Override
	public void notifyChange(final Object value, final Object property) {
		super.notifyChange(value, property);

		// also known as string changed
		if (property == IntervalContainerWorkingCopy.PROP_ALSO_KNOWN_AS_STRING_CHANGED) {
			final String string = (String) value;
			if (akaText != null && !akaText.isDisposed() && !akaText.getText().equals(string)) {
				akaText.setText(string);
			}
		}

		// interval added
		if (property == IntervalContainerWorkingCopy.PROP_INTERVAL_ADDED) {
			final Interval interval = (Interval) value;
			final int index = interval.getHalfsteps();
			final Button[] intervalButtons = buttons[index];
			for (final Button button : intervalButtons) {
				button.setSelection(workingCopy.getIntervalName(interval).equals(button.getData(KEY_NAME)));
			}
		}
		// interval removed
		else if (property == IntervalContainerWorkingCopy.PROP_INTERVAL_REMOVED) {
			final Interval interval = (Interval) value;
			final int index = interval.getHalfsteps();
			final Button[] intervalButtons = buttons[index];
			for (final Button intervalButton : intervalButtons) {
				intervalButton.setSelection(false);
			}
		}
	}

	@Override
	public void notifyChange(final Object source, final Object parentSource, final Object property) {
		if (property == CategoryList.PROP_REMOVED && source != null) {
			final Category category = categoryList.getRootCategory().getCategory(intervalContainer);
			if (source instanceof IntervalContainer && intervalContainer != null && intervalContainer.equals(source)
					|| source instanceof Category && category != null && category.equals(source)) {
				this.getEditorSite().getPage().closeEditor(this, false);
			}
		} else if (property == CategoryList.PROP_ADDED && source == intervalContainer) {
			if (getEditorInput() instanceof IntervalContainerEditorInput) {
				((IntervalContainerEditorInput) getEditorInput()).setNewElement(false);
			}

			setFocus();
			updateToolTip();
		} else if (property == CategoryList.PROP_CHANGED_ELEMENT
				&& (source == intervalContainer || source instanceof Category)) {
			if (source == intervalContainer) {
				setFocus();
			}
			updateToolTip();
		} else if (property == CategoryList.PROP_MOVED) {
			updateToolTip();
		} else if (property == IntervalContainerList.PROP_UPDATED_NAMES) {
			updateTableItems();
		}
	}

	private void updateTableItems() {
		workingCopy.updateIntervalNames();
		for (int i = 0; i < Constants.INTERVALS_NUMBER; i++) {
			final String[] names = setTableItemText(i, (TableItem) buttons[i][0].getData(KEY_ITEM));
			for (int j = 0; j < buttons[i].length; j++) {
				final String name = names[j];
				buttons[i][j].setData(KEY_NAME, name);
				final TableItem item = (TableItem) buttons[i][j].getData(KEY_ITEM);
				item.setText(name);
			}
		}
	}

	@Override
	protected String getTypeName() {
		return intervalContainer.getType() == IntervalContainer.TYPE_CHORD ? EditorMessages.IntervalContainerEditor_chord
				: EditorMessages.IntervalContainerEditor_scale;
	}

	@Override
	protected String getHelpId() {
		return intervalContainer.getType() == IntervalContainer.TYPE_CHORD ? CHORD_EDITOR_HELP_ID
				: SCALE_EDITOR_HELP_ID;
	}

	@Override
	protected void updateErrorMessage() {
		super.updateErrorMessage();

		if (!workingCopy.isValidIntervalList()) {
			getMessageManager().addMessage(EditorMessages.IntervalContainerEditor_message_intervals,
					getIntervalErrorMessage(), null, IMessageProvider.ERROR, intervalsComposite);
		} else {
			getMessageManager().removeMessage(EditorMessages.IntervalContainerEditor_message_intervals,
					intervalsComposite);
		}
	}

	@Override
	protected int getMinWidth() {
		return 360;
	}

	@Override
	public String getErrorMessage() {
		if (!workingCopy.isValidName() && workingCopy.isValidIntervalList()) {
			return getNameErrorMessage();
		}
		if (!workingCopy.isValidName() && !workingCopy.isValidIntervalList()) {
			return getTypeName() + EditorMessages.IntervalContainerEditor_must_have_a_name_and_at_least_two_intervals;
		}
		if (workingCopy.isValidName() && !workingCopy.isValidIntervalList()) {
			return getIntervalErrorMessage();
		}
		return null;
	}

	@Override
	public String getNameErrorMessage() {
		return getTypeName() + EditorMessages.IntervalContainerEditor__must_have_a_name;
	}

	public String getIntervalErrorMessage() {
		return getTypeName() + EditorMessages.IntervalContainerEditor_must_have_at_least_two_intervals;
	}
}
