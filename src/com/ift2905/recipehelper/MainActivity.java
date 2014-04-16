package com.ift2905.recipehelper;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

public class MainActivity extends ActionBarActivity implements OnMenuItemClickListener {

	PopupMenu optionMenu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
    	MenuInflater menuInf = getMenuInflater();
		menuInf.inflate(R.menu.action_bar_menu, menu);
		return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
        case R.id.menupopup:
        	showOptionsMenu(findViewById(R.id.menupopup));
        	return true;        
        default:
        	return super.onOptionsItemSelected(item);
        }
    }

    private void showOptionsMenu(View v) {
    	optionMenu = new PopupMenu(this, v);
    	MenuInflater inflater = optionMenu.getMenuInflater();
        inflater.inflate(R.menu.intro_page_menu, optionMenu.getMenu());
        optionMenu.setOnMenuItemClickListener(this);
        optionMenu.show();
	}

	/**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		int id = item.getItemId();
		String pageLaunched;
		switch(id){
		case R.id.shoplistmenuopt:
			pageLaunched = "shop";
			break;
		case R.id.historymenuopt:
			pageLaunched = "History";
			break;
		case R.id.favoritesmenuopt:
			pageLaunched = "Favorites";
			break;
		default:
			return false;
		}
		launchActivity(pageLaunched);
		return true;
	}

	private void launchActivity(String pageLaunched) {
		if (pageLaunched == "History" || pageLaunched == "Favorites"){
			Intent dtbAccessActivity = new Intent(this, HistoryListActivity.class);
			dtbAccessActivity.putExtra("pageType", pageLaunched);
			startActivity(dtbAccessActivity);
		}
	}

}
