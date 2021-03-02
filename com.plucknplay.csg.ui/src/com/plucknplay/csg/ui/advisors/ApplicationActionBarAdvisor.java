/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.advisors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.actions.ContributionItemFactory;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

import com.plucknplay.csg.core.Constants;
import com.plucknplay.csg.core.model.Factory;
import com.plucknplay.csg.core.model.Instrument;
import com.plucknplay.csg.core.model.Note;
import com.plucknplay.csg.core.model.sets.ChordList;
import com.plucknplay.csg.core.model.sets.ScaleList;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.IImageKeys;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.actions.AbstractSignModeAction;
import com.plucknplay.csg.ui.actions.AdvancedFretBlockModeAction;
import com.plucknplay.csg.ui.actions.DeletePerspectivesAction;
import com.plucknplay.csg.ui.actions.FlatSignModeAction;
import com.plucknplay.csg.ui.actions.FretBlockModeAction;
import com.plucknplay.csg.ui.actions.LeftRightHanderAction;
import com.plucknplay.csg.ui.actions.OctaveBlockModeAction;
import com.plucknplay.csg.ui.actions.OpenWorkbenchPreferencePageAction;
import com.plucknplay.csg.ui.actions.SavePerspectiveAction;
import com.plucknplay.csg.ui.actions.SharpAndFlatSignModeAction;
import com.plucknplay.csg.ui.actions.SharpSignModeAction;
import com.plucknplay.csg.ui.actions.ShowBlocksAction;
import com.plucknplay.csg.ui.actions.UpdateAction;
import com.plucknplay.csg.ui.actions.chords.CleanUpChordsAction;
import com.plucknplay.csg.ui.actions.chords.GlobalAddChordAction;
import com.plucknplay.csg.ui.actions.chords.OpenChordAction;
import com.plucknplay.csg.ui.actions.common.EnableEditorAreaAction;
import com.plucknplay.csg.ui.actions.common.LockToolbarAction;
import com.plucknplay.csg.ui.actions.common.NextRootNoteAction;
import com.plucknplay.csg.ui.actions.common.PlaySoundAction;
import com.plucknplay.csg.ui.actions.common.PreviousRootNoteAction;
import com.plucknplay.csg.ui.actions.common.ShowSupportAction;
import com.plucknplay.csg.ui.actions.common.ShowVideoTutorialsAction;
import com.plucknplay.csg.ui.actions.common.ShowWhatsNewAction;
import com.plucknplay.csg.ui.actions.common.ToggleStatusBarAction;
import com.plucknplay.csg.ui.actions.common.ToggleToolbarAction;
import com.plucknplay.csg.ui.actions.instruments.ActivateInstrumentAction;
import com.plucknplay.csg.ui.actions.instruments.ChangeCapoFretAction;
import com.plucknplay.csg.ui.actions.instruments.DecreaseCapoFretAction;
import com.plucknplay.csg.ui.actions.instruments.DecreaseFretNumberAction;
import com.plucknplay.csg.ui.actions.instruments.GlobalAddInstrumentAction;
import com.plucknplay.csg.ui.actions.instruments.IncreaseCapoFretAction;
import com.plucknplay.csg.ui.actions.instruments.IncreaseFretNumberAction;
import com.plucknplay.csg.ui.actions.instruments.OpenInstrumentAction;
import com.plucknplay.csg.ui.actions.scales.CleanUpScalesAction;
import com.plucknplay.csg.ui.actions.scales.GlobalAddScaleAction;
import com.plucknplay.csg.ui.actions.scales.OpenScaleAction;
import com.plucknplay.csg.ui.activation.ActivateAction;
import com.plucknplay.csg.ui.util.LoginUtil;
import com.plucknplay.csg.ui.util.NotesLabelProvider;
import com.plucknplay.csg.ui.util.WidgetFactory;
import com.plucknplay.csg.ui.views.ViewMessages;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor implements IPropertyChangeListener {

	private IAction globalAddInstrumentAction;
	private IAction globalAddChordAction;
	private IAction globalAddScaleAction;
	private IWorkbenchAction saveAction;
	private IWorkbenchAction saveAllAction;
	private IWorkbenchAction exitAction;
	private IWorkbenchAction aboutAction;
	private ActivateAction activateAction;
	private AbstractSignModeAction sharpAndFlatSignModeAction;
	private AbstractSignModeAction sharpSignModeAction;
	private AbstractSignModeAction flatSignModeAction;
	private IAction previousRootNoteAction;
	private IAction nextRootNoteAction;
	private IAction decreaseFretNumberAction;
	private IAction increaseFretNumberAction;
	private IAction changeCapoFretAction;
	private IAction decreaseCapoFretAction;
	private IAction increaseCapoFretAction;
	private IAction showBlocksAction;
	private FretBlockModeAction fretBlockModeAction;
	private AdvancedFretBlockModeAction advancedFretBlockModeAction;
	private OctaveBlockModeAction octaveBlockModeAction;
	private IAction cleanUpChordsAction;
	private IAction cleanUpScalesAction;
	private IAction openScaleAction;
	private IAction openInstrumentAction;
	private IAction openChordAction;
	private IAction activateInstrumentAction;
	private PlaySoundAction playSoundAction;
	private IAction toggleHandAction;
	private IAction savePerspectiveAction;
	private IWorkbenchAction savePerspectiveAsAction;
	private IWorkbenchAction resetPerspectiveAction;
	private IAction deletePerspectivesAction;
	private IWorkbenchAction closePerspectiveAction;
	private IWorkbenchAction closeAllPerspectivesAction;
	private IContributionItem perspectivesShortlist;
	private IContributionItem viewsShortlist;
	private IWorkbenchAction newEditorAction;
	private IWorkbenchAction minimizeAction;
	private IWorkbenchAction maximizeAction;
	private IWorkbenchAction activateEditorAction;
	private IWorkbenchAction nextEditorAction;
	private IWorkbenchAction previousEditorAction;
	private IWorkbenchAction closeEditorAction;
	private IWorkbenchAction closeAllEditorsAction;
	private IWorkbenchAction nextPartAction;
	private IWorkbenchAction previousPartAction;
	private IWorkbenchAction nextPerspectiveAction;
	private IWorkbenchAction previousPerspectiveAction;
	private EnableEditorAreaAction toggleEditorAreaAction;
	private IAction toggleToolbarAction;
	private IAction toggleStatusBarAction;
	private LockToolbarAction lockToolbarAction;
	private IAction workbenchPreferencesAction;
	private IWorkbenchAction preferencesAction;
	private IWorkbenchAction helpContentsAction;
	private IWorkbenchAction dynamicHelpAction;
	private IWorkbenchAction helpSearchAction;
	private ShowVideoTutorialsAction videoTutorialsAction;
	private ShowSupportAction supportAction;
	private ShowWhatsNewAction whatsNewAction;
	private IWorkbenchAction welcomeAction;
	private UpdateAction updateAction;

	private ToolItem rootNoteToolItem;
	private ComboViewer rootNoteComboViewer;
	private List<RootNoteAction> rootNoteActions;
	private List<FretNumberAction> fretNumberActions;

	public ApplicationActionBarAdvisor(final IActionBarConfigurer configurer) {
		super(configurer);
		Activator.getDefault().getPreferenceStore().addPropertyChangeListener(this);
	}

	@Override
	protected void makeActions(final IWorkbenchWindow window) {

		// FILE menu
		globalAddInstrumentAction = new GlobalAddInstrumentAction(window);
		register(globalAddInstrumentAction);

		globalAddChordAction = new GlobalAddChordAction(window);
		register(globalAddChordAction);

		globalAddScaleAction = new GlobalAddScaleAction(window);
		register(globalAddScaleAction);

		saveAction = ActionFactory.SAVE.create(window);
		register(saveAction);

		saveAllAction = ActionFactory.SAVE_ALL.create(window);
		register(saveAllAction);

		preferencesAction = ActionFactory.PREFERENCES.create(window);
		register(preferencesAction);

		exitAction = ActionFactory.QUIT.create(window);
		register(exitAction);

		// VIEW menu

		decreaseFretNumberAction = new DecreaseFretNumberAction();
		register(decreaseFretNumberAction);

		increaseFretNumberAction = new IncreaseFretNumberAction();
		register(increaseFretNumberAction);

		changeCapoFretAction = new ChangeCapoFretAction(window);
		register(changeCapoFretAction);

		decreaseCapoFretAction = new DecreaseCapoFretAction();
		register(decreaseCapoFretAction);

		increaseCapoFretAction = new IncreaseCapoFretAction();
		register(increaseCapoFretAction);

		toggleHandAction = new LeftRightHanderAction();
		register(toggleHandAction);

		// EXTRAS menu

		sharpAndFlatSignModeAction = new SharpAndFlatSignModeAction();
		register(sharpAndFlatSignModeAction);

		sharpSignModeAction = new SharpSignModeAction();
		register(sharpSignModeAction);

		flatSignModeAction = new FlatSignModeAction();
		register(flatSignModeAction);

		previousRootNoteAction = new PreviousRootNoteAction();
		register(previousRootNoteAction);

		nextRootNoteAction = new NextRootNoteAction();
		register(nextRootNoteAction);

		showBlocksAction = new ShowBlocksAction();
		register(showBlocksAction);

		fretBlockModeAction = new FretBlockModeAction();
		register(fretBlockModeAction);

		advancedFretBlockModeAction = new AdvancedFretBlockModeAction();
		register(advancedFretBlockModeAction);

		octaveBlockModeAction = new OctaveBlockModeAction();
		register(octaveBlockModeAction);

		cleanUpChordsAction = new CleanUpChordsAction(window);
		register(cleanUpChordsAction);

		cleanUpScalesAction = new CleanUpScalesAction(window);
		register(cleanUpScalesAction);

		openInstrumentAction = new OpenInstrumentAction(window);
		register(openInstrumentAction);

		openChordAction = new OpenChordAction(window);
		register(openChordAction);

		openScaleAction = new OpenScaleAction(window);
		register(openScaleAction);

		activateInstrumentAction = new ActivateInstrumentAction(window);
		register(activateInstrumentAction);

		playSoundAction = new PlaySoundAction(window);
		register(playSoundAction);

		// WINDOW menu
		newEditorAction = ActionFactory.NEW_EDITOR.create(window);
		register(newEditorAction);

		perspectivesShortlist = ContributionItemFactory.PERSPECTIVES_SHORTLIST.create(window);
		viewsShortlist = ContributionItemFactory.VIEWS_SHORTLIST.create(window);

		savePerspectiveAction = new SavePerspectiveAction(window);
		register(savePerspectiveAction);

		savePerspectiveAsAction = ActionFactory.SAVE_PERSPECTIVE.create(window);
		register(savePerspectiveAsAction);

		resetPerspectiveAction = ActionFactory.RESET_PERSPECTIVE.create(window);
		register(resetPerspectiveAction);

		deletePerspectivesAction = new DeletePerspectivesAction(window);
		register(deletePerspectivesAction);

		closePerspectiveAction = ActionFactory.CLOSE_PERSPECTIVE.create(window);
		register(closePerspectiveAction);

		closeAllPerspectivesAction = ActionFactory.CLOSE_ALL_PERSPECTIVES.create(window);
		register(closeAllPerspectivesAction);

		toggleEditorAreaAction = new EnableEditorAreaAction(window);
		register(toggleEditorAreaAction);

		lockToolbarAction = new LockToolbarAction(window);
		register(lockToolbarAction);

		toggleToolbarAction = new ToggleToolbarAction(window);
		register(toggleToolbarAction);

		toggleStatusBarAction = new ToggleStatusBarAction(window);
		register(toggleStatusBarAction);

		workbenchPreferencesAction = new OpenWorkbenchPreferencePageAction();
		register(workbenchPreferencesAction);

		// WINDOW - NAVIGATION menu
		maximizeAction = ActionFactory.MAXIMIZE.create(window);
		register(maximizeAction);

		minimizeAction = ActionFactory.MINIMIZE.create(window);
		register(minimizeAction);

		activateEditorAction = ActionFactory.ACTIVATE_EDITOR.create(window);
		register(activateEditorAction);

		nextEditorAction = ActionFactory.NEXT_EDITOR.create(window);
		register(nextEditorAction);

		previousEditorAction = ActionFactory.PREVIOUS_EDITOR.create(window);
		register(previousEditorAction);

		closeEditorAction = ActionFactory.CLOSE.create(window);
		register(closeEditorAction);

		closeAllEditorsAction = ActionFactory.CLOSE_ALL.create(window);
		register(closeAllEditorsAction);

		nextPartAction = ActionFactory.NEXT_PART.create(window);
		register(nextPartAction);

		previousPartAction = ActionFactory.PREVIOUS_PART.create(window);
		register(previousPartAction);

		nextPerspectiveAction = ActionFactory.NEXT_PERSPECTIVE.create(window);
		register(nextPerspectiveAction);

		previousPerspectiveAction = ActionFactory.PREVIOUS_PERSPECTIVE.create(window);
		register(previousPerspectiveAction);

		// HELP menu
		welcomeAction = ActionFactory.INTRO.create(window);
		register(welcomeAction);

		helpContentsAction = ActionFactory.HELP_CONTENTS.create(window);
		register(helpContentsAction);

		helpSearchAction = ActionFactory.HELP_SEARCH.create(window);
		register(helpSearchAction);

		dynamicHelpAction = ActionFactory.DYNAMIC_HELP.create(window);
		dynamicHelpAction.setImageDescriptor(Activator.getImageDescriptor(IImageKeys.DYNAMIC_HELP));
		register(dynamicHelpAction);

		videoTutorialsAction = new ShowVideoTutorialsAction(window);
		register(videoTutorialsAction);

		supportAction = new ShowSupportAction(window);
		register(supportAction);

		whatsNewAction = new ShowWhatsNewAction(window);
		register(whatsNewAction);

		activateAction = new ActivateAction();
		register(activateAction);

		updateAction = new UpdateAction(window);
		register(updateAction);

		aboutAction = ActionFactory.ABOUT.create(window);
		register(aboutAction);

		// Necessary Hack in order to ensure correct behaviour for keybinding
		sharpAndFlatSignModeAction.setActions(sharpSignModeAction, flatSignModeAction);
		sharpSignModeAction.setActions(sharpAndFlatSignModeAction, flatSignModeAction);
		flatSignModeAction.setActions(sharpAndFlatSignModeAction, sharpSignModeAction);
		fretBlockModeAction.setActions(advancedFretBlockModeAction, octaveBlockModeAction);
		advancedFretBlockModeAction.setActions(fretBlockModeAction, octaveBlockModeAction);
		octaveBlockModeAction.setActions(fretBlockModeAction, advancedFretBlockModeAction);

		// create root note actions
		rootNoteActions = new ArrayList<RootNoteAction>();
		for (int i = 0; i <= Constants.MAX_NOTES_VALUE; i++) {
			rootNoteActions.add(new RootNoteAction(i));
		}

		// create fret number actions
		fretNumberActions = new ArrayList<FretNumberAction>();
		for (int i = Constants.MIN_FRET_NUMBER; i <= Constants.MAX_FRET_NUMBER; i++) {
			fretNumberActions.add(new FretNumberAction(i));
		}
	}

	@Override
	protected void fillMenuBar(final IMenuManager menuBar) {

		final MenuManager fileMenu = new MenuManager(AdvisorMessages.ApplicationActionBarAdvisor_menu_file,
				IWorkbenchActionConstants.M_FILE);
		final MenuManager newMenu = new MenuManager(AdvisorMessages.ApplicationActionBarAdvisor_menu_new);
		final MenuManager viewMenu = new MenuManager(AdvisorMessages.ApplicationActionBarAdvisor_menu_view);
		final MenuManager notesModeMenu = new MenuManager(AdvisorMessages.ApplicationActionBarAdvisor_menu_notes_mode);
		final MenuManager rootNotesMenu = new MenuManager(AdvisorMessages.ApplicationActionBarAdvisor_menu_root_notes);
		final MenuManager fretNumbersMenu = new MenuManager(
				AdvisorMessages.ApplicationActionBarAdvisor_menu_fret_numbers);
		final MenuManager extrasMenu = new MenuManager(AdvisorMessages.ApplicationActionBarAdvisor_menu_extras);
		final MenuManager windowMenu = new MenuManager(AdvisorMessages.ApplicationActionBarAdvisor_menu_window,
				IWorkbenchActionConstants.M_WINDOW);
		final MenuManager navigationMenu = new MenuManager(AdvisorMessages.ApplicationActionBarAdvisor_menu_navigation,
				IWorkbenchActionConstants.M_NAVIGATE);
		final MenuManager helpMenu = new MenuManager(AdvisorMessages.ApplicationActionBarAdvisor_menu_help,
				IWorkbenchActionConstants.M_HELP);
		final MenuManager openPerspective = new MenuManager(
				AdvisorMessages.ApplicationActionBarAdvisor_menu_open_perspective);
		final MenuManager showView = new MenuManager(AdvisorMessages.ApplicationActionBarAdvisor_menu_show_view);

		menuBar.add(fileMenu);
		menuBar.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
		menuBar.add(viewMenu);
		menuBar.add(extrasMenu);
		menuBar.add(windowMenu);
		menuBar.add(helpMenu);

		// File
		fileMenu.add(newMenu);
		newMenu.add(globalAddInstrumentAction);
		newMenu.add(globalAddChordAction);
		newMenu.add(globalAddScaleAction);
		fileMenu.add(new GroupMarker("new_group")); //$NON-NLS-1$
		fileMenu.add(new Separator());
		fileMenu.add(saveAction);
		fileMenu.add(saveAllAction);
		fileMenu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		final ActionContributionItem preferencesActionItem = new ActionContributionItem(preferencesAction);
		fileMenu.add(preferencesActionItem);
		fileMenu.add(new Separator());
		final ActionContributionItem exitActionItem = new ActionContributionItem(exitAction);
		fileMenu.add(exitActionItem);

		// View
		viewMenu.add(notesModeMenu);
		notesModeMenu.add(sharpAndFlatSignModeAction);
		notesModeMenu.add(sharpSignModeAction);
		notesModeMenu.add(flatSignModeAction);
		viewMenu.add(new Separator());
		viewMenu.add(rootNotesMenu);
		for (final RootNoteAction action : rootNoteActions) {
			rootNotesMenu.add(action);
		}
		viewMenu.add(previousRootNoteAction);
		viewMenu.add(nextRootNoteAction);
		viewMenu.add(new Separator());
		viewMenu.add(fretNumbersMenu);
		for (final FretNumberAction action : fretNumberActions) {
			fretNumbersMenu.add(action);
		}
		viewMenu.add(decreaseFretNumberAction);
		viewMenu.add(increaseFretNumberAction);
		viewMenu.add(new Separator());
		viewMenu.add(changeCapoFretAction);
		viewMenu.add(decreaseCapoFretAction);
		viewMenu.add(increaseCapoFretAction);
		viewMenu.add(new Separator());
		viewMenu.add(showBlocksAction);
		viewMenu.add(fretBlockModeAction);
		viewMenu.add(advancedFretBlockModeAction);
		viewMenu.add(octaveBlockModeAction);
		viewMenu.add(new Separator());
		viewMenu.add(toggleHandAction);

		// Extras
		extrasMenu.add(activateInstrumentAction);
		extrasMenu.add(new Separator());
		extrasMenu.add(openInstrumentAction);
		extrasMenu.add(openChordAction);
		extrasMenu.add(openScaleAction);
		extrasMenu.add(new Separator());
		extrasMenu.add(cleanUpChordsAction);
		extrasMenu.add(cleanUpScalesAction);
		extrasMenu.add(new Separator());
		extrasMenu.add(playSoundAction);

		// Window
		windowMenu.add(newEditorAction);
		windowMenu.add(new Separator());
		windowMenu.add(openPerspective);
		openPerspective.add(perspectivesShortlist);
		windowMenu.add(showView);
		showView.add(viewsShortlist);
		windowMenu.add(new Separator());
		windowMenu.add(savePerspectiveAction);
		windowMenu.add(savePerspectiveAsAction);
		windowMenu.add(resetPerspectiveAction);
		windowMenu.add(deletePerspectivesAction);
		windowMenu.add(closePerspectiveAction);
		windowMenu.add(closeAllPerspectivesAction);
		windowMenu.add(new Separator());
		windowMenu.add(navigationMenu);
		windowMenu.add(new Separator());
		windowMenu.add(toggleEditorAreaAction);
		windowMenu.add(toggleStatusBarAction);
		windowMenu.add(toggleToolbarAction);
		windowMenu.add(lockToolbarAction);
		windowMenu.add(new Separator());
		windowMenu.add(workbenchPreferencesAction);

		// Navigation sub menu
		navigationMenu.add(maximizeAction);
		navigationMenu.add(minimizeAction);
		navigationMenu.add(new Separator());
		navigationMenu.add(activateEditorAction);
		navigationMenu.add(nextEditorAction);
		navigationMenu.add(previousEditorAction);
		navigationMenu.add(closeEditorAction);
		navigationMenu.add(closeAllEditorsAction);
		navigationMenu.add(new Separator());
		navigationMenu.add(nextPartAction);
		navigationMenu.add(previousPartAction);
		navigationMenu.add(new Separator());
		navigationMenu.add(nextPerspectiveAction);
		navigationMenu.add(previousPerspectiveAction);

		// Help
		helpMenu.add(welcomeAction);
		helpMenu.add(new Separator());
		helpMenu.add(helpContentsAction);
		helpMenu.add(helpSearchAction);
		helpMenu.add(dynamicHelpAction);
		helpMenu.add(new Separator());
		helpMenu.add(videoTutorialsAction);
		helpMenu.add(supportAction);
		helpMenu.add(whatsNewAction);
		helpMenu.add(new Separator());
		helpMenu.add(updateAction);
		if (!LoginUtil.isActivated()) {
			helpMenu.add(activateAction);
			LoginUtil.storeRegisterAction(activateAction, helpMenu);
		}
		helpMenu.add(new Separator());
		final ActionContributionItem aboutActionItem = new ActionContributionItem(aboutAction);
		helpMenu.add(aboutActionItem);
		helpMenu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

		// Hide Action set are shown in the Application's Menu at Mac OS X
		if (Platform.OS_MACOSX.equals(Platform.getOS())) {
			preferencesActionItem.setVisible(false);
			exitActionItem.setVisible(false);
			aboutActionItem.setVisible(false);
		}
	}

	@Override
	protected void fillCoolBar(final ICoolBarManager coolBar) {

		final ICoolBarManager cbManager = getActionBarConfigurer().getCoolBarManager();
		final ToolBarManager fileToolBar = new ToolBarManager(coolBar.getStyle() | SWT.BOTTOM);
		fileToolBar.add(saveAction);
		cbManager.add(new ToolBarContributionItem(fileToolBar, IWorkbenchActionConstants.TOOLBAR_FILE));

		final ToolBarManager extraToolBar = new ToolBarManager(coolBar.getStyle() | SWT.BOTTOM);
		extraToolBar.add(activateInstrumentAction);
		extraToolBar.add(openInstrumentAction);
		extraToolBar.add(openChordAction);
		extraToolBar.add(openScaleAction);
		extraToolBar.add(playSoundAction);
		cbManager.add(new ToolBarContributionItem(extraToolBar, "com.plucknplay.csg.toolbar.extras")); //$NON-NLS-1$

		final ToolBarManager notesModeToolBar = new ToolBarManager(coolBar.getStyle() | SWT.BOTTOM);
		notesModeToolBar.add(sharpAndFlatSignModeAction);
		notesModeToolBar.add(sharpSignModeAction);
		notesModeToolBar.add(flatSignModeAction);
		cbManager.add(new ToolBarContributionItem(notesModeToolBar, "com.plucknplay.csg.toolbar.notesMode")); //$NON-NLS-1$

		final ToolBarManager blocksToolBar = new ToolBarManager(coolBar.getStyle() | SWT.BOTTOM);
		blocksToolBar.add(showBlocksAction);
		blocksToolBar.add(fretBlockModeAction);
		blocksToolBar.add(advancedFretBlockModeAction);
		blocksToolBar.add(octaveBlockModeAction);
		cbManager.add(new ToolBarContributionItem(blocksToolBar, "com.plucknplay.csg.toolbar.blocks")); //$NON-NLS-1$

		final ToolBarManager helpToolBar = new ToolBarManager(coolBar.getStyle() | SWT.BOTTOM);
		helpToolBar.add(helpContentsAction);
		helpToolBar.add(dynamicHelpAction);
		cbManager.add(new ToolBarContributionItem(helpToolBar, IWorkbenchActionConstants.TOOLBAR_HELP));

		final ToolBarManager rootNoteToolBar = new ToolBarManager(coolBar.getStyle() | SWT.BOTTOM);
		rootNoteToolBar.add(new RootNoteContributionItem());
		cbManager.add(new ToolBarContributionItem(rootNoteToolBar, "com.plucknplay.csg.toolbar.rootNote")); //$NON-NLS-1$
	}

	@Override
	public void propertyChange(final PropertyChangeEvent event) {

		if (rootNoteComboViewer != null && rootNoteComboViewer.getCombo() != null
				&& !rootNoteComboViewer.getCombo().isDisposed()) {

			if (event.getProperty().equals(Preferences.GENERAL_H_NOTE_NAME)
					|| event.getProperty().equals(Preferences.GENERAL_B_NOTE_NAME)
					|| event.getProperty().equals(Preferences.NOTES_MODE)) {

				rootNoteComboViewer.refresh(true);

				if (rootNoteToolItem != null && !rootNoteToolItem.isDisposed() && rootNoteToolItem.getParent() != null
						&& !rootNoteToolItem.getParent().isDisposed()) {

					final int oldItemWidth = rootNoteToolItem.getWidth();
					final Point oldToolbarSize = rootNoteToolItem.getParent().getSize();

					rootNoteComboViewer.getCombo().setSize(
							rootNoteComboViewer.getCombo().computeSize(SWT.DEFAULT, SWT.DEFAULT));

					// Bug Fix für Bug #280
					final int newItemWidth = rootNoteComboViewer.getCombo().getSize().x;
					if (newItemWidth != oldItemWidth) {
						rootNoteToolItem.setWidth(newItemWidth);
						rootNoteToolItem.getParent().setSize(oldToolbarSize.x + newItemWidth - oldItemWidth,
								oldToolbarSize.y);
					}
				}

				for (final RootNoteAction action : rootNoteActions) {
					action.updateText();
				}
			}

			else if (event.getProperty().equals(Preferences.ROOT_NOTE) && event.getNewValue() instanceof Integer) {
				rootNoteComboViewer.getCombo().select((Integer) event.getNewValue());
				for (final RootNoteAction action : rootNoteActions) {
					action.updateCheckState();
				}
			}
		}

		if (event.getProperty().equals(Preferences.FRET_NUMBER) && event.getNewValue() instanceof Integer) {
			for (final FretNumberAction action : fretNumberActions) {
				action.updateCheckState();
			}
		}
	}

	@Override
	public void dispose() {
		Activator.getDefault().getPreferenceStore().removePropertyChangeListener(this);
		if (toggleEditorAreaAction != null) {
			toggleEditorAreaAction.dispose();
		}
		if (playSoundAction != null) {
			playSoundAction.dispose();
		}
		if (lockToolbarAction != null) {
			lockToolbarAction.dispose();
		}
	};

	private class RootNoteContributionItem extends ContributionItem {

		@Override
		public void fill(final ToolBar parent, final int index) {
			rootNoteToolItem = new ToolItem(parent, SWT.SEPARATOR);
			rootNoteToolItem.setText(AdvisorMessages.ApplicationActionBarAdvisor_root_note_combo_text);
			rootNoteToolItem.setToolTipText(AdvisorMessages.ApplicationActionBarAdvisor_root_note_combo_tooltip);
			rootNoteComboViewer = WidgetFactory.createRelativeNotesComboViewer(parent);
			rootNoteComboViewer.getCombo().setToolTipText(ViewMessages.ScalesView_root_note);
			rootNoteComboViewer.getCombo().select(
					Activator.getDefault().getPreferenceStore().getInt(Preferences.ROOT_NOTE));
			rootNoteComboViewer.getCombo().setVisible(true);
			rootNoteComboViewer.addSelectionChangedListener(new ISelectionChangedListener() {
				@Override
				public void selectionChanged(final SelectionChangedEvent event) {
					final IStructuredSelection selection = (IStructuredSelection) event.getSelection();
					if (selection != null && !selection.isEmpty()) {
						final Note note = (Note) selection.getFirstElement();
						ScaleList.getInstance().setCurrentRootNote(note);
						ChordList.getInstance().setCurrentRootNote(note);
						Activator.getDefault().getPreferenceStore().setValue(Preferences.ROOT_NOTE, note.getValue());
					}
				}
			});
			rootNoteComboViewer.getCombo()
					.setSize(rootNoteComboViewer.getCombo().computeSize(SWT.DEFAULT, SWT.DEFAULT));
			rootNoteToolItem.setWidth(rootNoteComboViewer.getCombo().getSize().x);
			rootNoteToolItem.setControl(rootNoteComboViewer.getCombo());
		}
	}

	private class RootNoteAction extends Action {

		private final NotesLabelProvider labelProvider;
		private final IPreferenceStore prefs;
		private final int noteValue;

		public RootNoteAction(final int noteValue) {
			prefs = Activator.getDefault().getPreferenceStore();
			labelProvider = new NotesLabelProvider(false);
			this.noteValue = noteValue;
			updateCheckState();
			updateText();
		}

		@Override
		public void run() {
			final Note note = Factory.getInstance().getNote(noteValue);
			ScaleList.getInstance().setCurrentRootNote(note);
			ChordList.getInstance().setCurrentRootNote(note);
			prefs.setValue(Preferences.ROOT_NOTE, noteValue);
			for (final RootNoteAction action : rootNoteActions) {
				action.updateCheckState();
			}
		}

		public void updateCheckState() {
			setChecked(prefs.getInt(Preferences.ROOT_NOTE) == noteValue);
		}

		public void updateText() {
			setText(labelProvider.getText(Factory.getInstance().getNote(noteValue)));
		}
	}

	private class FretNumberAction extends Action {

		private final IPreferenceStore prefs;
		private final int fretNumber;

		public FretNumberAction(final int fretNumber) {
			prefs = Activator.getDefault().getPreferenceStore();
			this.fretNumber = fretNumber;
			updateCheckState();
			updateText();
		}

		@Override
		public void run() {
			Instrument.setFretNumber(fretNumber);
			prefs.setValue(Preferences.FRET_NUMBER, fretNumber);
			final int currentCapoFret = Instrument.getCapoFret();
			if (currentCapoFret + Constants.MIN_ACTIVE_FRET_NUMBER > fretNumber) {
				final int newCapoFret = fretNumber - Constants.MIN_ACTIVE_FRET_NUMBER;
				Instrument.setCapoFret(newCapoFret);
				Activator.getDefault().getPreferenceStore().setValue(Preferences.CAPO_FRET, newCapoFret);
			}
			for (final FretNumberAction action : fretNumberActions) {
				action.updateCheckState();
			}
		}

		public void updateCheckState() {
			setChecked(prefs.getInt(Preferences.FRET_NUMBER) == fretNumber);
		}

		public void updateText() {
			setText("" + fretNumber); //$NON-NLS-1$
		}
	}
}
