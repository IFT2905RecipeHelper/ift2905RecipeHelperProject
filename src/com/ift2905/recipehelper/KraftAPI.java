package com.ift2905.recipehelper;

import java.io.IOException;
import java.io.InputStream;

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

import android.graphics.drawable.Drawable;


public class KraftAPI {
	
	String ingredient;
	Double quantite;
	Drawable icone;
	
	String erreur;
	
	KraftAPI() {
		
		erreur = null;
		
		String url = "http://www.kraftfoods.com/ws/RecipeWS.asmx" ;
		
		/**
		 *TODO: This thing. 
		 *You'll have to split this in two: one part for a recipe search, one part to get recipe info. 
		 *You'll probably need to add an argument to constructor.
		 *Remove the comments before the "catch" portions once its done.
		 */
		try {

		//} catch (ClientProtocolException e) {
		//	erreur = "Erreur HTTP (protocole) :"+e.getMessage();
		//} catch (IOException e) {
		//	erreur = "Erreur HTTP (IO) :"+e.getMessage();
		} catch (ParseException e) {
			erreur = "Erreur JSON (parse) :"+e.getMessage();
		}// catch (JSONException e) {
		//	erreur = "Erreur JSON :"+e.getMessage();
		//}
	}
	
	
	
	/*
	 * Méthode utilitaire qui permet de rapidement
	 * charger et obtenir une page web depuis
	 * l'internet.
	 * 
	 */
	private HttpEntity getHttp(String url) throws ClientProtocolException, IOException {
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet http = new HttpGet(url);
		HttpResponse response = httpClient.execute(http);
		return response.getEntity();    		
	}
		
	/*
	 * Méthode utilitaire qui permet
	 * d'obtenir une image depuis une URL.
	 * 
	 */
	private Drawable loadHttpImage(String url) throws ClientProtocolException, IOException {
		InputStream is = getHttp(url).getContent();
		Drawable d = Drawable.createFromStream(is, "src");
		return d;
	}

}
