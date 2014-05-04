package com.ift2905.recipehelper;


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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class MainActivity_2 extends Activity implements OnPageChangeListener, OnClickListener {

	private ViewPager pager;
	private MonPagerAdapter monAdapter;
	private LinearLayout recetteInfo;
	private Context ctx;

	private TextView etapeRecette;
	private TextView nomRecette;
	private TextView tempsRecette;
	private TextView nutritionRecette;
	private TextView nbServingRecette;
	private RatingBar bar;
	private ImageView icone;

	private KraftAPI recette=null;
	private String recipe_id="50257";
	private String nbServing="";
	private String rating="";
	private String acc="";
	private String ncc="";
	private String test="";

	private Button favoriButton;
	private Button showListButton;


	private ArrayList<String> ingredientList = new ArrayList<String>();
	private ArrayList<String> ingredientNameList = new ArrayList<String>();
	private ArrayList<String> ingredientIDList = new ArrayList<String>();

	private ListView listView1;
	private IngredientAdapter mainAdapter;

	// Minuteur
	private TextView text;
	long starttime = 0;
	long pauseTime=0;
	long resumeTime=0;
	private String b2State="pause";
	private Button b2;
	private Button b;
	private Button setTime;
	private EditText Ehours;
	private EditText Eminutes;
	private EditText Eseconds;
	private int rTime=0;
	private Timer timer = new Timer();

	final Handler h = new Handler(new Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			long millis = System.currentTimeMillis() - starttime;
			int seconds = (int) (millis / 1000); 
			seconds=rTime-seconds;
			int heures= seconds / 3600;
			int minutes;
			if(heures>=1)
				minutes = (seconds-heures*3600) / 60;
			else minutes = seconds / 60;
			seconds     = seconds % 60;

			String her="";
			String min="";
			String sec="";   

			if((""+heures).length()==1)
				her="0"+heures;
			else
				her=""+heures;  
			if((""+minutes).length()==1)
				min="0"+minutes;
			else
				min=""+minutes;  	      	  
			if((""+seconds).length()==1)
				sec="0"+seconds;
			else
				sec=""+seconds;

			text.setText(her+":"+min+":"+sec);
			if(seconds==0&& minutes==0&& heures==0){
				timer.cancel();
				timer.purge();
				b.setText("start");	
			}
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


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		recipe_id = getIntent().getStringExtra("recipeID");
		ctx=this;

		getWindow().requestFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		setContentView(R.layout.activity_main_2);

		recetteInfo=(LinearLayout)findViewById(R.id.recetteInfo);

		nomRecette=(TextView)findViewById(R.id.RecipeName);
		tempsRecette=(TextView)findViewById(R.id.RecipeTime);
		icone=(ImageView)findViewById(R.id.RecipeIcone);
		bar=(RatingBar)findViewById(R.id.ratingBar);

		favoriButton = (Button)findViewById(R.id.favoriButton);
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

	}
	
	@Override
    protected void onResume(){
    	super.onResume();
		new DownloadLoginTask().execute();

    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
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
				showListButton = (Button) page.findViewById(R.id.showListButton);

				showListButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) 
					{
						Button showListButton = (Button)v;

					}
				});

			}
			else if(position==2)
			{
				page=(LinearLayout)inflater.inflate(R.layout.etapes, null);
				etapeRecette=(TextView) page.findViewById(R.id.textViewEtapes);
				etapeRecette.setText(acc);

				//Minuteur Code
				text = (TextView) page.findViewById(R.id.minuteurView);
				Ehours = (EditText) page.findViewById(R.id.editHours);
				Eminutes = (EditText) page.findViewById(R.id.editMinutes);
				Eseconds = (EditText) page.findViewById(R.id.editSeconds);
				setTime = (Button) page.findViewById(R.id.SetTime);

				setTime.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) 
					{
						int heuresEntree=0, minutesEntree=0, secondsEntree=0;

						if(!Ehours.getText().toString().equals("")){
							heuresEntree = Integer.parseInt(Ehours.getText().toString());}

						if(!Eminutes.getText().toString().equals("")){
							minutesEntree = Integer.parseInt(Eminutes.getText().toString());}

						if(!Eseconds.getText().toString().equals("")){
							secondsEntree = Integer.parseInt(Eseconds.getText().toString());}

						// TODO Auto-generated method stub
						if( heuresEntree >= 0 )
						{
						} else {
							Context context = getApplicationContext();
							CharSequence text = "Entree invalide pour les heures";
							int duration = Toast.LENGTH_SHORT;
							Toast toast = Toast.makeText(context, text, duration);
							toast.show(); 
						}

						if( minutesEntree >= 0 && minutesEntree < 60)
						{
						} else {
							Context context = getApplicationContext();
							CharSequence text = "Entree invalide les minutes";
							int duration = Toast.LENGTH_SHORT;
							Toast toast = Toast.makeText(context, text, duration);
							toast.show(); 
						}

						if(secondsEntree >= 0 && secondsEntree < 60)
						{
						} else {
							Context context = getApplicationContext();
							CharSequence text = "Entree invalide pour les secondes";
							int duration = Toast.LENGTH_SHORT;
							Toast toast = Toast.makeText(context, text, duration);
							toast.show(); 
						}
						Ehours.setVisibility(View.INVISIBLE);
						Eminutes.setVisibility(View.INVISIBLE);
						Eseconds.setVisibility(View.INVISIBLE);
						setTime.setVisibility(View.INVISIBLE);

						rTime= heuresEntree*3600 + 	minutesEntree*60 + secondsEntree;
						
						String her="";
						String min="";
						String sec="";
						if((""+rTime/3600).length()==1)
							her="0"+rTime/3600;
						else
							her=""+rTime/3600;
						if((""+(rTime- (rTime/3600)*3600)/60).length()==1)
							min="0"+((rTime- (rTime/3600)*3600)/60);
						else
							min=""+((rTime- (rTime/3600)*3600)/60);


						if((""+rTime%60).length()==1)
							sec="0"+rTime%60;
						else
							sec=""+rTime%60;
						text.setText(her+":"+min+":"+sec);

					}
				});

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
							}else
							{
								timer.cancel();
								timer.purge();
							}

							String her="";
							String min="";
							String sec="";
							if((""+rTime/3600).length()==1)
								her="0"+rTime/3600;
							else
								her=""+rTime/3600;
							if((""+(rTime- (rTime/3600)*3600)/60).length()==1)
								min="0"+((rTime- (rTime/3600)*3600)/60);
							else
								min=""+((rTime- (rTime/3600)*3600)/60);


							if((""+rTime%60).length()==1)
								sec="0"+rTime%60;
							else
								sec=""+rTime%60;
							text.setText(her+":"+min+":"+sec);
							
							Ehours.setVisibility(View.VISIBLE);
							Eminutes.setVisibility(View.VISIBLE);
							Eseconds.setVisibility(View.VISIBLE);
							setTime.setVisibility(View.VISIBLE);

							b.setText("start");

						}else
						{
							starttime = System.currentTimeMillis();
							timer = new Timer();
							timer.schedule(new firstTask(), 0,500);
							
							Ehours.setVisibility(View.INVISIBLE);
							Eminutes.setVisibility(View.INVISIBLE);
							Eseconds.setVisibility(View.INVISIBLE);
							setTime.setVisibility(View.INVISIBLE);
							
							
							
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
		favoriButton = (Button)v;

		ContentResolver resolverFav = getContentResolver();

		if(favoriButton.getText().equals("Add to favorites"))
		{
			ContentValues valFav = new ContentValues();
			valFav.clear();
			valFav.put(RecipeDatabaseHelper.histOrFavColumns[0],(recette.description).get("RecipeID"));
			valFav.put(RecipeDatabaseHelper.histOrFavColumns[1], (recette.description).get("RecipeName"));
			valFav.put(RecipeDatabaseHelper.histOrFavColumns[2], (recette.description).get("TotalTime"));
			valFav.put(RecipeDatabaseHelper.histOrFavColumns[3], (recette.description).get("NumberOfServings"));
			valFav.put(RecipeDatabaseHelper.histOrFavColumns[4], (recette.description).get("AvgRating"));

			resolverFav.insert(RecipeContentProvider.getPageUri(RecipeContentProvider.FAVORITES), valFav);

			Log.d("OK", "BDD_Fav_ADD");
			Toast.makeText(ctx,"Added to favorites",Toast.LENGTH_SHORT).show();

			favoriButton.setText("Remove from favorites");
			//favoriButton.setBackgroundColor(Color.rgb(255,255,153));
		}
		else
		{

			resolverFav.delete(RecipeContentProvider.getPageUri(RecipeContentProvider.FAVORITES), "_id="+(recette.description).get("RecipeID"), null);

			Log.d("OK", "BDD_Fav_SUPP");
			Toast.makeText(ctx,"Removed from favorites",Toast.LENGTH_SHORT).show();

			favoriButton.setText("Add to favorites");


		}
	}



	private class DownloadLoginTask extends AsyncTask<String, String, KraftAPI> {

		protected void onPreExecute() {
			setProgressBarIndeterminateVisibility(true);
			recetteInfo.setVisibility(View.INVISIBLE);
			pager.setVisibility(View.INVISIBLE);


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
				Toast.makeText(MainActivity_2.this, "Oups!", Toast.LENGTH_SHORT).show();
				return;
			}

			if( api.erreur != null ) {
				Toast.makeText(MainActivity_2.this, api.erreur, Toast.LENGTH_SHORT).show();
				return;
			}
			recetteInfo.setVisibility(View.VISIBLE);
			pager.setVisibility(View.VISIBLE);


			//NumbersOfServings
			nbServing=(api.description).get("NumberOfServings");
			rating=(api.description).get("AvgRating");

			nomRecette.setText((api.description).get("RecipeName"));
			tempsRecette.setText((api.description).get("TotalTime") + " minutes");
			bar.setRating(Float.valueOf(rating));

			nbServingRecette.setText(nbServing);

			ContentResolver resolverHis = getContentResolver();
			ContentValues valHis = new ContentValues();
			valHis.clear();
			valHis.put(RecipeDatabaseHelper.histOrFavColumns[0],(api.description).get("RecipeID"));
			valHis.put(RecipeDatabaseHelper.histOrFavColumns[1], (api.description).get("RecipeName"));
			valHis.put(RecipeDatabaseHelper.histOrFavColumns[2], (api.description).get("TotalTime"));
			valHis.put(RecipeDatabaseHelper.histOrFavColumns[3], (api.description).get("NumberOfServings"));
			valHis.put(RecipeDatabaseHelper.histOrFavColumns[4], (api.description).get("AvgRating"));

			resolverHis.insert(RecipeContentProvider.getPageUri(RecipeContentProvider.HISTORY), valHis);
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

			ingredientNameList=(api.ingredientName);
			ingredientIDList=(api.ingredientID);

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
			nutritionRecette.setText(ncc);

			recette=api;
		}
	}

	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
		ImageView bmImage;
		Bitmap mIcon11 = null;

		public DownloadImageTask(ImageView bmImage) {
			this.bmImage = bmImage;
		}

		protected Bitmap doInBackground(String... urls) {
			String urldisplay = urls[0];

			try {
				InputStream in = new java.net.URL(urldisplay).openStream();
				mIcon11 = BitmapFactory.decodeStream(in);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return mIcon11;
		}

		protected void onPostExecute(Bitmap result) {
			bmImage.setImageBitmap(mIcon11);
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
				//LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inf.inflate(R.layout.list, parent, false);
			}

			TextView recipeIngredient = (TextView) view.findViewById(R.id.recipeIngredient);
			CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox);

			recipeIngredient.setText(l.get(position));

			view.setTag(Integer.valueOf(position));

			checkBox.setTag(position);

			checkBox.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					int pos = (Integer) view.getTag();

					final String ing_name = ingredientNameList.get(pos);
					final String ing_id = ingredientIDList.get(pos);

					ContentResolver resolverShop = getContentResolver();
					ContentValues valShop = new ContentValues();
					boolean checked = ((CheckBox) view).isChecked();

					if (checked) {
						//Ajoute a la Base de donnees pour la shopping list
						valShop.clear();
						valShop.put(RecipeDatabaseHelper.shopListColumns[0],ing_id);
						valShop.put(RecipeDatabaseHelper.shopListColumns[1], ing_name);
						valShop.put(RecipeDatabaseHelper.shopListColumns[2], recette.description.get("RecipeName"));

						resolverShop.insert(RecipeContentProvider.getPageUri(RecipeContentProvider.SHOPPINGLIST), valShop);
						Toast.makeText(ctx,"Added to the shopping list",Toast.LENGTH_SHORT).show();
						Log.d("OK", "BDD_Shop_ADD");
					} 
					else {
						//retirer de la shopping liste
						resolverShop.delete(RecipeContentProvider.getPageUri(RecipeContentProvider.SHOPPINGLIST), "_id="+ing_id , null);
						Toast.makeText(ctx,"Removed from the shopping list",Toast.LENGTH_SHORT).show();
						Log.d("OK", "BDD_Shop_SUPP");
					}

				}
			});

			return view;
		}
	}
}
