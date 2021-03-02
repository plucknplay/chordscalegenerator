/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.IImageKeys;
import com.plucknplay.csg.ui.listeners.ISimpleChangeListener;

public class LeftRightHanderContributionItem extends ContributionItem implements ISimpleChangeListener {

	private Label imageLabel;
	private Label textLabel;

	public LeftRightHanderContributionItem() {
		Activator.getDefault().addSimpleChangeListener(this);
	}

	@Override
	public void fill(final Composite parent) {
		final Composite composite = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(2).margins(20, 5).applyTo(composite);
		imageLabel = createLabel(composite);
		textLabel = createLabel(composite);
		updateLabels();
	}

	private Label createLabel(final Composite parent) {
		final Label label = new Label(parent, SWT.NONE);
		label.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(final MouseEvent e) {
				LeftRightHanderAction.perform();
				updateLabels();
			}
		});
		label.setSize(label.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		label.setToolTipText(ActionMessages.LeftRightHanderContributionItem_double_click_toggles_hand);
		return label;
	}

	private void updateLabels() {
		// set text and image
		textLabel
				.setText(Activator.getDefault().isLeftHander() ? ActionMessages.LeftRightHanderContributionItem_left_hander
						: ActionMessages.LeftRightHanderContributionItem_right_hander);
		imageLabel.setImage(Activator.getDefault().isLeftHander() ? Activator.getDefault().getImage(
				IImageKeys.LEFT_HAND) : Activator.getDefault().getImage(IImageKeys.RIGHT_HAND));

		// layout parent composite - that is necessary due to a SWT layout bug
		if (textLabel.getParent() != null && textLabel.getParent().getParent() != null) {
			textLabel.getParent().getParent().layout(true);
		}
	}

	@Override
	public void notifyChange(final Object property, final Object value) {
		if (property == Activator.PROP_HAND_CHANGED) {
			updateLabels();
		}
	}

	@Override
	public void dispose() {
		Activator.getDefault().removeSimpleChangeListener(this);
		super.dispose();
	}
}
