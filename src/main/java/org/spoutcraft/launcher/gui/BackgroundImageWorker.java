package org.spoutcraft.launcher.gui;

import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import org.spoutcraft.launcher.MirrorUtils;
import org.spoutcraft.launcher.async.Download;

public class BackgroundImageWorker extends SwingWorker<Object, Object> {

	private static final String	SPLASH_URL				= "http://urcraft.com/technic/splash/index.php";
	private static final String	TEKKIT_URL_01			= "http://technic.freeworldsgaming.com/tekkit-001.jpg";
	private static final String	TEKKIT_URL_02			= "http://technicpack.net/wp-content/uploads/2011/12/tekkitaltsmall.png";
	private static final int		IMAGE_CYCLE_TIME	= 24 * 60 * 60 * 1000;
	private File								backgroundImage;
	private String							url;

	public BackgroundImageWorker(File backgroundImage, String url) {
		this.backgroundImage = backgroundImage;
		this.url = url;
	}

	@Override
	protected Object doInBackground() {
		try {
			if (!backgroundImage.exists() || backgroundImage.length() < 10 * 1024 || System.currentTimeMillis() - backgroundImage.lastModified() > IMAGE_CYCLE_TIME) {
				//String url;
				if (!MirrorUtils.isAddressReachable(url)) {
					if (!MirrorUtils.isAddressReachable(TEKKIT_URL_01)) {
						url = TEKKIT_URL_02;
					} else {
						url = TEKKIT_URL_01;
					}
				} else {
					url = SPLASH_URL;
				}

				Download download = new Download(url, backgroundImage.getPath());
				download.run();

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
