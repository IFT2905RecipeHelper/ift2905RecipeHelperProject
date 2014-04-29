package com.example.recipehelper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import org.xmlpull.v1.XmlPullParserException;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class MainActivity_2 extends Activity implements OnPageChangeListener, OnClickListener {

	private ViewPager pager;
	private MonPagerAdapter monAdapter;
	private Context ctx;

	private TextView etapeRecette;
	private TextView nomRecette;
	private TextView tempsRecette;
	private TextView nutritionRecette;
	private TextView nbServingRecette;
	private TextView avgRecette;
	private ImageView icone;

	private KraftAPI recette=null;
	private String recipe_id="50257";
	private String nbServing="";
	private String acc="";
	private String ncc="";
	private String test="";

	private Button favoriButton;

	private ArrayList<String> ingredientList = new ArrayList<String>();
	private ListView listView1;
	private IngredientAdapter mainAdapter;

	// Minuteur
	TextView text;
	long starttime = 0;
	long pauseTime=0;
	long resumeTime=0;
	String b2State="pause";
	Button b2;
	Button b;
	//il faut que tu set rTime avec les entrées EN SECONDES
	int rTime=10;
	Timer timer = new Timer();

	final Handler h = new Handler(new Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			long millis = System.currentTimeMillis() - starttime;
			int seconds = (int) (millis / 1000); 
			seconds=rTime-seconds;
			int minutes = seconds / 60;
			seconds     = seconds % 60;
			String min="";
			String sec="";    	  
			if((""+minutes).length()==1)
				min="0"+minutes;
			else
				min=""+minutes;  	      	  
			if((""+seconds).length()==1)
				sec="0"+seconds;
			else
				sec=""+seconds;

			text.setText(min+":"+sec);
			if(seconds==0){
				timer.cancel();
				timer.purge();
				b.setText("start");					
			}
			return false;
		}
	});
	//remise a ziwow
	final Handler h2 = new Handler(new Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			String min="";
			String sec="";    	  
			if((""+rTime/60).length()==1)
				min="0"+rTime/60;
			else
				min=""+rTime/60;    	      	  
			if((""+rTime%60).length()==1)
				sec="0"+rTime%60;
			else
				sec=""+rTime%60;

			text.setText(min+":"+sec);
			return false;
		}
	});

	//tells handler to send a message
	class firstTask extends TimerTask {

		@Override
		public void run() {
			h.sendEmptyMessage(0);
		}
	};

	class resetTask extends TimerTask {

		@Override
		public void run() {
			h2.sendEmptyMessage(0);
		}
	};


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);



		ctx=this;

		getWindow().requestFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		setContentView(R.layout.activity_main);

		favoriButton = (Button)findViewById(R.id.favoriButton);

		nomRecette=(TextView)findViewById(R.id.RecipeName);
		tempsRecette=(TextView)findViewById(R.id.RecipeTime);
		icone=(ImageView)findViewById(R.id.RecipeIcone);
		avgRecette=(TextView)findViewById(R.id.RecipeRatingBar);

		favoriButton.setOnClickListener(this);


		pager=(ViewPager)findViewById(R.id.awesomepager);
		monAdapter = new MonPagerAdapter();
		pager.setAdapter(monAdapter);
		pager.setOnPageChangeListener(this);

		// Tabs de l'action bar
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		ActionBar.TabListener tabListener = new ActionBar.TabListener() {
			@Override
			public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
				pager.setCurrentItem(tab.getPosition());
			}
			@Override
			public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
			}
			@Override
			public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
			}
		};

		for (int i = 0; i < monAdapter.getCount(); i++) {
			actionBar.addTab(
					actionBar.newTab()
					.setText(monAdapter.getPageTitle(i))
					.setTabListener(tabListener));
		}

		pager.setOnPageChangeListener(
				new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						getActionBar().setSelectedNavigationItem(position);
					}
				});

		new DownloadLoginTask().execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	class MonPagerAdapter extends PagerAdapter {
		LayoutInflater inflater;

		MonPagerAdapter() {
			inflater= (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			if(position==0)
				return "Description";
			if(position==1)
				return "Ingredients";
			if(position==2)
				return "Etapes";
			return null;
		}

		@Override
		public boolean isViewFromObject(View v, Object ob) { return v==(View)ob; }

		@Override
		public Object instantiateItem(View container, int position) {

			LinearLayout page = null;

			if(position==0)
			{
				page=(LinearLayout)inflater.inflate(R.layout.description, null);
				nbServingRecette=(TextView) page.findViewById(R.id.nbServing);
				nutritionRecette=(TextView) page.findViewById(R.id.nutrition);
				
				nbServingRecette.setText(nbServing);
				nutritionRecette.setText(ncc);
			} 
			else if(position==1)
			{
				page=(LinearLayout)inflater.inflate(R.layout.ingredients, null);
				mainAdapter = new IngredientAdapter(ingredientList,ctx,inflater);
				listView1 = (ListView) page.findViewById(R.id.listView1);
				listView1.setAdapter(mainAdapter);
			}
			else if(position==2)
			{
				page=(LinearLayout)inflater.inflate(R.layout.etapes, null);
				etapeRecette=(TextView) page.findViewById(R.id.textViewEtapes);
				etapeRecette.setText(acc);
				
				//Minuteur Code
				text = (TextView)page.findViewById(R.id.minuteurView);
				//the text
				String min="";
				String sec="";    	  
				if((""+rTime/60).length()==1)
					min="0"+rTime/60;
				else
					min=""+rTime/60;    	      	  
				if((""+rTime%60).length()==1)
					sec="0"+rTime%60;
				else
					sec=""+rTime%60;
				text.setText(min+":"+sec);

				b = (Button)page.findViewById(R.id.SetReset);
				b.setText("start");
				b2 = (Button)page.findViewById(R.id.PauseContinue);
				b2.setText("pause");
				b.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Button b = (Button)v;
						if(b.getText().equals("reset")){
							if(!b2State.equals("pause")){
								timer.cancel();
								timer.purge();
								b2.setText("pause");
								//timer = new Timer();
							}else{
								timer.cancel();
								timer.purge();}

							//  timer.schedule(new resetTask(), 0,500);  

							String min="";
							String sec="";    	  
							if((""+rTime/60).length()==1)
								min="0"+rTime/60;
							else
								min=""+rTime/60;    	      	  
							if((""+rTime%60).length()==1)
								sec="0"+rTime%60;
							else
								sec=""+rTime%60;

							text.setText(min+":"+sec);
							b.setText("start");
						}else /*if(b.getTag().equals(start))*/{
							starttime = System.currentTimeMillis();
							timer = new Timer();
							timer.schedule(new firstTask(), 0,500);
							b.setText("reset");
						}

					}
				});
				b2.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Button b2 = (Button)v;
						if(b2.getText().equals("pause")){
							if(b.getText().equals("start"));
							else{
								timer.cancel();
								timer.purge();
								pauseTime= System.currentTimeMillis();
								b2.setText("continue");
								b2State=(String) b2.getText();}
						}else /*if(b2.getTag().equals(start))*/{
							resumeTime = System.currentTimeMillis();
							starttime = starttime + (resumeTime-pauseTime);             
							timer = new Timer();
							timer.schedule(new firstTask(), 0,500);
							b2.setText("pause");
							b2State=(String) b2.getText();

						}

					}
				});
				//Minuteur Code Fin
			}

			((ViewPager)container).addView(page,0);
			return page;
		}

		@Override
		public void destroyItem(View collection, int position, Object view) {
			((ViewPager) collection).removeView((View) view);
		}
	}
	@Override
	public void onPageScrollStateChanged(int arg0) { }
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) { }
	@Override
	public void onPageSelected(int position) { }	
	
	public void onPause() {super.onPause();}


	public void onClick(View v) {

		switch( v.getId() ) {
		case R.id.favoriButton:
			ContentResolver resolverFav = getContentResolver();
			ContentValues valFav = new ContentValues();
			valFav.clear();
			valFav.put(RecipeDatabaseHelper.favoriColumns[0],(recette.description).get("RecipeID"));
			valFav.put(RecipeDatabaseHelper.favoriColumns[1], (recette.description).get("RecipeName"));
			valFav.put(RecipeDatabaseHelper.favoriColumns[2], (recette.description).get("TotalTime"));
			valFav.put(RecipeDatabaseHelper.favoriColumns[3], (recette.description).get("NumberOfServings"));
			valFav.put(RecipeDatabaseHelper.favoriColumns[4], (recette.description).get("AvgRating"));

			resolverFav.insert(RecipeContentProvider.CONTENT_URI_FAV, valFav);

			Log.d("OK", "BDD_Fav");
			Toast.makeText(ctx,"Added to favorites list ",Toast.LENGTH_SHORT).show();
			break;
		}
	}



	private class DownloadLoginTask extends AsyncTask<String, String, KraftAPI> {

		protected void onPreExecute() {
			setProgressBarIndeterminateVisibility(true);
		}

		protected KraftAPI doInBackground(String... params) {
			KraftAPI api=null;
			try {
				api = new KraftAPI(KraftAPI.SEARCHBYID,recipe_id);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}   
			return api;
		}

		protected void onProgressUpdate(String... s) { }

		protected void onPostExecute(KraftAPI api) {
			setProgressBarIndeterminateVisibility(false);

			if( api == null ) {
				Toast.makeText(MainActivity_2.this, "FatalError", Toast.LENGTH_SHORT).show();
				return;
			}

			if( api.erreur != null ) {
				Toast.makeText(MainActivity_2.this, api.erreur, Toast.LENGTH_SHORT).show();
				return;
			}

			//NumbersOfServings
			nbServing=(api.description).get("NumberOfServings");

			nomRecette.setText((api.description).get("RecipeName"));
			tempsRecette.setText((api.description).get("TotalTime") + " minutes");
			avgRecette.setText((api.description).get("AvgRating"));

			ContentResolver resolverHis = getContentResolver();
			ContentValues valHis = new ContentValues();
			valHis.clear();
			valHis.put(RecipeDatabaseHelper.historyColumns[0],(api.description).get("RecipeID"));
			valHis.put(RecipeDatabaseHelper.historyColumns[1], (api.description).get("RecipeName"));
			valHis.put(RecipeDatabaseHelper.historyColumns[2], (api.description).get("TotalTime"));
			valHis.put(RecipeDatabaseHelper.historyColumns[3], (api.description).get("NumberOfServings"));
			valHis.put(RecipeDatabaseHelper.historyColumns[4], (api.description).get("AvgRating"));

			resolverHis.insert(RecipeContentProvider.CONTENT_URI_HIS, valHis);
			Log.d("OK", "BDD_His");

			Log.d((api.description).get("PhotoURL"),"BAD");
			new DownloadImageTask(icone).execute((api.description).get("PhotoURL"));

			int i=1;
			for(String element:(api.etapes))
			{
				acc += i + ". " + element + "\n\n";
				i++;
			}
			Log.d("ACC",acc);

			//Ingredients passe de Hashmap a List
			Set<String> keys=api.ingredients.keySet();
			Iterator<String> it=keys.iterator();
			while(it.hasNext()){
				Object cle=it.next();
				ingredientList.add((api.ingredients).get(cle));
			}

			//Verification Ingredients
			for(String element:(ingredientList))
			{
				test += element + "\n";
			}
			Log.d("BCC",test);


			//Nutrition
			for(String element:(api.nutritionDetail))
			{
				ncc += element + "\n";
			}
			recette=api;
		}
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

	private class IngredientAdapter extends BaseAdapter
	{
		ArrayList<String> l;
		boolean[] checkBoxState;
		Context c;
		LayoutInflater inf;


		IngredientAdapter(ArrayList<String> l, Context c, LayoutInflater inf)
		{
			this.l = l;
			this.c = c;
			this.inf=inf;
			checkBoxState = new boolean[l.size()];

		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return l.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return l.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			// TODO Auto-generated method stub

			if (view == null) {
				//				LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inf.inflate(R.layout.list, parent, false);
			}

			TextView recipeIngredient = (TextView) view.findViewById(R.id.recipeIngredient);
			CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox);

			recipeIngredient.setText(l.get(position));

			view.setTag(Integer.valueOf(position));

			checkBox.setTag(position);

			/*	checkBox.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					int pos = (Integer) view.getTag();

					final DataEquipe dataEquipe = listEquipe.get(pos);

					DBHelper dbh = new DBHelper(context);

					boolean checked = ((CheckBox) view).isChecked();

					if (checked) {
						//Marquer comme favori
						dbh.addNewFavori(Integer.parseInt(dataEquipe.getiDTeam()), dataEquipe.getNomEquipe());
					} 
					else {
						//Marquer comme non favori
						dbh.deleteFavoris(Integer.parseInt(dataEquipe.getiDTeam()));
					}

				}
			});  */
			return view;
		}
	}
}
