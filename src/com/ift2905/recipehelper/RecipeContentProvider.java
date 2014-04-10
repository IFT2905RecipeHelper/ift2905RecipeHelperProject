package com.ift2905.recipehelper;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
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
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		
		//Code pretty much taken from MeteoContentProvider
		int uriType = sURIMatcher.match(uri);
		
		switch (uriType) {
		case SHOPPINGLIST:
		case HISTORY:
		case FAVORITES:
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		
		queryBuilder.setTables(RecipeDatabaseHelper.tableNames[uriType-1]);
		
		SQLiteDatabase db = rdh.getWritableDatabase();
		Cursor cursor = queryBuilder.query(db, projection, selection,
				selectionArgs, null, null, sortOrder);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);

		return cursor;
		
		
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = rdh.getWritableDatabase();
		switch (uriType) {
		case SHOPPINGLIST:
			try {
				sqlDB.insertOrThrow(RecipeDatabaseHelper.tableNames[0], null, values);
			} catch ( SQLException e ) { return null; }
			break;
		case HISTORY:
			try {
				sqlDB.insertOrThrow(RecipeDatabaseHelper.tableNames[1], null, values);
			} catch ( SQLException e ) { return null; }
			break;
		case FAVORITES:
			try {
				sqlDB.insertOrThrow(RecipeDatabaseHelper.tableNames[2], null, values);
			} catch ( SQLException e ) { return null; }
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = rdh.getWritableDatabase();
		int rowsDeleted = 0;
		switch (uriType) {
		case SHOPPINGLIST:
			rowsDeleted = sqlDB.delete(RecipeDatabaseHelper.tableNames[0], selection,
					selectionArgs);
			break;
		case HISTORY:
			rowsDeleted = sqlDB.delete(RecipeDatabaseHelper.tableNames[1], selection,
					selectionArgs);
			break;
		case FAVORITES:
			rowsDeleted = sqlDB.delete(RecipeDatabaseHelper.tableNames[2], selection,
					selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsDeleted;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = rdh.getWritableDatabase();
		int rowsUpdated = 0;
		switch (uriType) {
		case SHOPPINGLIST:
			rowsUpdated = sqlDB.update(RecipeDatabaseHelper.tableNames[0], values, selection,
					selectionArgs);
			break;
		case HISTORY:
			rowsUpdated = sqlDB.update(RecipeDatabaseHelper.tableNames[1], values, selection,
					selectionArgs);
			break;
		case FAVORITES:
			rowsUpdated = sqlDB.update(RecipeDatabaseHelper.tableNames[2], values, selection,
					selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsUpdated;
	}
}
