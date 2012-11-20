package org.spoutcraft.launcher.gui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.spoutcraft.launcher.Util;

public class PageReader {
	public static ArrayList<String> readPage(String URL)
	{
		ArrayList<String> lines = new ArrayList<String>();
		InputStream is = null;
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(URL);
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			if (nameValuePairs != null)
				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();
		} catch (Throwable e) {
			try {
				throw new Exception("Error in http connection " + e.toString());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-1"), 8);
			String line = null;
			while ((line = reader.readLine()) != null) {
				lines.add(line);
			}
			is.close();
		} catch (Throwable e) {
			try {
				throw new Exception("Error converting result " + e.toString());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		
		return lines;
	}
	
	public static ArrayList<String> getPage(String URL) throws ClientProtocolException, IOException
	{
		ArrayList<String> lines = new ArrayList<String>();
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(URL);
		HttpResponse response = client.execute(request);

		// Get the response
		BufferedReader rd = new BufferedReader
		  (new InputStreamReader(response.getEntity().getContent()));
		    
		String line = "";
		while ((line = rd.readLine()) != null) {
		  lines.add(line);
		  Util.log(line);
		}  
		
		return lines;
	}
}
