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
import com.plucknplay.csg.core.model.Chord;
import com.plucknplay.csg.core.model.sets.Category;
import com.plucknplay.csg.core.model.sets.CategoryList;
import com.plucknplay.csg.core.model.sets.ChordList;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.actions.ActionMessages;
import com.plucknplay.csg.ui.actions.chords.AddChordAction;
import com.plucknplay.csg.ui.actions.chords.AddChordCategoryAction;
import com.plucknplay.csg.ui.actions.chords.CleanUpChordsAction;
import com.plucknplay.csg.ui.actions.chords.CutChordAction;
import com.plucknplay.csg.ui.actions.chords.DuplicateChordAction;
import com.plucknplay.csg.ui.actions.chords.ExportChordAction;
import com.plucknplay.csg.ui.actions.chords.ImportChordAction;
import com.plucknplay.csg.ui.actions.chords.PasteChordAction;
import com.plucknplay.csg.ui.actions.chords.RemoveChordAction;
import com.plucknplay.csg.ui.actions.common.RenameAction;
import com.plucknplay.csg.ui.actions.scales.OpenScaleFinderAction;
import com.plucknplay.csg.ui.activation.NlsUtil;
import com.plucknplay.csg.ui.editors.IntervalContainerEditor;
import com.plucknplay.csg.ui.editors.input.ChordEditorInput;
import com.plucknplay.csg.ui.views.dnd.ChordDropAdapter;
import com.plucknplay.csg.ui.views.dnd.ModelObjectDropAdapter;
import com.plucknplay.csg.ui.views.dnd.ModelObjectTransfer;

/**
 * This view shows all known chords of this application in a flat list.
 */
public class ChordsView extends AbstractIntervalContainerView {

	public static final String ID = "com.plucknplay.csg.ui.views.ChordsView"; //$NON-NLS-1$
	public static final String HELP_ID = "chords_view_context"; //$NON-NLS-1$

	private AddChordAction addAction;
	private AddChordCategoryAction addCategoryAction;
	private CutChordAction cutAction;
	private DuplicateChordAction duplicateAction;
	private PasteChordAction pasteAction;
	private RemoveChordAction removeAction;
	private RenameAction renameAction;
	private ImportChordAction importAction;
	private ExportChordAction exportAction;
	private OpenScaleFinderAction openScaleFinderAction;

	@Override
	public void createPartControl(final Composite parent) {
		super.createPartControl(parent);

		addAction = new AddChordAction(this, true);
		addCategoryAction = new AddChordCategoryAction(this, true);
		cutAction = new CutChordAction(this);
		duplicateAction = new DuplicateChordAction(this);
		pasteAction = new PasteChordAction(this);
		removeAction = new RemoveChordAction(this);
		renameAction = new RenameAction(this);
		importAction = new ImportChordAction(this);
		exportAction = new ExportChordAction(this);
		openScaleFinderAction = new OpenScaleFinderAction(this.getSite());

		addAction(addAction);
		addAction(addCategoryAction);
		addAction(cutAction);
		addAction(duplicateAction);
		addAction(pasteAction);
		addAction(removeAction);
		addAction(renameAction);
		addAction(importAction);
		addAction(exportAction);
		addAction(openScaleFinderAction);

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
		activator.registerAction(getSite(), openScaleFinderAction);
	}

	@Override
	protected CategoryList getCategoryList() {
		return ChordList.getInstance();
	}

	@Override
	protected IEditorInput getEditorInput(final Categorizable element, final Category category, final boolean newElement) {
		return new ChordEditorInput((Chord) element, category, newElement);
	}

	@Override
	protected String getEditorID() {
		return IntervalContainerEditor.ID;
	}

	@Override
	protected ModelObjectDropAdapter getDropAdapter(final TreeViewer viewer) {
		return new ChordDropAdapter(viewer);
	}

	@Override
	protected Object getDNDTransferType() {
		return ModelObjectTransfer.TYPE_CHORD;
	}

	@Override
	protected int getIntervalContainerSectionWeight() {
		return Activator.getDefault().getPreferenceStore().getInt(Preferences.CHORDS_VIEW_CHORD_SECTION_WEIGHT);
	}

	@Override
	protected int getIntervalSectionWeight() {
		return Activator.getDefault().getPreferenceStore().getInt(Preferences.CHORDS_VIEW_INTERVAL_SECTION_WEIGHT);
	}

	@Override
	protected void storeIntervalContainerSectionWeight(final int weight) {
		Activator.getDefault().getPreferenceStore().setValue(Preferences.CHORDS_VIEW_CHORD_SECTION_WEIGHT, weight);
	}

	@Override
	protected void storeIntervalSectionWeight(final int weight) {
		Activator.getDefault().getPreferenceStore().setValue(Preferences.CHORDS_VIEW_INTERVAL_SECTION_WEIGHT, weight);
	}

	@Override
	protected String getHelpId() {
		return HELP_ID;
	}

	@Override
	protected String getStatusLineWarning() {
		return NlsUtil.getStatusLineWarningForChords();
	}

	@Override
	protected void contributeToActionBars() {
		super.contributeToActionBars();

		// create actions
		final AddChordCategoryAction addCategoryAction2 = new AddChordCategoryAction(this, false);
		final AddChordAction addChordAction = new AddChordAction(this, false);
		final CleanUpChordsAction cleanUpAction = new CleanUpChordsAction(getSite().getWorkbenchWindow());
		cleanUpAction.setText(ActionMessages.CleanUpAction_short_text);

		// register actions
		final Activator activator = Activator.getDefault();
		activator.registerAction(getSite(), addCategoryAction2);
		activator.registerAction(getSite(), addChordAction);
		activator.registerAction(getSite(), cleanUpAction);

		// contribute actions
		final IActionBars bars = getViewSite().getActionBars();
		bars.getToolBarManager().appendToGroup(IWorkbenchActionConstants.MB_ADDITIONS, addChordAction);
		bars.getToolBarManager().appendToGroup(IWorkbenchActionConstants.MB_ADDITIONS, addCategoryAction2);

		bars.getMenuManager().appendToGroup(IWorkbenchActionConstants.MB_ADDITIONS, cleanUpAction);
		bars.getMenuManager().appendToGroup(IWorkbenchActionConstants.MB_ADDITIONS, new Separator());
		bars.getMenuManager().appendToGroup(IWorkbenchActionConstants.MB_ADDITIONS, addChordAction);
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
		mgr.appendToGroup(POPUP_BOTTOM_GROUP, openScaleFinderAction);
	}
}
