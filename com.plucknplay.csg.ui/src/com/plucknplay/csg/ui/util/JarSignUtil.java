/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.util;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.StringTokenizer;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.signedcontent.SignedContent;
import org.eclipse.osgi.signedcontent.SignedContentFactory;
import org.eclipse.osgi.signedcontent.SignerInfo;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.plucknplay.csg.ui.Activator;

public final class JarSignUtil {

	private JarSignUtil() {
	}

	@SuppressWarnings("unchecked")
	public static String getJarSignInfo() throws InvalidKeyException, SignatureException, CertificateException,
			NoSuchAlgorithmException, NoSuchProviderException, IOException {

		final Bundle bundle = Activator.getDefault().getBundle();
		final BundleContext bundleContext = bundle.getBundleContext();
		final ServiceReference factoryRef = bundleContext.getServiceReference(SignedContentFactory.class.getName());
		if (factoryRef == null) {
			return ""; //$NON-NLS-1$
		}
		final SignedContentFactory contentFactory = (SignedContentFactory) bundleContext.getService(factoryRef);
		final SignedContent content = contentFactory.getSignedContent(bundle);

		final StringBuffer buf = new StringBuffer();
		buf.append("Bundle Check\n"); //$NON-NLS-1$
		buf.append("------------\n\n"); //$NON-NLS-1$
		buf.append("BundleId: " + bundle.getBundleId() + "\n"); //$NON-NLS-1$ //$NON-NLS-2$
		buf.append("Bundle Location: " + bundle.getLocation() + "\n"); //$NON-NLS-1$ //$NON-NLS-2$
		buf.append("Bundle State: " + bundle.getState() + "\n"); //$NON-NLS-1$ //$NON-NLS-2$
		buf.append("Bundle Symbolic Name: " + bundle.getSymbolicName() + "\n\n"); //$NON-NLS-1$ //$NON-NLS-2$

		buf.append("Signed Content\n"); //$NON-NLS-1$
		buf.append("--------------"); //$NON-NLS-1$
		final SignerInfo[] infos = content.getSignerInfos();
		int i = 1;
		for (final SignerInfo info : infos) {
			buf.append("\n\n" + i + ". SingerInfo\n"); //$NON-NLS-1$ //$NON-NLS-2$
			buf.append("Info is trusted: " + info.isTrusted() + "\n"); //$NON-NLS-1$ //$NON-NLS-2$
			buf.append("Message Digest Algorithm: " + info.getMessageDigestAlgorithm() + "\n"); //$NON-NLS-1$ //$NON-NLS-2$
			final Properties[] certs = parseCerts(info.getCertificateChain());
			if (certs.length > 0) {
				for (final Iterator iter = certs[0].entrySet().iterator(); iter.hasNext();) {
					final Map.Entry entry = (Entry) iter.next();
					buf.append(entry.getKey());
					buf.append('=');
					buf.append(entry.getValue());
					if (iter.hasNext()) {
						buf.append('\n');
					}
				}
			}
			i++;
		}

		// buf.append("Signed Entries\n");
		// buf.append("--------------\n\n");
		// SignedContentEntry[] entries = content.getSignedEntries();
		// i = 1;
		// for (SignedContentEntry entry : entries) {
		// buf.append("\n\n" + i + ". SingedContentEntry\n");
		// buf.append("Entry Name: " + entry.getName() + "\n");
		// buf.append("Entry is signed: " + entry.isSigned() + "\n");
		// i++;
		// }

		return buf.toString();
	}

	@SuppressWarnings("unchecked")
	private static Properties[] parseCerts(final Certificate[] chain) {
		final List certs = new ArrayList(chain.length);
		for (int i = 0; i < chain.length; i++) {
			if (!(chain[i] instanceof X509Certificate)) {
				continue;
			}
			final Map cert = parseCert(((X509Certificate) chain[i]).getSubjectDN().getName());
			if (cert != null) {
				certs.add(cert);
			}
		}
		return (Properties[]) certs.toArray(new Properties[certs.size()]);
	}

	private static Properties parseCert(final String certString) {
		final StringTokenizer toker = new StringTokenizer(certString, ","); //$NON-NLS-1$
		final Properties cert = new Properties();
		while (toker.hasMoreTokens()) {
			final String pair = toker.nextToken();
			final int idx = pair.indexOf('=');
			if (idx > 0 && idx < pair.length() - 2) {
				final String key = pair.substring(0, idx).trim();
				String value = pair.substring(idx + 1).trim();
				if (value.length() > 2) {
					if (value.charAt(0) == '\"') {
						value = value.substring(1);
					}

					if (value.charAt(value.length() - 1) == '\"') {
						value = value.substring(0, value.length() - 1);
					}
				}
				cert.setProperty(key, value);
			}
		}
		return cert;
	}

	public static void validateJar() {
		boolean valid = false;
		final Bundle bundle = Activator.getDefault().getBundle();
		final BundleContext bundleContext = bundle.getBundleContext();
		final ServiceReference factoryRef = bundleContext.getServiceReference(SignedContentFactory.class.getName());
		if (factoryRef != null) {
			final SignedContentFactory contentFactory = (SignedContentFactory) bundleContext.getService(factoryRef);
			try {
				final SignedContent content = contentFactory.getSignedContent(bundle);
				final SignerInfo[] infos = content.getSignerInfos();
				if (infos.length > 0) {
					final Properties[] certs = parseCerts(infos[0].getCertificateChain());
					if (certs.length > 0) {
						final String country = certs[0].getProperty("C"); //$NON-NLS-1$
						final String state = certs[0].getProperty("ST"); //$NON-NLS-1$
						final String location = certs[0].getProperty("L"); //$NON-NLS-1$
						final String unit = certs[0].getProperty("OU"); //$NON-NLS-1$
						if ("DE".equalsIgnoreCase(country) && "TH".equalsIgnoreCase(state) //$NON-NLS-1$ //$NON-NLS-2$
								&& "Jena".equalsIgnoreCase(location) //$NON-NLS-1$
								&& "pluck-n-play".equalsIgnoreCase(unit)) { //$NON-NLS-1$
							valid = true;
						}
					}
				}
			} catch (final Exception e) {
				valid = true;
			}
		}
		if (!valid) {
			MessageDialog.openError(PlatformUI.getWorkbench().getDisplay().getActiveShell(), "Error", //$NON-NLS-1$
					"There was a critical problem while trying to start the Chord Scale Generator."); //$NON-NLS-1$
			PlatformUI.getWorkbench().close();
		}
	}
}
