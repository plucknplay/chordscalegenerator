/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.views;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.bindings.keys.formatting.NativeKeyFormatter;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;

import com.plucknplay.csg.core.Constants;
import com.plucknplay.csg.core.model.Block;
import com.plucknplay.csg.core.model.Chord;
import com.plucknplay.csg.core.model.Griptable;
import com.plucknplay.csg.core.model.Note;
import com.plucknplay.csg.core.model.enums.Clef;
import com.plucknplay.csg.core.model.sets.InstrumentList;
import com.plucknplay.csg.core.model.workingCopies.InstrumentWorkingCopy;
import com.plucknplay.csg.core.model.workingCopies.WorkingCopy;
import com.plucknplay.csg.core.model.workingCopies.WorkingCopyManager;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.IImageKeys;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.UIConstants;
import com.plucknplay.csg.ui.editPartFactories.NotesEditPartFactory;
import com.plucknplay.csg.ui.editParts.NotesDraftEditPart;
import com.plucknplay.csg.ui.model.Draft;
import com.plucknplay.csg.ui.model.NotesDraft;
import com.plucknplay.csg.ui.model.NotesDraftUtil;
import com.plucknplay.csg.ui.preferencePages.NotesViewPreferencePage;
import com.plucknplay.csg.ui.util.ButtonAction;

public class NotesView extends AbstractGraphicalCalculationView {

	public static final String ID = "com.plucknplay.csg.ui.views.NotesView"; //$NON-NLS-1$
	public static final String HELP_ID = "notes_view_context"; //$NON-NLS-1$

	private static final String GROUP_G = "group_g"; //$NON-NLS-1$
	private static final String GROUP_C = "group_c"; //$NON-NLS-1$
	private static final String GROUP_F = "group_f"; //$NON-NLS-1$

	private IPreferenceStore prefs;

	private DisplayModeAction blockModeAction;
	private DisplayModeAction ascendingModeAction;
	private DisplayModeAction descendingModeAction;

	private MenuManager clefMenu;
	private Map<Clef, ClefAction> clefActionsMap;
	private Map<Clef, String> clefGroupMap;
	private ResetDefaultClefAction resetDefaultClefAction;
	private SetDefaultClefAction setDefaultClefAction;

	private Button lockSignModeButton;
	private ButtonAction changeRootNoteButtonAction;

	private final KeyListener keyListener = new KeyListener() {
		@Override
		public void keyPressed(final KeyEvent e) {
			if (!getCastedContent().isEditable()) {
				return;
			}
			if (e.keyCode == SWT.SHIFT) {
				lockSignModeButton.setSelection(true);
			}
		}

		@Override
		public void keyReleased(final KeyEvent e) {
			if (!getCastedContent().isEditable()) {
				return;
			}
			if (e.keyCode == SWT.SHIFT) {
				lockSignModeButton.setSelection(false);
			}
		}
	};

	/**
	 * The edit part factory of this editor.
	 */
	private EditPartFactory editPartFactory;

	@Override
	public void init(final IViewSite site) throws PartInitException {
		super.init(site);
		prefs = Activator.getDefault().getPreferenceStore();
	}

	/**
	 * Returns the edit part factory that the graphical viewer will use.
	 * 
	 * @return the edit part factory that the graphical viewer will use
	 */
	@Override
	protected EditPartFactory getEditPartFactory() {
		if (editPartFactory == null) {
			editPartFactory = new NotesEditPartFactory(this);
		}
		return editPartFactory;
	}

	@Override
	protected void fillButtonsComposite(final Composite composite) {

		super.fillButtonsComposite(composite);

		// lock sign button
		lockSignModeButton = new Button(composite, SWT.TOGGLE | SWT.FLAT);
		lockSignModeButton.setToolTipText(ViewMessages.NotesView_lock_sign_mode
				+ " (" + new NativeKeyFormatter().format(SWT.SHIFT) + ")"); //$NON-NLS-1$ //$NON-NLS-2$
		lockSignModeButton.setImage(Activator.getDefault().getImage(IImageKeys.LOCK_SIGN));
		lockSignModeButton.addKeyListener(getEscKeyListener());
		GridDataFactory.fillDefaults().indent(0, VERTICAL_INDENT).hint(BUTTON_SIZE_BIG).applyTo(lockSignModeButton);

		// change root note button
		changeRootNoteButtonAction = createChangeRootNoteAction(composite);

		updateButtonSizes();
	}

	@Override
	protected Button[] getButtonsWithIndent() {
		return new Button[] { lockSignModeButton, changeRootNoteButtonAction.getButton() };
	}

	@Override
	protected void setInput(final Object input) {
		super.setInput(input);

		// update selection of display mode action
		if (getCastedContent() != null) {
			final String displayMode = NotesDraftUtil.getDisplayMode(getCastedContent());
			blockModeAction.setChecked(UIConstants.DISPLAY_AS_BLOCK.equals(displayMode));
			ascendingModeAction.setChecked(UIConstants.DISPLAY_AS_ASC_ARPEGGIO.equals(displayMode));
			descendingModeAction.setChecked(UIConstants.DISPLAY_AS_DESC_ARPEGGIO.equals(displayMode));
		}
	}

	@Override
	protected Draft createDraftContent(final Object input) {
		NotesDraft result = null;

		final Clef clef = Clef.valueOf(prefs.getString(Preferences.NOTES_VIEW_CLEF));
		final boolean sharpSignOn = !prefs.getString(Preferences.NOTES_MODE).equals(Constants.NOTES_MODE_ONLY_B);
		final boolean showBlocks = prefs.getBoolean(Preferences.SHOW_BLOCKS);

		// create notes draft
		if (input == null) {
			result = new NotesDraft(clef, sharpSignOn);
		} else if (input instanceof Griptable) {
			result = new NotesDraft((Griptable) input, clef, sharpSignOn);
		} else if (input instanceof Block) {
			final Block block = (Block) input;
			final boolean onlyBlock = getSearchMode()
					|| prefs.getBoolean(block.getIntervalContainer() instanceof Chord ? Preferences.NOTES_VIEW_SHOW_ONLY_CHORD_BLOCKS
							: Preferences.NOTES_VIEW_SHOW_ONLY_SCALE_BLOCKS);
			result = new NotesDraft((Block) input, clef, sharpSignOn, showBlocks && onlyBlock);
		}

		return result;
	}

	@Override
	protected Draft createContentClone() {
		final Draft content = getContent();
		return content != null ? new NotesDraft((NotesDraft) content) : null;
	}

	@Override
	protected void preControlResized() {
		if (getGraphicalViewer() != null && getGraphicalViewer().getControl() != null) {
			NotesDraftUtil.setContainerSize(getGraphicalViewer().getControl().getSize());
		}
	}

	@Override
	protected void contributeToActionBars() {
		super.contributeToActionBars();

		final Clef currentClef = Clef.valueOf(prefs.getString(Preferences.NOTES_VIEW_CLEF));

		clefActionsMap = new HashMap<Clef, ClefAction>();
		clefGroupMap = new HashMap<Clef, String>();

		createClefAction(Clef.F_ONE_OCTAVE_DEEPER, currentClef, Activator.getImageDescriptor(IImageKeys.CLEF_F),
				GROUP_F);
		createClefAction(Clef.F_STANDARD, currentClef, Activator.getImageDescriptor(IImageKeys.CLEF_F), GROUP_F);
		createClefAction(Clef.F_CHIAVETTE, currentClef, Activator.getImageDescriptor(IImageKeys.CLEF_F), GROUP_F);

		createClefAction(Clef.C_BARITON, currentClef, Activator.getImageDescriptor(IImageKeys.CLEF_C), GROUP_C);
		createClefAction(Clef.C_TENOR, currentClef, Activator.getImageDescriptor(IImageKeys.CLEF_C), GROUP_C);
		createClefAction(Clef.C_ALTO, currentClef, Activator.getImageDescriptor(IImageKeys.CLEF_C), GROUP_C);
		createClefAction(Clef.C_MEZZO_SOPRANO, currentClef, Activator.getImageDescriptor(IImageKeys.CLEF_C), GROUP_C);
		createClefAction(Clef.C_SOPRANO, currentClef, Activator.getImageDescriptor(IImageKeys.CLEF_C), GROUP_C);

		createClefAction(Clef.G_ONE_OCTAVE_DEEPER, currentClef, Activator.getImageDescriptor(IImageKeys.CLEF_G),
				GROUP_G);
		createClefAction(Clef.G_STANDARD, currentClef, Activator.getImageDescriptor(IImageKeys.CLEF_G), GROUP_G);
		createClefAction(Clef.G_CHIAVETTE, currentClef, Activator.getImageDescriptor(IImageKeys.CLEF_G), GROUP_G);
		createClefAction(Clef.G_ONE_OCTAVE_HIGHER, currentClef, Activator.getImageDescriptor(IImageKeys.CLEF_G),
				GROUP_G);
		createClefAction(Clef.G_TWO_OCTAVES_HIGHER, currentClef, Activator.getImageDescriptor(IImageKeys.CLEF_G),
				GROUP_G);

		resetDefaultClefAction = new ResetDefaultClefAction();
		setDefaultClefAction = new SetDefaultClefAction();
		updateClefActionEnablement();

		// create display mode actions
		blockModeAction = new DisplayModeAction("com.plucknplay.csg.ui.notes.display.mode.block", //$NON-NLS-1$
				ViewMessages.NotesView_mode_block, IImageKeys.NOTES_BLOCK, UIConstants.DISPLAY_AS_BLOCK);
		blockModeAction.setChecked(true);
		ascendingModeAction = new DisplayModeAction("com.plucknplay.csg.ui.notes.display.mode.ascending", //$NON-NLS-1$
				ViewMessages.NotesView_mode_ascending, IImageKeys.NOTES_ASCENDING, UIConstants.DISPLAY_AS_ASC_ARPEGGIO);
		ascendingModeAction.setChecked(false);
		descendingModeAction = new DisplayModeAction(
				"com.plucknplay.csg.ui.notes.display.mode.descending", //$NON-NLS-1$
				ViewMessages.NotesView_mode_descending, IImageKeys.NOTES_DESCENDING,
				UIConstants.DISPLAY_AS_DESC_ARPEGGIO);
		descendingModeAction.setChecked(false);

		// register actions
		final Activator activator = Activator.getDefault();
		activator.registerAction(getSite(), blockModeAction);
		activator.registerAction(getSite(), ascendingModeAction);
		activator.registerAction(getSite(), descendingModeAction);

		// add actions to action bars
		final IActionBars bars = getViewSite().getActionBars();

		clefMenu = new MenuManager(ViewMessages.NotesView_clef);
		updateClefActionVisibility();

		final MenuManager displayModeMenu = new MenuManager(ViewMessages.NotesView_display_mode);
		displayModeMenu.add(blockModeAction);
		displayModeMenu.add(ascendingModeAction);
		displayModeMenu.add(descendingModeAction);

		bars.getMenuManager().prependToGroup(BAR_ADDITIONS_LEFT, new Separator());
		bars.getMenuManager().prependToGroup(BAR_ADDITIONS_LEFT, displayModeMenu);
		bars.getMenuManager().prependToGroup(BAR_ADDITIONS_LEFT, clefMenu);

		bars.getToolBarManager().appendToGroup(BAR_ADDITIONS_LEFT, new Separator());
		bars.getToolBarManager().appendToGroup(BAR_ADDITIONS_LEFT, blockModeAction);
		bars.getToolBarManager().appendToGroup(BAR_ADDITIONS_LEFT, ascendingModeAction);
		bars.getToolBarManager().appendToGroup(BAR_ADDITIONS_LEFT, descendingModeAction);

		// add key listener - added here since lockSignModeAction must already
		// been created
		getGraphicalViewer().getControl().addKeyListener(keyListener);
	}

	private ClefAction createClefAction(final Clef clef, final Clef currentClef, final ImageDescriptor image,
			final String menuGroup) {
		final ClefAction clefAction = new ClefAction(clef, image);
		clefAction.setChecked(clef == currentClef);
		clefActionsMap.put(clef, clefAction);
		clefGroupMap.put(clef, menuGroup);
		return clefAction;
	}

	@Override
	public void notifyChange(final Object property, final Object value) {
		super.notifyChange(property, value);

		// update actions/buttons when notes draft has changed
		if (NotesDraft.PROP_NOTE_POSITION_ADDED.equals(property)
				|| NotesDraft.PROP_NOTE_POSITION_REMOVED.equals(property)
				|| NotesDraft.PROP_NOTE_POSITIONS_CHANGED.equals(property)) {
			updateFindButtons();
			updateClearAction();
			updateChangeRootNoteButton();
		}
	}

	@Override
	public void propertyChange(final PropertyChangeEvent event) {
		super.propertyChange(event);

		if (event.getProperty().equals(Preferences.NOTES_VIEW_DISPLAY_MODE_GRIPTABLES)
				|| event.getProperty().equals(Preferences.NOTES_VIEW_DISPLAY_MODE_CHORD_BLOCKS)
				|| event.getProperty().equals(Preferences.NOTES_VIEW_DISPLAY_MODE_CHORD_SCHEMES)
				|| event.getProperty().equals(Preferences.NOTES_VIEW_DISPLAY_MODE_SCALE_BLOCKS)
				|| event.getProperty().equals(Preferences.NOTES_VIEW_DISPLAY_MODE_SCALES)) {
			updateContentDescription(true);
			refresh();
		}

		else if (event.getProperty().equals(Preferences.NOTES_VIEW_SHOW_ONLY_CHORD_BLOCKS)
				|| event.getProperty().equals(Preferences.NOTES_VIEW_SHOW_ONLY_SCALE_BLOCKS)
				|| event.getProperty().equals(Preferences.SHOW_BLOCKS)) {

			if (getContent() == null || !getContent().isEditable()) {
				setInput(getInput());
			}
		}

		else if (event.getProperty().equals(Preferences.NOTES_VIEW_FILTER_CLEF_NO_NOTE_ON_STAFF)
				|| event.getProperty().equals(Preferences.NOTES_VIEW_FILTER_CLEF_NOT_WHOLE_STAFF_USED)
				|| event.getProperty().equals(Preferences.NOTES_VIEW_FILTER_CLEF_CHIAVETTE)
				|| event.getProperty().equals(Preferences.NOTES_VIEW_FALLBACK_CLEF)) {
			updateClefActionVisibility();
		}

		// general preferences changed (naming problems) & clef annotation
		else if (event.getProperty().equals(Preferences.GENERAL_H_NOTE_NAME)
				|| event.getProperty().equals(Preferences.GENERAL_B_NOTE_NAME)
				|| event.getProperty().equals(Preferences.ABSOLUTE_NOTE_NAMES_MODE)
				|| event.getProperty().equals(Preferences.NOTES_VIEW_SHOW_CLEF_ANNOTATION)
				|| event.getProperty().equals(Preferences.NOTES_VIEW_OPEN_NOTE_REPRESENTATION)
				|| event.getProperty().equals(Preferences.NOTES_VIEW_HIGHLIGHT_ROOT_NOTE)
				|| event.getProperty().equals(Preferences.NOTES_VIEW_USE_MAX_WIDTH)
				|| event.getProperty().equals(Preferences.NOTES_VIEW_FLEXIBLE_SPACING)
				|| event.getProperty().equals(Preferences.NOTES_VIEW_CLEF)) {
			refresh();
		}

		else if (event.getProperty().equals(Preferences.NOTES_MODE)) {
			getCastedContent().setSharpSignOn(!event.getNewValue().equals(Constants.NOTES_MODE_ONLY_B));
		}
	}

	@Override
	public void notifyChange(final Object source, final Object parentSource, final Object property) {
		super.notifyChange(source, parentSource, property);

		// update clef when the current instrument changes
		if (property == InstrumentList.PROP_CURRENT_INSTRUMENT_CHANGED
				|| property == InstrumentList.PROP_CHANGED_ELEMENT && source == getCurrentInstrument()) {

			if (getCurrentInstrument() == null) {
				return;
			}
			updateClefActionVisibility();

			final Clef clef = getCurrentInstrument().getClef();
			if (clef != Clef.NONE) {
				setClef(clef);
			}
		}
	}

	@Override
	protected String getPreferencePageID() {
		return NotesViewPreferencePage.ID;
	}

	private NotesDraft getCastedContent() {
		return (NotesDraft) getContent();
	}

	@Override
	protected boolean setSearchMode(final boolean searchMode) {
		// TODO #416 muss raus bzw. durch refresh() ersetzt werden
		if (searchMode && getInput() != null && getInput() instanceof Block) {
			setInput(getInput());
		}

		if (!super.setSearchMode(searchMode)) {
			return false;
		}

		lockSignModeButton.setEnabled(searchMode);
		lockSignModeButton.setSelection(false);

		blockModeAction.setEnabled(!searchMode);
		ascendingModeAction.setEnabled(!searchMode);
		descendingModeAction.setEnabled(!searchMode);

		return true;
	};

	@Override
	public int getExportHeight() {
		final int exportHeight = prefs.getInt(Preferences.NOTES_VIEW_EXPORT_HEIGHT);
		final boolean liveSize = prefs.getBoolean(Preferences.NOTES_VIEW_EXPORT_LIVE_SIZE);
		return liveSize ? -1 : exportHeight;
	}

	private void setClef(final Clef clef) {
		final ClefAction oldClefAction = clefActionsMap.get(getCastedContent().getClef());
		oldClefAction.setChecked(false);
		getCastedContent().setClef(clef);
		final ClefAction newClefAction = clefActionsMap.get(clef);
		newClefAction.setChecked(true);
		Activator.getDefault().getPreferenceStore().setValue(Preferences.NOTES_VIEW_CLEF, clef.toString());
		updateClefActionEnablement();
	}

	@Override
	protected String getHelpId() {
		return HELP_ID;
	}

	/* --- actions --- */

	private class ClefAction extends Action {

		private final Clef clef;

		public ClefAction(final Clef clef, final ImageDescriptor image) {
			this.clef = clef;
			setText(clef.getName());
			setToolTipText(clef.getName());
			setImageDescriptor(image);
		}

		@Override
		public void run() {
			setClef(clef);
		}

		@Override
		public int getStyle() {
			return AS_RADIO_BUTTON;
		}
	}

	public boolean isLockSignModeActivated() {
		return lockSignModeButton.getSelection();
	}

	@Override
	public void dispose() {
		if (getGraphicalViewer() != null) {
			final Control control = getGraphicalViewer().getControl();
			if (control != null && !control.isDisposed()) {
				control.removeKeyListener(keyListener);
			}
		}
		super.dispose();
	}

	private void updateClefActionVisibility() {
		final Note startNote = getCastedContent().getToneRange().getStartTone();
		final Note endNote = getCastedContent().getToneRange().getEndTone();

		final boolean filterChiavette = prefs.getBoolean(Preferences.NOTES_VIEW_FILTER_CLEF_CHIAVETTE);
		final boolean notWholeStaffUsed = prefs.getBoolean(Preferences.NOTES_VIEW_FILTER_CLEF_NOT_WHOLE_STAFF_USED);
		final boolean noNoteOnStaff = prefs.getBoolean(Preferences.NOTES_VIEW_FILTER_CLEF_NO_NOTE_ON_STAFF);
		final Clef fallbackClef = Clef.valueOf(prefs.getString(Preferences.NOTES_VIEW_FALLBACK_CLEF));
		final Clef activeClef = Clef.valueOf(prefs.getString(Preferences.NOTES_VIEW_CLEF));

		clefMenu.removeAll();
		clefMenu.add(new Separator(GROUP_G));
		clefMenu.add(new Separator(GROUP_C));
		clefMenu.add(new Separator(GROUP_F));

		boolean activeClefVisible = false;
		boolean noClefAdded = true;

		for (final Clef clef : Clef.values()) {
			if (clef == Clef.NONE) {
				continue;
			}

			final Note firstLineNote = clef.getFirstLineNote();
			final Note lastLineNote = clef.getLastLineNote();

			// determine whether clef is visible
			boolean hide = false;
			if (filterChiavette && (clef == Clef.G_CHIAVETTE || clef == Clef.F_CHIAVETTE)) {
				hide = true;
			} else if (notWholeStaffUsed) {
				hide = !(startNote.compareTo(firstLineNote) <= 0 && endNote.compareTo(lastLineNote) >= 0);
			} else if (noNoteOnStaff) {
				hide = startNote.compareTo(lastLineNote) > 0 || endNote.compareTo(firstLineNote) < 0;
			}

			// add clef to menu
			if (!hide || getCurrentInstrument().getClef() == clef) {
				clefMenu.appendToGroup(clefGroupMap.get(clef), clefActionsMap.get(clef));
				noClefAdded = false;
			}

			if (clef == activeClef && !hide) {
				activeClefVisible = true;
			}
		}

		// ensure that at least on clef is added and selected
		if (noClefAdded) {
			clefMenu.appendToGroup(clefGroupMap.get(fallbackClef), clefActionsMap.get(fallbackClef));
			setClef(fallbackClef);
		}
		if (!activeClefVisible) {
			setClef(fallbackClef);
		}

		// add default clef actions
		clefMenu.add(new Separator());
		clefMenu.add(resetDefaultClefAction);
		clefMenu.add(setDefaultClefAction);
	}

	private void updateClefActionEnablement() {
		if (getCurrentInstrument() == null) {
			return;
		}

		final Clef currentClef = Clef.valueOf(Activator.getDefault().getPreferenceStore()
				.getString(Preferences.NOTES_VIEW_CLEF));
		resetDefaultClefAction.setEnabled(getCurrentInstrument().getClef() != Clef.NONE
				&& getCurrentInstrument().getClef() != currentClef);
		setDefaultClefAction.setEnabled(currentClef != Clef.NONE && getCurrentInstrument().getClef() != currentClef);
	}

	/* --- actions --- */

	private class ResetDefaultClefAction extends Action {

		public ResetDefaultClefAction() {
			setActionDefinitionId("com.plucknplay.csg.ui.notes.reset.default.clef");
			setText(ViewMessages.NotesView_reset_default_clef);
			setToolTipText(ViewMessages.NotesView_reset_default_clef);
		}

		@Override
		public void run() {
			if (getCurrentInstrument() == null || getCurrentInstrument().getClef() == Clef.NONE) {
				return;
			}
			setClef(getCurrentInstrument().getClef());
		}
	}

	private class SetDefaultClefAction extends Action {

		public SetDefaultClefAction() {
			setActionDefinitionId("com.plucknplay.csg.ui.notes.set.default.clef");
			setText(ViewMessages.NotesView_set_default_clef);
			setToolTipText(ViewMessages.NotesView_set_default_clef);
		}

		@Override
		public void run() {
			if (getCurrentInstrument() == null) {
				return;
			}

			final WorkingCopy workingCopy = WorkingCopyManager.getInstance().getWorkingCopy(getCurrentInstrument(),
					InstrumentList.getInstance().getRootCategory().getCategory(getCurrentInstrument()), false);

			if (workingCopy instanceof InstrumentWorkingCopy) {
				final Clef currentClef = Clef.valueOf(Activator.getDefault().getPreferenceStore()
						.getString(Preferences.NOTES_VIEW_CLEF));

				if (MessageDialog.openQuestion(getSite().getShell(), ViewMessages.NotesView_set_default_clef, NLS.bind(
						ViewMessages.NotesView_set_default_clef_confirmation, getCurrentInstrument().getName(),
						currentClef.getName()))) {

					final InstrumentWorkingCopy iwc = (InstrumentWorkingCopy) workingCopy;
					iwc.setClef(currentClef);
					iwc.saveClef();
					updateClefActionVisibility();
					updateClefActionEnablement();
				}
			}
		}
	}

	private class DisplayModeAction extends Action {

		private final String displayMode;

		public DisplayModeAction(final String commandId, final String text, final String imagePath,
				final String displayMode) {
			this.displayMode = displayMode;
			setActionDefinitionId(commandId);
			setText(text);
			setToolTipText(text);
			setImageDescriptor(Activator.getImageDescriptor(imagePath));
		}

		@Override
		public void run() {
			final EditPart contents = getGraphicalViewer().getContents();
			if (contents instanceof NotesDraftEditPart) {
				final NotesDraftEditPart editPart = (NotesDraftEditPart) contents;
				editPart.setDisplayMode(displayMode);

				final Object model = editPart.getModel();
				if (model == null || !(model instanceof NotesDraft)) {
					return;
				}

				final NotesDraft draft = (NotesDraft) model;
				final boolean showBlocks = prefs.getBoolean(Preferences.SHOW_BLOCKS);
				if (NotesDraft.TYPE_GRIPTABLE.equals(draft.getType())) {
					prefs.putValue(Preferences.NOTES_VIEW_DISPLAY_MODE_GRIPTABLES, displayMode);
				} else if (NotesDraft.TYPE_CHORD.equals(draft.getType())) {
					prefs.putValue(
							showBlocks && prefs.getBoolean(Preferences.NOTES_VIEW_SHOW_ONLY_CHORD_BLOCKS) ? Preferences.NOTES_VIEW_DISPLAY_MODE_CHORD_BLOCKS
									: Preferences.NOTES_VIEW_DISPLAY_MODE_CHORD_SCHEMES, displayMode);
				} else if (NotesDraft.TYPE_SCALE.equals(draft.getType())) {
					prefs.putValue(
							showBlocks && prefs.getBoolean(Preferences.NOTES_VIEW_SHOW_ONLY_SCALE_BLOCKS) ? Preferences.NOTES_VIEW_DISPLAY_MODE_SCALE_BLOCKS
									: Preferences.NOTES_VIEW_DISPLAY_MODE_SCALES, displayMode);
				}
			}
			refresh();
		}

		@Override
		public int getStyle() {
			return AS_RADIO_BUTTON;
		}
	}
}
