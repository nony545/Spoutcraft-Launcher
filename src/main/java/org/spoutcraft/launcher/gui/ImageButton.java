package org.spoutcraft.launcher.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;

@SuppressWarnings("serial")
public class ImageButton extends JButton {
	public Image image;
	
	public ImageButton(URL url)
	{
		try {
			image = ImageIO.read(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (image != null) this.setSize(new Dimension(image.getWidth(this), image.getHeight(this)));
		setOptions();
	}
	
	public ImageButton(Image image)
	{
		this.image = image;
		if (image != null) this.setSize(new Dimension(image.getWidth(this), image.getHeight(this)));
		setOptions();
	}
	
	public ImageButton(String url)
	{
		try {
			image = ImageIO.read(getClass().getResource(url));
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (image != null) this.setSize(new Dimension(image.getWidth(this), image.getHeight(this)));
		setOptions();
	}
	
	public ImageButton(ImageIcon modPackLogo) {
		this.image = modPackLogo.getImage();
		if (image != null) this.setSize(new Dimension(image.getWidth(this), image.getHeight(this)));
		setOptions();
	}

	private void setOptions()
	{
		this.setContentAreaFilled(false);
		this.setBorderPainted(false);
		this.setRolloverEnabled(false);
		this.setFocusPainted(false);
	}
	
	@Override
	protected void paintComponent(Graphics g)  
	  {  
	    super.paintComponent(g);  
	    if(image != null) g.drawImage(image, 0,0,this);  
	  } 
}
