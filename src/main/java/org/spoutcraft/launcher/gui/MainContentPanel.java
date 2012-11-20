package org.spoutcraft.launcher.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

public class MainContentPanel extends JComponent {
	public Image image;
	public Image background;
	
    public MainContentPanel() {
        try {
			this.image = ImageIO.read(getClass().getResource("/org/spoutcraft/launcher/background/background.png"));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        updateBackground();
    }
    
    public void updateBackground() {
    	try {
    		Robot rbt = new Robot();
    		Toolkit tk = Toolkit.getDefaultToolkit();
    		Dimension dim = tk.getScreenSize();
    		background = rbt.createScreenCapture(
    				new Rectangle(0,0,(int)dim.getWidth(),(int)dim.getHeight()));
    		
    	} catch (Exception e) {
			// TODO: handle exception
    		e.printStackTrace();
		}
    }
    
    @Override
    protected void paintComponent(Graphics g) {
    	Point pos = this.getLocationOnScreen( );
        Point offset = new Point(-pos.x,-pos.y);
    	g.drawImage(background,offset.x,offset.y,null);
    	g.drawImage(image, 0,0, this);
    }
}