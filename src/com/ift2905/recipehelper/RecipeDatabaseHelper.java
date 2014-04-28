package com.ift2905.recipehelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class RecipeDatabaseHelper extends SQLiteOpenHelper {

	static final String[] tableNames = {"shop_list", "history","favorites"};
	static final String[] shopListColumns = {"_id", "ingredient","from_recipe"};
	static final String[] historyColumns = {"_id", "recipe_name", "cooking_time", "servings", "rating"};
	static final String[] favoriColumns = {"_id", "recipe_name", "cooking_time", "servings", "rating"};
	
	Context context;
	public RecipeDatabaseHelper(Context context) {
		super(context, "recipe.db", null, 1);
		this.context=context;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		String createShopListTable = "create table " + tableNames[0] + " ("
				+ shopListColumns[0] + " integer primary key, " 
				+ shopListColumns[1] + " text, "
				+ shopListColumns[2] + " text)";
		
		db.execSQL(createShopListTable);
		
		String historyTable = "create table " + tableNames[1] + " ("
				+ historyColumns[0] + " integer primary key, "
				+ historyColumns[1] + " text, "
				+ historyColumns[2] + " text, "
				+ historyColumns[3] + " text, "
				+ historyColumns[4] + " text)";
			
		db.execSQL(historyTable);
			
		String favoriTable = "create table " + tableNames[2] + " ("
				+ favoriColumns[0] + " integer primary key, "
				+ favoriColumns[1] + " text, "
				+ favoriColumns[2] + " text, "
				+ favoriColumns[3] + " text, "
				+ favoriColumns[4] + " text)";
			
		db.execSQL(favoriTable);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		for (String name: tableNames){
			db.execSQL("drop table if exists "+name);
		}
		onCreate(db);
	}

}
