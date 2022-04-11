package com.ui.plugin.clock.views;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import javax.inject.Named;
import org.eclipse.e4.ui.services.IServiceConstants;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;
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

public class TimeZoneTableView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "com.ui.plugin.clock.TimeZoneTableView";

	private TableViewer tableViewer;

	@Inject
	@Optional
	private ESelectionService selectionService;

	@Override
	public void createPartControl(Composite parent) {
	}

	@PostConstruct
	public void create(Composite parent) {
		final TableViewer tableViewer = new TableViewer(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		tableViewer.getTable().setHeaderVisible(true);
		tableViewer.setContentProvider(ArrayContentProvider.getInstance());

		final TimeZoneIDColumn idColumn = new TimeZoneIDColumn();
		final TimeZoneNameColumn nameColumn = new TimeZoneNameColumn();
		final TimeZoneOffsetColumn offsetColumn = new TimeZoneOffsetColumn();
		TimeZoneSummerTimeColumn summerTimeColumn = new TimeZoneSummerTimeColumn();

		idColumn.addColumnTo(tableViewer);
		nameColumn.addColumnTo(tableViewer);
		offsetColumn.addColumnTo(tableViewer);
		summerTimeColumn.addColumnTo(tableViewer);

		final Object[] array = ZoneId.getAvailableZoneIds().stream().map(ZoneId::of).toArray();

		tableViewer.setInput(array);

		this.tableViewer = tableViewer;
	}

	@Focus
	public void focus() {
		this.tableViewer.getControl().setFocus();
	}

	@Override
	public void setFocus() {
	}
	
	public boolean hasTableViewer() {
		return Objects.nonNull(this.tableViewer);
	}

	@Inject
	@Optional
	public void setTimeZone(@Named(IServiceConstants.ACTIVE_SELECTION) ZoneId timeZone) {
		if (!Objects.nonNull(timeZone) && !this.hasTableViewer())
			return;
		this.tableViewer.setSelection(new StructuredSelection(timeZone));
		this.tableViewer.reveal(timeZone);

	}

	public abstract class TimeZoneColumn extends ColumnLabelProvider {
		public abstract String getText(Object element);

		public abstract String getTitle();

		public int getWidth() {
			return 250;
		}

		public int getAlignment() {
			return SWT.LEFT;
		}

		public TableViewerColumn addColumnTo(TableViewer viewer) {
			final TableViewerColumn tableViewerColumn = new TableViewerColumn(viewer, SWT.NONE);
			final TableColumn column = tableViewerColumn.getColumn();
			column.setMoveable(true);
			column.setResizable(true);
			column.setText(this.getTitle());
			column.setWidth(this.getWidth());
			column.setAlignment(this.getAlignment());
			tableViewerColumn.setLabelProvider(this);
			return tableViewerColumn;
		}
	}

	public class TimeZoneOffsetColumn extends TimeZoneColumn {
		public String getText(Object element) {
			if (element instanceof ZoneId) {

				final ZoneId zone = ((ZoneId) element);
				final ZoneOffset offset = ZonedDateTime.now(zone).getOffset();

				return offset.toString();
			} else {
				return "";
			}
		}

		public String getTitle() {
			return "Offset";
		}
	}

	public class TimeZoneNameColumn extends TimeZoneColumn {
		public String getText(Object element) {
			if (element instanceof ZoneId) {
				return ((ZoneId) element).getDisplayName(TextStyle.FULL, Locale.getDefault());
			} else {
				return "";
			}
		}

		public String getTitle() {
			return "Display Name";
		}
	}

	public class TimeZoneSummerTimeColumn extends TimeZoneColumn {
		public String getText(Object element) {
			if (element instanceof ZoneId) {
				final ZoneId zone = ((ZoneId) element);
				final Boolean summer = TimeZone.getTimeZone(zone).useDaylightTime();
				return summer.toString();
			} else {
				return "";
			}
		}

		public String getTitle() {
			return "Summer Time";
		}
	}

	public class TimeZoneIDColumn extends TimeZoneColumn {
		public String getText(Object element) {
			if (element instanceof ZoneId) {
				return ((ZoneId) element).getId();
			} else {
				return "";
			}
		}

		public String getTitle() {
			return "ID";
		}
	}
}
