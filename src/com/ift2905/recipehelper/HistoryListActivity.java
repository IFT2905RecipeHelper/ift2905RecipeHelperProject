package com.ift2905.recipehelper;

import java.util.Arrays;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.SimpleCursorAdapter.ViewBinder;

public class HistoryListActivity extends ListActivity implements
LoaderManager.LoaderCallbacks<Cursor>  {

	//Favorites ou History
	String pageType;
	
	Cursor queryResult;
	SimpleCursorAdapter adapter;
	
	static final String[] FROM = RecipeDatabaseHelper.histOrFavColumns;
	static final int[] TO = {
		R.id.RecipeID,
		R.id.RecipeName,
		R.id.RecipeTime,
		R.id.RecipeServings,
		R.id.RecipeRating
	};
	private static final int LOADER_ID = 1;
	
	static final ViewBinder VIEW_BINDER = new ViewBinder() {
		@Override
		public boolean setViewValue(View v, Cursor c, int index) {
			switch( v.getId() ) {
			case R.id.RecipeID:
			case R.id.RecipeName:
			case R.id.RecipeTime:
			case R.id.RecipeServings:
				return false;
			case R.id.RecipeRating:
				int rating = c.getInt(index);
				String ratingString = rating + "/5";
				((TextView)v).setText(ratingString);
				return true;
			}
			
			return false;
		}	    	
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		pageType = getIntent().getStringExtra("pageType");
	}
	
	protected void onResume(){
		super.onResume();
		getLoaderManager().initLoader(LOADER_ID, null, this);

		adapter = new SimpleCursorAdapter(this, R.layout.recipe_info_layout, queryResult, FROM, TO, 0);
		adapter.setViewBinder(VIEW_BINDER);

		setListAdapter(adapter);
		
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		CursorLoader cursorLoader = null;
		if (pageType == null || pageType == "History"){
			cursorLoader = new CursorLoader(this, RecipeContentProvider.getPageUri(RecipeContentProvider.HISTORY),
					RecipeDatabaseHelper.histOrFavColumns, null, null, null);
		} else if (pageType == "Favorites"){
			cursorLoader = new CursorLoader(this, RecipeContentProvider.getPageUri(RecipeContentProvider.FAVORITES),
					RecipeDatabaseHelper.histOrFavColumns, null, null, null);
		}
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		switch (loader.getId()) {
		case LOADER_ID:
			adapter.swapCursor(queryResult);
			break;
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.swapCursor(null);		
	}	
}
