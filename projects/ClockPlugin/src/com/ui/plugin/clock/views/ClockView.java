package com.ui.plugin.clock.views;

import java.time.LocalTime;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

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

	@Override
	public void createPartControl(final Composite parent) {
		final Canvas clock = new Canvas(parent, SWT.NONE);
		clock.addPaintListener(this::drawClock);
		clock.addPaintListener(this::paintControl);
		
		ClockView.moveSecondHand(clock).start();
		
	}

	private void drawClock(final PaintEvent event) {
		event.gc.drawArc(event.x, event.y, event.width - 1, event.height - 1, 0, 360);
	}

	public void paintControl(final PaintEvent event) {
		event.gc.drawArc(event.x, event.y, event.width - 1, event.height - 1, 0, 360);
		final int seconds = LocalTime.now().getSecond();
		final int arc = (15 - seconds) * 6 % 360;
		final Color blue = event.display.getSystemColor(SWT.COLOR_BLUE);
		event.gc.setBackground(blue);
		event.gc.fillArc(event.x, event.y, event.width - 1, event.height - 1, arc - 1, 2);
	}
	
	private static Thread moveSecondHand(final Canvas clock) {
		
		final Runnable runClock = () ->{
			while (!clock.isDisposed()) {
				try {
					clock.dispose();
				}catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		};
		
		final Thread runner = new Thread(runClock,"Tick Tack");
		runner.setUncaughtExceptionHandler((thread, exception) -> {
			exception.printStackTrace();
		});
		
		return runner;
		
	}

	@Override
	public void setFocus() {
	}
}
