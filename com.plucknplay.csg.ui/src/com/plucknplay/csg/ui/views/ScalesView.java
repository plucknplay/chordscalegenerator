/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.views;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchActionConstants;

import com.plucknplay.csg.core.model.Categorizable;
import com.plucknplay.csg.core.model.Scale;
import com.plucknplay.csg.core.model.sets.Category;
import com.plucknplay.csg.core.model.sets.CategoryList;
import com.plucknplay.csg.core.model.sets.ScaleList;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.actions.ActionMessages;
import com.plucknplay.csg.ui.actions.common.CopyBlocksToClipboardAction;
import com.plucknplay.csg.ui.actions.common.RenameAction;
import com.plucknplay.csg.ui.actions.scales.AddScaleAction;
import com.plucknplay.csg.ui.actions.scales.AddScaleCategoryAction;
import com.plucknplay.csg.ui.actions.scales.CleanUpScalesAction;
import com.plucknplay.csg.ui.actions.scales.CutScaleAction;
import com.plucknplay.csg.ui.actions.scales.DuplicateScaleAction;
import com.plucknplay.csg.ui.actions.scales.ExportScaleAction;
import com.plucknplay.csg.ui.actions.scales.ImportScaleAction;
import com.plucknplay.csg.ui.actions.scales.PasteScaleAction;
import com.plucknplay.csg.ui.actions.scales.RemoveScaleAction;
import com.plucknplay.csg.ui.activation.NlsUtil;
import com.plucknplay.csg.ui.editors.IntervalContainerEditor;
import com.plucknplay.csg.ui.editors.input.ScaleEditorInput;
import com.plucknplay.csg.ui.views.dnd.ModelObjectDropAdapter;
import com.plucknplay.csg.ui.views.dnd.ModelObjectTransfer;
import com.plucknplay.csg.ui.views.dnd.ScaleDropAdapter;

/**
 * This view shows all known chords of this application in a flat list.
 */
public class ScalesView extends AbstractIntervalContainerView {

	public static final String ID = "com.plucknplay.csg.ui.views.ScalesView"; //$NON-NLS-1$
	public static final String HELP_ID = "scales_view_context"; //$NON-NLS-1$

	private AddScaleAction addAction;
	private AddScaleCategoryAction addCategoryAction;
	private CutScaleAction cutAction;
	private DuplicateScaleAction duplicateAction;
	private PasteScaleAction pasteAction;
	private RemoveScaleAction removeAction;
	private RenameAction renameAction;
	private ImportScaleAction importAction;
	private ExportScaleAction exportAction;
	private CopyBlocksToClipboardAction copyAction;

	@Override
	public void createPartControl(final Composite parent) {
		super.createPartControl(parent);

		// show intervals action
		addAction = new AddScaleAction(this, true);
		addCategoryAction = new AddScaleCategoryAction(this, true);
		cutAction = new CutScaleAction(this);
		duplicateAction = new DuplicateScaleAction(this);
		pasteAction = new PasteScaleAction(this);
		removeAction = new RemoveScaleAction(this);
		renameAction = new RenameAction(this);
		importAction = new ImportScaleAction(this);
		exportAction = new ExportScaleAction(this);
		copyAction = new CopyBlocksToClipboardAction(this, true);

		addAction(addAction);
		addAction(addCategoryAction);
		addAction(cutAction);
		addAction(duplicateAction);
		addAction(pasteAction);
		addAction(removeAction);
		addAction(renameAction);
		addAction(importAction);
		addAction(exportAction);
		addAction(copyAction);

		final Activator activator = Activator.getDefault();
		activator.registerAction(getSite(), addAction);
		activator.registerAction(getSite(), addCategoryAction);
		activator.registerAction(getSite(), cutAction);
		activator.registerAction(getSite(), duplicateAction);
		activator.registerAction(getSite(), pasteAction);
		activator.registerAction(getSite(), removeAction);
		activator.registerAction(getSite(), renameAction);
		activator.registerAction(getSite(), importAction);
		activator.registerAction(getSite(), exportAction);
		activator.registerAction(getSite(), copyAction);
	}

	@Override
	protected CategoryList getCategoryList() {
		return ScaleList.getInstance();
	}

	@Override
	protected IEditorInput getEditorInput(final Categorizable element, final Category category, final boolean newElement) {
		return new ScaleEditorInput((Scale) element, category, newElement);
	}

	@Override
	protected String getEditorID() {
		return IntervalContainerEditor.ID;
	}

	@Override
	protected ModelObjectDropAdapter getDropAdapter(final TreeViewer viewer) {
		return new ScaleDropAdapter(viewer);
	}

	@Override
	protected Object getDNDTransferType() {
		return ModelObjectTransfer.TYPE_SCALE;
	}

	@Override
	protected int getIntervalContainerSectionWeight() {
		return Activator.getDefault().getPreferenceStore().getInt(Preferences.SCALES_VIEW_SCALE_SECTION_WEIGHT);
	}

	@Override
	protected int getIntervalSectionWeight() {
		return Activator.getDefault().getPreferenceStore().getInt(Preferences.SCALES_VIEW_INTERVAL_SECTION_WEIGHT);
	}

	@Override
	protected void storeIntervalContainerSectionWeight(final int weight) {
		Activator.getDefault().getPreferenceStore().setValue(Preferences.SCALES_VIEW_SCALE_SECTION_WEIGHT, weight);
	}

	@Override
	protected void storeIntervalSectionWeight(final int weight) {
		Activator.getDefault().getPreferenceStore().setValue(Preferences.SCALES_VIEW_INTERVAL_SECTION_WEIGHT, weight);
	}

	@Override
	protected String getHelpId() {
		return HELP_ID;
	}

	@Override
	protected String getStatusLineWarning() {
		return NlsUtil.getStatusLineWarningForScales();
	}

	@Override
	protected void contributeToActionBars() {
		super.contributeToActionBars();

		// create actions
		final AddScaleCategoryAction addCategoryAction2 = new AddScaleCategoryAction(this, false);
		final AddScaleAction addScaleAction = new AddScaleAction(this, false);
		final CleanUpScalesAction cleanUpAction = new CleanUpScalesAction(getSite().getWorkbenchWindow());
		cleanUpAction.setText(ActionMessages.CleanUpAction_short_text);

		// register actions
		final Activator activator = Activator.getDefault();
		activator.registerAction(getSite(), addCategoryAction2);
		activator.registerAction(getSite(), addScaleAction);
		activator.registerAction(getSite(), cleanUpAction);

		// contribute actions
		final IActionBars bars = getViewSite().getActionBars();
		bars.getToolBarManager().appendToGroup(IWorkbenchActionConstants.MB_ADDITIONS, addScaleAction);
		bars.getToolBarManager().appendToGroup(IWorkbenchActionConstants.MB_ADDITIONS, addCategoryAction2);

		bars.getMenuManager().appendToGroup(IWorkbenchActionConstants.MB_ADDITIONS, cleanUpAction);
		bars.getMenuManager().appendToGroup(IWorkbenchActionConstants.MB_ADDITIONS, new Separator());
		bars.getMenuManager().appendToGroup(IWorkbenchActionConstants.MB_ADDITIONS, addScaleAction);
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
		mgr.appendToGroup(POPUP_EDIT_GROUP, duplicateAction);
		mgr.appendToGroup(POPUP_EDIT_GROUP, pasteAction);
		mgr.appendToGroup(POPUP_EDIT_GROUP, removeAction);
		mgr.appendToGroup(POPUP_EDIT_GROUP, renameAction);
		mgr.appendToGroup(POPUP_IMPORT_GROUP, importAction);
		mgr.appendToGroup(POPUP_IMPORT_GROUP, exportAction);
		mgr.appendToGroup(IWorkbenchActionConstants.MB_ADDITIONS, copyAction);
	}
}
