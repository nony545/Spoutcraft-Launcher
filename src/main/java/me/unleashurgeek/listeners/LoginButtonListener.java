package me.unleashurgeek.listeners;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.imageio.ImageIO;

import org.spoutcraft.launcher.gui.ImageButton;

public class LoginButtonListener implements MouseListener {

	ImageButton button;
	
	public LoginButtonListener(ImageButton button)
	{
		this.button = button;
	}
	
	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		try {
			button.image = ImageIO.read(getClass().getResource("/org/spoutcraft/launcher/LaunchButtons/loginHover.png"));
			button.repaint();
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		try {
			button.image = ImageIO.read(getClass().getResource("/org/spoutcraft/launcher/LaunchButtons/login.png"));
			button.repaint();
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
