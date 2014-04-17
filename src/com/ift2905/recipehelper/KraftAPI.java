package com.ift2905.recipehelper;

import java.io.IOException;
import java.io.InputStream;
import java.lang.IllegalArgumentException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;

import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.Xml;


public class KraftAPI {
	
	public static final String URLSTART = "http://www.kraftfoods.com/ws/RecipeWS.asmx";
	public static final String RECIPESOFTHEWEEK = "/GetRecipesOfTheWeek";
	public static final String SEARCHBYKEYWORD = "/GetRecipesByKeywords";
	public static final String SEARCHBYID = "/GetRecipesByRecipeIDs";
	public static final String ISITEID = "iSiteID=1";
	public static final String IBRANDID = "iBrandID=1";
	public static final String ILANGID = "iLangID=1";
	public static final String BISRECIPEPHOTOREQUIRED = "bIsRecipePhotoRequired=true";
	public static final String BISREADYIN30MINS = "bIsReadyIn30Mins=false";
	
	String ingredient;
	Double quantite;
	Drawable icone;	
	String erreur;
	
	ArrayList<HashMap<String,String>> searchResults;
	
	InputStream recipeStream;
	KraftAPI(String functionInvoked){
		
		Log.d("RecipeHelper", "Creating API");
		if (!(functionInvoked.equals(RECIPESOFTHEWEEK))){ 
			throw new IllegalArgumentException("Incorrect page invoked. Either page does not exist, or there are missing args.");
		}
		try {
			recipeStream = getHttpROTW().getContent();
			
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(recipeStream, null);
			parser.nextTag();
			
			searchResults = new ArrayList<HashMap<String,String>>();
			
			parser.require(XmlPullParser.START_TAG, null, "ROTDSummariesResponse");
			HashMap<String,String> currentInfo = null;
			int tagsfound = 0;
			while (parser.next() != XmlPullParser.END_DOCUMENT){
				String tagName = parser.getName();
				if (parser.getEventType() == XmlPullParser.END_TAG && tagName.equals("ROTDSummary")) {
		            searchResults.add(currentInfo);
		        } else if (parser.getEventType() == XmlPullParser.START_TAG && tagName.equals("ROTDSummary")){
					currentInfo = new HashMap<String,String>();
				} else if (parser.getEventType() != XmlPullParser.START_TAG){
					continue;
				} else if (tagName.equals("RecipeID")){
					currentInfo.put("RecipeID", parser.nextText());
				}else if (tagName.equals("RecipeName")){
					currentInfo.put("RecipeName", parser.nextText());
				} else if (tagName.equals("TotalTime")){
					currentInfo.put("TotalTime", parser.nextText());
				} else if (tagName.equals("NumberOfServings")){
					currentInfo.put("NumberOfServings", parser.nextText());
				} else if (tagName.equals("AvgRating")){
					currentInfo.put("AvgRating", parser.nextText());
				} else if (tagName.equals("PhotoURL")){
					currentInfo.put("PhotoURL", parser.nextText());
				}
			}
			
			recipeStream.close();
		} catch (ClientProtocolException e) {
			Log.d("RecipeHelper", "Erreur HTTP (protocole) :"+e.getMessage());
		} catch (IOException e) {
			Log.d("RecipeHelper", "Erreur HTTP (IO) :"+e.getMessage());
		} catch (XmlPullParserException e) {
			Log.d("RecipeHelper", e.getMessage());
		} 
	}
	
	
	
	/*
	 * Méthode utilitaire qui permet de rapidement
	 * charger et obtenir une page web depuis
	 * l'internet.
	 * 
	 */
	private HttpEntity getHttpROTW() throws ClientProtocolException, IOException {
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet http = new HttpGet(URLSTART + RECIPESOFTHEWEEK
				+ "?" + ISITEID + "&" + IBRANDID + "&" + ILANGID);
		HttpResponse response = httpClient.execute(http);
		return response.getEntity();    		
	}
	private HttpEntity getHttpByKeyword(String[] keywords, int page) throws ClientProtocolException, IOException {
		//HttpClient httpClient = new DefaultHttpClient();
		//HttpGet http = new HttpGet(url);
		//HttpResponse response = httpClient.execute(http);
		return null; //response.getEntity();
	}
	private HttpEntity getHttpByID(String id) throws ClientProtocolException, IOException {
		//HttpClient httpClient = new DefaultHttpClient();
		//HttpGet http = new HttpGet(url);
		//HttpResponse response = httpClient.execute(http);
		return null; //response.getEntity();    		
	}
		
	/*
	 * Méthode utilitaire qui permet
	 * d'obtenir une image depuis une URL.
	 * 
	 */
	private Drawable loadHttpImage(String url) throws ClientProtocolException, IOException {
		return null;
	}
	public ArrayList<HashMap<String,String>> getSearchResults(){
		return searchResults;
	}

}
