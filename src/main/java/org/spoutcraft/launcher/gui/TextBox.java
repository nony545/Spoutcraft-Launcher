package org.spoutcraft.launcher.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class TextBox extends JTextField implements FocusListener {
	private static final long serialVersionUID = 1L;
	protected final JLabel label;
	public TextBox(JComponent parent, String label) {
		this.label = new JLabel(label);
		addFocusListener(this);
		parent.add(this.label);
		this.setBackground(new Color(220, 220, 220));
		this.setBorder(new TextBorder(5, getBackground()));
	}
	
	@Override
	public void setFont(Font font) {
		super.setFont(font);
		if (label != null) {
			label.setFont(font);
		}
	}
	
	@Override
	public void setBounds(int x, int y, int w, int h) {
		super.setBounds(x, y, w, h);
		label.setBounds(x + 5, y + 3, w - 5, h - 5);
	}

	public void focusGained(FocusEvent e) {
		label.setVisible(false);
	}

	public void focusLost(FocusEvent e) {
		if (getText().length() == 0) {
			label.setVisible(true);
		}
	}
}
