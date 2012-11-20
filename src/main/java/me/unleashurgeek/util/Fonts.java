package me.unleashurgeek.util;

import java.awt.Font;

public class Fonts {
	
	public final Font minecraft;
	
	public Fonts()
	{
		this.minecraft = getFont("minecraft", 12);
	}
	
	public static Font getFont(String fontLocation, int size) {
		Font minecraft;
		try {
			minecraft = Font.createFont(Font.TRUETYPE_FONT, Fonts.class.getResourceAsStream("/org/spoutcraft/launcher/fonts/" + fontLocation + ".ttf")).deriveFont((float)size);
		} catch (Exception e) {
			e.printStackTrace();
			// Fallback
			minecraft = new Font("Arial", Font.PLAIN, size);
		}
		return minecraft;
	}
}
