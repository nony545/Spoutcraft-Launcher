package org.spoutcraft.launcher.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JProgressBar;

public class ProgressBar extends JProgressBar {
	
	private final TransparentComponent transparency = new TransparentComponent(this, false);
	
	public ProgressBar()
	{
		setFocusable(false);
		setOpaque(false);
	}
	
	@Override
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) transparency.setup(g);

		// Draw bar
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, getWidth(), getHeight());

		//Draw progress
		g2d.setColor(Color.ORANGE);
		int x = (int) (getWidth() * getPercentComplete());
		g2d.fillRect(0, 0, x, getHeight());

		transparency.cleanup(g2d);
		g2d = (Graphics2D) g;

		if (this.isStringPainted()) {
		g2d.setFont(getFont());
		g2d.setColor(Color.BLACK);
		g2d.drawString(this.getString(), 58, 590);
		}
	}
}
