package com.ui.plugin.clock.views;

import java.time.ZoneId;

import javax.annotation.PostConstruct;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
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

	@Override
	public void createPartControl(Composite parent) {
	}

	@PostConstruct
	public void create(Composite parent) {
		final TableViewer tableViewer = new TableViewer(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		tableViewer.getTable().setHeaderVisible(true);
		tableViewer.setContentProvider(ArrayContentProvider.getInstance());
		
		final TimeZoneIDColumn idColumn = new TimeZoneIDColumn();
		
		idColumn.addColumnTo(tableViewer);

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
			column.setText(getTitle());
			column.setWidth(getWidth());
			column.setAlignment(getAlignment());
			tableViewerColumn.setLabelProvider(this);
			return tableViewerColumn;
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
