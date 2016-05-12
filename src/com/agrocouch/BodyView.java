package com.agrocouch;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Toast;

public class BodyView extends Activity {
	WebView webView1;
	int ctime;
	String user,title,sb;
	TestAdapter mDbHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_body_view);
		
		mDbHelper = new TestAdapter(this);
		webView1 = (WebView) findViewById(R.id.webview1);
		
		Intent intent = getIntent();
		ctime = intent.getIntExtra("created_time",0);
		sb = intent.getStringExtra("body_value");
		title = intent.getStringExtra("title");
		user = intent.getStringExtra("user");
		
		Log.v(SearchActivity.TAG, user);
		Log.v(SearchActivity.TAG, title);
		
		
		webView1.loadDataWithBaseURL("",sb , "text/html", "UTF-8",""); 
	}

	
	public void InsertBookMark(View view)
	{
		
		
		    mDbHelper.createDatabase();       
		    mDbHelper.open(); 
		   
		    boolean flag = mDbHelper.isPresent(title,ctime,user);
		    Log.v(SearchActivity.TAG,Boolean.toString(flag));
		    
		    if(flag)
		    {
		    	Toast.makeText(getApplicationContext(),"This page is already in bookmark", Toast.LENGTH_SHORT).show();
		    }
		    else
		    {	    	
		    	Toast.makeText(getApplicationContext(),"This page is added as bookmark", Toast.LENGTH_SHORT).show();
			    mDbHelper.insertBookmark(title,ctime,user);
		    }
		   
		       //end checking duplicate record
		   
		    mDbHelper.close();	
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.bookmark_option, menu);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {

            if(item.getItemId()==R.id.action_view_bookmark){
                   
            	Intent intent = new Intent(this,BookmarkList.class);
            	startActivity(intent);            	
            	
            }
            return super.onOptionsItemSelected(item);
    }

}
