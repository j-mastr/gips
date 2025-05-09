package org.emoflon.gips.eclipse.view;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IToolTipProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.emoflon.gips.eclipse.TracePlugin;
import org.emoflon.gips.eclipse.service.ProjectContext;
import org.emoflon.gips.eclipse.trace.TraceModelLink;
import org.emoflon.gips.eclipse.view.model.ContextNode;
import org.emoflon.gips.eclipse.view.model.LinkModelNode;
import org.emoflon.gips.eclipse.view.model.ValueNode;

final class TraceLabelProvider extends LabelProvider implements ILabelProvider, IToolTipProvider {

	private Image projectImage;
	private Image projectClosedImage;
	private Image rightImage;
	private Image leftImage;

	public TraceLabelProvider() {
		ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
		this.projectImage = sharedImages.getImage(org.eclipse.ui.ide.IDE.SharedImages.IMG_OBJ_PROJECT);
		this.projectClosedImage = sharedImages.getImage(org.eclipse.ui.ide.IDE.SharedImages.IMG_OBJ_PROJECT_CLOSED);
		this.rightImage = sharedImages.getImage(ISharedImages.IMG_TOOL_FORWARD);
		this.leftImage = sharedImages.getImage(ISharedImages.IMG_TOOL_BACK);
	}

	@Override
	public Image getImage(Object element) {
		if (element instanceof ContextNode node)
			return node.hasChilds() ? projectImage : projectClosedImage;

		if (element instanceof LinkModelNode node) {
			return switch (node.getDirection()) {
			case FORWARD -> rightImage;
			case BACKWARD -> leftImage;
			default -> null;
			};
		}

		return null;
	}

	@Override
	public String getText(Object element) {
		String name = element != null ? element.toString() : "???";

		if (element instanceof LinkModelNode node) {
			ProjectContext context = TracePlugin.getInstance().getContextManager().getContext(node.getContextId());
			return switch (node.getDirection()) {
			case FORWARD -> {
				TraceModelLink link = context.getModelLink(node.getParent().getModelId(), node.getModelId());
				yield "Creates '" + name + " (maps " + link.getSourceNodeIds().size() + " to "
						+ link.getTargetNodeIds().size() + " nodes)";
			}
			case BACKWARD -> {
				TraceModelLink link = context.getModelLink(node.getModelId(), node.getParent().getModelId());
				yield "Created by '" + name + " (maps " + link.getSourceNodeIds().size() + " to "
						+ link.getTargetNodeIds().size() + " nodes)";
			}
			};
		} else if (element instanceof ValueNode node) {
			ProjectContext context = TracePlugin.getInstance().getContextManager().getContext(node.getContextId());
			var values = context.getModelValues(node.getModelId());
			return "Stored values: " + values.size();
		}

		return name;
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	public String getToolTipText(Object element) {
		return "Tooltip (" + element + ")";
	}

//		@Override
//		public Point getToolTipShift(Object object) {
//			return new Point(5, 5);
//		}
//
//		@Override
//		public int getToolTipDisplayDelayTime(Object object) {
//			return 2000;
//		}
//
//		@Override
//		public int getToolTipTimeDisplayed(Object object) {
//			return 5000;
//		}
//
//		@Override
//		public void update(ViewerCell cell) {
//			cell.setText(cell.getElement().toString());
//		}

}