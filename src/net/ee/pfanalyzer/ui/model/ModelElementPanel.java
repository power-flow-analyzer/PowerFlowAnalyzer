package net.ee.pfanalyzer.ui.model;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;

import net.ee.pfanalyzer.model.AbstractNetworkElement;
import net.ee.pfanalyzer.model.CombinedNetworkElement;
import net.ee.pfanalyzer.model.NetworkFlag;
import net.ee.pfanalyzer.model.property.ChildDataProperty;
import net.ee.pfanalyzer.model.property.ElementProperty;
import net.ee.pfanalyzer.model.property.ElementPropertyList;
import net.ee.pfanalyzer.preferences.IPreferenceConstants;
import net.ee.pfanalyzer.ui.NetworkElementSelectionManager;

public class ModelElementPanel extends JPanel {

	private ElementPanelController controller;
	private Object data;
	private Box elementContainer;
	private Group currentGroupContainer;
	private JLabel titleLabel;
	private final static Cursor DEFAULT_CURSOR = new Cursor(Cursor.DEFAULT_CURSOR);
	private final static Cursor HAND_CURSOR = new Cursor(Cursor.HAND_CURSOR);
	private Font titleFont = new Font(null, Font.BOLD, 16);
//	private Font groupFont = new Font(null, Font.BOLD, 12);
	
	protected ModelElementPanel(ElementPanelController controller) {
		super(new BorderLayout());
		this.controller = controller;
		
		titleLabel = new JLabel("", SwingConstants.CENTER);
		titleLabel.setFont(titleFont);
		titleLabel.setBorder(new EmptyBorder(5, 10, 5, 10));
		add(titleLabel, BorderLayout.NORTH);
		
		elementContainer = Box.createVerticalBox();
		elementContainer.setBorder(new EmptyBorder(0, 10, 10, 10));
		add(new JScrollPane(elementContainer), BorderLayout.CENTER);
		
		setOpaque(false);
		elementContainer.setOpaque(false);
	}
	
	public ElementPanelController getController() {
		return controller;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
	
	protected void setTitle(String title) {
		titleLabel.setText(title);
	}
	
	protected Group addElementGroup(String name) {
//		JLabel groupLabel = new JLabel(name);
//		groupLabel.setFont(groupFont);
		elementContainer.add(Box.createVerticalStrut(10));
//		elementContainer.add(groupLabel);
//		currentGroupContainer = Box.createVerticalBox();
		currentGroupContainer = new Group(name);
//		currentGroupContainer.setBorder(new EmptyBorder(0, 30, 0, 0));
//		TitledBorder border = new TitledBorder(name);
//		border.setTitleColor(Color.BLACK);
//		border.setTitleFont(groupFont);
//		currentGroupContainer.setBorder(border);
		elementContainer.add(currentGroupContainer);
		return currentGroupContainer;
	}
	
	protected void finishLayout() {
//		elementContainer.add(Box.createVerticalGlue());
//		elementContainer.add(Box.createVerticalGlue());
//		elementContainer.add(Box.createVerticalGlue());
	}
	
	protected void addElementLink(CombinedNetworkElement<?> element) {
		currentGroupContainer.addElementLink(element);
	}
	
	protected void addElementLink(AbstractNetworkElement childData) {
		currentGroupContainer.addElementLink(childData);
	}
	
	protected void removeAllElements() {
		elementContainer.removeAll();
	}
	
	protected void addProperties(ElementPropertyList properties, Group propGroup) {
		for (ElementProperty prop : properties.getProperties()) {
			if(showProperty(prop) == false)
				continue;
			JLabel label = new JLabel(prop.getLabel() + ": ");
			propGroup.add(label);
			if(prop instanceof ChildDataProperty) {
//				propGroup.addElementLink(element);
			} else {
				JLabel valueLabel = new JLabel(prop.getTextValue());
				propGroup.add(valueLabel);
			}
		}
	}
	
	private boolean showProperty(ElementProperty p) {
		return getController().getViewerProperty(
				IPreferenceConstants.PROPERTY_UI_SHOW_MODEL_PREFIX + p.getPropertyName(), true);
	}
	
	protected void addFlags(AbstractNetworkElement childData, Group flagGroup) {
		for (NetworkFlag flag : childData.getFlags()) {
			boolean isCorrect = flag.isCorrect();
			JLabel label = new JLabel(flag.getLabel());
			if(isCorrect == false)
				label.setForeground(Color.RED);
			flagGroup.add(label);
			double percentage = flag.getPercentage();
			if(percentage > -1) {
				ProgressBar progressBar = new ProgressBar((int) Math.floor(percentage), isCorrect);
				JPanel resizer = new JPanel(new BorderLayout());
				resizer.add(progressBar, BorderLayout.CENTER);
				resizer.add(new JLabel(" " + (int) Math.floor(percentage) + "% "), BorderLayout.EAST);
				flagGroup.add(resizer);
			} else
				flagGroup.add(new JLabel("unknown"));
		}
	}
	
	class ProgressBar extends JComponent {
		final int BAR_HEIGHT = 20;
		double value;
		boolean isCorrect;
		ProgressBar(double value, boolean isCorrect) {
			this.value = value;
			this.isCorrect = isCorrect;
		}
		
		protected void paintComponent(Graphics g) {
			int y = getHeight() / 2 - BAR_HEIGHT / 2;
			g.setColor(Color.WHITE);
			g.fillRect(0, y, getWidth() - 1, BAR_HEIGHT);
			int barWidth = Math.min((int) (getWidth() * value / 100), getWidth());
			if(isCorrect)
				g.setColor(Color.GREEN.darker());
			else
				g.setColor(Color.RED);
			g.fillRect(0, y, barWidth - 1, BAR_HEIGHT);
			g.setColor(Color.BLACK);
			g.drawRect(0, y, getWidth() - 1, BAR_HEIGHT);
			String text = (int) value + "%";
			Rectangle2D rect = g.getFontMetrics(g.getFont()).getStringBounds(text, g);
			int x = (int) (getWidth() / 2 - rect.getWidth() / 2);
			g.drawString(text, x, (int) (y + rect.getHeight() + 1));
		}
	}
	
	class HyperLinkLabel extends JLabel {
		
		private Object networkElement;
		
		HyperLinkLabel(final String text, Object element) {
			this(text, element, Color.BLUE);
		}
		
		HyperLinkLabel(final String text, Object element, Color foreground) {
			super("<html>" + text);
			this.networkElement = element;
			setForeground(foreground);
			MouseInputListener mouseListener = new MouseInputAdapter() {
				@Override
				public void mouseEntered(MouseEvent e) {
					setText("<html><u>" + text);
					setCursor(HAND_CURSOR);
				}
				@Override
				public void mouseExited(MouseEvent e) {
					setText("<html>" + text);
					setCursor(DEFAULT_CURSOR);
				}
				@Override
				public void mouseClicked(MouseEvent e) {
					NetworkElementSelectionManager.getInstance().selectionChanged(networkElement);
				}
			};
			addMouseListener(mouseListener);
		}
	}
	
	class Group extends JPanel {
		
		Font groupFont = new Font(null, Font.BOLD, 12);
		TitledBorder border;
		
		Group(String title) {
			super(new GridLayout(0, 2));
			border = new TitledBorder(title);
			border.setTitleColor(Color.BLACK);
			border.setTitleFont(groupFont);
			setBorder(border);
		}
		
		protected void addElementLink(CombinedNetworkElement<?> element) {
			Color foreground = Color.BLUE;
			if(element.isCorrect() == false)
				foreground = Color.RED;
			add(new HyperLinkLabel(element.getLabel(), element, foreground));
		}
		
		protected void addElementLink(AbstractNetworkElement childData) {
			Color foreground = Color.BLUE;
			if(childData.isCorrect() == false)
				foreground = Color.RED;
			if(childData.isActive() == false)
				foreground = Color.DARK_GRAY;
			add(new HyperLinkLabel(childData.getDisplayName(), childData, foreground));
		}
		
		public void removeFlags() {
			removeAll();
			setLayout(new GridLayout(0, 2));
			revalidate();
		}
//		
//		protected void addElementLink(ChildData childData, Color foreground) {
//			add(new HyperLinkLabel(childData.getDisplayName(), childData, foreground));
//		}
	}
}

