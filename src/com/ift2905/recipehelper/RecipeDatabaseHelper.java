package com.ift2905.recipehelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RecipeDatabaseHelper extends SQLiteOpenHelper {

	static final String[] tableNames = {"shop_list","history","favorites"};
	static final String[] shopListColumns = {"ingredient","from_recipe","checked"};
	static final String[] histOrFavColumns = {"recipe_id", "recipe_name", "cooking_time", "servings", "rating"};
	Context context;
	public RecipeDatabaseHelper(Context context) {
		super(context, "meteo.db", null, 1);
		this.context=context;
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
