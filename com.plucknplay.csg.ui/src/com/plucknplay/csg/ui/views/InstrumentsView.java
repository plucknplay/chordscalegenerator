/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.views;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchActionConstants;

import com.plucknplay.csg.core.model.Categorizable;
import com.plucknplay.csg.core.model.Instrument;
import com.plucknplay.csg.core.model.sets.Category;
import com.plucknplay.csg.core.model.sets.CategoryList;
import com.plucknplay.csg.core.model.sets.InstrumentList;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.actions.common.RenameAction;
import com.plucknplay.csg.ui.actions.instruments.AddInstrumentAction;
import com.plucknplay.csg.ui.actions.instruments.AddInstrumentCategoryAction;
import com.plucknplay.csg.ui.actions.instruments.CopyInstrumentAction;
import com.plucknplay.csg.ui.actions.instruments.CutInstrumentAction;
import com.plucknplay.csg.ui.actions.instruments.ExportInstrumentAction;
import com.plucknplay.csg.ui.actions.instruments.ImportInstrumentAction;
import com.plucknplay.csg.ui.actions.instruments.PasteInstrumentAction;
import com.plucknplay.csg.ui.actions.instruments.RemoveInstrumentAction;
import com.plucknplay.csg.ui.actions.instruments.SetCurrentInstrumentAction;
import com.plucknplay.csg.ui.activation.NlsUtil;
import com.plucknplay.csg.ui.editors.InstrumentEditor;
import com.plucknplay.csg.ui.editors.input.InstrumentEditorInput;
import com.plucknplay.csg.ui.views.dnd.InstrumentDropAdapter;
import com.plucknplay.csg.ui.views.dnd.ModelObjectDropAdapter;
import com.plucknplay.csg.ui.views.dnd.ModelObjectTransfer;

/**
 * This view shows all known instruments of this application in a flat list.
 */
public class InstrumentsView extends AbstractCategoryView {

	public static final String ID = "com.plucknplay.csg.ui.views.InstrumentsView"; //$NON-NLS-1$
	public static final String HELP_ID = "instruments_view_context"; //$NON-NLS-1$

	private AddInstrumentAction addAction;
	private AddInstrumentCategoryAction addCategoryAction;
	private CutInstrumentAction cutAction;
	private CopyInstrumentAction copyAction;
	private PasteInstrumentAction pasteAction;
	private RemoveInstrumentAction removeAction;
	private RenameAction renameAction;
	private ImportInstrumentAction importAction;
	private ExportInstrumentAction exportAction;
	private SetCurrentInstrumentAction setCurrentInstrumentAction;

	@Override
	public void createPartControl(final Composite parent) {

		createCategoryViewer(parent);

		// set context-sensitive help
		Activator.getDefault().setHelp(parent, HELP_ID);

		// create and register actions
		addAction = new AddInstrumentAction(this, true);
		addCategoryAction = new AddInstrumentCategoryAction(this, true);
		cutAction = new CutInstrumentAction(this);
		copyAction = new CopyInstrumentAction();
		pasteAction = new PasteInstrumentAction(this);
		removeAction = new RemoveInstrumentAction(this);
		renameAction = new RenameAction(this);
		importAction = new ImportInstrumentAction(this);
		exportAction = new ExportInstrumentAction(this);
		setCurrentInstrumentAction = new SetCurrentInstrumentAction(this);

		addAction(addAction);
		addAction(addCategoryAction);
		addAction(cutAction);
		addAction(copyAction);
		addAction(pasteAction);
		addAction(removeAction);
		addAction(renameAction);
		addAction(importAction);
		addAction(exportAction);
		addAction(setCurrentInstrumentAction);

		final Activator activator = Activator.getDefault();
		activator.registerAction(getSite(), addAction);
		activator.registerAction(getSite(), addCategoryAction);
		activator.registerAction(getSite(), cutAction);
		activator.registerAction(getSite(), copyAction);
		activator.registerAction(getSite(), pasteAction);
		activator.registerAction(getSite(), removeAction);
		activator.registerAction(getSite(), renameAction);
		activator.registerAction(getSite(), importAction);
		activator.registerAction(getSite(), exportAction);
		activator.registerAction(getSite(), setCurrentInstrumentAction);
	}

	@Override
	public void notifyChange(final Object source, final Object parentSource, final Object property) {
		super.notifyChange(source, parentSource, property);
		if (property == InstrumentList.PROP_CURRENT_INSTRUMENT_CHANGED) {
			if (source != null) {
				refreshViewer();
			}
		}
	}

	@Override
	protected CategoryList getCategoryList() {
		return InstrumentList.getInstance();
	}

	@Override
	protected IEditorInput getEditorInput(final Categorizable element, final Category category, final boolean newElement) {
		return new InstrumentEditorInput((Instrument) element, category, newElement);
	}

	@Override
	protected String getEditorID() {
		return InstrumentEditor.ID;
	}

	@Override
	protected int getDNDOpererations() {
		return DND.DROP_COPY | DND.DROP_MOVE;
	}

	@Override
	protected ModelObjectDropAdapter getDropAdapter(final TreeViewer viewer) {
		return new InstrumentDropAdapter(viewer);
	}

	@Override
	protected Object getDNDTransferType() {
		return ModelObjectTransfer.TYPE_INSTRUMENT;
	}

	@Override
	protected void contributeToActionBars() {
		super.contributeToActionBars();

		// create actions
		final AddInstrumentCategoryAction addCategoryAction2 = new AddInstrumentCategoryAction(this, false);
		final AddInstrumentAction addInstrumentAction = new AddInstrumentAction(this, false);

		// register actions
		final Activator activator = Activator.getDefault();
		activator.registerAction(getSite(), addCategoryAction2);
		activator.registerAction(getSite(), addInstrumentAction);

		// contribute actions
		final IActionBars bars = getViewSite().getActionBars();
		bars.getToolBarManager().appendToGroup(IWorkbenchActionConstants.MB_ADDITIONS, addInstrumentAction);
		bars.getToolBarManager().appendToGroup(IWorkbenchActionConstants.MB_ADDITIONS, addCategoryAction2);

		bars.getMenuManager().appendToGroup(IWorkbenchActionConstants.MB_ADDITIONS, addInstrumentAction);
		bars.getMenuManager().appendToGroup(IWorkbenchActionConstants.MB_ADDITIONS, addCategoryAction2);
	}

	@Override
	protected void addToContextMenu(final IMenuManager mgr) {
		// new menu
		final IMenuManager newMenu = new MenuManager(NlsUtil.getMenu_new());
		newMenu.add(addAction);
		newMenu.add(addCategoryAction);
		mgr.appendToGroup(POPUP_NEW_GROUP, newMenu);

		// actions
		mgr.appendToGroup(POPUP_EDIT_GROUP, cutAction);
		mgr.appendToGroup(POPUP_EDIT_GROUP, copyAction);
		mgr.appendToGroup(POPUP_EDIT_GROUP, pasteAction);
		mgr.appendToGroup(POPUP_EDIT_GROUP, removeAction);
		mgr.appendToGroup(POPUP_EDIT_GROUP, renameAction);
		mgr.appendToGroup(POPUP_IMPORT_GROUP, importAction);
		mgr.appendToGroup(POPUP_IMPORT_GROUP, exportAction);
		mgr.appendToGroup(POPUP_BOTTOM_GROUP, setCurrentInstrumentAction);
	}
}
