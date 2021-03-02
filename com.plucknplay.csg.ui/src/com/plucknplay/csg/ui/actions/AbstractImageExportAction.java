/*
 * Copyright (c) 2009-2013 pluck-n-play, a software project of the Meißner & Meißner GbR.
 * All rights reserved.
 */
package com.plucknplay.csg.ui.actions;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.ScalableLayeredPane;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPartSite;

import com.plucknplay.csg.core.model.sets.InstrumentList;
import com.plucknplay.csg.ui.Activator;
import com.plucknplay.csg.ui.Preferences;
import com.plucknplay.csg.ui.actions.general.IViewSelectionAction;
import com.plucknplay.csg.ui.views.AbstractGraphicalCalculationView;
import com.plucknplay.csg.ui.views.BoxView;
import com.plucknplay.csg.ui.views.FretboardView;
import com.plucknplay.csg.ui.views.TabView;

public abstract class AbstractImageExportAction extends Action implements IViewSelectionAction {

	private final IViewPart view;
	private final boolean updateEnablement;
	private final Shell shell;

	public AbstractImageExportAction(final IViewPart view) {
		this.view = view;
		updateEnablement = view instanceof FretboardView || view instanceof BoxView || view instanceof TabView;
		shell = view.getSite().getShell();
	}

	@Override
	public void run() {

		if (!(view instanceof AbstractGraphicalCalculationView)) {
			return;
		}

		final AbstractGraphicalCalculationView graphicalView = (AbstractGraphicalCalculationView) view;
		final ScalableLayeredPane rootFigure = graphicalView.getScalableLayeredPane();
		final int normWidth = graphicalView.getNormWidth();
		final int normHeight = graphicalView.getNormHeight();
		final int exportHeight = graphicalView.getExportHeight();

		if (rootFigure == null || normWidth == 0 || normHeight == 0) {
			return;
		}

		final String result = getFileName();
		if (result != null) {
			createImageFile(rootFigure, normWidth, normHeight, exportHeight, result);
		}
	}

	public void createImageFile(final ScalableLayeredPane rootFigure, final int normWidth, final int normHeight,
			final int exportHeight, final String result) {

		// 1) Determine necessary sizing parameters
		final Rectangle rectangle = rootFigure.getBounds();
		final double oldScale = rootFigure.getScale();
		double newScale;
		int imageWidth;
		int imageHeight;

		// live size
		if (exportHeight == -1) {
			final double normRatio = 1.0d * normWidth / normHeight;
			final double realRatio = 1.0d * rectangle.width / rectangle.height;
			imageHeight = rectangle.height;
			imageWidth = rectangle.width;
			if (realRatio > normRatio) {
				imageWidth = imageHeight * normWidth / normHeight;
			}
			if (realRatio < normRatio) {
				imageHeight = imageWidth * normHeight / normWidth;
			}
			newScale = oldScale;
		}

		// fixed size
		else {
			imageWidth = exportHeight * normWidth / normHeight;
			imageHeight = exportHeight;
			newScale = oldScale * exportHeight / rectangle.height;
		}

		// 2) Create a new Graphics for an Image onto which we want to paint
		// rootFigure.
		final Image img = new Image(null, imageWidth, imageHeight);
		final GC imageGC = new GC(img);
		final Color white = new Color(null, 255, 255, 255);
		final Color black = new Color(null, 0, 0, 0);
		imageGC.setForeground(black);
		imageGC.setBackground(white);
		imageGC.setLineStyle(SWT.LINE_SOLID);
		imageGC.setLineWidth(1);
		final Graphics imgGraphics = new SWTGraphics(imageGC);

		// 3) Draw rootFigure onto image. After that image will be ready for
		// save.
		rootFigure.setScale(newScale);
		rootFigure.paint(imgGraphics);
		rootFigure.setScale(oldScale);

		// 4) Save image
		final ImageData[] imgData = new ImageData[1];
		imgData[0] = img.getImageData();

		// if (result.endsWith(".png") && view instanceof
		// AbstractGraphicalCalculationView) {
		//
		// int blackPixel = imgData[0].palette.getPixel(new RGB(0, 0, 0));
		//
		// for (int x = 0; x < imgData[0].width; x++) {
		// for (int y = 0; y < imgData[0].height; y++) {
		//
		// int pixel = imgData[0].getPixel(x, y);
		// RGB rgb = imgData[0].palette.getRGB(pixel);
		//
		// imgData[0].setAlpha(x, y, 255);
		// imgData[0].setPixel(x, y, pixel);
		//
		// if (rgb.red == rgb.green && rgb.green == rgb.blue) {
		// if (rgb.red == 255) {
		// imgData[0].setAlpha(x, y, 0);
		// } else {
		// imgData[0].setPixel(x, y, blackPixel);
		// imgData[0].setAlpha(x, y, 255 - rgb.red);
		// }
		// }
		// }
		// }
		// } else {
		// imgData[0].transparentPixel = imgData[0].getPixel(0, 0);
		// int pixel = imgData[0].palette.getPixel(transparentColor.getRGB());
		// imgData[0].transparentPixel = pixel;
		// }

		final int format = result.endsWith(".bmp") ? SWT.IMAGE_BMP : result.endsWith(".png") ? SWT.IMAGE_PNG : SWT.IMAGE_JPEG; //$NON-NLS-1$ //$NON-NLS-2$
		final ImageLoader imgLoader = new ImageLoader();
		imgLoader.data = imgData;
		imgLoader.save(result, format);

		// 5) release OS resources
		imageGC.dispose();
		img.dispose();
		white.dispose();
		black.dispose();
	}

	@Override
	public void selectionChanged(final ISelection selection) {
		if (!updateEnablement) {
			return;
		}
		setEnabled(InstrumentList.getInstance().getCurrentInstrument() != null);
	}

	protected IWorkbenchPartSite getSite() {
		return view.getSite();
	}

	protected Shell getShell() {
		return shell;
	}

	protected IViewPart getView() {
		return view;
	}

	/**
	 * Returns the absolute pathname string of the image file to export to.
	 * 
	 * @return the absolute pathname string of the image file to export to
	 */
	public abstract String getFileName();

	/**
	 * Returns the absolute pathname string of the temp image file with the
	 * given name extension. The method ensures that the returned file exists.
	 * 
	 * @param nameExtension
	 *            the name extension, must not be null
	 * 
	 * @return the absolute pathname string of the temp image file with the
	 *         given name extension
	 */
	protected String getTempFile(final String nameExtension) {
		if (nameExtension == null) {
			throw new IllegalArgumentException();
		}

		final String temp = "temp";
		final String extension = Activator.getDefault().getPreferenceStore()
				.getString(Preferences.CLIPBOARD_EXPORT_FILE_EXTENSION);
		final IPath path = Platform.getLocation().append(temp).addTrailingSeparator()
				.append(temp + nameExtension + extension);
		final File dataFile = path.toFile();

		if (!dataFile.exists()) {
			try {
				final File parentDirectory = dataFile.getParentFile();
				if (!parentDirectory.exists()) {
					parentDirectory.mkdirs();
				}
				if (!dataFile.exists()) {
					dataFile.createNewFile();
				}
			} catch (final IOException e) {
			}
		}

		return dataFile.getAbsolutePath();
	}
}
