/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.views;

import org.eclipse.gef.EditPartFactory;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;

import com.plucknplay.csg.core.model.Factory;
import com.plucknplay.csg.core.model.IBlock;
import com.plucknplay.csg.core.util.ToneRange;
import com.plucknplay.csg.core.util.ToneRangeMode;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.UIConstants;
import com.plucknplay.csg.ui.actions.AbstractShowAction;
import com.plucknplay.csg.ui.actions.ShowIntervalsAction;
import com.plucknplay.csg.ui.actions.ShowNotesAction;
import com.plucknplay.csg.ui.editPartFactories.KeyboardEditPartFactory;
import com.plucknplay.csg.ui.editParts.KeyboardDraftEditPart;
import com.plucknplay.csg.ui.model.Draft;
import com.plucknplay.csg.ui.model.KeyboardDraft;
import com.plucknplay.csg.ui.preferencePages.KeyboardViewPreferencePage;
import com.plucknplay.csg.ui.util.enums.KeySizeMode;

public class KeyboardView extends AbstractGraphicalCalculationView implements IModeView {

	public static final String ID = "com.plucknplay.csg.ui.views.KeyboardView"; //$NON-NLS-1$
	public static final String HELP_ID = "keyboard_view_context"; //$NON-NLS-1$

	private IPreferenceStore prefs;

	private ToneRangeMode toneRangeMode;

	private AbstractShowAction showNotesAction;
	private AbstractShowAction showIntervalsAction;

	private Button changeRootNoteButton;

	/**
	 * The edit part factory of this editor.
	 */
	private EditPartFactory editPartFactory;

	/**
	 * Returns the edit part factory that the graphical viewer will use.
	 * 
	 * @return the edit part factory that the graphical viewer will use
	 */
	@Override
	protected EditPartFactory getEditPartFactory() {
		if (editPartFactory == null) {
			editPartFactory = new KeyboardEditPartFactory();
		}
		return editPartFactory;
	}

	private KeyboardDraftEditPart getEditPart() {
		return (KeyboardDraftEditPart) getGraphicalViewer().getContents();
	}

	@Override
	public void init(final IViewSite site) throws PartInitException {
		super.init(site);
		prefs = Activator.getDefault().getPreferenceStore();
		determineToneRange();
	}

	@Override
	protected void fillButtonsComposite(final Composite composite) {
		super.fillButtonsComposite(composite);

		// change root note button
		changeRootNoteButton = createChangeRootNoteAction(composite).getButton();
	}

	@Override
	protected Button[] getButtonsWithIndent() {
		return new Button[] { changeRootNoteButton };
	}

	@Override
	protected Draft createDraftContent(final Object input) {
		KeyboardDraft result = null;

		final ToneRange toneRange = determineToneRange();

		// create notes draft
		if (input == null) {
			result = new KeyboardDraft(toneRange);
		} else if (input instanceof IBlock) {
			result = new KeyboardDraft((IBlock) input, toneRange);
		}

		return result;
	}

	@Override
	protected Draft createContentClone() {
		final Draft content = getContent();
		return content != null ? new KeyboardDraft((KeyboardDraft) content) : null;
	}

	private ToneRange determineToneRange() {
		ToneRange result = null;
		toneRangeMode = ToneRangeMode.valueOf(prefs.getString(Preferences.KEYBOARD_VIEW_TONE_RANGE_MODE));
		if (toneRangeMode != ToneRangeMode.USER_DEFINED) {
			result = toneRangeMode.getToneRange();
		} else {
			final int startToneIndex = prefs.getInt(Preferences.KEYBOARD_VIEW_TONE_RANGE_START_TONE);
			final int endToneIndex = prefs.getInt(Preferences.KEYBOARD_VIEW_TONE_RANGE_END_TONE);
			if (startToneIndex != -1 && endToneIndex != -1 && startToneIndex + 11 <= endToneIndex) {
				final Factory factory = Factory.getInstance();
				result = new ToneRange(factory.getNoteByIndex(startToneIndex), factory.getNoteByIndex(endToneIndex));
			}
		}
		return result;
	}

	@Override
	protected void preControlResized() {
		if (getGraphicalViewer() != null && getGraphicalViewer().getControl() != null) {
			KeySizeMode.FLEXIBLE.setContainerSize(getGraphicalViewer().getControl().getSize());
		}
	}

	@Override
	protected void contributeToActionBars() {
		super.contributeToActionBars();

		String mode = prefs.getString(Preferences.KEYBOARD_VIEW_MODE);
		if (!UIConstants.MODE_POINTS.equals(mode) && !UIConstants.MODE_NOTES.equals(mode)
				&& !UIConstants.MODE_INTERVALS.equals(mode)) {
			mode = UIConstants.MODE_NOTES;
		}

		// create actions
		showNotesAction = new ShowNotesAction(this, UIConstants.MODE_NOTES);
		showNotesAction.setChecked(UIConstants.MODE_NOTES.equals(mode));

		showIntervalsAction = new ShowIntervalsAction(this, UIConstants.MODE_INTERVALS);
		showIntervalsAction.setChecked(UIConstants.MODE_INTERVALS.equals(mode));

		// register actions
		final Activator activator = Activator.getDefault();
		activator.registerAction(getSite(), showNotesAction);
		activator.registerAction(getSite(), showIntervalsAction);

		// add actions to menu
		final IActionBars bars = getViewSite().getActionBars();
		bars.getMenuManager().prependToGroup(BAR_ADDITIONS_LEFT, new Separator());
		bars.getMenuManager().appendToGroup(BAR_ADDITIONS_LEFT, showNotesAction);
		bars.getMenuManager().appendToGroup(BAR_ADDITIONS_LEFT, showIntervalsAction);

		// add actions to toolbar
		bars.getToolBarManager().appendToGroup(BAR_ADDITIONS_LEFT, new Separator());
		bars.getToolBarManager().appendToGroup(BAR_ADDITIONS_LEFT, showNotesAction);
		bars.getToolBarManager().appendToGroup(BAR_ADDITIONS_LEFT, showIntervalsAction);
	}

	@Override
	protected String getPreferencePageID() {
		return KeyboardViewPreferencePage.ID;
	}

	@Override
	public void propertyChange(final PropertyChangeEvent event) {
		super.propertyChange(event);
		final String property = event.getProperty();

		final KeyboardDraftEditPart editPart = getEditPart();

		// preferences changed
		if (property.equals(Preferences.KEYBOARD_VIEW_HIGHLIGHT_ROOT_NOTE)
				|| property.equals(Preferences.KEYBOARD_VIEW_HIGHLIGHT_ROOT_NOTE_WITH_SHAPE)
				|| property.equals(Preferences.KEYBOARD_VIEW_HIGHLIGHT_ROOT_NOTE_WITH_COLOR)
				|| property.equals(Preferences.KEYBOARD_VIEW_HIGHLIGHT_ROOT_NOTE_COLOR_ID)
				|| property.equals(Preferences.KEYBOARD_VIEW_FRAME_NOTES_INTERVALS)
				|| property.equals(Preferences.KEYBOARD_VIEW_NOTES_INTERVALS_BACKGROUND)
				|| property.equals(Preferences.KEYBOARD_VIEW_SHOW_ADDITIONAL_NOTES_ON_EMPTY_KEYBOARD)
				|| property.equals(Preferences.KEYBOARD_VIEW_SHOW_ADDITIONAL_NOTES_FOR_GRIPTABLE)
				|| property.equals(Preferences.KEYBOARD_VIEW_SHOW_ADDITIONAL_NOTES_FOR_CHORD_AND_SCALE)
				|| property.equals(Preferences.KEYBOARD_VIEW_SHOW_ADDITIONAL_NOTES_FOR_BLOCK)
				|| property.equals(Preferences.KEYBOARD_VIEW_SHOW_ADDITIONAL_CHORD_NOTES)
				|| property.equals(Preferences.KEYBOARD_VIEW_SHOW_ADDITIONAL_BLOCK_NOTES)
				|| property.equals(Preferences.KEYBOARD_VIEW_SHOW_ADDITIONAL_NOTES_IN_BLACK)) {

			editPart.refreshNotes(false);
		}

		// block presentation changed
		else if (property.equals(Preferences.KEYBOARD_VIEW_BLOCK_PRESENTATION)) {
			editPart.refreshKeys(false);
			editPart.refreshNotes(false);
		}

		// naming changed
		else if (property.equals(Preferences.NOTES_MODE) || property.equals(Preferences.INTERVAL_NAMES_MODE)
				|| property.equals(Preferences.INTERVAL_NAMES_USE_DIFFERENT_ROOT_INTERVAL_NAME)
				|| property.equals(Preferences.INTERVAL_NAMES_ROOT_INTERVAL_NAME)
				|| property.equals(Preferences.INTERVAL_NAMES_USE_DELTA_IN_MAJOR_INTERVALS)
				|| property.equals(Preferences.GENERAL_H_NOTE_NAME) || property.equals(Preferences.GENERAL_B_NOTE_NAME)
				|| property.equals(Preferences.ABSOLUTE_NOTE_NAMES_MODE)
				|| property.equals(Preferences.KEYBOARD_VIEW_KEY_SIZE)) {

			refresh();
		}

		// tone range changed
		else if (property.equals(Preferences.KEYBOARD_VIEW_TONE_RANGE_MODE)
				|| toneRangeMode == ToneRangeMode.USER_DEFINED
				&& (property.equals(Preferences.KEYBOARD_VIEW_TONE_RANGE_START_TONE) || property
						.equals(Preferences.KEYBOARD_VIEW_TONE_RANGE_END_TONE))) {

			setInput(getInput());
			refresh();
		}

		// block preferences changed
		else if (property.equals(Preferences.BLOCK_MODE) || property.equals(Preferences.FRET_BLOCK_RANGE)
				|| property.equals(Preferences.FRET_BLOCK_USE_EMPTY_STRINGS)
				|| property.equals(Preferences.ADVANCED_FRET_BLOCK_RANGE)
				|| property.equals(Preferences.ADVANCED_FRET_BLOCK_STRING_RANGE_DECREASE)
				|| property.equals(Preferences.ADVANCED_FRET_BLOCK_USE_EMPTY_STRINGS)
				|| property.equals(Preferences.OCTAVE_BLOCK_ONLY_ROOT_NOTES)
				|| property.equals(Preferences.SHOW_BLOCKS) || property.equals(Preferences.ROOT_NOTE)) {

			if (getContent() == null || !getContent().isEditable()) {
				setInput(getInput());
			}
		}
	}

	@Override
	public void notifyChange(final Object property, final Object value) {

		// update actions/buttons when keyboard draft has changed
		if (property == KeyboardDraft.PROP_KEYBOARD_NOTE_ADDED || property == KeyboardDraft.PROP_KEYBOARD_NOTE_REMOVED) {
			updateFindButtons();
			updateClearAction();
			updateChangeRootNoteButton();
		}
	}

	@Override
	protected String getHelpId() {
		return HELP_ID;
	}

	@Override
	public int getExportHeight() {
		final int exportHeight = prefs.getInt(Preferences.KEYBOARD_VIEW_EXPORT_HEIGHT);
		final boolean liveSize = prefs.getBoolean(Preferences.KEYBOARD_VIEW_EXPORT_LIVE_SIZE);
		return liveSize ? -1 : exportHeight;
	}

	@Override
	public void setMode(final String mode) {

		final KeyboardDraftEditPart contents = (KeyboardDraftEditPart) (getGraphicalViewer().getContents() instanceof KeyboardDraftEditPart ? getGraphicalViewer()
				.getContents() : null);
		if (contents != null) {
			contents.setMode(mode);
		}

		// store preference
		prefs.setValue(Preferences.KEYBOARD_VIEW_MODE, mode);

		// update check state
		// (necessary if actions was performed via key binding)
		showNotesAction.setChecked(UIConstants.MODE_NOTES.equals(mode));
		showIntervalsAction.setChecked(UIConstants.MODE_INTERVALS.equals(mode));
	}
}
