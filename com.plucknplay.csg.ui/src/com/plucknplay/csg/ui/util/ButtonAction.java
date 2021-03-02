/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.util;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.bindings.Binding;
import org.eclipse.jface.bindings.BindingManagerEvent;
import org.eclipse.jface.bindings.IBindingManagerListener;
import org.eclipse.jface.bindings.TriggerSequence;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.keys.IBindingService;

import com.plucknplay.csg.ui.Activator;

public class ButtonAction extends Action {

	private final Button button;
	private final IWorkbenchPartSite site;
	private IBindingService bindingService;

	private boolean toggleButton = true;

	private final IBindingManagerListener bindingListener = new IBindingManagerListener() {
		@Override
		public void bindingManagerChanged(final BindingManagerEvent event) {
			updateButtonsText();
		}
	};

	public ButtonAction(final IWorkbenchPartSite site, final Composite composite, final int style,
			final String toolTipText, final String imagePath, final String commandId) {

		this.site = site;

		// define button
		button = new Button(composite, style);
		button.setToolTipText(toolTipText);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				if (button.isEnabled()) {
					toggleButton = false;
					run();
				}
			}
		});

		// define action
		setId(commandId);
		setActionDefinitionId(commandId);
		setText(toolTipText);
		setToolTipText(toolTipText);

		// register action
		Activator.getDefault().registerAction(site, this);

		// update text and image
		setImagePath(imagePath);
		updateButtonsText();
	}

	@Override
	public void run() {
		if (checkButton() && (SWT.TOGGLE & button.getStyle()) != 0 && toggleButton) {
			button.setSelection(!button.getSelection());
		}
		toggleButton = true;
	}

	private boolean checkButton() {
		return button != null && !button.isDisposed();
	}

	public Button getButton() {
		return button;
	}

	@Override
	public void setEnabled(final boolean enabled) {
		super.setEnabled(enabled);
		if (checkButton()) {
			button.setEnabled(enabled);
		}
	}

	public void setImagePath(final String imagePath) {
		if (imagePath != null) {
			setImageDescriptor(Activator.getImageDescriptor(imagePath));
			if (checkButton()) {
				button.setImage(Activator.getDefault().getImage(imagePath));
			}
		}
	}

	private IBindingService getBindingService() {
		if (bindingService == null) {
			bindingService = (IBindingService) site.getService(IBindingService.class);
			if (bindingService != null) {
				bindingService.addBindingManagerListener(bindingListener);
			}
		}
		return bindingService;
	}

	private void updateButtonsText() {
		final IBindingService service = getBindingService();
		if (service == null) {
			return;
		}

		for (final Binding binding : service.getBindings()) {
			final ParameterizedCommand parameterizedCommand = binding.getParameterizedCommand();
			if (parameterizedCommand != null) {
				final Command command = parameterizedCommand.getCommand();
				if (command != null) {
					if (getActionDefinitionId().equals(command.getId())) {
						final TriggerSequence sequence = binding.getTriggerSequence();
						if (sequence != null && checkButton()) {
							final StringBuffer buf = new StringBuffer(getToolTipText());
							final String bindingText = sequence.format();
							if (bindingText != null && !"".equals(bindingText)) {
								buf.append(" ("); //$NON-NLS-1$
								buf.append(bindingText);
								buf.append(")"); //$NON-NLS-1$
							}
							button.setToolTipText(buf.toString());
							break;
						}
					}
				}
			}
		}
	}

	public void dispose() {
		if (getBindingService() != null) {
			getBindingService().removeBindingManagerListener(bindingListener);
		}
	}
}
