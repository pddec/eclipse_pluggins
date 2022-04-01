package com.ui.plugin.clock.views;

import java.util.Arrays;
import java.util.function.Predicate;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
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

	@Override
	public void createPartControl(final Composite parent) {
		
		RowLayout layout = new RowLayout(SWT.HORIZONTAL);
		parent.setLayout(layout);
 
		final ClockWidget clockWidget1 = ClockWidget.builder()
				.parent(parent)
				.style(SWT.NONE)
				.color(new RGB(255,0,0))
				.build();
		final ClockWidget clockWidget2 = ClockWidget.builder()
				.parent(parent)
				.color(new RGB(0,255,0))
				.style(SWT.NONE)
				.build();
		final ClockWidget clockWidget3 = ClockWidget.builder()
				.parent(parent)
				.color(new RGB(0,0,255))
				.style(SWT.NONE)
				.build();
		
		clockWidget1.setLayoutData(new RowData(20,20));
		clockWidget2.setLayoutData(new RowData(50,50));
		clockWidget3.setLayoutData(new RowData(100,100));

		clockWidget1.initPaintListener();
		clockWidget2.initPaintListener();
		clockWidget3.initPaintListener();
		
		Object[] objects = parent.getDisplay().getDeviceData().objects;
		
		final Predicate<Object> predicate = object -> object instanceof Color;
		
		final Long count = Arrays.stream(objects).filter(predicate).count();
		
		System.err.println("There are " + count + " Color instances");
		
		/*
		 * final Thread runnerClock1 = clockWidget1.moveSecondHand(); final Thread
		 * runnerClock2 = clockWidget1.moveSecondHand(); final Thread runnerClock3 =
		 * clockWidget1.moveSecondHand();
		 * 
		 * final ExecutorService services = Executors.newFixedThreadPool(3);
		 * 
		 * services.submit(runnerClock1); services.submit(runnerClock2);
		 * services.submit(runnerClock3);
		 */
	}

	@Override
	public void setFocus() {
	}
}
