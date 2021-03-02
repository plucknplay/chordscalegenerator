/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.views;

import org.eclipse.gef.EditPartFactory;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;

import com.plucknplay.csg.core.model.FretBlock;
import com.plucknplay.csg.core.model.IBlock;
import com.plucknplay.csg.core.model.Instrument;
import com.plucknplay.csg.core.model.sets.InstrumentList;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.IImageKeys;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.editPartFactories.BoxEditPartFactory;
import com.plucknplay.csg.ui.editParts.BoxDraftEditPart;
import com.plucknplay.csg.ui.model.BoxDraft;
import com.plucknplay.csg.ui.model.Draft;
import com.plucknplay.csg.ui.preferencePages.BoxViewPreferencePage;
import com.plucknplay.csg.ui.util.ButtonAction;
import com.plucknplay.csg.ui.util.enums.BoxViewPresentationMode;

public class BoxView extends AbstractGraphicalCalculationView {

	public static final String ID = "com.plucknplay.csg.ui.views.BoxView"; //$NON-NLS-1$
	public static final String HELP_ID = "box_view_context"; //$NON-NLS-1$

	private static final int MIN_FRET_NUMBER = 3;

	private static final String UNCHANGED = "unchanged"; //$NON-NLS-1$
	private static final String CHECK = "check"; //$NON-NLS-1$
	private static final String UNCHECK = "uncheck"; //$NON-NLS-1$

	private ButtonAction addFretButtonAction;
	private ButtonAction removeFretButtonAction;
	private ButtonAction changeRootNoteButtonAction;

	private ShowFingeringAction showFingeringAction;
	private ShowNotesAction showNotesAction;
	private ShowIntervalsAction showIntervalsAction;

	private String tempFingeringMode;
	private String tempNotesMode;
	private String tempIntervalsMode;

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
			editPartFactory = new BoxEditPartFactory();
		}
		return editPartFactory;
	}

	@Override
	protected void fillButtonsComposite(final Composite composite) {

		super.fillButtonsComposite(composite);

		// remove fret button
		removeFretButtonAction = new ButtonAction(getSite(), composite, SWT.PUSH | SWT.FLAT,
				ViewMessages.BoxView_remove_fret, null, "com.plucknplay.csg.ui.removeColumn") { //$NON-NLS-1$
			@Override
			public void run() {
				final int fretWidth = getCastedContent().getFretWidth() - 1;
				if (fretWidth >= MIN_FRET_NUMBER) {
					getCastedContent().setFretWidth(fretWidth);
					updateColumnActions();
				}
			}
		};
		removeFretButtonAction.getButton().addKeyListener(getEscKeyListener());
		GridDataFactory.fillDefaults().indent(0, VERTICAL_INDENT).hint(BUTTON_SIZE_BIG)
				.applyTo(removeFretButtonAction.getButton());

		// add fret button
		addFretButtonAction = new ButtonAction(getSite(), composite, SWT.PUSH | SWT.FLAT,
				ViewMessages.BoxView_add_fret, null, "com.plucknplay.csg.ui.addColumn") { //$NON-NLS-1$
			@Override
			public void run() {
				final Instrument currentInstrument = InstrumentList.getInstance().getCurrentInstrument();
				final int maxFretWidth = Math.min(FretBlock.MAX_FRET_RANGE, currentInstrument.getFretCount()
						- currentInstrument.getMinFret());

				final int fretWidth = getCastedContent().getFretWidth() + 1;
				if (fretWidth <= maxFretWidth) {
					getCastedContent().setFretWidth(fretWidth);
					updateColumnActions();
				}
			}
		};
		addFretButtonAction.getButton().addKeyListener(getEscKeyListener());
		GridDataFactory.fillDefaults().hint(BUTTON_SIZE_BIG).applyTo(addFretButtonAction.getButton());
		updateFretButtonImages();

		// change root note button
		changeRootNoteButtonAction = createChangeRootNoteAction(composite);

		// no barre button
		createNoBarreAction(composite);
	}

	@Override
	protected Button[] getButtonsWithIndent() {
		return new Button[] { removeFretButtonAction.getButton(), changeRootNoteButtonAction.getButton() };
	}

	private void updateFretButtonImages() {
		final boolean leftHander = Activator.getDefault().isLeftHander();
		final BoxViewPresentationMode presentationMode = BoxViewPresentationMode.valueOf(Activator.getDefault()
				.getPreferenceStore().getString(Preferences.BOX_VIEW_PRESENTATION_MODE));
		removeFretButtonAction
				.setImagePath(presentationMode == BoxViewPresentationMode.VERTICAL ? IImageKeys.TRIANGLE_UP
						: leftHander ? IImageKeys.TRIANGLE_RIGHT : IImageKeys.TRIANGLE_LEFT);
		addFretButtonAction
				.setImagePath(presentationMode == BoxViewPresentationMode.VERTICAL ? IImageKeys.TRIANGLE_DOWN
						: leftHander ? IImageKeys.TRIANGLE_LEFT : IImageKeys.TRIANGLE_RIGHT);
	}

	@Override
	protected Draft createDraftContent(final Object input) {
		Draft result = null;
		if (input == null) {
			result = new BoxDraft();
		} else if (input instanceof IBlock) {
			result = new BoxDraft((IBlock) input);
		}
		return result;
	}

	@Override
	protected Draft createContentClone() {
		final Draft content = getContent();
		return content != null ? new BoxDraft((BoxDraft) content) : null;
	}

	@Override
	protected void contributeToActionBars() {
		super.contributeToActionBars();

		final IPreferenceStore prefs = Activator.getDefault().getPreferenceStore();
		final boolean defaultShowFingering = prefs.getBoolean(Preferences.BOX_VIEW_SHOW_FINGERING);
		final boolean defaultShowNotes = prefs.getBoolean(Preferences.BOX_VIEW_SHOW_NOTES);
		final boolean defaultShowIntervals = prefs.getBoolean(Preferences.BOX_VIEW_SHOW_INTERVALS);

		// create actions
		showFingeringAction = new ShowFingeringAction();
		showFingeringAction.setChecked(defaultShowFingering);

		showNotesAction = new ShowNotesAction();
		showNotesAction.setChecked(defaultShowNotes);

		showIntervalsAction = new ShowIntervalsAction();
		showIntervalsAction.setChecked(defaultShowIntervals);

		// register actions
		final Activator activator = Activator.getDefault();
		activator.registerAction(getSite(), showFingeringAction);
		activator.registerAction(getSite(), showNotesAction);
		activator.registerAction(getSite(), showIntervalsAction);

		// add actions to menu and toolbar
		final IActionBars bars = getViewSite().getActionBars();

		bars.getMenuManager().appendToGroup(BAR_ADDITIONS_LEFT, new Separator());
		bars.getMenuManager().appendToGroup(BAR_ADDITIONS_LEFT, showFingeringAction);
		bars.getMenuManager().appendToGroup(BAR_ADDITIONS_LEFT, showNotesAction);
		bars.getMenuManager().appendToGroup(BAR_ADDITIONS_LEFT, showIntervalsAction);

		bars.getToolBarManager().appendToGroup(BAR_ADDITIONS_LEFT, new Separator());
		bars.getToolBarManager().appendToGroup(BAR_ADDITIONS_LEFT, showFingeringAction);
		bars.getToolBarManager().appendToGroup(BAR_ADDITIONS_LEFT, showNotesAction);
		bars.getToolBarManager().appendToGroup(BAR_ADDITIONS_LEFT, showIntervalsAction);
	}

	@Override
	public void notifyChange(final Object property, final Object value) {
		super.notifyChange(property, value);

		// update find chord action when draft has changed
		if (property == BoxDraft.PROP_ASSIGNMENT_CHANGED) {
			updateFindButtons();
			updateClearAction();
			updateChangeRootNoteButton();
			updateShowBarreButton();
		}

		if (property == BoxDraft.PROP_SHOW_BARRE_CHANGED) {
			updateShowBarreButton();
		}

		// update add/remove fret button images
		if (property == Activator.PROP_HAND_CHANGED) {
			updateFretButtonImages();
			refresh();
		}
	}

	@Override
	public void propertyChange(final PropertyChangeEvent event) {
		super.propertyChange(event);
		final String property = event.getProperty();

		if (property.equals(Preferences.BOX_VIEW_PRESENTATION_MODE)) {
			updateFretButtonImages();
		}

		if (property.equals(Preferences.BOX_VIEW_SHOW_BARRE)) {
			updateShowBarreButton();
		}

		if (property.equals(Preferences.BOX_VIEW_PRESENTATION_MODE)
				|| property.equals(Preferences.BOX_VIEW_FRAME_HIGHLIGHT_NUT)
				|| property.equals(Preferences.BOX_VIEW_FRAME_HIGHLIGHT_OUTER_FRETS)
				|| property.equals(Preferences.BOX_VIEW_FRAME_DRAW_DOUBLED_STRINGS)
				|| property.equals(Preferences.BOX_VIEW_FRAME_DRAW_FRETLESS_FRETS_DOTTED)
				|| property.equals(Preferences.BOX_VIEW_FRAME_GRAY_COLOR)
				|| property.equals(Preferences.BOX_VIEW_FRET_NUMBERS_MODE)
				|| property.equals(Preferences.BOX_VIEW_FRET_NUMBERS_HORIZONTAL_POSITION)
				|| property.equals(Preferences.BOX_VIEW_FRET_NUMBERS_VERTICAL_POSITION)
				|| property.equals(Preferences.BOX_VIEW_FRET_NUMBERS_PLACE_AT_FIRST_FINGER)
				|| property.equals(Preferences.BOX_VIEW_FRET_NUMBERS_VISIBLE_FOR_FIRST_FRET)
				|| property.equals(Preferences.BOX_VIEW_FRET_NUMBERS_FRAMED)
				|| property.equals(Preferences.BOX_VIEW_FRET_NUMBERS_GRAY_COLOR)
				|| property.equals(Preferences.BOX_VIEW_SHOW_MUTED_STRINGS)
				|| property.equals(Preferences.BOX_VIEW_HIGHLIGHT_ROOT_NOTE)
				|| property.equals(Preferences.BOX_VIEW_HIGHLIGHT_ROOT_NOTE_WITH_COLOR)
				|| property.equals(Preferences.BOX_VIEW_HIGHLIGHT_ROOT_NOTE_WITH_SHAPE)
				|| property.equals(Preferences.BOX_VIEW_POINTS_SIZE)
				|| property.equals(Preferences.BOX_VIEW_POINTS_BACKGROUND)
				|| property.equals(Preferences.BOX_VIEW_FRAME_INSIDE)
				|| property.equals(Preferences.BOX_VIEW_BACKGROUND_INSIDE)
				|| property.equals(Preferences.BOX_VIEW_SHOW_BARRE) || property.equals(Preferences.BOX_VIEW_BARRE_MODE)
				|| property.equals(Preferences.BOX_VIEW_BARRE_LINE_WIDTH)
				|| property.equals(Preferences.BOX_VIEW_BARRE_BAR_BACKGROUND)
				|| property.equals(Preferences.BOX_VIEW_BARRE_BAR_SHOW_ELEMENTS_INSIDE)
				|| property.equals(Preferences.BOX_VIEW_BARRE_BAR_SHOW_SINGLE_FINGER_NUMBER)
				|| property.equals(Preferences.BOX_VIEW_EMPTY_AND_MUTED_STRINGS_CLOSE_TO_FRAME)
				|| property.equals(Preferences.BOX_VIEW_EMPTY_AND_MUTED_STRINGS_SIZE)
				|| property.equals(Preferences.BOX_VIEW_EMPTY_STRINGS_BACKGROUND_WHITE)
				|| property.equals(Preferences.BOX_VIEW_BACKGROUND_OUTSIDE)
				|| property.equals(Preferences.BOX_VIEW_FRAME_OUTSIDE)
				|| property.equals(Preferences.GENERAL_FINGERING_MODE)
				|| property.equals(Preferences.GENERAL_FINGERING_MODE_CUSTOM_NOTATION)) {

			refresh();
		}

		else if (property.equals(Preferences.BOX_VIEW_FRAME_MIN_FRET_COUNT)
				|| property.equals(Preferences.BOX_VIEW_FRAME_MAX_UNASSIGNED_FRET_COUNT)
				|| property.equals(Preferences.NOTES_MODE) || property.equals(Preferences.INTERVAL_NAMES_MODE)
				|| property.equals(Preferences.INTERVAL_NAMES_USE_DIFFERENT_ROOT_INTERVAL_NAME)
				|| property.equals(Preferences.INTERVAL_NAMES_ROOT_INTERVAL_NAME)
				|| property.equals(Preferences.INTERVAL_NAMES_USE_DELTA_IN_MAJOR_INTERVALS)
				|| property.equals(Preferences.BOX_VIEW_FRAME_SMALL_FRETS)) {

			refresh();
		}

		else if (property.equals(BoxViewPreferencePage.BOX_VIEW_SHOW_OUTSIDE_BOX_CHANGED)) {

			// update content
			final BoxDraftEditPart contents = (BoxDraftEditPart) (getGraphicalViewer().getContents() instanceof BoxDraftEditPart ? getGraphicalViewer()
					.getContents() : null);
			if (contents != null) {
				final IPreferenceStore prefs = Activator.getDefault().getPreferenceStore();
				contents.setShowFingeringOutside(prefs.getBoolean(Preferences.BOX_VIEW_SHOW_FINGERING_OUTSIDE_BOX));
				contents.setShowNotesOutside(prefs.getBoolean(Preferences.BOX_VIEW_SHOW_NOTES_OUTSIDE_BOX));
				contents.setShowIntervalsOutside(prefs.getBoolean(Preferences.BOX_VIEW_SHOW_INTERVALS_OUTSIDE_BOX));
			}

			// update check state of show actions
			if (!getSearchMode()) {
				final Object fingeringState = CHECK;
				Object notesState = CHECK;
				Object intervalsState = CHECK;

				if (showNotesInside() && showFingeringsInside()) {
					notesState = UNCHECK;
				}
				if (showIntervalsInside() && (showFingeringsInside() || showNotesInside())) {
					intervalsState = UNCHECK;
				}
				setCheckStateOfShowActions(fingeringState, notesState, intervalsState);
			}
		}
	}

	@Override
	protected String getPreferencePageID() {
		return BoxViewPreferencePage.ID;
	}

	@Override
	protected void setInput(final Object input) {
		super.setInput(input);
		updateColumnActions();
	}

	private BoxDraft getCastedContent() {
		return (BoxDraft) getContent();
	}

	@Override
	protected boolean setSearchMode(final boolean searchMode) {
		if (!super.setSearchMode(searchMode)) {
			return false;
		}

		updateColumnActions();
		updateShowBarreButton();

		if (searchMode) {
			tempFingeringMode = showFingeringAction.isChecked() ? CHECK : UNCHECK;
			tempNotesMode = showNotesAction.isChecked() ? CHECK : UNCHECK;
			tempIntervalsMode = showIntervalsAction.isChecked() ? CHECK : UNCHECK;
			setCheckStateOfShowActions(!showFingeringAction.isChecked() || usePointsMode() ? UNCHECK : CHECK, CHECK,
					CHECK);
			checkFastEditing();
		} else {
			if (tempFingeringMode != null && tempNotesMode != null && tempIntervalsMode != null) {
				setCheckStateOfShowActions(tempFingeringMode, tempNotesMode, tempIntervalsMode);
			}
		}

		return true;
	};

	private void updateColumnActions() {
		final boolean searchMode = getSearchMode();

		final Instrument currentInstrument = InstrumentList.getInstance().getCurrentInstrument();
		if (currentInstrument == null) {
			addFretButtonAction.setEnabled(false);
			removeFretButtonAction.setEnabled(false);
			return;
		}
		final int maxFretWidth = Math.min(FretBlock.MAX_FRET_RANGE, currentInstrument.getFretCount()
				- currentInstrument.getMinFret());

		addFretButtonAction.setEnabled(searchMode && getCastedContent().getFretWidth() < maxFretWidth);
		removeFretButtonAction.setEnabled(searchMode && getCastedContent().getFretWidth() > MIN_FRET_NUMBER);
	}

	@Override
	public int getExportHeight() {
		final IPreferenceStore prefs = Activator.getDefault().getPreferenceStore();
		final int exportHeight = prefs.getInt(Preferences.BOX_VIEW_EXPORT_HEIGHT);
		final boolean liveSize = prefs.getBoolean(Preferences.BOX_VIEW_EXPORT_LIVE_SIZE);
		return liveSize ? -1 : exportHeight;
	}

	@Override
	protected boolean showBlockInfo() {
		return true;
	}

	@Override
	protected String getHelpId() {
		return HELP_ID;
	}

	@Override
	public void dispose() {
		if (addFretButtonAction != null) {
			addFretButtonAction.dispose();
		}
		if (removeFretButtonAction != null) {
			removeFretButtonAction.dispose();
		}
		super.dispose();
	}

	/* --- actions --- */

	private abstract class AbstractShowAction extends Action {

		public AbstractShowAction(final String id, final String text, final String imagePath) {
			setActionDefinitionId(id);
			setText(text);
			setToolTipText(text);
			setImageDescriptor(Activator.getImageDescriptor(imagePath));
		}

		@Override
		public int getStyle() {
			return AS_CHECK_BOX;
		}
	}

	/**
	 * Action to enable/disable the show fingering option.
	 */
	private class ShowFingeringAction extends AbstractShowAction {

		private static final String COMMAND_ID = "com.plucknplay.csg.ui.showFingering"; //$NON-NLS-1$

		public ShowFingeringAction() {
			super(COMMAND_ID, ViewMessages.BoxView_show_fingering, IImageKeys.SHOW_FINGERING);
		}

		@Override
		public void run() {
			if (getSearchMode()) {
				setCheckStateOfShowActions(isChecked() ? CHECK : UNCHECK, UNCHANGED, UNCHANGED);
				checkFastEditing();
			} else {
				setCheckStateOfShowActions(isChecked() ? CHECK : UNCHECK, isChecked() && showFingeringsInside()
						&& showNotesInside() ? UNCHECK : UNCHANGED, isChecked() && showFingeringsInside()
						&& showIntervalsInside() ? UNCHECK : UNCHANGED);
			}
		}
	}

	/**
	 * Action to enable/disable the show notes option.
	 */
	private class ShowNotesAction extends AbstractShowAction {

		private static final String COMMAND_ID = "com.plucknplay.csg.ui.showNotes"; //$NON-NLS-1$

		public ShowNotesAction() {
			super(COMMAND_ID, ViewMessages.BoxView_show_notes, IImageKeys.SHOW_NOTES);
		}

		@Override
		public void run() {
			if (getSearchMode()) {
				setCheckStateOfShowActions(UNCHANGED, isChecked() ? CHECK : UNCHECK, UNCHANGED);
			} else {
				setCheckStateOfShowActions(isChecked() && showNotesInside() && showFingeringsInside() ? UNCHECK
						: UNCHANGED, isChecked() ? CHECK : UNCHECK, isChecked() && showNotesInside()
						&& showIntervalsInside() ? UNCHECK : UNCHANGED);
			}
		}
	}

	/**
	 * Action to enable/disable the show intervals option.
	 */
	private class ShowIntervalsAction extends AbstractShowAction {

		private static final String COMMAND_ID = "com.plucknplay.csg.ui.showIntervals"; //$NON-NLS-1$

		public ShowIntervalsAction() {
			super(COMMAND_ID, ViewMessages.BoxView_show_intervals, IImageKeys.SHOW_INTERVALS);
		}

		@Override
		public void run() {
			if (getSearchMode()) {
				setCheckStateOfShowActions(UNCHANGED, UNCHANGED, isChecked() ? CHECK : UNCHECK);
			} else {
				setCheckStateOfShowActions(isChecked() && showIntervalsInside() && showFingeringsInside() ? UNCHECK
						: UNCHANGED, isChecked() && showIntervalsInside() && showNotesInside() ? UNCHECK : UNCHANGED,
						isChecked() ? CHECK : UNCHECK);
			}
		}
	}

	// handle check state of show actions
	private void setCheckStateOfShowActions(final Object fingering, final Object notes, final Object intervals) {

		final BoxDraftEditPart contents = (BoxDraftEditPart) (getGraphicalViewer().getContents() instanceof BoxDraftEditPart ? getGraphicalViewer()
				.getContents() : null);

		if (contents != null) {
			if (fingering != UNCHANGED) {
				showFingeringAction.setChecked(fingering == CHECK);
				contents.setShowFingering(showFingeringAction.isChecked());
			}
			if (notes != UNCHANGED) {
				showNotesAction.setChecked(notes == CHECK);
				contents.setShowNotes(showNotesAction.isChecked());
			}
			if (intervals != UNCHANGED) {
				showIntervalsAction.setChecked(intervals == CHECK);
				contents.setShowIntervals(showIntervalsAction.isChecked());
			}
		}
		if (fingering != UNCHANGED) {
			Activator.getDefault().getPreferenceStore()
					.setValue(Preferences.BOX_VIEW_SHOW_FINGERING, showFingeringAction.isChecked());
		}
		if (notes != UNCHANGED) {
			Activator.getDefault().getPreferenceStore()
					.setValue(Preferences.BOX_VIEW_SHOW_NOTES, showNotesAction.isChecked());
		}
		if (intervals != UNCHANGED) {
			Activator.getDefault().getPreferenceStore()
					.setValue(Preferences.BOX_VIEW_SHOW_INTERVALS, showIntervalsAction.isChecked());
		}
		refresh();
	}

	protected boolean showFingeringsInside() {
		return !Activator.getDefault().getPreferenceStore().getBoolean(Preferences.BOX_VIEW_SHOW_FINGERING_OUTSIDE_BOX);
	}

	protected boolean showNotesInside() {
		return !Activator.getDefault().getPreferenceStore().getBoolean(Preferences.BOX_VIEW_SHOW_NOTES_OUTSIDE_BOX);
	}

	protected boolean showIntervalsInside() {
		return !Activator.getDefault().getPreferenceStore().getBoolean(Preferences.BOX_VIEW_SHOW_INTERVALS_OUTSIDE_BOX);
	}
}
