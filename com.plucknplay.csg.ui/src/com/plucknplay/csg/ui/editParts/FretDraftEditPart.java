/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.editParts;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.draw2d.IFigure;
import org.eclipse.ui.progress.UIJob;

import com.plucknplay.csg.core.model.FretboardPosition;
import com.plucknplay.csg.ui.model.FretDraft;

public abstract class FretDraftEditPart extends AbstractDraftEditPart {

	public void editFingering(final FretboardPosition fbp) {
		editFingering(fbp, null);
	}

	public abstract void editFingering(FretboardPosition fbp, IFigure figure);

	@Override
	public void setEditManager() {
		if (!showFingering()) {
			return;
		}

		final UIJob uiJob = new UIJob("") { //$NON-NLS-1$
			@Override
			public IStatus runInUIThread(final IProgressMonitor monitor) {
				final List<FretboardPosition> fbps = getFretDraft().getFretboardPositions(false);
				Collections.sort(fbps, getFretDraft().getFretboardPositionComparator());
				if (showFingering() && getFretDraft().isEditable() && !fbps.isEmpty()) {
					final FretboardPosition fbp = fbps.get(0);
					final FretboardPosition relativeFbp = new FretboardPosition(fbp.getString(), fbp.getFret()
							- getFretDraft().getStartFret() + 1);
					editFingering(relativeFbp);
				}
				return Status.OK_STATUS;
			}
		};
		uiJob.schedule(FAST_EDITING_DELAY);
	}

	protected abstract FretDraft getFretDraft();

	protected abstract boolean showFingering();
}
