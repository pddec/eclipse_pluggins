package com.ui.plugin.clock.views;

import java.time.ZoneId;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.part.ViewPart;

import com.ui.plugin.clock.widget.ClockWidget;

public class TimeZoneView extends ViewPart {

	public void createPartControl(Composite parent) {
		final Map<String, Set<ZoneId>> timeZones = TimeZoneView.getTimeZones();

		final CTabFolder tabs = new CTabFolder(parent, SWT.BOTTOM);

		timeZones.forEach(TimeZoneView.eachRegionZone(tabs));

		tabs.setSelection(0);
	}

	public static Map<String, Set<ZoneId>> getTimeZones() {
		final Supplier<Set<ZoneId>> sortedZones = () -> new TreeSet<>(TimeZoneView.comparator());

		final Function<ZoneId, String> zoneID = zone -> zone.getId().split("/")[0];

		return ZoneId.getAvailableZoneIds().parallelStream() // stream
				.filter(s -> s.contains("/")) // with / in them
				.map(ZoneId::of)
				.collect(Collectors.groupingBy(zoneID, TreeMap::new, Collectors.toCollection(sortedZones)));
	}

	private static BiConsumer<String, Set<ZoneId>> eachRegionZone(final CTabFolder tabs) {

		return (region, zones) -> {
			final CTabItem item = new CTabItem(tabs, SWT.NONE);
			
			final ScrolledComposite scrolled = new ScrolledComposite(tabs, SWT.H_SCROLL | SWT.V_SCROLL);

			final Composite clocks = new Composite(scrolled, SWT.NONE);
			scrolled.setContent(clocks);
			clocks.setLayout(new RowLayout());

			final Point size = clocks.computeSize(SWT.DEFAULT, SWT.DEFAULT);
			
			scrolled.setMinSize(size);
			scrolled.setExpandHorizontal(true);
			scrolled.setExpandVertical(true);
			
			final Color systemColor = clocks.getDisplay()
					.getSystemColor(SWT.COLOR_LIST_BACKGROUND);
			
			clocks.setBackground(systemColor);
			
			item.setControl(scrolled);
			item.setText(region);

			final RGB rgb = new RGB(128, 128, 128);
			zones.forEach(TimeZoneView.eachZone(rgb, clocks));
		};
	}
	
	private static Consumer<ZoneId> eachZone(final RGB rgb, final Composite clocks){
		return zone -> {
			
			final Group group = new Group(clocks, SWT.SHADOW_ETCHED_IN);
			group.setText(zone.getId().split("/")[1]);
			group.setLayout(new FillLayout());

			final ClockWidget clock = ClockWidget.builder()
					.parent(group)
					.style(SWT.NONE)
					.color(rgb)
					.zone(zone)
					.build();
			
			clock.initDisposeListener();
			clock.initPaintListener();
		};
		
	}


	private static Comparator<ZoneId> comparator() {

		return new Comparator<ZoneId>() {

			@Override
			public int compare(final ZoneId z1, final ZoneId z2) {
				return z1.getId().compareTo(z2.getId());
			}
		};

	}

	@Override
	public void setFocus() {

	}
}
