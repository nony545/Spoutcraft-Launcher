package me.unleashurgeek.listeners;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;

public class JFrameListener implements MouseListener, MouseMotionListener {
	
	private JFrame jFrame;
	
	private Point start_drag = new Point();
	private Point start_loc = new Point();
	
	public JFrameListener(JFrame jFrame) {
		this.jFrame = jFrame;
	}
	
	Point getScreenLocation(MouseEvent e) {
        Point cursor = e.getPoint();
        Point target_location = jFrame.getLocationOnScreen();
        return new Point((int) (target_location.getX() + cursor.getX()),
            (int) (target_location.getY() + cursor.getY()));
      }
	
	
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		start_drag = getScreenLocation(e);
		start_loc = jFrame.getLocation();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mouseDragged(MouseEvent e) {
		Point current = this.getScreenLocation(e);
	    Point offset = new Point((int) current.getX() - (int) start_drag.getX(),
	        (int) current.getY() - (int) start_drag.getY());

	    Point new_location = new Point(
	        (int) (this.start_loc.getX() + offset.getX()), (int) (this.start_loc
	            .getY() + offset.getY()));
	        jFrame.setLocation(new_location); 
	}


	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
