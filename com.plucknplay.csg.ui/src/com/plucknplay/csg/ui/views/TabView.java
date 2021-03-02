/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.views;

import org.eclipse.gef.EditPartFactory;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.plucknplay.csg.core.model.Block;
import com.plucknplay.csg.core.model.FretBlock;
import com.plucknplay.csg.core.model.Griptable;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.IImageKeys;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.editPartFactories.TabEditPartFactory;
import com.plucknplay.csg.ui.model.Draft;
import com.plucknplay.csg.ui.model.TabDraft;
import com.plucknplay.csg.ui.preferencePages.TabViewPreferencePage;
import com.plucknplay.csg.ui.util.ButtonAction;

public class TabView extends AbstractGraphicalCalculationView {

	public static final String ID = "com.plucknplay.csg.ui.views.TabView"; //$NON-NLS-1$
	public static final String HELP_ID = "tab_view_context"; //$NON-NLS-1$

	private ButtonAction addColumnButtonAction;
	private ButtonAction removeColumnButtonAction;
	private ButtonAction changeRootNoteButtonAction;

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
			editPartFactory = new TabEditPartFactory();
		}
		return editPartFactory;
	}

	@Override
	protected void fillButtonsComposite(final Composite composite) {

		super.fillButtonsComposite(composite);

		// remove column button
		removeColumnButtonAction = new ButtonAction(getSite(), composite, SWT.PUSH | SWT.FLAT,
				ViewMessages.TabView_remove_column, IImageKeys.TRIANGLE_LEFT, "com.plucknplay.csg.ui.removeColumn") { //$NON-NLS-1$
			@Override
			public void run() {
				final int columns = getCastedContent().getNumberOfColumns() - 1;
				if (columns >= 1) {
					getCastedContent().setNumberOfColumns(columns);
					updateColumnActions();
				}
			}
		};
		removeColumnButtonAction.getButton().addKeyListener(getEscKeyListener());
		GridDataFactory.fillDefaults().indent(0, VERTICAL_INDENT).hint(BUTTON_SIZE_BIG)
				.applyTo(removeColumnButtonAction.getButton());

		// add column button
		addColumnButtonAction = new ButtonAction(getSite(), composite, SWT.PUSH | SWT.FLAT,
				ViewMessages.TabView_add_column, IImageKeys.TRIANGLE_RIGHT, "com.plucknplay.csg.ui.addColumn") { //$NON-NLS-1$
			@Override
			public void run() {
				final int columns = getCastedContent().getNumberOfColumns() + 1;
				if (columns <= FretBlock.MAX_FRET_RANGE + 1) {
					getCastedContent().setNumberOfColumns(columns);
					updateColumnActions();
				}
			}
		};
		addColumnButtonAction.getButton().addKeyListener(getEscKeyListener());
		GridDataFactory.fillDefaults().hint(BUTTON_SIZE_BIG).applyTo(addColumnButtonAction.getButton());

		// change root note button
		changeRootNoteButtonAction = createChangeRootNoteAction(composite);

		updateButtonSizes();
	}

	@Override
	protected Button[] getButtonsWithIndent() {
		return new Button[] { removeColumnButtonAction.getButton(), changeRootNoteButtonAction.getButton() };
	}

	@Override
	public void notifyChange(final Object property, final Object value) {
		super.notifyChange(property, value);

		// update find chord action when tab draft has changed
		if (property == TabDraft.PROP_COLUMN_NUMBER_CHANGED || property == TabDraft.PROP_FRETBOARD_POSITION_CHANGED
				|| property == TabDraft.PROP_FRETBOARD_POSITIONS_CLEARED) {
			updateFindButtons();
			updateClearAction();
			updateChangeRootNoteButton();
		}
	}

	@Override
	public void propertyChange(final PropertyChangeEvent event) {
		super.propertyChange(event);
		if (event.getProperty().equals(Preferences.TAB_VIEW_SHOW_MUTED_STRINGS)
				|| event.getProperty().equals(Preferences.TAB_VIEW_DRAW_DOUBLED_STRINGS)
				|| event.getProperty().equals(Preferences.TAB_VIEW_HIGHLIGHT_ROOT_NOTE)
				|| event.getProperty().equals(Preferences.TAB_VIEW_HIGHLIGHT_ROOT_NOTE_WITH_BOLD_FONT)
				|| event.getProperty().equals(Preferences.TAB_VIEW_HIGHLIGHT_ROOT_NOTE_WITH_FRAME)
				|| event.getProperty().equals(Preferences.TAB_VIEW_HIGHLIGHT_ROOT_NOTE_WITH_COLOR)
				|| event.getProperty().equals(Preferences.TAB_VIEW_HIGHLIGHT_ROOT_NOTE_COLOR_ID)
				|| event.getProperty().equals(Preferences.NOTES_MODE)) {
			refresh();
		}
	}

	@Override
	protected String getPreferencePageID() {
		return TabViewPreferencePage.ID;
	}

	@Override
	protected Draft createDraftContent(final Object input) {
		Draft result = null;
		if (input == null) {
			result = new TabDraft();
		} else if (input instanceof Griptable) {
			result = new TabDraft((Griptable) input);
		} else if (input instanceof Block) {
			result = new TabDraft((Block) input);
		}
		return result;
	}

	@Override
	protected Draft createContentClone() {
		final Draft content = getContent();
		return content != null ? new TabDraft((TabDraft) content) : null;
	}

	@Override
	protected void setInput(final Object input) {
		super.setInput(input);
		updateColumnActions();
		refresh();
	}

	private TabDraft getCastedContent() {
		return (TabDraft) getContent();
	}

	@Override
	protected boolean setSearchMode(final boolean searchMode) {
		if (!super.setSearchMode(searchMode)) {
			return false;
		}

		updateColumnActions();
		checkFastEditing();

		return true;
	};

	private void updateColumnActions() {
		final boolean searchMode = getSearchMode();
		addColumnButtonAction.setEnabled(searchMode
				&& getCastedContent().getNumberOfColumns() != FretBlock.MAX_FRET_RANGE + 1);
		removeColumnButtonAction.setEnabled(searchMode && getCastedContent().getNumberOfColumns() != 1);
	}

	@Override
	public int getExportHeight() {
		final IPreferenceStore prefs = Activator.getDefault().getPreferenceStore();
		final int exportHeight = prefs.getInt(Preferences.TAB_VIEW_EXPORT_HEIGHT);
		final boolean liveSize = prefs.getBoolean(Preferences.TAB_VIEW_EXPORT_LIVE_SIZE);
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
		if (addColumnButtonAction != null) {
			addColumnButtonAction.dispose();
		}
		if (removeColumnButtonAction != null) {
			removeColumnButtonAction.dispose();
		}
		super.dispose();
	}
}
