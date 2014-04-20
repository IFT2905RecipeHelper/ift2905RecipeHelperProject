package com.ift2905.recipehelper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import android.app.Activity;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class RecipeSearchActivity extends Activity {
	int pagesLoaded = 0;
	String[] gotKeywords;
	ArrayList<HashMap<String,String>> searchResults = new ArrayList<HashMap<String,String>>();
	RecipeAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_page_layout);
		Intent intent = getIntent();
	    if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
	      String query = intent.getStringExtra(SearchManager.QUERY);
	      gotKeywords = query.split(" ");
	      for (String s: gotKeywords){
	    	  Log.d("RecipeHelper", "I have a keyword called " + s);
	      }
	    }
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		new updateAPITtask().execute();
		adapter = new RecipeAdapter(this, searchResults);
		ListView lv = (ListView)findViewById(R.id.resultlist);
		lv.setAdapter(adapter);
		Button loadMore = (Button)findViewById(R.id.loadmore);
		loadMore.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				new updateAPITtask().execute();				
			}			
		});
	}
	
	private class RecipeAdapter extends ArrayAdapter<HashMap<String,String>>{
		Context context;
		ArrayList<HashMap<String,String>> list;
		RecipeAdapter(Context c, ArrayList<HashMap<String,String>> l){
			super(c, R.layout.recipe_info_layout, l);
			context = c;
			list = l;
		}
		public View getView(int position, View convertView, ViewGroup parent){
			LayoutInflater inflater = (LayoutInflater) context
			        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.recipe_info_layout, parent, false);
			HashMap<String,String> map = list.get(position);
			TextView id = (TextView)rowView.findViewById(R.id.RecipeID);
			id.setText(map.get("RecipeID"));
			TextView name = (TextView)rowView.findViewById(R.id.RecipeName);
			name.setText(map.get("RecipeName"));
			TextView time = (TextView)rowView.findViewById(R.id.RecipeTime);
			time.setText(map.get("TotalTime"));
			TextView servings = (TextView)rowView.findViewById(R.id.RecipeServings);
			servings.setText(map.get("NumberOfServings"));
			TextView rating = (TextView)rowView.findViewById(R.id.RecipeRating);
			rating.setText(map.get("AvgRating")+ "/5");
			ImageView photo = (ImageView)rowView.findViewById(R.id.RecipeIcon);
			new DownloadImageTask(photo).execute(map.get("PhotoURL"));
			return rowView;
		}
		
		private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
			  ImageView bmImage;

			  public DownloadImageTask(ImageView bmImage) {
			      this.bmImage = bmImage;
			  }

			  protected Bitmap doInBackground(String... urls) {
			      String urldisplay = urls[0];
			      Bitmap mIcon11 = null;
			      try {
			        InputStream in = new java.net.URL(urldisplay).openStream();
			        mIcon11 = BitmapFactory.decodeStream(in);
			      } catch (Exception e) {
			          e.printStackTrace();
			      }
			      return mIcon11;
			  }

			  protected void onPostExecute(Bitmap result) {
			      bmImage.setImageBitmap(result);
			  }
			}
	}
	
	private class updateAPITtask extends AsyncTask<Void, Void, ArrayList<HashMap<String,String>>>{

		@Override
		protected ArrayList<HashMap<String,String>> doInBackground(Void... params) {
			try {
				KraftAPI api = new KraftAPI(KraftAPI.SEARCHBYKEYWORD, gotKeywords, pagesLoaded);
				return api.getSearchResults();
			} catch (Exception e) {
				Log.e("RecipeHelper", "Failed to create API: " + e.getMessage());
			}
			return new ArrayList<HashMap<String,String>>();
		}

		@Override
		protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
			adapter.addAll(result);
			adapter.notifyDataSetChanged();
			pagesLoaded++;
		}
		
	}

}
