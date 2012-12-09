package org.spoutcraft.launcher.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.spoutcraft.launcher.GameUpdater;
import org.spoutcraft.launcher.LibrariesYML;
import org.spoutcraft.launcher.MD5Utils;
import org.spoutcraft.launcher.Main;
import org.spoutcraft.launcher.MinecraftYML;
import org.spoutcraft.launcher.MirrorUtils;
import org.spoutcraft.launcher.PlatformUtils;
import org.spoutcraft.launcher.SettingsUtil;
import org.spoutcraft.launcher.Util;
import org.spoutcraft.launcher.async.DownloadListener;
import org.spoutcraft.launcher.exception.NoMirrorsAvailableException;
import org.spoutcraft.launcher.gui.LoginDialog.UserPasswordInformation;
import org.spoutcraft.launcher.modpacks.ModLibraryYML;
import org.spoutcraft.launcher.modpacks.ModPackListYML;
import org.spoutcraft.launcher.modpacks.ModPackUpdater;
import org.spoutcraft.launcher.modpacks.ModPackYML;

import me.unleashurgeek.listeners.ComponentMover;
import me.unleashurgeek.listeners.JFrameListener;
import me.unleashurgeek.listeners.LoginButtonListener;
import me.unleashurgeek.util.Fonts;

@SuppressWarnings("serial")
public class MainForm extends JFrame implements ActionListener, MouseListener, DownloadListener {
		// NEED BETTER MAIN BACKGROUND TRANSPARENCY
		MainContentPanel mainContentPane;
		
		// WindowsButtons (Close and Minimize)
		private WindowsButton closeButton 		= new WindowsButton("close");
		private WindowsButton minimizeButton 	= new WindowsButton("minimize");
		// Login/Play Button
		private   ImageButton loginButton		= new ImageButton("/org/spoutcraft/launcher/LaunchButtons/login.png");		
		// ModPack Selector
		private   ImageButton modLeft 			= new ImageButton("/org/spoutcraft/launcher/modLeft.png");
		private   ImageButton modRight 			= new ImageButton("/org/spoutcraft/launcher/modRight.png");
		private   ImageButton noMod = new ImageButton("/org/spoutcraft/launcher/background/no_mod.png");
		// Options Button
		private ImageButton optionsButton = new ImageButton("/org/spoutcraft/launcher/options.png");
		OptionDialog options = null;
		// login Dialog
		LoginDialog login = null;
		public String workingDir = PlatformUtils.getWorkingDirectory().getAbsolutePath();
		
		public static Fonts fonts;
		
		// Temp until I add the modpack code
		private ArrayList<ImageButton> modPacks = new ArrayList<ImageButton>();
		private  ProgressBar progressBar;
		
		private int currentModButton = 0;
		ArrayList<String> latestNews;
		
		static Point mouseDownCompCoords;
		
		public static UpdateDialog updateDialog;
		public static final ModPackUpdater gameUpdater = new ModPackUpdater();
		public boolean mcUpdate = false;
		public boolean spoutUpdate = false;
		public boolean modpackUpdate = false;
		public static String[]	values	= null;
		private int	success = LauncherFrame.ERROR_IN_LAUNCH;
		public static String pass = null;
		
		public static final ArrayList<String> newsTitleArray = new ArrayList<String>();
			
		public HashMap<String, UserPasswordInformation> usernames = new HashMap<String, UserPasswordInformation>();
		
		public MainForm()
		{
			loadLauncherData();
			
			MainForm.updateDialog = new UpdateDialog(this);
			gameUpdater.setListener(this);
			fonts = new Fonts();
			
			// Sets the Size of the window
			Dimension monitorSize = Toolkit.getDefaultToolkit().getScreenSize();
			this.setBounds((monitorSize.width - 910) / 2, (monitorSize.height - 620) / 2, 948, 646);
			// Disables windows bar and Icons
			this.setUndecorated(true);
			// Allows the JFrame to be dragged 
			//this.addMouseListener(new JFrameListener())
			
			
			// ContentPane to hold the JFrame's Content (Buttons, Labels, etc)
			mainContentPane = new MainContentPanel();
			mainContentPane.setBorder(new EmptyBorder(0, 0, 0, 0));
			this.setContentPane(mainContentPane);
			mainContentPane.setLayout(null);
			new ComponentMover(this, mainContentPane);
			
			//--------------------Minimize/Maximize Buttons----------------------
			closeButton.setLocation(920, 5);
			minimizeButton.setLocation(895, 5);
			
			
			//------------------------ModPack Selector-----------------------------\
			//modPack.setBounds(10, 228, 595, 282);
			getModLeft().setLocation(10, 228);
			getModRight().setLocation(570, 228);
			
			for (String item : ModPackListYML.modpackMap.keySet()) {
				if (!Main.isOffline || GameUpdater.canPlayOffline(item)) {
					ImageButton button = new ImageButton(ModPackListYML.getModPackLogo(item));
					button.setVisible(false);
					button.setName(item);
					button.setBounds(10, 228, 595, 282);
					button.addMouseListener(this);
					modPacks.add(button);
				}
			}
			this.noMod.addMouseListener(this);
			this.getModRight().addActionListener(this);
			this.getModRight().addMouseListener(this);
			this.getModLeft().addActionListener(this);
			this.getModLeft().addMouseListener(this);
			getModRight().setVisible(false);
			getModLeft().setVisible(false);
			
			//------------------------Login/Play Button---------------------------
			getLoginButton().setLocation(595, 555);
			getLoginButton().addMouseListener(new LoginButtonListener(getLoginButton()));
			getLoginButton().addActionListener(this);
			login = new LoginDialog(this);
			login.setVisible(false);
			
			//----------------------Progress Bar----------------------------------
			setProgressBar(new ProgressBar());
			getProgressBar().setBounds(51, 555, 430, 26);
			
			//---------------------------Options----------------------------------
			getOptionsButton().setLocation(504, 555);
			getOptionsButton().setFont(new Font("Arial", Font.PLAIN, 11));
			getOptionsButton().setOpaque(false);
			getOptionsButton().addActionListener(this);
			getOptionsButton().setEnabled(false);
			
			NewsPane newsPane = new NewsPane(this);
			newsPane.setBounds(615, 200, 948, 646);
			
			// Add the Components to the ContentPane
			
			mainContentPane.add(closeButton);
			mainContentPane.add(minimizeButton);
			mainContentPane.add(getLoginButton());
			mainContentPane.add(getProgressBar());
			mainContentPane.add(getOptionsButton());
			mainContentPane.add(newsPane);
			
			mainContentPane.add(getModLeft());
			mainContentPane.add(getModRight());
			mainContentPane.setComponentZOrder(getModLeft(), 0);
			mainContentPane.setComponentZOrder(getModRight(), 0);
			
			for (ImageButton button : modPacks) {
				mainContentPane.add(button);
				mainContentPane.setComponentZOrder(button, 1);
			}
			mainContentPane.add(noMod);
			
			readUsedUsernames();
			
			this.setVisible(true);
		}
		
		public void loadLauncherData() {
			MirrorUtils.updateMirrorsYMLCache();
			MD5Utils.updateMD5Cache();
			ModPackListYML.updateModPacksYMLCache();

			ModPackListYML.getAllModPackResources();
			ModPackListYML.loadModpackLogos();

			LibrariesYML.updateLibrariesYMLCache();
			ModLibraryYML.updateModLibraryYML();
			
			if (SettingsUtil.getModPackSelection() != null) {
				noMod.setBounds(10, 228, 595, 282);
				updateBranding();
				
			} else {	
				noMod.setBounds(10, 228, 595, 282);
			}
		}
		
		public void updateBranding() {
			getLoginButton().setEnabled(false);
			getOptionsButton().setEnabled(false);
			setTitle("Loading Modpack Data...");
			SwingWorker<Object, Object> updateThread = new SwingWorker<Object, Object>() {

				@Override
				protected Object doInBackground() throws Exception {
					ModPackListYML.setCurrentModpack();
					return null;
				}

				@Override
				protected void done() {
					if (options == null) {
						options = new OptionDialog();
						options.modPackList = ModPackListYML.modpackMap;
						options.setVisible(false);
					}
					
					System.out.println(ModPackListYML.currentModPack);
					getLoginButton().setEnabled(true);
					getOptionsButton().setEnabled(true);
					mainContentPane.image = Toolkit.getDefaultToolkit().getImage(ModPackYML.getModPackBackground());
					mainContentPane.repaint();
					//background.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(ModPackYML.getModPackBackground())));
					setIconImage(Toolkit.getDefaultToolkit().getImage(ModPackYML.getModPackIcon()));
					try {
						noMod.image= ImageIO.read(new File(ModPackYML.getModPackLogo()));
						noMod.repaint();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//setTitle(String.format("Aegis Launcher - %s - (%s)", Main.build, ModPackListYML.currentModPackLabel));
					options.reloadSettings();
					MinecraftYML.updateMinecraftYMLCache();
					//setModLoaderEnabled();
				}
			};
			updateThread.execute();
		}
		
		public void setModLoaderEnabled() {
			File modLoaderConfig = new File(GameUpdater.modconfigsDir, "ModLoader.cfg");
			boolean modLoaderExists = modLoaderConfig.exists();
		}
		
		private Cipher getCipher(int mode, String password) throws Exception {
			Random random = new Random(43287234L);
			byte[] salt = new byte[8];
			random.nextBytes(salt);
			PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, 5);

			SecretKey pbeKey = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(new PBEKeySpec(password.toCharArray()));
			Cipher cipher = Cipher.getInstance("PBEWithMD5AndDES");
			cipher.init(mode, pbeKey, pbeParamSpec);
			return cipher;
		}
		
		private void readUsedUsernames() {
			int i = 0;
			try {
				File lastLogin = new File(PlatformUtils.getWorkingDirectory(), "lastlogin");
				if (!lastLogin.exists()) { return; }
				Cipher cipher = getCipher(2, "passwordfile");

				DataInputStream dis;
				if (cipher != null) {
					dis = new DataInputStream(new CipherInputStream(new FileInputStream(lastLogin), cipher));
				} else {
					dis = new DataInputStream(new FileInputStream(lastLogin));
				}

				try {
					// noinspection InfiniteLoopStatement
					while (true) {
						String user = dis.readUTF();
						boolean isHash = dis.readBoolean();
						if (isHash) {
							byte[] hash = new byte[32];
							dis.read(hash);

							usernames.put(user, new UserPasswordInformation(hash));
						} else {
							String password = dis.readUTF();
							if (!password.isEmpty()) {
								i++;
								String username = user;
								if (dis.readBoolean())
									username = dis.readUTF();
							}
							usernames.put(user, new UserPasswordInformation(password));
						}
					}
				} catch (EOFException ignored) {
				}
				dis.close();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			String eventId = e.getActionCommand();
			if (source == getModLeft())
			{
				for (JButton button : modPacks)
				{
						button.setVisible(false);
				}
				JButton button = null;
				if (ModPackListYML.currentModPack == null) {
					button = modPacks.get(modPacks.size() - 1);
					currentModButton = modPacks.size() - 1;
					noMod.setVisible(false);
				} else {
					if (currentModButton == 0) {
						button = modPacks.get(modPacks.size() - 1);
						currentModButton = modPacks.size() - 1;
					} else {
						button = modPacks.get(currentModButton - 1);
						currentModButton = currentModButton - 1;
					}
				}
				noMod.setVisible(false);
				button.setVisible(true);
				mainContentPane.setComponentZOrder(button, 1);
				
				if (ModPackListYML.currentModPack == null) {
					SettingsUtil.init();
					GameUpdater.copy(SettingsUtil.settingsFile, ModPackListYML.ORIGINAL_PROPERTIES);
				} else {
					GameUpdater.copy(SettingsUtil.settingsFile, new File(GameUpdater.modpackDir, "launcher.properties"));
				}
				
				SettingsUtil.setModPack(button.getName());
				this.updateBranding();
				mainContentPane.setComponentZOrder(getModLeft(), 0);
				mainContentPane.setComponentZOrder(getModRight(), 0);
				return;
			}
			else if (source == getModRight())
			{
				for (JButton button : modPacks)
				{
						button.setVisible(false);
				}
				JButton button = null;
				if (ModPackListYML.currentModPack == null) {
					button = modPacks.get(0);
					currentModButton = 0;
					noMod.setVisible(false);
				} else {
					if (currentModButton == (modPacks.size() - 1)) {
						button = modPacks.get(0);
						currentModButton = 0;
					} else {
						button = modPacks.get(currentModButton + 1);
						currentModButton = currentModButton + 1;
					}
				}
				noMod.setVisible(false);
				button.setVisible(true);
				mainContentPane.setComponentZOrder(button, 1);
				
				if (ModPackListYML.currentModPack == null) {
					SettingsUtil.init();
					GameUpdater.copy(SettingsUtil.settingsFile, ModPackListYML.ORIGINAL_PROPERTIES);
				} else {
					GameUpdater.copy(SettingsUtil.settingsFile, new File(GameUpdater.modpackDir, "launcher.properties"));
				}
				
				SettingsUtil.setModPack(button.getName());
				this.updateBranding();
				mainContentPane.setComponentZOrder(getModLeft(), 0);
				mainContentPane.setComponentZOrder(getModRight(), 0);
				return;
			}
			else if (source == getOptionsButton())
			{
				options.setVisible(true);
				options.setBounds((int) getBounds().getCenterX() - 250, (int) getBounds().getCenterY() - 75, 300, 325);
				login.setVisible(false);
			}
			else if (source == getLoginButton())
			{
				if (SettingsUtil.getModPackSelection() == null) {
					JOptionPane.showMessageDialog(getParent(), "No mod Selected. Please, select a mod!");
				} else {
					this.getLoginButton().setEnabled(false);
					this.getOptionsButton().setEnabled(false);
					this.getModLeft().setEnabled(false);
					this.getModRight().setEnabled(false);
					options.setVisible(false);
					login.setVisible(true);
					login.setBounds((int) getBounds().getCenterX() - 250, (int) getBounds().getCenterY() - 75, 346, 182);
					login.mainContentPane.updateBackground();
				}
			}
		}
		
		public void updateThread() {
			SwingWorker<Boolean, String> updateThread = new SwingWorker<Boolean, String>() {

				boolean	error = false;

				@Override
				protected void done() {
					// FileUtils.cleanDirectory(GameUpdater.tempDir);
					loginButton.setEnabled(true);
					optionsButton.setEnabled(true);
					modLeft.setEnabled(true);
					modRight.setEnabled(true);
					progressBar.repaint();
					if (!error) {
						runGame();
					}
					this.cancel(true);
				}

				@Override
				protected Boolean doInBackground() throws Exception {
					try {
						if (mcUpdate) {
							gameUpdater.updateMC();
						}
						if (spoutUpdate) {
							gameUpdater.updateSpoutcraft();
						}
						if (modpackUpdate) {
							gameUpdater.updateModPackMods();
						}
					} catch (NoMirrorsAvailableException e) {
						JOptionPane.showMessageDialog(getParent(), "No Mirrors Are Available to download from!\nTry again later.");
					} catch (Exception e) {
						e.printStackTrace();
						JOptionPane.showMessageDialog(getParent(), "Update Failed!");
						error = true;
						enableUI();
						this.cancel(true);
						return false;
					}
					return true;
				}

				@Override
				protected void process(List<String> chunks) {
					progressBar.setString(chunks.get(0));
				}
			};
			updateThread.execute();
		}

		public void enableUI() {
			getLoginButton().setEnabled(true);
			getOptionsButton().setEnabled(true);
		}
		
		public void runGame() {
			LauncherFrame launcher = new LauncherFrame();
			launcher.setLoginForm(this);
			int result = (Main.isOffline) ? launcher.runGame(null, null, null, null) : launcher.runGame(values[2].trim(), values[3].trim(), values[1].trim(), pass);
			if (result == LauncherFrame.SUCCESSFUL_LAUNCH) {
				MainForm.updateDialog.dispose();
				MainForm.updateDialog = null;
				setVisible(false);
				Main.loginForm = null;

				dispose();
			} else if (result == LauncherFrame.ERROR_IN_LAUNCH) {
				getLoginButton().setEnabled(true);
				getOptionsButton().setEnabled(true);
				progressBar.setVisible(false);
			}

			this.success = result;
			// Do nothing for retrying launch
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			this.getModLeft().setVisible(true);
			this.getModRight().setVisible(true);
			mainContentPane.setComponentZOrder(getModLeft(), 0);
			mainContentPane.setComponentZOrder(getModRight(), 0);
		}

		@Override
		public void mouseExited(MouseEvent e) {
			this.getModLeft().setVisible(false);
			this.getModRight().setVisible(false);
		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		public ProgressBar getProgressBar() {
			return progressBar;
		}

		public void setProgressBar(ProgressBar progressBar) {
			this.progressBar = progressBar;
		}

		@Override
		public void stateChanged(String fileName, float progress) {
			int intProgress = Math.round(progress);

			if (intProgress >= 0) {
				progressBar.setValue(intProgress);
				progressBar.setIndeterminate(false);
			} else {
				progressBar.setIndeterminate(true);
			}

			fileName = fileName.replace(workingDir, "");
			if (fileName.contains("?")) {
				fileName = fileName.substring(0, fileName.indexOf("?"));
			}

			if (fileName.length() > 60) {
				fileName = fileName.substring(0, 60) + "...";
			}
			String progressText = intProgress + "% " + fileName;
			if (intProgress < 0) progressText = fileName;
			progressBar.setString(progressText);
		}

		public ImageButton getModRight() {
			return modRight;
		}

		public void setModRight(ImageButton modRight) {
			this.modRight = modRight;
		}

		public ImageButton getModLeft() {
			return modLeft;
		}

		public void setModLeft(ImageButton modLeft) {
			this.modLeft = modLeft;
		}

		public ImageButton getOptionsButton() {
			return optionsButton;
		}

		public void setOptionsButton(ImageButton optionsButton) {
			this.optionsButton = optionsButton;
		}

		public ImageButton getLoginButton() {
			return loginButton;
		}

		public void setLoginButton(ImageButton loginButton) {
			this.loginButton = loginButton;
		}
}
