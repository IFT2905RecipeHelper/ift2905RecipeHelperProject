package com.example.monrecipehelper;


import java.util.ArrayList;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.Toast;

public class SLListActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor> {

	static ArrayList<String> recettes = new ArrayList<String>();
	Cursor queryResult;
	SimpleCursorAdapter adapter;
	static final String[] FROM = {RecipeDatabaseHelper.shopListColumns[1],RecipeDatabaseHelper.shopListColumns[2]};
	static final int[] TO = {
		R.id.ingredientRecipe,
		R.id.titleRecipe
	};
	
	private static final int LOADER_ID = 1;	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onResume() {
		super.onResume();
		getLoaderManager().initLoader(LOADER_ID, null, this);
		//adapter = new mySimpleCursorAdapter(this, R.layout.shopping_list_act, queryResult, FROM, TO);
		adapter = new mySimpleCursorAdapter(this, R.layout.rec_ingredients_cont, queryResult, FROM, TO);
		setListAdapter(adapter);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		CursorLoader cursorLoader = null;
		cursorLoader = new CursorLoader(this, RecipeContentProvider.getPageUri(RecipeContentProvider.SHOPPINGLIST), RecipeDatabaseHelper.shopListColumns, null, null, "");
		return cursorLoader;
		
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		switch (loader.getId()) {
		case LOADER_ID:
			adapter.swapCursor(data);
			break;
		}
		
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		adapter.swapCursor(null);		
	}
	
	public class mySimpleCursorAdapter extends SimpleCursorAdapter{
		LayoutInflater mInflater;
		
		public mySimpleCursorAdapter(Context context, int layout, Cursor c,
				String[] from, int[] to) {
			super(context, layout, c, from, to);
			mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			View v=convertView;
			if( v==null ) {				
				//v=mInflater.inflate(R.layout.shopping_list_act, parent,false);
				v=mInflater.inflate(R.layout.rec_ingredients_cont, parent,false);
			}
			
			TextView txt_recipe = (TextView)v.findViewById(R.id.titleRecipe);
			TextView txt_ingredient = (TextView)v.findViewById(R.id.ingredientRecipe);
			//CheckBox cb_recipe = (CheckBox)v.findViewById(R.id.chkRecipe);
			//CheckBox cb_ingredient = (CheckBox)v.findViewById(R.id.chkIngredient);
			ImageButton cb_recipe = (ImageButton)v.findViewById(R.id.chkBtnRecipe);
			ImageButton cb_ingredient = (ImageButton)v.findViewById(R.id.chkBtnIngredient);

			final Cursor c=this.getCursor();
			
			// position demandee
			c.moveToPosition(position);
			String recipe = c.getString(c.getColumnIndex(RecipeDatabaseHelper.shopListColumns[2]));
			String ingredient = c.getString(c.getColumnIndex(RecipeDatabaseHelper.shopListColumns[1]));
			
			// position precedente
			String recipeLast="9999c";
			if( position>0 ) {
				c.moveToPrevious();
				recipeLast = c.getString(c.getColumnIndex(RecipeDatabaseHelper.shopListColumns[2]));
			}
			
			txt_recipe.setText(recipe);
			txt_ingredient.setText(ingredient);
					
			if( recipe.equals(recipeLast) ) {			
				txt_recipe.setVisibility(View.GONE);
				cb_recipe.setVisibility(View.GONE);
			}else{
				txt_recipe.setVisibility(View.VISIBLE);
				cb_recipe.setVisibility(View.VISIBLE);
			}			

			cb_ingredient.setTag(Integer.valueOf(position));	
			cb_ingredient.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					ImageButton cb_ingredient = (ImageButton)v.findViewById(R.id.chkBtnIngredient);
					int pos=(Integer)cb_ingredient.getTag();
					c.moveToPosition(pos);
					String ingredient = c.getString(c.getColumnIndex(RecipeDatabaseHelper.shopListColumns[1]));
					String recipe = c.getString(c.getColumnIndex(RecipeDatabaseHelper.shopListColumns[2]));
					ContentResolver resolver = getContentResolver();
					resolver.delete(RecipeContentProvider.getPageUri(RecipeContentProvider.SHOPPINGLIST), RecipeDatabaseHelper.shopListColumns[1]+" = '"+ingredient+"'"
							+ " AND "+RecipeDatabaseHelper.shopListColumns[2]+" = '"+recipe+"'", null);
				}
				
			});
			
			cb_recipe.setTag(Integer.valueOf(position));
			cb_recipe.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					ImageButton cb_recipe = (ImageButton)v.findViewById(R.id.chkBtnRecipe);
					int pos=(Integer)cb_recipe.getTag();
					c.moveToPosition(pos);
					String recipe = c.getString(c.getColumnIndex(RecipeDatabaseHelper.shopListColumns[2]));
					ContentResolver resolver = getContentResolver();
					resolver.delete(RecipeContentProvider.getPageUri(RecipeContentProvider.SHOPPINGLIST), RecipeDatabaseHelper.shopListColumns[2]+" = '"+recipe+"'", null);
				}
				
			});
			
			return v;
		}

		
		
	}
	
}
