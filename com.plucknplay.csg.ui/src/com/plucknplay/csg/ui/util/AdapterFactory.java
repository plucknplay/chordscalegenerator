/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.util;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.model.WorkbenchAdapter;

import com.plucknplay.csg.core.model.Chord;
import com.plucknplay.csg.core.model.Instrument;
import com.plucknplay.csg.core.model.Scale;
import com.plucknplay.csg.core.model.sets.Category;
import com.plucknplay.csg.core.model.sets.InstrumentList;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.IImageKeys;
import com.plucknplay.csg.ui.views.ViewMessages;

public class AdapterFactory implements IAdapterFactory {

	private static final Class<?>[] SUPPORTED_TYPES = new Class[] { IWorkbenchAdapter.class };

	private final IWorkbenchAdapter categoryAdapter = new WorkbenchAdapter() {
		@Override
		public String getLabel(final Object obj) {
			return ((Category) obj).getName();
		}

		@Override
		public ImageDescriptor getImageDescriptor(final Object obj) {
			return Activator.getImageDescriptor(IImageKeys.CATEGORY);
		}
	};

	private final IWorkbenchAdapter instrumentAdapter = new WorkbenchAdapter() {
		@Override
		public String getLabel(final Object obj) {
			final Instrument instrument = (Instrument) obj;
			String result = instrument.getName();
			if (InstrumentList.getInstance().getCurrentInstrument() == instrument) {
				result += ViewMessages.InstrumentsView_active;
			}
			return result;
		}

		@Override
		public ImageDescriptor getImageDescriptor(final Object obj) {
			final Instrument instrument = (Instrument) obj;
			if (InstrumentList.getInstance().getCurrentInstrument() == instrument) {
				return Activator.getImageDescriptor(IImageKeys.CURRENT_INSTRUMENT);
			}
			return Activator.getImageDescriptor(IImageKeys.INSTRUMENT);
		}
	};

	private final IWorkbenchAdapter chordAdapter = new WorkbenchAdapter() {
		@Override
		public String getLabel(final Object obj) {
			return ((Chord) obj).getName();
		}

		@Override
		public ImageDescriptor getImageDescriptor(final Object obj) {
			return Activator.getImageDescriptor(IImageKeys.CHORD);
		}
	};

	private final IWorkbenchAdapter scaleAdapter = new WorkbenchAdapter() {
		@Override
		public String getLabel(final Object obj) {
			return ((Scale) obj).getName();
		}

		@Override
		public ImageDescriptor getImageDescriptor(final Object obj) {
			return Activator.getImageDescriptor(IImageKeys.SCALE);
		}
	};

	@Override
	public Class<?>[] getAdapterList() {
		return SUPPORTED_TYPES;
	}

	@Override
	public Object getAdapter(final Object adaptableObject, final Class adapterType) {
		if (adapterType == IWorkbenchAdapter.class && adaptableObject instanceof Category) {
			return categoryAdapter;
		}
		if (adapterType == IWorkbenchAdapter.class && adaptableObject instanceof Instrument) {
			return instrumentAdapter;
		}
		if (adapterType == IWorkbenchAdapter.class && adaptableObject instanceof Chord) {
			return chordAdapter;
		}
		if (adapterType == IWorkbenchAdapter.class && adaptableObject instanceof Scale) {
			return scaleAdapter;
		}
		return null;
	}

}
