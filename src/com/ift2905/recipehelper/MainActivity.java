package com.ift2905.recipehelper;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.support.v7.widget.SearchView;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity implements OnMenuItemClickListener {

	PopupMenu optionMenu;
	RecipeAdapter adapter;
	ListView recipeOfTheWeekList;
	ArrayList<HashMap<String,String>> recipesOfTheWeek = new ArrayList<HashMap<String,String>>();
	Context context = this;
	LinearLayout mainLayout;
	
	Thread API_THREAD;
	
	final Thread NOTIFY_THREAD = new Thread(new Runnable(){

		@Override
		public void run() {
	        adapter = new RecipeAdapter(context, recipesOfTheWeek);
	        recipeOfTheWeekList.setAdapter(adapter);
		}
		
	});
	
	final Thread API_FAILED_THREAD = new Thread(new Runnable(){

		@Override
		public void run() {
			TextView didNotConnect = new TextView(context);
			didNotConnect.setText("Thank you for using Recipe Helper. Please connect to the internet do that you may search and view recipes.");
			didNotConnect.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			didNotConnect.setTextSize(16);
			//mainLayout.addView(didNotConnect);
		}
		
	});
	
	String[] from =  {"RecipeID", "RecipeName", "TotalTime", "NumberOfServings", "AvgRating", "PhotoURL"};
	int[] to = {R.id.RecipeID, R.id.RecipeName, R.id.RecipeTime, R.id.RecipeServings, R.id.RecipeRating, R.id.RecipeIcon};
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainLayout = (LinearLayout)findViewById(R.id.container);
        setContentView(R.layout.activity_main);
        recipeOfTheWeekList = (ListView)findViewById(R.id.RecipesOfTheWeek);
    }
    
    @Override
    protected void onResume(){
    	super.onResume();
    	
    	//Need to reistantiate thread to reset hasBeenStarted
    	API_THREAD = new Thread(new Runnable(){

    		@Override
    		public void run() {
    			try { 
    			KraftAPI api = new KraftAPI(KraftAPI.RECIPESOFTHEWEEK);
    			recipesOfTheWeek = api.getSearchResults();
    			runOnUiThread(NOTIFY_THREAD);
    			} catch (Exception e){
    				runOnUiThread(API_FAILED_THREAD);
    			}
    		}		
    	});
    	API_THREAD.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
    	MenuInflater menuInf = getMenuInflater();
		menuInf.inflate(R.menu.action_bar_menu, menu);
		MenuItem searchItem = menu.findItem(R.id.initsearch);
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
	    SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
	    searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
		return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
        case R.id.menupopup:
        	showOptionsMenu(findViewById(R.id.menupopup));
        	return true;        
        default:
        	return super.onOptionsItemSelected(item);
        }
    }

    private void showOptionsMenu(View v) {
    	optionMenu = new PopupMenu(this, v);
    	MenuInflater inflater = optionMenu.getMenuInflater();
        inflater.inflate(R.menu.intro_page_menu, optionMenu.getMenu());
        optionMenu.setOnMenuItemClickListener(this);
        optionMenu.show();
	}

	/**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		int id = item.getItemId();
		String pageLaunched;
		switch(id){
		case R.id.shoplistmenuopt:
			pageLaunched = "shop";
			break;
		case R.id.historymenuopt:
			pageLaunched = "History";
			break;
		case R.id.favoritesmenuopt:
			pageLaunched = "Favorites";
			break;
		default:
			return false;
		}
		launchActivity(pageLaunched);
		return true;
	}

	private void launchActivity(String pageLaunched) {
		if (pageLaunched.equals("History") || pageLaunched.equals("Favorites")){
			Intent dtbAccessActivity = new Intent(this, HistoryListActivity.class);
			dtbAccessActivity.putExtra("pageType", pageLaunched);
			startActivity(dtbAccessActivity);
		} else if (pageLaunched.equals("shop")){
			Intent dtbAccessActivity = new Intent(this, SLListActivity.class);
			startActivity(dtbAccessActivity);
		}
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
			time.setText(map.get("TotalTime")+" minutes");
			TextView servings = (TextView)rowView.findViewById(R.id.RecipeServings);
			servings.setText(map.get("NumberOfServings"));
			TextView rating = (TextView)rowView.findViewById(R.id.RecipeRating);
			rating.setText(map.get("AvgRating")+ "/5");
			ImageView photo = (ImageView)rowView.findViewById(R.id.RecipeIcon);
			new DownloadImageTask(photo).execute(map.get("PhotoURL"));
			rowView.setTag(map.get("RecipeID"));
			return rowView;
		}
		
		/*
		 * This is code from stackoverflow
		 * http://stackoverflow.com/questions/5776851/load-image-from-url
		 * We need this in order to use the images provided to use by the kraft API
		 * The code lets us load the picture on a seperate thread so as to not slow down the activity.
		 */
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
	
	public void openRecipePage(View v){
		Intent recipeAccessActivity = new Intent(this, MainActivity_2.class);
		recipeAccessActivity.putExtra("recipeID", (String)v.getTag());
		startActivity(recipeAccessActivity);
	}
}
