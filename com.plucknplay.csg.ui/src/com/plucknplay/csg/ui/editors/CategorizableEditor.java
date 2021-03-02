/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.editors;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.EditorPart;

import com.plucknplay.csg.core.model.workingCopies.IWorkingCopyChangeListener;
import com.plucknplay.csg.core.model.workingCopies.WorkingCopy;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.IImageKeys;
import com.plucknplay.csg.ui.editors.input.CategorizableEditorInput;
import com.plucknplay.csg.ui.util.StatusLineUtil;

public abstract class CategorizableEditor extends EditorPart implements IWorkingCopyChangeListener {

	private Text nameText;

	private WorkingCopy workingCopy;
	private IEditorSite site;
	private boolean dirty;

	private FormToolkit toolkit;
	private ScrolledForm form;
	private Image image;
	private Button saveButton;
	private Text commentText;
	private IMessageManager messageManager;

	protected void init(final IEditorSite site, final WorkingCopy workingCopy,
			final CategorizableEditorInput editorInput) {

		setSite(site);
		setInput(editorInput);

		this.workingCopy = workingCopy;
		this.site = site;
		image = editorInput.getImageDescriptor().createImage();

		setPartName(workingCopy.getName());
		setDirty(workingCopy.isDirty());
		setTitleImage(image);
	}

	@Override
	public void createPartControl(final Composite parent) {

		// init ui forms
		toolkit = new FormToolkit(parent.getDisplay());
		form = toolkit.createScrolledForm(parent);
		// form = createScrolledForm(parent);
		form.setText(getTypeName());
		form.setImage(image);

		// create header with toolbar
		toolkit.decorateFormHeading(form.getForm());
		final Composite headComposite = toolkit.createComposite(form.getForm().getHead(), SWT.NONE);
		headComposite.setBackground(null);

		GridLayoutFactory.fillDefaults().numColumns(3).margins(2, 5).extendedMargins(13, 0, 0, 0).spacing(5, 5)
				.applyTo(headComposite);

		createHeadClient(toolkit, headComposite);
		form.setHeadClient(headComposite);
		createToolbar(toolkit, form.getForm());

		// create main area
		final Composite body = form.getBody();
		GridLayoutFactory.fillDefaults().applyTo(body);

		final Composite composite = toolkit.createComposite(body);
		GridDataFactory.fillDefaults().hint(getMinWidth(), SWT.DEFAULT).grab(true, true).applyTo(composite);

		// scrolledFormComposite = new ScrolledFormComposite(body, toolkit);
		// scrolledFormComposite.setExpandHorizontal(true);
		// scrolledFormComposite.setExpandVertical(true);
		// scrolledFormComposite.setBackground(toolkit.getColors().getBackground());
		// scrolledFormComposite.setForeground(toolkit.getColors().getForeground());
		// GridDataFactory.fillDefaults().grab(true,
		// true).applyTo(scrolledFormComposite);
		//
		// Composite composite = (Composite) scrolledFormComposite.getContent();
		GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).margins(5, 5).extendedMargins(0, 0, 10, 0)
				.spacing(5, 15).applyTo(composite);

		createMainArea(toolkit, composite);

		// final initialization
		Activator.getDefault().setHelp(parent, getHelpId());

		messageManager = new ManagedForm(toolkit, form).getMessageManager();
		messageManager.setDecorationPosition(SWT.LEFT | SWT.TOP);

		updateErrorMessage();
		reflow();
	}

	protected Text getNameText() {
		return nameText;
	}

	protected void reflow() {
		// scrolledFormComposite.reflow(true);
		form.reflow(true);
	}

	protected FormToolkit getToolkit() {
		return toolkit;
	}

	protected Shell getShell() {
		return form.getShell();
	}

	protected abstract int getMinWidth();

	protected abstract void createHeadClient(FormToolkit toolkit, Composite headComposite);

	protected abstract void createMainArea(FormToolkit toolkit, Composite composite);

	protected abstract String getTypeName();

	protected abstract String getHelpId();

	protected void createNameComposite(final FormToolkit toolkit, final Composite parent) {
		final Label nameLabel = toolkit.createLabel(parent, EditorMessages.CategorizableEditor_name);
		nameLabel.setBackground(null);

		nameText = toolkit.createText(parent, workingCopy.getName(), SWT.BORDER);
		nameText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(final ModifyEvent e) {
				workingCopy.setName(nameText.getText());
				updateErrorMessage();
			}
		});
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).span(2, 1).indent(3, 0).grab(true, true)
				.applyTo(nameText);
	}

	protected void createCommmentSection(final FormToolkit toolkit, final Composite composite) {

		// comment section
		final Section commentSection = toolkit.createSection(composite, Section.EXPANDED);
		commentSection.setText(EditorMessages.CategorizableEditor_comment);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).span(2, 1).indent(10, 0)
				.applyTo(commentSection);
		toolkit.createCompositeSeparator(commentSection);

		// comment composite
		final Composite commentComposite = toolkit.createComposite(commentSection);
		GridLayoutFactory.fillDefaults().margins(5, 5).applyTo(commentComposite);
		commentSection.setClient(commentComposite);

		// comment text
		commentText = toolkit.createText(commentComposite, "", SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL); //$NON-NLS-1$
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).minSize(1, 80).applyTo(commentText);
		commentText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(final ModifyEvent e) {
				workingCopy.setComment(commentText.getText());
			}
		});
	}

	@Override
	public void doSaveAs() {
		// do nothing
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	protected void setDirty(final boolean dirty) {
		this.dirty = dirty;
		firePropertyChange(PROP_DIRTY);
	}

	@Override
	public boolean isDirty() {
		return dirty;
	}

	protected void updateToolTip() {
		setTitleToolTip(getEditorInput().getToolTipText());
	}

	protected void createToolbar(final FormToolkit toolkit, final Form form) {
		final IToolBarManager toolbar = form.getToolBarManager();

		// save contribution item
		final ControlContribution save = new ControlContribution("save") { //$NON-NLS-1$

			@Override
			protected Control createControl(final Composite parent) {
				saveButton = toolkit.createButton(parent, EditorMessages.CategorizableEditor_save, SWT.PUSH);
				saveButton.setBackground(null);
				saveButton.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(final SelectionEvent e) {
						doSave(new NullProgressMonitor());
					}
				});
				saveButton.setEnabled(workingCopy.isDirty());
				return saveButton;
			}
		};
		toolbar.add(save);

		form.updateToolBar();
		toolbar.update(true);
		form.setToolBarVerticalAlignment(SWT.TOP);
	}

	@Override
	public void notifyChange(final Object value, final Object property) {
		// name
		if (property == WorkingCopy.PROP_NAME_CHANGED) {
			if (!nameText.isDisposed() && !nameText.getText().equals(value)) {
				nameText.setText((String) value);
			}
			if (!getPartName().equals(value)) {
				setPartName((String) value);
				updateToolTip();
			}
		}

		// comment
		else if (property == WorkingCopy.PROP_COMMENT_CHANGED) {
			if (!commentText.isDisposed() && !commentText.getText().equals(value)) {
				commentText.setText((String) value);
			}
		}

		// dirty state
		else if (property == WorkingCopy.PROP_DIRTY_STATE_CHANGED) {
			final boolean enabled = ((Boolean) value).booleanValue();
			setDirty(enabled);
			updateSaveButtonEnablement();
		}
	}

	private void updateSaveButtonEnablement() {
		saveButton.setEnabled(getErrorMessage() == null && workingCopy.isDirty());
	}

	@Override
	public void setFocus() {
		StatusLineUtil.clearStatusLine(getEditorSite());
		if (nameText != null && !nameText.isDisposed()) {
			nameText.setFocus();
			nameText.forceFocus();
			updateErrorMessage();
		}
	}

	@Override
	public void dispose() {
		image.dispose();
		site.getActionBars().getStatusLineManager().setErrorMessage(null);
		super.dispose();
	}

	/**
	 * Shows an error message at the status line.
	 */
	protected void updateErrorMessage() {

		// update status line
		final IStatusLineManager statusLineManager = site.getActionBars().getStatusLineManager();
		if (getErrorMessage() == null) {
			StatusLineUtil.clearStatusLine(getEditorSite());
			statusLineManager.setErrorMessage(null, null);
		} else {
			statusLineManager.setErrorMessage(Activator.getDefault().getImage(IImageKeys.ERROR), getErrorMessage());
			StatusLineUtil.prepareStatusLineForError(getEditorSite());
		}

		// udate error message inside editor
		if (!workingCopy.isValidName()) {
			messageManager.addMessage(EditorMessages.CategorizableEditor_message_name, getNameErrorMessage(), null,
					IMessageProvider.ERROR, nameText);
		} else {
			messageManager.removeMessage(EditorMessages.CategorizableEditor_message_name, nameText);
		}

		// update save button
		updateSaveButtonEnablement();
	}

	protected abstract String getErrorMessage();

	protected abstract String getNameErrorMessage();

	protected IMessageManager getMessageManager() {
		return messageManager;
	}

	// private ScrolledForm createScrolledForm(Composite parent) {
	// ScrolledForm form = new ScrolledForm(parent, SWT.NONE) {
	// public void setMessage(String newMessage, int newType, IMessage[]
	// messages) {
	// ((Form) getContent()).setMessage(newMessage, newType, messages);
	// // reflow(true);
	// }
	// };
	// form.setExpandHorizontal(true);
	// form.setExpandVertical(true);
	// form.setFont(JFaceResources.getHeaderFont());
	// return form;
	// }
}
