package com.plucknplay.csg.data;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

public class Activator extends Plugin {

	public static final String PLUGIN_ID = "com.plucknplay.csg.data"; //$NON-NLS-1$

	private static Activator plugin;

	public Activator() {
		plugin = this;
	}

	@Override
	public void stop(final BundleContext context) throws Exception {
		try {
			plugin = null;
		} finally {
			super.stop(context);
		}
	}

	/**
	 * Returns the shared instance.
	 */
	public static Activator getDefault() {
		return plugin;
	}
}
