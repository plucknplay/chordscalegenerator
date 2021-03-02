/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions.common;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IViewPart;

import com.plucknplay.csg.core.model.Block;
import com.plucknplay.csg.core.model.FretBlock;
import com.plucknplay.csg.core.model.IntervalContainer;
import com.plucknplay.csg.core.model.OctaveBlock;
import com.plucknplay.csg.ui.actions.AbstractCopyResultsToClipboardAction;
import com.plucknplay.csg.ui.actions.ActionMessages;
import com.plucknplay.csg.ui.model.BlockManager;

public class CopyBlocksToClipboardAction extends AbstractCopyResultsToClipboardAction {

	private IntervalContainer selectedIntervalContainer;
	private final boolean blocks;

	public CopyBlocksToClipboardAction(final IViewPart view, final boolean blocks) {
		super(view);
		this.blocks = blocks;
		final String text = blocks ? ActionMessages.CopyBlocksToClipboardAction_scale_blocks_text
				: ActionMessages.CopyBlocksToClipboardAction_chord_blocks_text;
		setText(text);
		setToolTipText(text);
		setEnabled(false);
	}

	@Override
	protected List<?> determineSelectedResults() {
		if (selectedIntervalContainer == null) {
			return null;
		}

		final List<Block> results = new ArrayList<Block>();

		final BlockManager mgr = BlockManager.getInstance();
		mgr.storeCurrentData();
		mgr.reset();

		final Block block = mgr.getDefaultBlock(selectedIntervalContainer);

		// determine all fret blocks
		if (block instanceof FretBlock) {
			FretBlock fretBlock = (FretBlock) block;
			boolean moveToNext = true;
			while (moveToNext) {
				results.add(fretBlock);
				final FretBlock newFretBlock = new FretBlock(fretBlock);
				mgr.moveBlock(newFretBlock, BlockManager.RIGHT);
				moveToNext = newFretBlock.getMinFret() != fretBlock.getMinFret();
				fretBlock = new FretBlock(newFretBlock);
			}
		}

		// determine all octave blocks
		else {

			final OctaveBlock initialOctaveBlock = new OctaveBlock((OctaveBlock) block);
			OctaveBlock octaveBlockUp = new OctaveBlock(initialOctaveBlock);

			boolean moveToNextUp = true;
			while (moveToNextUp) {

				OctaveBlock octaveBlockRight = new OctaveBlock(octaveBlockUp);

				boolean moveToNextRight = true;
				while (moveToNextRight) {
					if (octaveBlockRight.isComplete()) {
						results.add(octaveBlockRight);
					}
					final OctaveBlock newOctaveBlock = new OctaveBlock(octaveBlockRight);
					mgr.moveBlock(newOctaveBlock, BlockManager.RIGHT);
					moveToNextRight = !newOctaveBlock.getStartFretboardPosition().equals(
							octaveBlockRight.getStartFretboardPosition());
					octaveBlockRight = new OctaveBlock(newOctaveBlock);
				}

				final OctaveBlock newOctaveBlock = new OctaveBlock(octaveBlockUp);
				mgr.moveBlock(newOctaveBlock, BlockManager.UP);
				moveToNextUp = !newOctaveBlock.getStartFretboardPosition().equals(
						octaveBlockUp.getStartFretboardPosition());
				octaveBlockUp = new OctaveBlock(newOctaveBlock);
			}

			if (results.isEmpty()) {
				results.add(initialOctaveBlock);
			}
		}

		mgr.resetToStoredData();

		return results;
	}

	@Override
	protected void showEmptyResultsInfoDialog() {
		// should never happen
		MessageDialog.openInformation(getShell(), ActionMessages.CopyResultsToClipboardAction_copy_to_clipboard,
				blocks ? ActionMessages.CopyBlocksToClipboardAction_no_scale_blocks_available
						: ActionMessages.CopyBlocksToClipboardAction_no_chord_blocks_available);
	}

	@Override
	public void selectionChanged(final ISelection selection) {
		super.selectionChanged(selection);
		boolean enabled = false;
		if (selection != null && selection instanceof IStructuredSelection) {
			final IStructuredSelection structered = (IStructuredSelection) selection;
			if (structered.size() == 1 && structered.getFirstElement() instanceof IntervalContainer) {
				selectedIntervalContainer = (IntervalContainer) structered.getFirstElement();
				enabled = true;
			}
		}
		setEnabled(enabled);
	}

	@Override
	protected String getMonitorTaskName() {
		final StringBuffer buf = new StringBuffer(blocks ? ActionMessages.CopyBlocksToClipboardAction_scale_blocks_text
				: ActionMessages.CopyBlocksToClipboardAction_chord_blocks_text);
		buf.append(".");
		return buf.toString();
	}
}
