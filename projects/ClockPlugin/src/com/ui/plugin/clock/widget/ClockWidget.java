package com.ui.plugin.clock.widget;

import java.time.LocalTime;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

public class ClockWidget extends Canvas {

	private ClockWidget(Composite parent, int style) {
		super(parent, style);
	}

	public void initPaintListener() {
		super.addPaintListener(this::drawClock);
		super.addPaintListener(this::paintControl);
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

	@Override
	public Point computeSize(int width, int height, boolean changed) {

		if (width == SWT.DEFAULT)
			return new Point(height, height);
		
		if (height == SWT.DEFAULT)
			return new Point(width, width);

		final int size = Math.min(width, height);

		if (size == SWT.DEFAULT)
			new Point(50, 50);

		return new Point(size, size);
	}

	public Thread moveSecondHand() {

		final Runnable runClock = () -> {
			while (!this.isDisposed()) {
				try {
					// clock.redraw();
					this.getDisplay().asyncExec(() -> this.redraw());
					Thread.sleep(1000);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		};

		final Thread runner = new Thread(runClock, "Tick Tack");
		runner.setUncaughtExceptionHandler((thread, exception) -> {
			exception.printStackTrace();
		});

		return runner;

	}
	
	public static ClockWidgetBuilder builder() {
		return new ClockWidgetBuilder();
	}
	
	public static class ClockWidgetBuilder {
		
		private Composite parent;
		private int style;
		
		public ClockWidgetBuilder parent(final Composite parent) {
			this.parent = parent;
			return this;
		}
		
		public ClockWidgetBuilder style(final int style) {
			this.style = style;
			return this;
		}
		
		public ClockWidget build() {
			return new ClockWidget(this.parent,this.style);
		}
		
	}
	

}