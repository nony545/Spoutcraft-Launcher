package org.spoutcraft.launcher.gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;

import org.spoutcraft.launcher.GameUpdater;
import org.spoutcraft.launcher.MinecraftUtils;
import org.spoutcraft.launcher.PlatformUtils;
import org.spoutcraft.launcher.exception.AccountMigratedException;
import org.spoutcraft.launcher.exception.BadLoginException;
import org.spoutcraft.launcher.exception.MCNetworkException;
import org.spoutcraft.launcher.exception.MinecraftUserNotPremiumException;
import org.spoutcraft.launcher.exception.OutdatedMCLauncherException;
import org.spoutcraft.launcher.modpacks.ModPackListYML;
import me.unleashurgeek.listeners.ComponentMover;



public class LoginDialog extends JDialog implements ActionListener, KeyListener {

	private final PasswordBox passwordField;
	private final TextBox usernameField;
	
	// WindowsButtons (Close and Minimize)
	private LoginWindowsButton closeButton;
	
	public LoginContentPanel mainContentPane;
	
	private MainForm jFrame;
	
	JButton button = new JButton("LOGIN");
	JCheckBox rememberCheckbox = new JCheckBox("Remember", true);
	
	public LoginDialog(MainForm jFrame) {
		
		this.jFrame = jFrame;
		this.setResizable(false);
		this.setUndecorated(true);
		this.setAlwaysOnTop(true);
		this.addKeyListener(this);
		
		mainContentPane = new LoginContentPanel();
		mainContentPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		this.setContentPane(mainContentPane);
		mainContentPane.setLayout(null);
		mainContentPane.addKeyListener(this);
		new ComponentMover(this, mainContentPane);
		
		passwordField = new PasswordBox(mainContentPane, "Password");
		passwordField.setBounds(56, 65, 236, 37);
		passwordField.setBackground(new Color(220, 220, 220));
		passwordField.setFont(MainForm.fonts.minecraft);
		passwordField.addKeyListener(this);
		
		usernameField = new TextBox(mainContentPane, "Username");
		usernameField.setBounds(56, 21, 236, 37);
		usernameField.addActionListener(this);
		usernameField.setOpaque(false);
		usernameField.setFont(MainForm.fonts.minecraft);
		usernameField.setEditable(true);
		usernameField.addKeyListener(this);
		
		button.setBounds(134, 115, 176, 56);
		button.addActionListener(this);
		button.addKeyListener(this);
		button.setFont(MainForm.fonts.minecraft);
		
		rememberCheckbox.setFont(MainForm.fonts.minecraft);
		rememberCheckbox.setOpaque(false);
		rememberCheckbox.setBounds(26, 115, 103, 23);
		rememberCheckbox.addKeyListener(this);
		
		//--------------------Minimize/Maximize Buttons----------------------
		closeButton = new LoginWindowsButton("close", jFrame);
		closeButton.setLocation(316, 5);
		
		mainContentPane.add(closeButton);
		mainContentPane.add(usernameField);
		mainContentPane.add(passwordField);
		mainContentPane.add(button);
		mainContentPane.add(rememberCheckbox);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == button) {
			doLogin();
		}
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (button.isEnabled() && e.getKeyCode() == KeyEvent.VK_ENTER) {
			doLogin();
			
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}
	
	public void doLogin() {
		doLogin(usernameField.getText(), new String(passwordField.getPassword()), false);
	}

	public void doLogin(final String user, final String pass) {
		doLogin(user, pass, true);
	}

	public void doLogin(final String user, final String pass, final boolean cmdLine) {
		if (user == null || user.isEmpty() || pass == null || pass.isEmpty())
		{
			JOptionPane.showMessageDialog(this, "You must enter both a username and password");
			return;
		}
		this.dispatchEvent(new WindowEvent(
				this, WindowEvent.WINDOW_CLOSING
		));
		SwingWorker<Boolean, Boolean> loginThread = new SwingWorker<Boolean, Boolean>() {
			@Override
			protected Boolean doInBackground() {
				jFrame.getProgressBar().setVisible(true);
				jFrame.getProgressBar().setString("Connecting to www.minecraft.net...");
				String password = pass.toString();
				try {
					MainForm.values = MinecraftUtils.doLogin(user, pass, jFrame.getProgressBar());
					return true;
				} catch (AccountMigratedException e) {
					JOptionPane.showMessageDialog(getParent(), "Account migrated, use e-mail as username");
					this.cancel(true);
					jFrame.getLoginButton().setEnabled(true);
					jFrame.getOptionsButton().setEnabled(true);
					jFrame.getModLeft().setEnabled(true);
					jFrame.getModRight().setEnabled(true);
				} catch (BadLoginException e) {
					JOptionPane.showMessageDialog(getParent(), "Incorrect username/password combination");
					this.cancel(true);
					jFrame.getLoginButton().setEnabled(true);
					jFrame.getOptionsButton().setEnabled(true);
					jFrame.getModLeft().setEnabled(true);
					jFrame.getModRight().setEnabled(true);
				} catch (MinecraftUserNotPremiumException e) {
					JOptionPane.showMessageDialog(getParent(), "You purchase a minecraft account to play");
					this.cancel(true);
					jFrame.getLoginButton().setEnabled(true);
					jFrame.getOptionsButton().setEnabled(true);
					jFrame.getModLeft().setEnabled(true);
					jFrame.getModRight().setEnabled(true);
				} catch (MCNetworkException e) {
					UserPasswordInformation info = null;

					for (String username : jFrame.usernames.keySet()) {
						if (username.equalsIgnoreCase(user)) {
							info = jFrame.usernames.get(username);
							break;
						}
					}

					boolean authFailed = (info == null);

					if (!authFailed) {
						if (info.isHash) {
							try {
								MessageDigest digest = MessageDigest.getInstance("SHA-256");
								byte[] hash = digest.digest(pass.getBytes());
								for (int i = 0; i < hash.length; i++) {
									if (hash[i] != info.passwordHash[i]) {
										authFailed = true;
										break;
									}
								}
							} catch (NoSuchAlgorithmException ex) {
								authFailed = true;
							}
						} else {
							authFailed = !(password.equals(info.password));
						}
					}

					if (authFailed) {
						JOptionPane.showMessageDialog(getParent(), "Unable to authenticate account with minecraft.net");
					} else {
						int result = JOptionPane.showConfirmDialog(getParent(), "Would you like to run in offline mode?", "Unable to Connect to Minecraft.net", JOptionPane.YES_NO_OPTION);
						if (result == JOptionPane.YES_OPTION) {
							MainForm.values = new String[] { "0", "0", user, "0" };
							return true;
						}
					}
					this.cancel(true);
					jFrame.getLoginButton().setEnabled(true);
					jFrame.getOptionsButton().setEnabled(true);
					jFrame.getModLeft().setEnabled(true);
					jFrame.getModRight().setEnabled(true);
				} catch (OutdatedMCLauncherException e) {
					JOptionPane.showMessageDialog(getParent(), "Incompatible Login Version.");
					jFrame.getLoginButton().setEnabled(true);
					jFrame.getOptionsButton().setEnabled(true);
					jFrame.getModLeft().setEnabled(true);
					jFrame.getModRight().setEnabled(true);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					this.cancel(true);
					jFrame.getLoginButton().setEnabled(true);
					jFrame.getOptionsButton().setEnabled(true);
					jFrame.getModLeft().setEnabled(true);
					jFrame.getModRight().setEnabled(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
				jFrame.enableUI();
				this.cancel(true);
				return false;
			}

			@Override
			protected void done() {
				if (MainForm.values == null || MainForm.values.length < 4) { return; }
				MainForm.pass = pass;
				String profileName = MainForm.values[2].toString();

				MessageDigest digest = null;

				try {
					digest = MessageDigest.getInstance("SHA-256");
				} catch (NoSuchAlgorithmException e) {
				}

				MainForm.gameUpdater.user = usernameField.getText(); //values[2].trim();
				MainForm.gameUpdater.downloadTicket = MainForm.values[1].trim();
				if (!cmdLine) {
					String password = new String(passwordField.getPassword());
					if (rememberCheckbox.isSelected()) {
						jFrame.usernames.put(MainForm.gameUpdater.user, new UserPasswordInformation(password, profileName));
					} else {
						if (digest == null) {
							jFrame.usernames.put(MainForm.gameUpdater.user, new UserPasswordInformation(""));
						} else {
							jFrame.usernames.put(MainForm.gameUpdater.user, new UserPasswordInformation(digest.digest(password.getBytes())));
						}
					}
					writeUsernameList();
				}

				SwingWorker<Boolean, String> updateThread = new SwingWorker<Boolean, String>() {

					@Override
					protected Boolean doInBackground() throws Exception {
						publish("Checking for Minecraft Update...\n");
						try {
							jFrame.mcUpdate = MainForm.gameUpdater.checkMCUpdate();
						} catch (Exception e) {
							e.printStackTrace();
						}

						publish("Checking for Spoutcraft update...\n");
						try {
							jFrame.spoutUpdate = MainForm.gameUpdater.isSpoutcraftUpdateAvailable();
						} catch (Exception e) {
							e.printStackTrace();
						}

						publish(String.format("Checking for %s update...\n", ModPackListYML.currentModPackLabel));
						try {
							jFrame.modpackUpdate = MainForm.gameUpdater.isModpackUpdateAvailable();
						} catch (Exception e) {
							e.printStackTrace();
						}
						return true;
					}

					@Override
					protected void done() {
						if (jFrame.modpackUpdate) {
							MainForm.updateDialog.setToUpdate(ModPackListYML.currentModPackLabel);
						} else if (jFrame.spoutUpdate) {
							MainForm.updateDialog.setToUpdate("Spoutcraft");
						} else if (jFrame.mcUpdate) {
							MainForm.updateDialog.setToUpdate("Minecraft");
						}
						if (jFrame.mcUpdate ||jFrame. spoutUpdate || jFrame.modpackUpdate) {
							if (!GameUpdater.binDir.exists() || jFrame.mcUpdate) {
								jFrame.updateThread();
							} else {
								MainForm.updateDialog.setVisible(true);
							}
						} else {
							jFrame.runGame();
						}
						this.cancel(true);
					}

					@Override
					protected void process(List<String> chunks) {
						jFrame.getProgressBar().setString(chunks.get(0));
					}
				};
				updateThread.execute();
				this.cancel(true);
			}
		};
		loginThread.execute();
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
	
	private void writeUsernameList() {
		try {
			File lastLogin = new File(PlatformUtils.getWorkingDirectory(), "lastlogin");

			Cipher cipher = getCipher(1, "passwordfile");

			DataOutputStream dos;
			if (cipher != null) {
				dos = new DataOutputStream(new CipherOutputStream(new FileOutputStream(lastLogin), cipher));
			} else {
				dos = new DataOutputStream(new FileOutputStream(lastLogin, true));
			}
			for (String user : jFrame.usernames.keySet()) {
				dos.writeUTF(user);
				UserPasswordInformation info = jFrame.usernames.get(user);
				dos.writeBoolean(info.isHash);
				if (info.isHash) {
					dos.write(info.passwordHash);
				} else {
					dos.writeUTF(info.password);
				}
				dos.writeBoolean(info.hasProfileName());
				dos.writeUTF(info.getProfileName());
			}
			dos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static final class UserPasswordInformation {

		public boolean	isHash;
		public byte[]		passwordHash	= null;
		public String		password		= null;
		private String		profileName		= "";

		public UserPasswordInformation(String pass, String profileName) {
			this(pass);
			this.setProfileName(profileName);
		}

		public UserPasswordInformation(String pass) {
			isHash = false;
			password = pass;
		}

		public UserPasswordInformation(byte[] hash) {
			isHash = true;
			passwordHash = hash;
		}

		public Boolean hasProfileName() {
			if (getProfileName().equals("")) {
				return false;
			}
			return true;
		}

		/**
		 * @return the profileName
		 */
		public String getProfileName() {
			return profileName;
		}

		/**
		 * @param profileName the profileName to set
		 */
		public void setProfileName(String profileName) {
			this.profileName = profileName;
		}
	}

}
