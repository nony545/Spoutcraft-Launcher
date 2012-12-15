package org.spoutcraft.launcher.gui;

import java.awt.Color;
import java.awt.Container;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.swing.JLabel;

import me.unleashurgeek.util.Fonts;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.spoutcraft.launcher.modpacks.ModPackYML;

public class NewsPane extends Container {
	
	public final MainForm mainForm;
	
	private ArrayList<String> newsTitleList = new ArrayList<String>(); 
	private ArrayList<String> newsLinkList  = new ArrayList<String>(); 
	
	public static ArrayList<JLabel> newsTitles = new ArrayList<JLabel>();
	public static ArrayList<JLabel> newsSeperators   = new ArrayList<JLabel>();
	
	public NewsPane(MainForm mainForm) {
		this.mainForm = mainForm;
		
		this.createNews("http://www.aegisgaming.org/home/m/6918568/rss/true.rss");
		if (!newsTitleList.isEmpty())
		for (int i = 0; i < 8; i++)
		{
			if (i >= newsTitleList.size())
				return;
			JLabel testLine = new HyperlinkJLabel(newsTitleList.get(i), newsLinkList.get(i));
			testLine.setForeground(Color.decode("0x" + ModPackYML.getNewsColor()));
			testLine.setFont(MainForm.fonts.minecraft);
			testLine.setBounds(this.getX() + 15, ((i+1)*31), 250, 22);
			newsTitles.add(testLine);
			
			//JLabel separator = new JLabel("_________________");
			JLabel separator = new JLabel("____", JLabel.CENTER);
			separator.setHorizontalTextPosition(JLabel.CENTER);
			separator.setVerticalTextPosition(JLabel.BOTTOM);
			
			separator.setFont(Fonts.getFont("minecraft", 22));
			separator.setForeground(Color.decode("0x" + ModPackYML.getNewsColor()));
			separator.setBounds(getX(), 36 + (i*31), 250, 22);
			newsSeperators.add(separator);
			
			this.add(testLine);
			this.add(separator);
		}
		
		JLabel moreNews = new HyperlinkJLabel("More News!", "http://www.aegisgaming.org/home/m/6918568");
		moreNews.setForeground(Color.decode("0x" + ModPackYML.getNewsColor()));
		moreNews.setFont(MainForm.fonts.minecraft);
		moreNews.setBounds(this.getX() + 15, 279, 250, 22);
		this.add(moreNews);
		newsTitles.add(moreNews);
	}
	
	public static void ChangeColor(Color color)
	{
		for (JLabel label : newsTitles)
		{
			label.setForeground(color);
		}
		
		for (JLabel label : newsSeperators)
		{
			label.setForeground(color);
		}
	}	
	
	public void createNews(String url) {
		HttpClient client = new DefaultHttpClient();
		HttpGet method = new HttpGet(url);
		try {
			HttpResponse response = client.execute(method);

			// Get the response
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		    
			String line = "";
			while ((line = rd.readLine()) != null) {
				if (line.contains("<title>")) {
					
					StringBuilder sb =  new StringBuilder();
					sb.append(line.trim()).delete(sb.length() - 8, sb.length()).delete(0, 7);
					
					if (!sb.toString().equals("Aegis Gaming")) {
						newsTitleList.add(sb.toString());
					}
				} else if (line.contains("<link>")) {
					
					StringBuilder sb = new StringBuilder();
					sb.append(line.trim()).delete(sb.length() - 7, sb.length()).delete(0, 6);
					
					if (!sb.toString().equals("http://www.aegisgaming.org/home/m/6918568")) {
						newsLinkList.add(sb.toString());
					}
				}
			}  
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
