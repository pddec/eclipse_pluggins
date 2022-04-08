package com.ui.plugin.clock.views;

import java.time.ZoneId;
import java.util.Collection;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
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

public class TimeZoneTreeView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "TimeZoneTreeView";

	private TreeViewer treeViewer;
	
	@Override
	public void createPartControl(Composite parent) {}

	@PostConstruct
	public void create(Composite parent) {
		final TreeViewer treeViewer = new TreeViewer(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
		treeViewer.setLabelProvider(new TimeZoneLabelProvider());
		treeViewer.setContentProvider(new TimeZoneContentProvider());
		treeViewer.setInput(new Object[]{TimeZoneView.getTimeZones()}); 
		
		this.treeViewer = treeViewer;
	}

	@Focus
	public void focus() {
	  this.treeViewer.getControl().setFocus();
	}
	
	public class TimeZoneContentProvider implements ITreeContentProvider{
		@Override
		@SuppressWarnings("rawtypes")
		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof Map)
				return ((Map) parentElement).entrySet().toArray();

			if (parentElement instanceof Map.Entry)
				return this.getChildren(((Map.Entry) parentElement).getValue());

			if (parentElement instanceof Collection)
				return ((Collection) parentElement).toArray();
			
			return new Object[0];

		}

		@Override
		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof Object[])
				return (Object[]) inputElement;

			return new Object[0];
		}

		@Override
		public Object getParent(Object arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		@SuppressWarnings("rawtypes")
		public boolean hasChildren(Object element) {
			if (element instanceof Map)
				return !((Map) element).isEmpty();
			if (element instanceof Map.Entry)
				return this.hasChildren(((Map.Entry) element).getValue());
			if (element instanceof Collection)
				return !((Collection) element).isEmpty();

			return false;
		}
		
	}

	public class TimeZoneLabelProvider extends LabelProvider  {
		@SuppressWarnings("rawtypes")
		public String getText(Object element) {
			if (element instanceof Map)
				return "Time Zones";

			if (element instanceof Map.Entry)
				return ((Map.Entry) element).getKey().toString();

			if (element instanceof ZoneId)
				return ((ZoneId) element).getId().split("/")[1];

			return "Unknown type: " + element.getClass();

		}

		
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}
}
