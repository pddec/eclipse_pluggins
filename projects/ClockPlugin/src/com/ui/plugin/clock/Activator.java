package com.ui.plugin.clock;

import java.io.InputStream;
import java.time.ZoneId;
import java.util.Objects;
import java.util.function.Function;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;

import org.eclipse.swt.graphics.Region;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.ui.plugin.clock.widget.ClockWidget;

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
		final Runnable runner = Activator.trayRunner(this)
				.apply(display);

		display.asyncExec(runner);

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
	private static Function<Display,Runnable> trayRunner(final Activator that) {

		return (display) -> () -> {
			final InputStream resourceAsStream = Activator.class.getResourceAsStream("/icons/sample.gif");

			that.image = new Image(display, resourceAsStream);

			final Tray tray = display.getSystemTray();

			if (Objects.isNull(tray) && !that.hasImage())
				return;

			that.trayItem = new TrayItem(tray, SWT.NONE);
			that.trayItem.setToolTipText("Hello World");
			that.trayItem.setVisible(true);
			that.trayItem.setText("Hello World");
			that.trayItem.setImage(that.image);

			final SelectionAdapter openShell = Activator.openShell(that).apply(display);

			that.trayItem.addSelectionListener(openShell);
		};
	}
	
	private static int[] circle(int radius, int offsetX, int offsetY) {
		int[] polygon = new int[8 * radius + 4];
		// x^2 + y^2 = r^2
		for (int index = 0; index < 2 * radius + 1; index++) {
			
			final int x = index - radius;
			
			final double radiusPow = Math.pow(radius, 2);
			final double xPow = Math.pow(x, 2);
			
			final int y = (int) Math.sqrt(radiusPow - xPow);
			
			polygon[2 * index] = offsetX + x;
			polygon[2 * index + 1] = offsetY + y;
			polygon[8 * radius - 2 * index - 2] = offsetX + x;
			polygon[8 * radius - 2 * index - 1] = offsetY - y;
		}
		
		return polygon;
	}

	private static Function<Display, SelectionAdapter> openShell(final Activator that) {
		return (display) -> new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				final Shell shell = new Shell(display,SWT.ON_TOP | SWT.NO_TRIM);
				
				final Region region = new Region();
				
				final int[] circle =  Activator.circle(25, 25, 25);
				
				region.add(circle);
				
				shell.setRegion(region);
				shell.setLayout(new FillLayout());
				
				final ClockWidget clock = ClockWidget.builder()
					.shell(shell)
					.style(SWT.NONE)
					.color(new RGB(255, 0, 255))
					.zone(ZoneId.systemDefault())
					.build();
				
				clock.initDisposeListener();
				clock.initPaintListener();
		
				shell.pack();
				shell.open();
				shell.addDisposeListener(event -> region.dispose());
			}
		};
	}

}
