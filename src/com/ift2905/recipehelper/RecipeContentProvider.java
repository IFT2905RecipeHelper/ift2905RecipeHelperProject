package com.ift2905.recipehelper;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

public class RecipeContentProvider extends ContentProvider {
	
	RecipeDatabaseHelper rdh;

	private static final String AUTHORITY = "com.ift2905.recipehelper";
	
	public static final int SHOPPINGLIST = 1;
	public static final int HISTORY = 2;
	public static final int FAVORITES = 3;
	private static final int[] ALLCODES = {SHOPPINGLIST, HISTORY, FAVORITES};
	
	private static final String PATHSHOPLIST = "shop_list";
	private static final String PATHHISTORY = "history";
	private static final String PATHFAVORITES = "favorites";
	private static final String[] ALLPATHS = {PATHSHOPLIST, PATHHISTORY, PATHFAVORITES};
	
	private static final  UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		for (int i = 0; i < ALLCODES.length; i++){
			sURIMatcher.addURI(AUTHORITY, ALLPATHS[i], ALLCODES[i]);
		}
		
	}
	@Override
	public boolean onCreate() {
		rdh = new RecipeDatabaseHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}
}
