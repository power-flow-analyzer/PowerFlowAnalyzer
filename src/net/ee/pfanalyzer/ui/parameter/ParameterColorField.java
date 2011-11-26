package net.ee.pfanalyzer.ui.parameter;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;

import net.ee.pfanalyzer.model.data.NetworkParameter;
import net.ee.pfanalyzer.ui.viewer.network.Outline;

public class ParameterColorField extends ParameterValuePanel implements ActionListener {
	
	private final static int BORDER = 5;
	
	private JButton button;
	private Color color;
	
	public ParameterColorField(IParameterMasterElement element, NetworkParameter property, NetworkParameter propertyValue) {
		super(element, property, propertyValue);
		button.addActionListener(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(ignoreAction())
			return;
		Color newColor = JColorChooser.showDialog(this, "Select a color", color);
		if(newColor != null)
			color = newColor;
		repaint();
		NetworkParameter property = getMasterElement().getParameter(getPropertyID(), true);
		String value = getColorValue();
		String oldValue = property.getValue();
		property.setValue(value);
		fireValueChanged(oldValue, value);
		refresh();
	}
	
	@Override
	protected void createValuePanel() {
		button = new JButton() {
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				if(color != null) {
					g.setColor(color);
					g.fillRect(BORDER, BORDER, getWidth() - 2 * BORDER, getHeight() - 2 * BORDER);
					g.setColor(Color.BLACK);
					g.drawRect(BORDER, BORDER, getWidth() - 2 * BORDER, getHeight() - 2 * BORDER);
				}
			}
		};
	}
	
	public JButton getButton() {
		return button;
	}

	@Override
	protected JComponent getValuePanel() {
		return button;
	}
	
	private String getColorValue() {
		if(color != null)
			return color.getRed() + "," + color.getGreen() + "," + color.getBlue();
		return null;
	}

	@Override
	protected void setValue(String value) {
		color = Outline.parseColor(value);
	}
}