package com.ift2905.recipehelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RecipeDatabaseHelper extends SQLiteOpenHelper {

	static final String[] tableNames = {"shop_list", "history","favorites"};
	static final String[] shopListColumns = {"ingredient","from_recipe","checked"};
	static final String[] histOrFavColumns = {"recipe_id", "recipe_name", "cooking_time", "servings", "rating"};
	Context context;
	public RecipeDatabaseHelper(Context context) {
		super(context, "recipe.db", null, 1);
		this.context=context;
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		String createShopListTable = "create table" + tableNames + " ("
				+ shopListColumns[0] + " text primary_key, " 
				+ shopListColumns[1] + " text, "
				+ shopListColumns[2] + " text)";
		
		db.execSQL(createShopListTable);
		
		for (int i = 1; i < tableNames.length; i++){
			String createTable = "create table" + tableNames[i] + " ("
					+ histOrFavColumns[0] + " text primary_key, "
					+ histOrFavColumns[1] + " text, "
					+ histOrFavColumns[2] + " text, "
					+ histOrFavColumns[3] + " text, "
					+ histOrFavColumns[4] + " integer)";
			
			db.execSQL(createTable);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		for (String name: tableNames){
			db.execSQL("drop table if exists "+name);
		}
		onCreate(db);
	}

}
