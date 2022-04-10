package com.ui.plugin.clock.views;

import java.time.ZoneId;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.function.Function;

import java.util.function.Predicate;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.DeviceData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import com.ui.plugin.clock.widget.ClockWidget;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view
 * shows data obtained from the model. The sample creates a dummy model on the
 * fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the
 * model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be
 * presented in the view. Each view can present the same model objects using
 * different labels and icons, if needed. Alternatively, a single label provider
 * can be shared between views in order to ensure that objects of the same type
 * are presented in the same way everywhere.
 * <p>
 */

public class ClockView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "com.ui.plugin.clock.views.ClockView";

	private Combo timeZones;

	@Override
	public void createPartControl(final Composite parent) {

		this.timeZones = new Combo(parent, SWT.DROP_DOWN);

		this.timeZones.setVisibleItemCount(5);

		ZoneId.getAvailableZoneIds()
		.stream()
		.forEach(this.timeZones::add);
		
		final RowLayout layout = new RowLayout(SWT.HORIZONTAL);
		parent.setLayout(layout);

		final ClockWidget clockWidget1 = ClockWidget.builder()
				.parent(parent)
				.style(SWT.NONE)
				.color(new RGB(255, 0, 0))
				.zone(ZoneId.systemDefault())
				.build();

		final ClockWidget clockWidget2 = ClockWidget.builder()
				.parent(parent)
				.color(new RGB(0, 255, 0))
				.style(SWT.NONE)
				.zone(ZoneId.systemDefault())
				.build();

		final ClockWidget clockWidget3 = ClockWidget.builder()
				.parent(parent)
				.color(new RGB(0, 0, 255))
				.style(SWT.NONE)
				.zone(ZoneId.systemDefault())
				.build();

		clockWidget1.setLayoutData(new RowData(20, 20));
		clockWidget1.initDisposeListener();
		clockWidget1.initPaintListener();
		clockWidget2.setLayoutData(new RowData(50, 50));
		clockWidget2.initDisposeListener();
		clockWidget2.initPaintListener();
		clockWidget3.setLayoutData(new RowData(100, 100));
		clockWidget3.initDisposeListener();
		clockWidget3.initPaintListener();

		final DeviceData data = parent.getDisplay().getDeviceData();

		final Predicate<Object> predicate = object -> object instanceof Color;

		final Long count = Arrays.stream(data.objects).filter(predicate).count();

		System.err.println("There are " + count + " Color instances");

		final Runnable runnerClock1 = ClockWidget.moveSecondHand(clockWidget1);
		final Runnable runnerClock2 = ClockWidget.moveSecondHand(clockWidget2);
		final Runnable runnerClock3 = ClockWidget.moveSecondHand(clockWidget3);

		final ExecutorService services = Executors.newFixedThreadPool(3, ClockView.clockFactory());

		services.submit(runnerClock1);
		services.submit(runnerClock2);
		services.submit(runnerClock3);

		final SelectionListener timeZoneClock3 = ClockView.timeZoneListener(this)
				.apply(clockWidget3);

		this.timeZones.addSelectionListener(timeZoneClock3);

	}

	private static Function<ClockWidget, SelectionListener> timeZoneListener(final ClockView that) {
		return (clockWidget) -> new SelectionListener() {

			@Override
			public void widgetDefaultSelected(final SelectionEvent event) {
				clockWidget.setZone(ZoneId.systemDefault());
				clockWidget.redraw();
			}

			@Override
			public void widgetSelected(final SelectionEvent event) {
				final String id = that.timeZones.getText();
				clockWidget.setZone(ZoneId.of(id));
				clockWidget.redraw();
			}
		};
	}

	private static ThreadFactory clockFactory() {
		return (final Runnable runClock) -> {
			final Thread runner = new Thread(runClock, "Tick Tack");
			runner.setUncaughtExceptionHandler((thread, exception) -> {
				exception.printStackTrace();
			});
			return runner;
		};
	}

	@Override
	public void setFocus() {
		this.timeZones.setFocus();
	}
}
