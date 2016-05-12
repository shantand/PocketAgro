package com.agrocouch;

import java.util.ArrayList;


import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class BookmarkList extends Activity {

	ArrayList<BookmarkRecord> bookmarkList=null;
	TestAdapter mDbHelper; 
	BookmarkArrayAdapter adapter;
	ListView list;
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_bookmark_list);
       
        list= (ListView)findViewById(R.id.listview);
        
        mDbHelper = new TestAdapter(this);
        displayBookmarkList(); 
        adapter=new BookmarkArrayAdapter(getApplicationContext(),bookmarkList);
        list.setAdapter(adapter);  
        adapter.notifyDataSetChanged();
        list.setOnItemClickListener(new OnItemClickListener() 
        {
        public void onItemClick(AdapterView<?> parent, View view,int position, long id) 
        {
        	BookmarkRecord record=new BookmarkRecord();
        	record=bookmarkList.get(position);
        	String ctime=record.getCreate_time();
        	String user = record.getRecord_user();
        	String title = record.getRecord_title();
       	    
        	Log.v(SearchActivity.TAG, title + " " + user + " " + ctime);
        	
        	Intent intent = new Intent(getApplicationContext(),BookmarkDisplayActivity.class);
        	intent.putExtra("title", title);
        	intent.putExtra("user", user);
        	intent.putExtra("ctime", ctime);
        	
        	startActivity(intent);      	
        		
        }
        });
    }
    @Override
    public void onResume()
    {
    	list.setAdapter(adapter);  
        adapter.notifyDataSetChanged();
        super.onResume();
    }
    public void onClick(View v) 
	{
		final ImageButton button = (ImageButton) v;
		
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BookmarkList.this);
   	 	alertDialogBuilder.setTitle("Delete Bookmark");
   	 	alertDialogBuilder.setMessage("Are you sure?");
   	 	alertDialogBuilder.setPositiveButton("Yes",new DialogInterface.OnClickListener() 
   	 	{
   			public void onClick(DialogInterface dialog,int id) 
   			{
   				deleteRow(button);  
  			}
  		 });
   	 	
       	alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() 
       	{
       	    public void onClick(DialogInterface dialog,int id) 
       	    {
       			dialog.cancel();
       		}
       	});
       	
       	AlertDialog alertDialog = alertDialogBuilder.create();
       	alertDialog.show();	
	}
    public void displayBookmarkList() 
    {
    	bookmarkList=new ArrayList<BookmarkRecord>();
		mDbHelper.createDatabase();       
		mDbHelper.open(); 
		bookmarkList=mDbHelper.displayBookmark(); 
		mDbHelper.close();
	}
    
    public void deleteRow(ImageButton button)
    {
    	BookmarkRecord row = (BookmarkRecord) button.getTag();
    	try
    	{
	    	mDbHelper.createDatabase();   
			mDbHelper.open(); 
			mDbHelper.deleteBookmark(row.getRecord_title(),row.getCreate_time(),row.getRecord_user());
			adapter.deleteRow(row);
			adapter.notifyDataSetChanged();
    	}
    	catch(Exception e)
    	{
    		Log.v(SearchActivity.TAG, e.getMessage());
    	}
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.bookmark_list, menu);
		return true;
	}

}
