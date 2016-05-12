package com.agrocouch;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Window;
import android.webkit.WebView;

public class BookmarkDisplayActivity extends Activity 
{
	final String mimeType="text/html";
    final String encoding="UTF-8";
	WebView webView;
	TestAdapter mDbHelper;
	Record record;
	String title,ctime,user;
	public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_bookmark_display);
        
        mDbHelper = new TestAdapter(this);
        webView = (WebView) findViewById(R.id.webview2);
        
        //Enabling javascript
        JavaScriptInterface jsInterface = new JavaScriptInterface();
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(jsInterface, "JSInterface");
        webView.getSettings().setBuiltInZoomControls(true);   
        //
        Bundle extras = getIntent().getExtras(); 
        if(extras!=null) 
        {
         title=extras.getString("title");
         user=extras.getString("user");
         ctime=extras.getString("ctime");
        }
        
        Log.v(SearchActivity.TAG, title + " " + user + " " + ctime);
        displayRecords(title,ctime,user);
        StringBuilder sb = new StringBuilder();
        
        //adding back button
        //sb.append("<input type=\"image\" src=\"file:///android_asset/images/back.jpeg\" name=\"back\" align=\"left\" onClick=\"window.JSInterface.backButton()\"><br>");
        sb.append(cleanImageUrl(record.getBodyValue()));
		webView.loadDataWithBaseURL("",sb.toString(),mimeType,encoding,""); 
    }
	public void displayRecords(String title, String ctime, String user) 
    {
		record = new Record();
		try
		{
		    TestAdapter mDbHelper = new TestAdapter(this); 
			mDbHelper.createDatabase();       
			mDbHelper.open(); 
			record=mDbHelper.searchRecordByTitle(title,ctime,user); 
			mDbHelper.close();
		}
		catch(Exception e)
		{
			Log.v(SearchActivity.TAG,e.getMessage());
		}
	}
	
	public String cleanImageUrl(String str)
    {
    	String filesDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        
    	str=str.replaceAll("/sites/default/files/uas%20raichur/chickpea%20and%20pegionpea/", "file://" + filesDir + "/uasr/Images/");
        str=str.replaceAll("/sites/default/files/uas%20raichur/diseases%20of%20paddy/", "file://" + filesDir + "/uasr/Images/");
        str=str.replaceAll("/sites/default/files/uas%20raichur/cotton%20diseases/Rust/","file://" + filesDir + "/uasr/Images/");
        str=str.replaceAll("/sites/default/files/uas%20raichur/cotton%20diseases/wilt/","file://" + filesDir + "/uasr/Images/");
        str=str.replaceAll("/sites/default/files/uas%20raichur/agrowiki/agrowiki1/", "file://" + filesDir + "/uasr/Images/");
        str=str.replaceAll("/sites/default/files/uas%20raichur/agrowiki/agrowiki2/", "file://" + filesDir + "/uasr/Images/");
        str=str.replaceAll("/sites/default/files/uas%20raichur/agrowiki/agrowiki3/", "file://" + filesDir + "/uasr/Images/");
        str=str.replaceAll("/sites/default/files/uas%20raichur/agrowiki/agrowiki4/", "file://" + filesDir + "/uasr/Images/");
        str=str.replaceAll("/sites/default/files/uas%20raichur/cotton%20pests/","file://" + filesDir + "/uasr/Images/");
        str=str.replaceAll("/sites/default/files/uas%20raichur/cotton%20PBW/","file://" + filesDir + "/uasr/Images/");
        str=str.replaceAll("/sites/default/files/uas%20raichur/Rice%20pests/", "file://" + filesDir + "/uasr/Images/");
        str=str.replaceAll("http://agropedia.iitk.ac.in/sites/default/files/","file://" + filesDir + "/uasr/Images/");
        str=str.replaceAll("/sites/default/files/uas%20raichur/", "file://" + filesDir + "/uasr/Images/");
        str=str.replaceAll("../../sites/default/files/","file://" + filesDir + "/uasr/Images/");
        str=str.replaceAll("/sites/default/files/","file://" + filesDir + "/uasr/Images/");
        str=str.replaceAll("%20","_");
        return str;
    }
	
	//javascript method
    public class JavaScriptInterface 
    {
    	JavaScriptInterface() 
    	{}      
        public void backButton()
        {
        	finish();
        }
   
    }

}

