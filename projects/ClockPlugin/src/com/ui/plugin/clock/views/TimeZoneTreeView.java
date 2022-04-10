package com.ui.plugin.clock.views;

import java.net.URL;
import java.util.Locale;
import java.time.*;
import java.time.format.TextStyle;
import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.part.ViewPart;

import com.ui.plugin.clock.widget.ClockWidget;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;

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

	@Inject
	private ISharedImages images;

	@Override
	public void createPartControl(Composite parent) {
	}

	@PostConstruct
	public void create(Composite parent) {
		final TreeViewer treeViewer = new TreeViewer(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);

		treeViewer.setContentProvider(new TimeZoneContentProvider());
		treeViewer.setInput(new Object[] { TimeZoneView.getTimeZones() });

		treeViewer.setLabelProvider(TimeZoneTreeView.delegatingStyled(parent, this));
		treeViewer.setData("REVERSE", Boolean.TRUE);
		treeViewer.setComparator(new TimeZoneViewerComparator());
		treeViewer.setExpandPreCheckFilters(true);
		treeViewer.setFilters(new ViewerFilter[] { new TimeZoneViewerFilter("GMT") });

		treeViewer.addDoubleClickListener(TimeZoneTreeView.doubleClickListener());

		this.treeViewer = treeViewer;
	}

	@Focus
	public void focus() {

		this.treeViewer.getControl().setFocus();
	}

	private static DelegatingStyledCellLabelProvider delegatingStyled(final Composite parent,
			final TimeZoneTreeView that) {

		final ResourceManager rm = JFaceResources.getResources();
		final LocalResourceManager lrm = new LocalResourceManager(rm, parent);

		final ImageRegistry ir = new ImageRegistry(lrm);
		final URL sample = that.getClass().getResource("/icons/sample.gif");
		ir.put("sample", ImageDescriptor.createFromURL(sample));

		final FontRegistry fr = JFaceResources.getFontRegistry();

		final TimeZoneLabelProvider labelProvider = new TimeZoneLabelProvider(that.images, ir, fr);

		return new DelegatingStyledCellLabelProvider(labelProvider);

	}

	private static IDoubleClickListener doubleClickListener() {

		return event -> {
			final Viewer viewer = event.getViewer();
			final Shell shell = viewer.getControl().getShell();
			final ISelection sel = viewer.getSelection();

			final Supplier<Object> selection = () -> {

				final boolean selectionInstance = sel instanceof IStructuredSelection;

				if (!selectionInstance || sel.isEmpty())
					return null;

				return ((IStructuredSelection) sel).getFirstElement();

			};

			final Object selectedValue = selection.get();

			if (selectedValue instanceof ZoneId) {
				final ZoneId timeZone = (ZoneId) selectedValue;
				MessageDialog.openInformation(shell, timeZone.getId(), timeZone.toString());
			}

			MessageDialog.openInformation(shell, "Double click", "Double click detected");
		};

	}

	public class TimeZoneDialog extends MessageDialog {
		private ZoneId timeZone;

		public TimeZoneDialog(Shell parentShell, ZoneId timeZone) {
			super(parentShell, timeZone.getId(), null, "Time Zone " + timeZone.getId(), INFORMATION,
					new String[] { IDialogConstants.OK_LABEL }, 0);
			this.timeZone = timeZone;
		}
		
		protected Control createCustomArea(Composite parent) {
			  final ClockWidget clock = ClockWidget.builder()
					  .parent(parent)
					  .style(SWT.NONE)
					  .zone(this.timeZone)
					  .color(new RGB(128,255,0))
					  .build();
			  return parent;
			}

		
	}

	public class TimeZoneContentProvider implements ITreeContentProvider {
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

	public static class TimeZoneLabelProvider extends LabelProvider implements IStyledLabelProvider, IFontProvider {

		private final ISharedImages images;
		private final FontRegistry fr;

		private ImageRegistry ir;

		public TimeZoneLabelProvider(ISharedImages images, ImageRegistry ir, FontRegistry fr) {
			this.images = images;
			this.ir = ir;
			this.fr = fr;
		}

		public Font getFont(Object element) {
			final Font italic = this.fr.getItalic(JFaceResources.DEFAULT_FONT);
			return italic;
		}

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

		public StyledString getStyledText(Object element) {
			final String text = getText(element);
			final StyledString styledString = new StyledString(text);
			final boolean instance = element instanceof ZoneId;

			if (instance) {
				final ZoneId zone = (ZoneId) element;
				final ZoneOffset offset = ZonedDateTime.now(zone).getOffset();
				styledString.append(" (" + offset + ")", StyledString.DECORATIONS_STYLER);
			}

			return styledString;
		}

		public Image getImage(Object element) {
			if (element instanceof Map.Entry)
				return this.images.getImage(ISharedImages.IMG_OBJ_FOLDER);

			if (element instanceof ZoneId)
				return this.ir.get("sample");

			return super.getImage(element);
		}
	}

	public class TimeZoneViewerComparator extends ViewerComparator {

		public int compare(Viewer viewer, Object z1, Object z2) {

			final Supplier<Integer> compareTo = () -> {

				final boolean z1Instance = z1 instanceof ZoneId;
				final boolean z2Instance = z2 instanceof ZoneId;

				if (!z1Instance && !z2Instance)
					return z1.toString().compareTo(z2.toString());

				final Instant now = Instant.now();
				final ZonedDateTime zdt1 = ZonedDateTime.ofInstant(now, (ZoneId) z1);
				final ZonedDateTime zdt2 = ZonedDateTime.ofInstant(now, (ZoneId) z2);
				return zdt1.compareTo(zdt2);
			};

			final int compare = compareTo.get();
			final String data = String.valueOf(viewer.getData("REVERSE"));
			final boolean reverse = Boolean.parseBoolean(data);

			if (reverse)
				return -compare;

			return compare;

		}
	}

	public class TimeZoneViewerFilter extends ViewerFilter {
		private String pattern;

		public TimeZoneViewerFilter(String pattern) {
			this.pattern = pattern;
		}

		public boolean select(Viewer v, Object parent, Object element) {
			final boolean zoneInstance = element instanceof ZoneId;

			if (!zoneInstance)
				return true;

			final ZoneId zone = (ZoneId) element;
			final Locale locale = Locale.getDefault();

			final String displayName = zone.getDisplayName(TextStyle.FULL, locale);

			return displayName.contains(this.pattern);

		}
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}
}
