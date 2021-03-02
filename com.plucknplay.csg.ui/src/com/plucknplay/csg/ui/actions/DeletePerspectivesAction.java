/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveRegistry;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.dialogs.ListSelectionDialog;

import com.plucknplay.csg.ui.perspectives.ChordsPerspective;
import com.plucknplay.csg.ui.perspectives.ScalesPerspective;
import com.plucknplay.csg.ui.perspectives.SetupPerspective;
import com.plucknplay.csg.ui.util.DefaultCollectionContentProvider;

public class DeletePerspectivesAction extends Action {

	private static final String ID = "com.plucknplay.csg.ui.actions.delete.perspectives"; //$NON-NLS-1$
	private static final String COMMAND_ID = "com.plucknplay.csg.ui.deletePerspectives"; //$NON-NLS-1$

	private final IWorkbenchWindow window;

	public DeletePerspectivesAction(final IWorkbenchWindow window) {
		this.window = window;
		setId(ID);
		setActionDefinitionId(COMMAND_ID);
		setText(ActionMessages.DeletePerspectivesAction_text);
	}

	@Override
	public void run() {

		final IPerspectiveRegistry registry = window.getWorkbench().getPerspectiveRegistry();

		// (1) determine own perspectives
		final List<IPerspectiveDescriptor> ownPerspectives = new ArrayList<IPerspectiveDescriptor>();
		final IPerspectiveDescriptor[] perspectives = registry.getPerspectives();
		for (final IPerspectiveDescriptor perspective : perspectives) {
			if (perspective.getId().equals(SetupPerspective.ID) || perspective.getId().equals(ChordsPerspective.ID)
					|| perspective.getId().equals(ScalesPerspective.ID)) {
				continue;
			}
			ownPerspectives.add(perspective);
		}

		// (2) open deletion dialog
		if (ownPerspectives.isEmpty()) {
			MessageDialog.openInformation(window.getShell(), ActionMessages.DeletePerspectivesAction_dialog_title,
					ActionMessages.DeletePerspectivesAction_no_perspectives_msg);
		} else {
			final ListSelectionDialog dialog = new ListSelectionDialog(window.getShell(), ownPerspectives,
					new DefaultCollectionContentProvider(), new LabelProvider() {
						private Image perspectiveImage;

						@Override
						public Image getImage(final Object element) {
							if (element instanceof IPerspectiveDescriptor) {
								if (perspectiveImage == null) {
									perspectiveImage = ((IPerspectiveDescriptor) element).getImageDescriptor()
											.createImage();
								}
								return perspectiveImage;
							}
							return super.getImage(element);
						}

						@Override
						public String getText(final Object element) {
							if (element instanceof IPerspectiveDescriptor) {
								return ((IPerspectiveDescriptor) element).getLabel();
							}
							return super.getText(element);
						}

						@Override
						public void dispose() {
							perspectiveImage.dispose();
							super.dispose();
						}
					}, ActionMessages.DeletePerspectivesAction_choose_perspectives_msg);

			dialog.setTitle(ActionMessages.DeletePerspectivesAction_dialog_title);
			dialog.setHelpAvailable(false);

			if (dialog.open() == Dialog.OK) {
				// (3) delete selected perspectives
				final Object[] results = dialog.getResult();
				for (final Object result2 : results) {
					if (result2 instanceof IPerspectiveDescriptor) {
						final IPerspectiveDescriptor perspective = (IPerspectiveDescriptor) result2;
						window.getActivePage().closePerspective(perspective, true, true);
						registry.deletePerspective(perspective);
					}
				}
			}
		}
	}
}
