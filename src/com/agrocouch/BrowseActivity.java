package com.agrocouch;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Intent;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
public class BrowseActivity extends Activity 
{
	String data,recordTitle,recordBody,recordUser;
	int recordCreatedTime;
	private TextView title;
	private Button btn_prev;
	private Button btn_next;
	private int pageCount=0;
	private int increment=0;
	
	ListView lv;
	final String mimeType = "text/html";
    final String encoding = "UTF-8";

	ArrayList<Record> items=null;
	ArrayList<String> titles=null;
	ArrayList<String> user=null;
	MobileArrayAdapter madapter;
	
	TestAdapter mDbHelper; 

	//pagination data
    public int totalRecord=0;
    public int itemPerPage=50;

    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browse_text);
                
        lv = (ListView) findViewById(R.id.listview1);
        /*webView=(WebView) findViewById(R.id.webview);
        webView.setVerticalScrollBarEnabled(true);
        webView.setHorizontalScrollBarEnabled(true);
        //Enabling javascript
        JavaScriptInterface jsInterface = new JavaScriptInterface();
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(jsInterface, "JSInterface");
        webView.getSettings().setBuiltInZoomControls(true);*/
                
        mDbHelper = new TestAdapter(this);
      
        btn_prev=(Button)findViewById(R.id.prev);
        btn_next=(Button)findViewById(R.id.next);
        title=(TextView)findViewById(R.id.title);
        btn_prev.setEnabled(false);
               
        countTotalRecords();//geting total record
                
        int val=totalRecord%itemPerPage;
    	val = val==0?0:1;
    	pageCount = totalRecord/itemPerPage+val;
    	
    	loadList("0");
        
        //**********************************************
        btn_next.setOnClickListener(new OnClickListener() 
        {
    		public void onClick(View v) 
    		{
    			increment++;
    			loadList(String.valueOf(increment));
    			CheckEnable();
    		}
    	});
        
        btn_prev.setOnClickListener(new OnClickListener() 
        {
    		public void onClick(View v) 
    		{
    			increment--;
    			loadList(String.valueOf(increment));
    			CheckEnable();
    		}
    	});

    }
    private void CheckEnable()
	{
		if(increment+1 == pageCount)
		{
			btn_next.setEnabled(false);
		}
		else if(increment == 0)
		{
			btn_prev.setEnabled(false);
		}
		else
		{
			btn_prev.setEnabled(true);
			btn_next.setEnabled(true);
		}
	}
    
    public void countTotalRecords() 
    {
		mDbHelper.createDatabase();       
		mDbHelper.open(); 
		totalRecord=mDbHelper.browseAllRecord();
		mDbHelper.close();
	}
    public void loadList(String index) 
    {
    	int number=Integer.parseInt(index);
    	int start =number*itemPerPage;
    	
    	items=new ArrayList<Record>();
		mDbHelper.createDatabase();      
		mDbHelper.open(); 
		items=mDbHelper.browseAllText(String.valueOf(start));
		mDbHelper.close();
		
		title.setText("Page "+(number+1)+" of "+pageCount);
		
		if(items.size()!=0)
	      {
			  StringBuilder sb = new StringBuilder();
			  titles = new ArrayList<String>();
			  user = new ArrayList<String>();
	    	  for(int i=0;i<items.size(); i++)
	    	  {
	    		Record record = new Record();
	    		record = items.get(i);

		        //sb.append("<a href=\"#\" onClick=\"window.JSInterface.startVideo('"+i+"')\">");
		        /*sb.append("<a href=\"#\" onClick=\"window.JSInterface.startVideo('"+i+"')\">");
		        sb.append(record.getTitle());
		        sb.append("</a><br><br>");*/
	    		titles.add(record.getTitle());
	    		user.add(record.getUser());
		        
	    	  }
	    	  madapter = new MobileArrayAdapter(this, titles, user);
	    	  lv.setAdapter(madapter);
	    	  
	    	  
	    	  lv.setOnItemClickListener(new OnItemClickListener() {
	    	     @Override
	    	     public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
	    	        //Object listItem = lv.getItemAtPosition(position);
	    	    	 
	    	    	 	Record record = new Record();
	    	     		record = items.get(position);
	    	     		
	    	     		recordTitle=record.getTitle();
	    	         	recordBody= cleanImageUrl(record.getBodyValue());
	    	         	recordCreatedTime = record.get_create_time();
	    	         	recordUser = record.getUser();
	    	         	
	    	         	   		
	    	     		Intent intent = new Intent(BrowseActivity.this, BodyView.class);
	    	 	        intent.putExtra("body_value", recordBody );
	    	 	        intent.putExtra("title",recordTitle);
	    	 	        intent.putExtra("created_time",recordCreatedTime);
	    	 	        intent.putExtra("user",recordUser);
	    	 	        startActivity(intent);
	    	     } 
	    	  });
	    	  //webView.loadDataWithBaseURL("",sb.toString(), mimeType, encoding,""); 
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
    	 public void startVideo(String id)
         {
        	
         	final String mimeType="text/html";
             final String encoding="UTF-8";
             
             String modifiedHtml;
             Record record = new Record();
     		record = items.get(Integer.parseInt(id));
     		
     		recordTitle=record.getTitle();
         	recordBody= cleanImageUrl(record.getBodyValue());
         	recordCreatedTime = record.get_create_time();
         	recordUser = record.getUser();
         	
         	   		
     		Intent intent = new Intent(BrowseActivity.this, BodyView.class);
 	        intent.putExtra("body_value", recordBody );
 	        intent.putExtra("title",recordTitle);
 	        intent.putExtra("created_time",recordCreatedTime);
 	        intent.putExtra("user",recordUser);
 	        startActivity(intent);
     		//webView.loadDataWithBaseURL("",sb.toString(), mimeType, encoding,""); 
         }   
    }
}