package org.jd.sa.resource;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.font.FontRenderContext;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultTreeCellRenderer;

public class IconLoader {

	private static JTree _tree;
	private static Font _currentFont;
	private static Font _defaultFont;

	public enum Icons {
		DISK, FILE, FOLDER_OPEN, FOLDER_CLOSE, REFRESH_BTN, ANAYZE_BTN, SORT_ASC_BTN, SORT_DESC_BTN, FRAME_ICON
	}

	public static void initialize(JTree tree) {
		_tree = tree;
		_currentFont = tree.getFont();
		_defaultFont = UIManager.getFont("Tree.font");
	}

	public static final Icon getIcon(Icons iconType) {
		Icon icon = null;
		switch (iconType) {
		case DISK:
			icon = getTreeIcon("icons/drive.png");
			break;
		case FILE:
			icon = getTreeIcon("icons/file.png");
			break;
		case FOLDER_OPEN:
			icon = getTreeIcon("icons/f_opened.png");
			break;
		case FOLDER_CLOSE:
			icon = getTreeIcon("icons/f_closed.png");
			break;
		case REFRESH_BTN:
			icon = getButtonIcon("icons/refresh.png");
			break;
		case ANAYZE_BTN:
			icon = getButtonIcon("icons/analyze.png");
			break;
		case SORT_ASC_BTN:
			icon = getButtonIcon("icons/sort_asc.png");
			break;
		case SORT_DESC_BTN:
			icon = getButtonIcon("icons/sort_desc.png");
			break;
		default:
			break;
		}
		return icon;
	}

	public static final Image getIconImage(Icons iconType) {
		Image iconImage = null;
		switch (iconType) {
		case FRAME_ICON:
			iconImage = getFrameIcon("icons/scanning.png");
			break;
		default:
			break;
		}
		return iconImage;
	}

	private static Icon getButtonIcon(String imgPath) {

		try {
			return new ImageIcon(ImageIO.read(Resources.class
					.getResource(imgPath)));
		} catch (IOException e) {
			return null;
		}
	}

	private static Icon getTreeIcon(String imgPath) {

		double scaleFactor = (double) _currentFont.getSize2D()
				/ _defaultFont.getSize2D();

		try {
			Image img = ImageIO.read(Resources.class.getResource(imgPath));
			Icon icon = new ImageIcon(img);
			int width = icon.getIconWidth();
			int height = icon.getIconHeight();

			width = (int) Math.ceil(width * scaleFactor);
			height = (int) Math.ceil(height * scaleFactor);

			BufferedImage image = new BufferedImage(width, height,
					BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2 = image.createGraphics();
			g2.scale(scaleFactor, scaleFactor);
			icon.paintIcon(_tree, g2, 0, 0);
			g2.dispose();

			return icon;
		} catch (IOException e) {
			return null;
		}
	}

	public static final void update(DefaultTreeCellRenderer renderer) {
		Collection<Integer> iconSizes = Arrays.asList(renderer.getOpenIcon()
				.getIconHeight(), renderer.getClosedIcon().getIconHeight(),
				renderer.getLeafIcon().getIconHeight());

		// Convert points to pixels
		Point2D p = new Point2D.Float(0, _currentFont.getSize2D());
		FontRenderContext context = _tree.getFontMetrics(_currentFont)
				.getFontRenderContext();
		context.getTransform().transform(p, p);
		int fontSizeInPixels = (int) Math.ceil(p.getY());

		_tree.setRowHeight(Math.max(fontSizeInPixels,
				Collections.max(iconSizes) + 2));
	}

	private static Image getFrameIcon(String imgPath) {
		try {
			return ImageIO.read(Resources.class.getResource(imgPath));
		} catch (IOException e) {
			return null;
		}
	}
}
