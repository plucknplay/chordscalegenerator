/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.views.dnd;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;

public abstract class AbstractTransfer extends ByteArrayTransfer {

	@Override
	protected int[] getTypeIds() {
		return new int[] { registerType(getTypeName()) };
	}

	@Override
	protected String[] getTypeNames() {
		return new String[] { getTypeName() };
	}

	protected abstract String getTypeName();

	@Override
	protected void javaToNative(final Object object, final TransferData transferData) {
		final byte[] bytes = toByteArray((List<?>) object);
		if (bytes != null) {
			super.javaToNative(bytes, transferData);
		}
	}

	@Override
	protected Object nativeToJava(final TransferData transferData) {
		final byte[] bytes = (byte[]) super.nativeToJava(transferData);
		return fromByteArray(bytes);
	}

	private byte[] toByteArray(final List<?> list) {
		byte[] bytes = new byte[0];
		try {
			final ByteArrayOutputStream bos = new ByteArrayOutputStream();
			final ObjectOutputStream out = new ObjectOutputStream(bos);
			out.writeObject(list);
			out.close();
			bytes = bos.toByteArray();
		} catch (final IOException e) {
		}
		return bytes;
	}

	private List<?> fromByteArray(final byte[] bytes) {
		List<?> list = null;
		try {
			final ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes));
			list = (List<?>) in.readObject();
			in.close();
		} catch (final ClassNotFoundException e) {
		} catch (final IOException e) {
		}
		return list;
	}
}
