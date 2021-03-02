/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;

import com.plucknplay.csg.core.model.IntervalContainer;
import com.plucknplay.csg.core.model.sets.CategoryList;
import com.plucknplay.csg.core.model.sets.ChordList;
import com.plucknplay.csg.core.model.sets.IntervalContainerList;
import com.plucknplay.csg.core.model.sets.ScaleList;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.IImageKeys;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.UIConstants;
import com.plucknplay.csg.ui.util.IntervalTableViewerLabelProvider;
import com.plucknplay.csg.ui.util.IntervalViewerSorter;
import com.plucknplay.csg.ui.util.MyFocusListener;
import com.plucknplay.csg.ui.util.StatusLineUtil;

/**
 * This view is an abstract view for interval containers.
 */
public abstract class AbstractIntervalContainerView extends AbstractCategoryView {

	private TableViewer intervalViewer;

	private CLabel intervalCLabel;
	private ViewForm viewForm;
	private SashForm sashForm;

	private IAction verticalAlignmentAction;
	private IAction horizontalAlignmentAction;
	private IAction automaticAlignmentAction;

	private IntervalContainer lastSelectedIntervalContainer;

	private IPreferenceStore prefs;

	private final ControlListener sashFormControlListener = new ControlAdapter() {
		@Override
		public void controlResized(final ControlEvent e) {
			applyAutomaticViewOrientation();
		}
	};

	private final ControlListener intervalViewerControlListener = new ControlAdapter() {
		@Override
		public void controlResized(final ControlEvent e) {
			// store first weight
			final int[] weights = sashForm.getWeights();
			if (weights != null && weights.length > 1) {
				final int weight1 = weights[0];
				final int weight2 = weights[1];
				storeIntervalContainerSectionWeight(weight1);
				storeIntervalSectionWeight(weight2);
			}
		}
	};

	/**
	 * Content provider for the interval viewer.
	 */
	private static class IntervalViewerContentProvider implements IStructuredContentProvider {
		@Override
		public void inputChanged(final Viewer v, final Object oldInput, final Object newInput) {
		}

		@Override
		public void dispose() {
		}

		@Override
		public Object[] getElements(final Object parent) {
			if (!(parent instanceof IntervalContainer)) {
				throw new IllegalArgumentException();
			}
			return ((IntervalContainer) parent).getIntervals().toArray();
		}
	}

	@Override
	public void createPartControl(final Composite parent) {

		// init preferences
		prefs = Activator.getDefault().getPreferenceStore();

		// create category viewer
		sashForm = new SashForm(parent, SWT.VERTICAL);
		createCategoryViewer(sashForm);

		// create interval viewer
		createIntervalsViewForm();

		// create actions
		final IAction showIntervalsAction = new ShowIntervalsAction();
		verticalAlignmentAction = new SetVerticalViewOrientationAction();
		horizontalAlignmentAction = new SetHorizontalViewOrientationAction();
		automaticAlignmentAction = new SetAutomaticViewOrientationAction();

		// register actions
		final Activator activator = Activator.getDefault();
		activator.registerAction(getSite(), showIntervalsAction);
		activator.registerAction(getSite(), verticalAlignmentAction);
		activator.registerAction(getSite(), horizontalAlignmentAction);
		activator.registerAction(getSite(), automaticAlignmentAction);

		// contribute actions
		final IActionBars bars = getViewSite().getActionBars();
		bars.getMenuManager().appendToGroup("additions_right", showIntervalsAction); //$NON-NLS-1$
		bars.getToolBarManager().appendToGroup("additions_right", showIntervalsAction); //$NON-NLS-1$
		bars.getMenuManager().appendToGroup("additions_right_2", verticalAlignmentAction); //$NON-NLS-1$
		bars.getMenuManager().appendToGroup("additions_right_2", horizontalAlignmentAction); //$NON-NLS-1$
		bars.getMenuManager().appendToGroup("additions_right_2", automaticAlignmentAction); //$NON-NLS-1$

		// set context-sensitive help
		Activator.getDefault().setHelp(parent, getHelpId());
	}

	/**
	 * Returns the help id for this view.
	 * 
	 * @return the help id for this view
	 */
	protected abstract String getHelpId();

	/**
	 * Creates the view form for the interval viewer.
	 */
	private void createIntervalsViewForm() {
		// interval list at the bottom
		viewForm = new ViewForm(sashForm, SWT.NONE);
		intervalViewer = new TableViewer(viewForm, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		intervalViewer.setContentProvider(new IntervalViewerContentProvider());
		intervalViewer.setLabelProvider(new IntervalTableViewerLabelProvider());
		intervalViewer.getControl().addFocusListener(new MyFocusListener(this));
		viewForm.setContent(intervalViewer.getTable());

		// interval label
		intervalCLabel = new CLabel(viewForm, SWT.NONE);
		intervalCLabel.setText(ViewMessages.IntervalContainerView_Intervals);
		intervalCLabel.setImage(Activator.getDefault().getImage(IImageKeys.INTERVAL));
		viewForm.setTopLeft(intervalCLabel);

		// set height ratio between chords and intervals viewer
		sashForm.setWeights(new int[] { getIntervalContainerSectionWeight(), getIntervalSectionWeight() });

		// set input (if necessary)
		if (!getViewer().getSelection().isEmpty()) {
			setIntervalInput((IntervalContainer) ((IStructuredSelection) getViewer().getSelection()).getFirstElement());
		}
	}

	protected abstract int getIntervalContainerSectionWeight();

	protected abstract int getIntervalSectionWeight();

	protected abstract void storeIntervalContainerSectionWeight(int weight);

	protected abstract void storeIntervalSectionWeight(int weight);

	@Override
	public void selectionChanged(final SelectionChangedEvent event) {
		super.selectionChanged(event);
		if (event != null && event.getSelection() instanceof IStructuredSelection) {
			final IStructuredSelection structured = (IStructuredSelection) event.getSelection();
			if (structured.size() == 1) {
				final Object first = structured.getFirstElement();
				if (first instanceof IntervalContainer) {
					final IntervalContainer intervalContainer = (IntervalContainer) first;
					setIntervalInput(intervalContainer);
				}
			}
		}
	}

	private void setIntervalInput(final IntervalContainer intervalContainer) {
		lastSelectedIntervalContainer = intervalContainer;
		intervalViewer.setSorter(new IntervalViewerSorter(intervalContainer));
		intervalViewer.setLabelProvider(new IntervalTableViewerLabelProvider(intervalContainer, false));
		intervalViewer.setInput(intervalContainer);
		intervalCLabel.setText(intervalContainer != null ? ViewMessages.IntervalContainerView_intervals_2
				+ intervalContainer.getName() + ")" : ViewMessages.IntervalContainerView_Intervals); //$NON-NLS-1$
	}

	@Override
	public void notifyChange(final Object source, final Object parentSource, final Object property) {
		super.notifyChange(source, parentSource, property);
		if (property == CategoryList.PROP_REMOVED && source == lastSelectedIntervalContainer) {
			setIntervalInput(null);
		}
		if (property == CategoryList.PROP_CHANGED_WHOLE_LIST && lastSelectedIntervalContainer != null
				&& !getCategoryList().getRootCategory().contains(lastSelectedIntervalContainer)) {
			setIntervalInput(null);
		}
		if (property == IntervalContainerList.PROP_UPDATED_NAMES) {
			intervalViewer.refresh(true);
		}
	}

	@Override
	protected int getDNDOpererations() {
		return DND.DROP_MOVE;
	}

	/**
	 * Sets the view orientation.
	 * 
	 * @param setVertical
	 *            true, if the orientation of the view shall be vertical, false
	 *            otherwise
	 */
	private void setViewOrientation(final boolean setVertical) {
		if (verticalAlignmentAction != null && horizontalAlignmentAction != null && automaticAlignmentAction != null) {
			verticalAlignmentAction.setChecked(setVertical);
			horizontalAlignmentAction.setChecked(!setVertical);
			automaticAlignmentAction.setChecked(false);
		}
		setViewOrientationHelper(setVertical);
	}

	private void setViewOrientationHelper(final boolean setVertical) {
		if (setVertical) {
			sashForm.setOrientation(SWT.VERTICAL);
		} else {
			sashForm.setOrientation(SWT.HORIZONTAL);
		}
	}

	/**
	 * Computes the view orientation automatically.
	 */
	private void applyAutomaticViewOrientation() {
		if (sashForm == null || sashForm.isDisposed()) {
			return;
		}
		if (verticalAlignmentAction != null && horizontalAlignmentAction != null && automaticAlignmentAction != null) {
			verticalAlignmentAction.setChecked(false);
			horizontalAlignmentAction.setChecked(false);
			automaticAlignmentAction.setChecked(true);
		}
		setViewOrientationHelper(sashForm.getBounds().height >= sashForm.getBounds().width);
	}

	@Override
	public void setFocus() {
		super.setFocus();
		StatusLineUtil.handleTestVersionWarning(getViewSite(), getStatusLineWarning());
	}

	protected abstract String getStatusLineWarning();

	/* --- Actions --- */

	/**
	 * Action for enabling/disabling intervals form view.
	 */
	private class ShowIntervalsAction extends Action {

		private static final String COMMAND_ID = "com.plucknplay.csg.ui.showIntervalSection"; //$NON-NLS-1$

		public ShowIntervalsAction() {
			setId("chordGenerator.ui.views.IntervalContainerView.ShowIntervalsAction"); //$NON-NLS-1$
			setActionDefinitionId(COMMAND_ID);
			setText(ViewMessages.IntervalContainerView_show_intervals);
			setToolTipText(ViewMessages.IntervalContainerView_show_intervals);
			setImageDescriptor(Activator.getImageDescriptor(IImageKeys.INTERVAL));

			// load preferences
			setChecked(getCategoryList() == ChordList.getInstance()
					&& prefs.getBoolean(Preferences.CHORDS_VIEW_SHOW_INTERVAL_SECTION)
					|| getCategoryList() == ScaleList.getInstance()
					&& prefs.getBoolean(Preferences.SCALES_VIEW_SHOW_INTERVAL_SECTION));

			performAction();
		}

		@Override
		public int getStyle() {
			return IAction.AS_CHECK_BOX;
		}

		@Override
		public void run() {
			performAction();

			// store preferences
			if (getCategoryList() == ChordList.getInstance()) {
				prefs.setValue(Preferences.CHORDS_VIEW_SHOW_INTERVAL_SECTION, isChecked());
			} else if (getCategoryList() == ScaleList.getInstance()) {
				prefs.setValue(Preferences.SCALES_VIEW_SHOW_INTERVAL_SECTION, isChecked());
			}
		}

		private void performAction() {
			if (isChecked()) {
				sashForm.setMaximizedControl(null);
				intervalViewer.getControl().addControlListener(intervalViewerControlListener);
			} else {
				sashForm.setMaximizedControl(getFirstViewForm());
				intervalViewer.getControl().removeControlListener(intervalViewerControlListener);
			}
		}
	}

	/**
	 * Action to set the automatic view orientation.
	 */
	private class SetAutomaticViewOrientationAction extends Action {

		private static final String COMMAND_ID = "com.plucknplay.csg.ui.automaticAlignment"; //$NON-NLS-1$

		public SetAutomaticViewOrientationAction() {
			setId("chordGenerator.ui.views.IntervalContainerView.SetAutomaticViewOrientationAction"); //$NON-NLS-1$
			setActionDefinitionId(COMMAND_ID);
			setText(ViewMessages.IntervalContainerView_automatic_view_orientation);
			setToolTipText(ViewMessages.IntervalContainerView_automatic_view_orientation);
			setImageDescriptor(Activator.getImageDescriptor(IImageKeys.AUTOMATIC_VIEW_ORIENTATION));

			// load preferences
			setChecked(getCategoryList() == ChordList.getInstance()
					&& prefs.getString(Preferences.CHORDS_VIEW_ORIENTATION).equals(
							UIConstants.AUTOMATIC_VIEW_ORIENTATION)
					|| getCategoryList() == ScaleList.getInstance()
					&& prefs.getString(Preferences.SCALES_VIEW_ORIENTATION).equals(
							UIConstants.AUTOMATIC_VIEW_ORIENTATION));

			if (isChecked()) {
				sashForm.addControlListener(sashFormControlListener);
				applyAutomaticViewOrientation();
			}
		}

		@Override
		public int getStyle() {
			return AS_RADIO_BUTTON;
		}

		@Override
		public void run() {
			sashForm.addControlListener(sashFormControlListener);
			applyAutomaticViewOrientation();

			// store preferences
			if (getCategoryList() == ChordList.getInstance()) {
				prefs.setValue(Preferences.CHORDS_VIEW_ORIENTATION, UIConstants.AUTOMATIC_VIEW_ORIENTATION);
			} else if (getCategoryList() == ScaleList.getInstance()) {
				prefs.setValue(Preferences.SCALES_VIEW_ORIENTATION, UIConstants.AUTOMATIC_VIEW_ORIENTATION);
			}
		}
	}

	/**
	 * Action to set the vertical view orientation.
	 */
	private class SetVerticalViewOrientationAction extends Action {

		private static final String COMMAND_ID = "com.plucknplay.csg.ui.verticalAlignment"; //$NON-NLS-1$

		public SetVerticalViewOrientationAction() {
			setId("chordGenerator.ui.views.IntervalContainerView.SetVerticalViewOrientationAction"); //$NON-NLS-1$
			setActionDefinitionId(COMMAND_ID);
			setText(ViewMessages.IntervalContainerView_vertical_view_orientation);
			setToolTipText(ViewMessages.IntervalContainerView_vertical_view_orientation);
			setImageDescriptor(Activator.getImageDescriptor(IImageKeys.VERTIVAL_VIEW_ORIENTATION));

			// load preferences
			setChecked(getCategoryList() == ChordList.getInstance()
					&& prefs.getString(Preferences.CHORDS_VIEW_ORIENTATION).equals(
							UIConstants.VERTICAL_VIEW_ORIENTATION)
					|| getCategoryList() == ScaleList.getInstance()
					&& prefs.getString(Preferences.SCALES_VIEW_ORIENTATION).equals(
							UIConstants.VERTICAL_VIEW_ORIENTATION));

			// init orientation
			if (isChecked()) {
				setViewOrientation(true);
			}
		}

		@Override
		public int getStyle() {
			return AS_RADIO_BUTTON;
		}

		@Override
		public void run() {
			sashForm.removeControlListener(sashFormControlListener);
			setViewOrientation(true);

			// store preferences
			if (getCategoryList() == ChordList.getInstance()) {
				prefs.setValue(Preferences.CHORDS_VIEW_ORIENTATION, UIConstants.VERTICAL_VIEW_ORIENTATION);
			} else if (getCategoryList() == ScaleList.getInstance()) {
				prefs.setValue(Preferences.SCALES_VIEW_ORIENTATION, UIConstants.VERTICAL_VIEW_ORIENTATION);
			}
		}
	}

	/**
	 * Action to set the horizontal view orientation.
	 */
	private class SetHorizontalViewOrientationAction extends Action {

		private static final String COMMAND_ID = "com.plucknplay.csg.ui.horizontalAlignment"; //$NON-NLS-1$

		public SetHorizontalViewOrientationAction() {
			setId("chordGenerator.ui.views.IntervalContainerView.SetHorizontalViewOrientationAction"); //$NON-NLS-1$
			setActionDefinitionId(COMMAND_ID);
			setText(ViewMessages.IntervalContainerView_horizontal_view_orientation);
			setToolTipText(ViewMessages.IntervalContainerView_horizontal_view_orientation);
			setImageDescriptor(Activator.getImageDescriptor(IImageKeys.HORIZONTAL_VIEW_ORIENTATION));

			// load preferences
			setChecked(getCategoryList() == ChordList.getInstance()
					&& prefs.getString(Preferences.CHORDS_VIEW_ORIENTATION).equals(
							UIConstants.HORIZONTAL_VIEW_ORIENTATION)
					|| getCategoryList() == ScaleList.getInstance()
					&& prefs.getString(Preferences.SCALES_VIEW_ORIENTATION).equals(
							UIConstants.HORIZONTAL_VIEW_ORIENTATION));

			// init orientation
			if (isChecked()) {
				setViewOrientation(false);
			}
		}

		@Override
		public int getStyle() {
			return AS_RADIO_BUTTON;
		}

		@Override
		public void run() {
			sashForm.removeControlListener(sashFormControlListener);
			setViewOrientation(false);

			// store preferences
			if (getCategoryList() == ChordList.getInstance()) {
				prefs.setValue(Preferences.CHORDS_VIEW_ORIENTATION, UIConstants.HORIZONTAL_VIEW_ORIENTATION);
			} else if (getCategoryList() == ScaleList.getInstance()) {
				prefs.setValue(Preferences.SCALES_VIEW_ORIENTATION, UIConstants.HORIZONTAL_VIEW_ORIENTATION);
			}
		}
	}
}
