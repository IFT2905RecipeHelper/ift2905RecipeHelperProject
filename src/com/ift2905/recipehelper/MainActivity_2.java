package com.example.olavapayer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity_2 extends Activity implements OnPageChangeListener {

	ViewPager pager;
	MonPagerAdapter monAdapter;
	Context ctx;
	
	//TextView tvInfo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_2);

		ctx=this;
		
		pager=(ViewPager)findViewById(R.id.awesomepager);
		monAdapter = new MonPagerAdapter();
		pager.setAdapter(monAdapter);
		pager.setOnPageChangeListener(this);
		
		//tvInfo=(TextView)findViewById(R.id.textView1);
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
			// on va utiliser les services d'un "inflater"
			inflater= (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		
		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public boolean isViewFromObject(View v, Object ob) { return v==(View)ob; }

		@Override
		public Object instantiateItem(View container, int position) {
			
			Toast.makeText(ctx,"Page "+position, Toast.LENGTH_LONG).show();
			
			LinearLayout page;
			
			if(position==0) {
				page=(LinearLayout)inflater.inflate(R.layout.description, null);
				
			} 
			else if(position==1){
				page=(LinearLayout)inflater.inflate(R.layout.ingredients, null);			
			}
			else{
				page=(LinearLayout)inflater.inflate(R.layout.etapes, null);			
			}
			
			// On doit ensuite l'ajouter au parent fourni
			((ViewPager)container).addView(page,0);
			
			return page;
		}

		@Override
		public void destroyItem(View collection, int position, Object view) {
			Toast.makeText(ctx,"DESTROY "+position,Toast.LENGTH_SHORT).show();
			
			// On commence par enlever notre page du parent
			((ViewPager) collection).removeView((View) view);
		}

		@Override
		public void finishUpdate(View v) {}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {}

		@Override
		public Parcelable saveState() { return null; }

		@Override
		public void startUpdate(View arg0) {}
		
	}

	@Override
	public void onPageScrollStateChanged(int arg0) { }

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) { }

	@Override
	public void onPageSelected(int position) {
		//tvInfo.setText("La page "+position+" a ete choisie!!!!");
	}
	
	
}
