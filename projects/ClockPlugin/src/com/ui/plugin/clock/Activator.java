package com.ui.plugin.clock;

import java.io.InputStream;
import java.util.Objects;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "ClockPlugin"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	private TrayItem trayItem;
	private Image image;

	/**
	 * The constructor
	 */
	public Activator() {}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		Activator.plugin = this;

		final Display display = Display.getDefault();

		display.asyncExec(() -> {

			final InputStream resourceAsStream = Activator.class.getResourceAsStream("/icons/sample.gif");

			this.image = new Image(display, resourceAsStream);

			final Tray tray = display.getSystemTray();

			if (Objects.isNull(tray) && !this.hasImage())
				return;

			this.trayItem = new TrayItem(tray, SWT.NONE);
			this.trayItem.setToolTipText("Hello World");
			this.trayItem.setVisible(true);
			this.trayItem.setText("Hello World");
			this.trayItem.setImage(this.image);

		});
	}

	private boolean hasImage() {
		return Objects.nonNull(this.image);
	}

	private boolean hasTrayItem() {
		return Objects.nonNull(this.trayItem);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		Activator.plugin = null;
		super.stop(context);

		if (!this.hasTrayItem()) 
			Display.getDefault().asyncExec(trayItem::dispose);
		
		if (!this.hasImage()) 
			Display.getDefault().asyncExec(image::dispose);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return Activator.plugin;
	}

}
