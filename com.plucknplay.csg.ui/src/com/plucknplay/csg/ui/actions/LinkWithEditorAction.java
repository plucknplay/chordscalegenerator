/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.preference.IPreferenceStore;

import com.plucknplay.csg.core.model.sets.CategoryList;
import com.plucknplay.csg.core.model.sets.ChordList;
import com.plucknplay.csg.core.model.sets.InstrumentList;
import com.plucknplay.csg.core.model.sets.ScaleList;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.IImageKeys;
import com.plucknplay.csg.ui.Preferences;

public class LinkWithEditorAction extends Action {

	private static final String COMMAND_ID = "org.eclipse.ui.navigate.linkWithEditor"; //$NON-NLS-1$

	private final CategoryList categoryList;
	private final IPreferenceStore prefs;

	public LinkWithEditorAction(final CategoryList categoryList) {

		this.categoryList = categoryList;

		setId(IActionIds.ACT_LINK_WITH_EDITOR);
		setActionDefinitionId(COMMAND_ID);
		setText(ActionMessages.LinkWithEditorAction_text);
		setToolTipText(ActionMessages.LinkWithEditorAction_text);
		setImageDescriptor(Activator.getImageDescriptor(IImageKeys.SYNCED));

		// load preferences
		prefs = Activator.getDefault().getPreferenceStore();
		setChecked(categoryList == InstrumentList.getInstance()
				&& prefs.getBoolean(Preferences.INSTRUMENTS_VIEW_LINKED_WITH_EDITOR)
				|| categoryList == ChordList.getInstance()
				&& prefs.getBoolean(Preferences.CHORDS_VIEW_LINKED_WITH_EDITOR)
				|| categoryList == ScaleList.getInstance()
				&& prefs.getBoolean(Preferences.SCALES_VIEW_LINKED_WITH_EDITOR));
	}

	@Override
	public int getStyle() {
		return AS_CHECK_BOX;
	}

	@Override
	public void run() {
		if (categoryList == null) {
			return;
		}

		// merely store preferences
		if (categoryList == InstrumentList.getInstance()) {
			prefs.setValue(Preferences.INSTRUMENTS_VIEW_LINKED_WITH_EDITOR, isChecked());
		} else if (categoryList == ChordList.getInstance()) {
			prefs.setValue(Preferences.CHORDS_VIEW_LINKED_WITH_EDITOR, isChecked());
		} else if (categoryList == ScaleList.getInstance()) {
			prefs.setValue(Preferences.SCALES_VIEW_LINKED_WITH_EDITOR, isChecked());
		}
	}
}
