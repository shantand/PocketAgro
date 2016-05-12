package com.agrocouch;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;


public class UasrMainActivity extends TabActivity {

	ImageView imageView;
	//TextView update;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.tabs_display);
                
        imageView=(ImageView) findViewById(R.id.imageView1);
       // update=(TextView) (findViewById(R.id.update));
      //  update.setText("Last Update 29/10/2013");
        Resources res = getResources();
        Intent search= new Intent(this,SearchActivity.class);
        Intent Bookmark= new Intent(this,BookmarkList.class);
        Intent Browse= new Intent(this,BrowseActivity.class);
        Intent Create = new Intent(this,Insert_new_article.class);
        
        TabHost mTabHst = getTabHost();
        
        mTabHst.addTab(mTabHst.newTabSpec("text").setIndicator("Search",res.getDrawable(R.drawable.one)).setContent(search));
        mTabHst.addTab(mTabHst.newTabSpec("tab_test2").setIndicator("Browse",res.getDrawable(R.drawable.two)).setContent(Browse));
        mTabHst.addTab(mTabHst.newTabSpec("tab_test3").setIndicator("Create",res.getDrawable(R.drawable.three)).setContent(Create));
        mTabHst.addTab(mTabHst.newTabSpec("tab_test4").setIndicator("Bookmark",res.getDrawable(R.drawable.four)).setContent(Bookmark));
        
        mTabHst.setCurrentTab(0); 
        
       
    }
}
	
	
	