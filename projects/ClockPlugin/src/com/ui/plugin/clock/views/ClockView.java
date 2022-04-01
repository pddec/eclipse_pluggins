package com.ui.plugin.clock.views;


import java.time.ZoneId;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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
		
		ZoneId.getAvailableZoneIds().stream()
		.forEach(this.timeZones::add);

		final RowLayout layout = new RowLayout(SWT.HORIZONTAL);
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
		
		final SelectionListener timeZoneClock3 = this.timeZoneListener(clockWidget3);
		
		this.timeZones.addSelectionListener(timeZoneClock3);
		
	}
	
	private SelectionListener timeZoneListener(final ClockWidget clockWidget) {
		return new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent event) {
				final String id = timeZones.getText();
				clockWidget.setZone(ZoneId.of(id));
				clockWidget.redraw();
			}

			@Override
			public void widgetSelected(SelectionEvent event) {
				clockWidget.setZone(ZoneId.systemDefault());
				clockWidget.redraw(); 
			}
		};
	}

	@Override
	public void setFocus() {
	}
}
