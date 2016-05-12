package com.agrocouch;

import java.util.ArrayList;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;


public class ResultsPage extends Activity {
	
	String data,recordTitle;
	ArrayList<Record> items=null;
	
	String noresult;
	EditText searchText;
	Button searchButton;
	String search;
	
	WebView webView;
	TestAdapter mDbHelper; 
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_results_page);          
        
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.results_page, menu);
		return true;
	}
	
	
    

}
