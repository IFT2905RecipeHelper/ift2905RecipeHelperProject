package com.example.recipehelper;

import java.io.IOException;
import java.io.InputStream;
import java.lang.IllegalArgumentException;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.Xml;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import android.util.Log;

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
	int totalResults;
	
	InputStream recipeStream;
	KraftAPI(String functionInvoked) throws IOException{
		
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
			throw e;
		} catch (IOException e) {
			Log.d("RecipeHelper", "Erreur HTTP (IO) :"+e.getMessage());
			throw e;
		} catch (XmlPullParserException e) {
			Log.d("RecipeHelper", e.getMessage());
		} 
	}
	
	KraftAPI (String functionInvoked, String[] keywords, int pageToLoad) throws IOException, XmlPullParserException{
		if (!(functionInvoked.equals(SEARCHBYKEYWORD))){ 
			throw new IllegalArgumentException("Incorrect page invoked. Either page does not exist, or there are missing args.");
		}
		try {
			recipeStream = getHttpByKeyword(keywords, pageToLoad).getContent();
			
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(recipeStream, null);
			Log.d("RecipeHelper", "setInput has been passed");
			parser.nextTag();
			Log.d("RecipeHelper", "nextTag has been passed");
			
			searchResults = new ArrayList<HashMap<String,String>>();
			
			parser.require(XmlPullParser.START_TAG, null, "RecipeSummariesResponse");
			Log.d("RecipeHelper", "Required has been passed");
			HashMap<String,String> currentInfo = null;
			while (parser.next() != XmlPullParser.END_DOCUMENT){
				String tagName = parser.getName();
				Log.d("RecipeHelper","Found a tag called" + tagName);
				if (parser.getEventType() == XmlPullParser.END_TAG && tagName.equals("RecipeSummary")) {
		            searchResults.add(currentInfo);
		        } else if (parser.getEventType() == XmlPullParser.START_TAG && tagName.equals("RecipeSummary")){
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
			throw e;
		} catch (IOException e) {
			Log.d("RecipeHelper", "Erreur HTTP (IO) :"+e.getMessage());
			throw e;
		} catch (XmlPullParserException e) {
			Log.d("RecipeHelper", e.getMessage());
			throw e;
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
		StringBuilder urlString = new StringBuilder(URLSTART + SEARCHBYKEYWORD + "?");
		Log.d("RecipeHelper", "What is keyword.length? " + keywords.length);
		for (int i = 0; i < 6; i++){
			Log.d("RecipeHelper", "What is i? " + i);
			urlString.append("sKeyword" + (i+1) + "=");
			if (i < keywords.length) urlString.append(keywords[i] + "&");
			else urlString.append("&");
		}
		urlString.append(BISRECIPEPHOTOREQUIRED + "&"
				+ BISREADYIN30MINS + "&" + 
				"sSortField=&sSortDirection=&" + IBRANDID + "&"
				+ ILANGID + "&iStartRow=" + (page * 25)
				+ "&iEndRow=" + (page * 25 + 24));
		Log.d("RecipeHelper", urlString.toString());
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet http = new HttpGet(urlString.toString());
		HttpResponse response = httpClient.execute(http);
		return response.getEntity();
	}
		
	public ArrayList<HashMap<String,String>> getSearchResults(){
		return searchResults;
	}
	
	
	// Méthode qui établie la connexion HTTP et revoi la String xml de la page
	
	public String getXml(String id) {
		String xml = "" ;
		try {
			HttpEntity page = getHttpByID(id);
			xml = EntityUtils.toString(page,HTTP.UTF_8);
				 
		} catch (ClientProtocolException e) {
			e.printStackTrace();
				
		} catch (IOException e) {
			e.printStackTrace();
		}
       		return xml;
    	}
    
    
    // Parse la String xml et revoi un Document exploitable par la DOM
    	public Document getDomElement(String xml){
        	Document document = null;
        	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        	try {
			DocumentBuilder docBuilder = factory.newDocumentBuilder();
 
            		InputSource input = new InputSource();
                	input.setCharacterStream(new StringReader(xml));
                	document = docBuilder.parse(input); 
            	} catch (ParserConfigurationException e) {
                	Log.e("Error: ", e.getMessage());
            	} catch (SAXException e) {
                	Log.e("Error: ", e.getMessage());
            	} catch (IOException e) {
                	Log.e("Error: ", e.getMessage());
            	}
            	return document;
	 }
	 
	 public String getValue(Element item, String val) {
	    NodeList n = item.getElementsByTagName(val);        
	    return this.getElementValue(n.item(0));
	}
	 
	public final String getElementValue( Node element ) {
	         Node child;
	         if(element!=null && element.hasChildNodes())
	         {
	             for(child=element.getFirstChild(); child!=null; child=child.getNextSibling())
	             {
	                 if(child.getNodeType()==Node.TEXT_NODE )
	                     return child.getNodeValue();
	           } 
	         }
			return "";
	}
	  
	  private HttpEntity getHttpByID(String id) throws ClientProtocolException, IOException {
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet http = new HttpGet("http://www.kraftfoods.com/kraftrecipews/kraftRecipeWs.asmx/GetRecipeByRecipeIdFull?recipeId="+id);
		HttpResponse response = httpClient.execute(http);
		return response.getEntity();    		
	}
}
