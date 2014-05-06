package com.ift2905.recipehelper;

import java.io.IOException;
import java.io.InputStream;
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

public class KraftAPI {
	
	public static final String URLSTART = "http://www.kraftfoods.com/ws/RecipeWS.asmx";
	public static final String RECIPESOFTHEWEEK = "/GetRecipesOfTheWeek";
	public static final String SEARCHBYKEYWORD = "/GetRecipesByKeywords";
	public static final String SEARCHBYID = "/GetRecipeByRecipeID";
	public static final String ISITEID = "iSiteID=1";
	public static final String IBRANDID = "iBrandID=1";
	public static final String ILANGID = "iLangID=1";
	public static final String STRIPHTML = "bStripHTML=true";
	public static final String BISRECIPEPHOTOREQUIRED = "bIsRecipePhotoRequired=true";
	public static final String BISREADYIN30MINS = "bIsReadyIn30Mins=false";
	
	String ingredient;
	Double quantite;
	Drawable icone;	
	String erreur;
	
	ArrayList<HashMap<String,String>> searchResults;
	ArrayList<String> ingredients;
	ArrayList<String> ingredientName;
	ArrayList<String> ingredientID;
	ArrayList<String> etapes;
	HashMap<String,String> description;
	ArrayList<String> nutritionDetail;
	public static String nbServing="";
	
	int totalResults;
	
	InputStream recipeStream;
	KraftAPI(String functionInvoked) throws IOException{
		
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
	
	KraftAPI(String functionInvoked, String id) throws IOException, XmlPullParserException
	{
		if (!(functionInvoked.equals(SEARCHBYID))){ 
			throw new IllegalArgumentException("Incorrect page invoked. Either page does not exist, or there are missing args.");
		}
		try {
			recipeStream = getHttpByID(id).getContent();
			String ing_name="",ing_id="", ing_qty="", nutri="";



			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(recipeStream, null);
			parser.nextTag();


			ingredients = new ArrayList<String>();
			etapes = new ArrayList<String>();
			description = new HashMap<String,String>();
			nutritionDetail = new ArrayList<String>();
			ingredientID = new ArrayList<String>();
			ingredientName = new ArrayList<String>();

			parser.require(XmlPullParser.START_TAG, null, "RecipeDetailResponse");

			while (parser.next() != XmlPullParser.END_DOCUMENT)
			{
				String tagName = parser.getName();

				if (parser.getEventType() != XmlPullParser.START_TAG){
					continue;
				} else if (parser.getName().equals("ComplimentaryRecipes")){
					break;
				}
				//Description
				else if (parser.getName().equals("RecipeID")){
					description.put("RecipeID", parser.nextText());
				}else if (parser.getName().equals("RecipeName")){
					description.put("RecipeName", parser.nextText());
				} else if (parser.getName().equals("TotalTime")){
					description.put("TotalTime", parser.nextText());
				} else if (parser.getName().equals("NumberOfServings")){
					description.put("NumberOfServings", parser.nextText());
				} else if (parser.getName().equals("AvgRating")){
					description.put("AvgRating", parser.nextText());
				} else if (parser.getName().equals("PhotoURL")){
					description.put("PhotoURL", parser.nextText());
				}

				//Ingredients

				else if(tagName.equals("IngredientDetails"))
				{

					while(!(parser.next() == XmlPullParser.END_TAG && parser.getName().equals("IngredientDetails")))
					{
						if (parser.getEventType() != XmlPullParser.START_TAG)
						{
							continue;
						}
						else if (parser.getName().equals("RecipeIngredientID")){
							ing_id = parser.nextText();
							ingredientID.add(ing_id);
						} else if (parser.getName().equals("IngredientName")){
							ing_name = parser.nextText();
							ingredientName.add(ing_name);
						} else if (parser.getName().equals("QuantityText")){
							ing_qty = parser.nextText();
						} else if (parser.getName().equals("QuantityUnit")){
							if(ing_qty.equals("")){
								ingredients.add(ing_name);
							}else{
							ingredients.add(ing_qty + " " + parser.nextText() + " of " + ing_name);}
						}
					}
				}

				//Etapes
				else if(tagName.equals("PreparationDetails"))
				{
					while(!(parser.next() == XmlPullParser.END_TAG && parser.getName().equals("PreparationDetails")))
					{
						if (parser.getEventType() != XmlPullParser.START_TAG)
						{
							continue;
						} else if(parser.getName().equals("Description"))
						{
							etapes.add(parser.nextText());
						}
					}

				}

				//Nutritions
				else if(tagName.equals("NutritionItemDetails"))
				{
					while(!(parser.next() == XmlPullParser.END_TAG && parser.getName().equals("NutritionItemDetails")))
					{
						if (parser.getEventType() != XmlPullParser.START_TAG)
						{
							continue;
						} else if(parser.getName().equals("NutritionItemName"))
						{
							nutri=parser.nextText();
						} else if(parser.getName().equals("Quantity"))
						{
							nutri+= ": " + parser.nextText();

						}else if(parser.getName().equals("Unit"))
						{
							nutri+=parser.nextText();
							nutritionDetail.add(nutri);
						}
					}
				}
			}
			recipeStream.close();
		}catch (ClientProtocolException e) {
			Log.d("RecipeHelper", "Erreur HTTP (protocole) :"+e.getMessage());
			throw e;
		} catch (IOException e) {
			Log.d("RecipeHelper", "Erreur HTTP (IO) :"+e.getMessage());
			throw e;
		} catch (XmlPullParserException e) {
			Log.e("RecipeHelper", e.getMessage());
			throw e;
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
			parser.nextTag();
			
			searchResults = new ArrayList<HashMap<String,String>>();
			
			parser.require(XmlPullParser.START_TAG, null, "RecipeSummariesResponse");
			HashMap<String,String> currentInfo = null;
			while (parser.next() != XmlPullParser.END_DOCUMENT){
				String tagName = parser.getName();
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
	 * MÃ©thode utilitaire qui permet de rapidement
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
		for (int i = 0; i < 6; i++){
			urlString.append("sKeyword" + (i+1) + "=");
			if (i < keywords.length) urlString.append(keywords[i] + "&");
			else urlString.append("&");
		}
		urlString.append(BISRECIPEPHOTOREQUIRED + "&"
				+ BISREADYIN30MINS + "&" + 
				"sSortField=&sSortDirection=&" + IBRANDID + "&"
				+ ILANGID + "&iStartRow=" + (page * 25)
				+ "&iEndRow=" + (page * 25 + 24));
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet http = new HttpGet(urlString.toString());
		HttpResponse response = httpClient.execute(http);
		return response.getEntity();
	}
	
	private HttpEntity getHttpByID(String id) throws ClientProtocolException, IOException {
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet http = new HttpGet(URLSTART + SEARCHBYID
				+ "?" + "iRecipeID=" + id + "&" + STRIPHTML + "&" + IBRANDID + "&" + ILANGID);
		HttpResponse response = httpClient.execute(http);
		return response.getEntity();    		
	}
		
	public ArrayList<HashMap<String,String>> getSearchResults(){
		return searchResults;
	}
	
	public ArrayList<String> getIngredients(){
		return ingredients;
	}

	public ArrayList<String> getEtapes(){
		return etapes;
	}

	public HashMap<String,String> getDescription(){
		return description;
	}

	public ArrayList<String> getNutritionDetails(){
		return nutritionDetail;
	}
	public ArrayList<String> getIngredientID(){
		return ingredientID;
	}
	
	public ArrayList<String> getIngredientName(){
		return ingredientName;
	}
}
