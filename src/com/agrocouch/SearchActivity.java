package com.agrocouch;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import org.codehaus.jackson.JsonNode;
import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.ReplicationCommand;
import org.ektorp.ViewQuery;
import org.ektorp.ViewResult;
import org.ektorp.ViewResult.Row;
import org.ektorp.http.HttpClient;
import org.ektorp.impl.StdCouchDbInstance;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.couchbase.cblite.CBLDatabase;
import com.couchbase.cblite.CBLServer;
import com.couchbase.cblite.CBLView;
import com.couchbase.cblite.CBLViewMapBlock;
import com.couchbase.cblite.CBLViewMapEmitBlock;
import com.couchbase.cblite.ektorp.CBLiteHttpClient;
import com.couchbase.cblite.router.CBLURLStreamHandlerFactory;
import com.couchbase.cblite.support.Base64.InputStream;


import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Emitter;
import com.couchbase.lite.LiveQuery;
import com.couchbase.lite.Manager;
import com.couchbase.lite.Mapper;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.listener.LiteListener;
import com.couchbase.lite.replicator.Replication;
import com.couchbase.lite.replicator.Replication.ChangeListener;



public class SearchActivity extends Activity implements Replication.ChangeListener
{
	
	String data,recordTitle,recordBody,recordUser;
	int recordCreatedTime;
	ArrayList<Record> items=null;
	ArrayList<KeywordRecord> terms = null;
	ArrayList<String> user=null;
	ArrayList<String> titles=null;
	ArrayList<String> recentlySearchedTerms = null;
	
	String noresult;
	EditText searchText;
	Button searchButton;
	String search;
	
	WebView webView;
	TextView tv;
	TestAdapter mDbHelper; 
	String mimeType;
	String encoding;
	int last_synch_pt;
	MobileArrayAdapter madapter;
	
	public static String TAG = "AgroCouch";

    //constants
    public static final String DATABASE_NAME = "agrodb_no_nid";
    public static final String dDocName = "agrodb_no_nid-local";
    public static final String dDocId = "_design/" + dDocName;
    public static final String bytimecreated = "by_created_time_8";    
    public static final String defaultDatabaseUrl = "http://172.27.20.69:5984/agrodb_no_nid";

    //splash screen
    
    //main screen
    protected EditText addItemEditText;
    protected ListView itemListView;
    protected ListView lv;
    protected List<QueryRow> results;
    
    public static  ViewResult viewResult;
    public static  ViewQuery viewQuery;
    protected GrocerySyncEktorpAsyncTask startupTask;
    
    
    
    //couch internals
    protected static CBLServer server;
    protected static HttpClient httpClient;
    protected static LiteListener listener;
    
    protected static Manager manager;
    private Database database;
    private Query query;
    protected  QueryEnumerator rowEnum; 
    protected com.couchbase.lite.View viewItemsByDate;
    //protected static Manager mgr;
    

    //ektorp impl
    protected CouchDbInstance dbInstance;
    protected CouchDbConnector couchDbConnector;
    protected ReplicationCommand pushReplicationCommand;
    protected ReplicationCommand pullReplicationCommand;

    protected ProgressDialog dialog; 
 
    
    public final static String EXTRA_MESSAGE = "com.Agrocouch.MESSAGE";
    
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_search_layout);   
       
        mDbHelper = new TestAdapter(this);
	        
	    searchText=(EditText) findViewById(R.id.searchText);
	    searchButton=(Button) findViewById(R.id.searchButton);
	    tv = (TextView) findViewById(R.id.tv1);
	    
	    max_created_time();
	    
	    lv = (ListView) findViewById(R.id.listview2);
	    lv.setOnItemClickListener(new OnItemClickListener() {
	        	@Override
	    	     public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
	    	         	Record record = new Record();
	    	     		record = items.get(position);
	    	     		
	    	     		recordTitle=record.getTitle();
	    	         	recordBody= cleanImageUrl(record.getBodyValue());
	    	         	recordCreatedTime = record.get_create_time();
	    	         	recordUser = record.getUser();
	    	         	
	    	         	   		
	    	     		Intent intent = new Intent(SearchActivity.this, BodyView.class);
	    	 	        intent.putExtra("body_value", recordBody );
	    	 	        intent.putExtra("title",recordTitle);
	    	 	        intent.putExtra("created_time",recordCreatedTime);
	    	 	        intent.putExtra("user",recordUser);
	    	 	        startActivity(intent);
	    	     } 
	    	  });
	        
	        //startTouchDB();
	        //startEktorp();
	        
	        try { 
	        	query = null;rowEnum = null;
	            startCBLite();
	        } catch (Exception e) {
	            Toast.makeText(getApplicationContext(), "Error Initializing CBLIte, see logs for details", Toast.LENGTH_LONG).show();
	            Log.e(TAG, "Error initializing CBLite", e);
	        }
    }
    
    protected void startCBLite() throws Exception {

        manager = new Manager(Environment.getExternalStorageDirectory(), Manager.DEFAULT_OPTIONS);
        
        //install a view definition needed by the application
        database = manager.getDatabase(DATABASE_NAME);
        viewItemsByDate = database.getView(String.format("%s/%s", dDocName, bytimecreated));
        viewItemsByDate.setMap(new Mapper() {
			@Override
			public void map(Map<String, Object> document, Emitter emitter) {
				// TODO Auto-generated method stub
				String ctime = document.get("created_time").toString();
                int n = Integer.parseInt(ctime);
                if(n>1392879188)               
                	emitter.emit(ctime.toString(), document);
                Log.v(TAG, ctime);
			}
        }, "1.0");


        startLiveQuery(viewItemsByDate);
        startSync();
    }

    private void startSync() {

        URL syncUrl;
        try {
            syncUrl = new URL(defaultDatabaseUrl);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        Replication pullReplication = database.createPullReplication(syncUrl);
        pullReplication.setContinuous(true);

        Replication pushReplication = database.createPushReplication(syncUrl);
        pushReplication.setContinuous(true);

        pullReplication.start();
        pushReplication.start();

        pullReplication.addChangeListener( this);
        pushReplication.addChangeListener(this);

    } 
    @Override
    public void changed(Replication.ChangeEvent event) {

        Replication replication = event.getSource();
        Log.v(TAG, "Replication : " + replication + " changed.");
        if (!replication.isRunning()) {
            String msg = String.format("Replicator %s not running", replication);
            Log.v(TAG, msg);
        }
        else {
            int processed = replication.getCompletedChangesCount();
            int total = replication.getChangesCount();
            String msg = String.format("Replicator processed %d / %d", processed, total);
            Log.v(TAG, msg);
        }
    }
    
    
    private void startLiveQuery(com.couchbase.lite.View view) throws Exception {

        final ProgressDialog progressDialog = showLoadingSpinner();

        if (query == null) {
            query = view.createQuery();query.setLimit(20);query.setDescending(true);
            
            try {
            	if(rowEnum ==null) 
            	{
            		rowEnum = query.run();
            		for (Iterator<QueryRow> it = rowEnum; it.hasNext();) {
                        QueryRow row = it.next();
                        
                        Document item = row.getDocument();
                    	String title = item.getProperty("title").toString();
                    	String word_score = item.getProperty("word_score").toString();
                    	String body_value = item.getProperty("body_value").toString();
                    	String user = item.getProperty("user").toString();
                    	
                    	int ctime = Integer.parseInt(item.getProperty("created_time").toString());
                    	//Log.v(TAG, title);
                    	
                    	
                    	mDbHelper.createDatabase();       
                		mDbHelper.open(); 
                    	if(ctime > last_synch_pt)
                    	{                    		
                    		if(!mDbHelper.isPresentNodeData(title, ctime, user))
                    		{
                    			mDbHelper.insertRecord(ctime, title, word_score, body_value, user);
                    			Log.v(TAG, "Inserting " + Integer.toString(ctime) + " " +  title );
                    		}
                    	}
                      	mDbHelper.close();
            		}
            	}            	              
            } catch (CouchbaseLiteException e) {
                e.printStackTrace();
            }
            progressDialog.dismiss();
        }

    }
    private List<QueryRow> displayRows(QueryEnumerator queryEnumerator) {

        final List<QueryRow> rows = getRowsFromQueryEnumerator(queryEnumerator);
        return rows;
    }


    private List<QueryRow> getRowsFromQueryEnumerator(QueryEnumerator queryEnumerator) {
        List<QueryRow> rows = new ArrayList<QueryRow>();
        for (Iterator<QueryRow> it = queryEnumerator; it.hasNext();) {
            QueryRow row = it.next();
            rows.add(row);
        }
        return rows;
    }
    private ProgressDialog showLoadingSpinner() {
        ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.show();
        return progress;
    }
    
   
    public void send_msg(View v) 
	{
    	search = searchText.getText().toString();		
		if(search.equalsIgnoreCase("")){
			tv.setText("Enter a valid search term");
		}
		else{
			insertInDb(search);  
			displayRecords(search);
			if(items.size()==0){
				tv.setText("No Results Found");				 
			}
			else{
				titles = new ArrayList<String>();
				user = new ArrayList<String>();
				for(int i=0;i<items.size(); i++){
				    Record record = new Record();
				    record = items.get(i);
					        
					titles.add(record.getTitle());
				    user.add(record.getUser());
				}
				tv.setText("Total " + Integer.toString(items.size()) + " Records Found for " + search );
				madapter = new MobileArrayAdapter(this, titles, user);
				lv.setAdapter(madapter);
			}
		}   	
	}
    
    protected void onDestroy() {
    	if(manager != null) {
            manager.close();
        }
        
        super.onDestroy();
    }
    
    protected void startTouchDB() {
        String filesDir = Environment.getExternalStorageDirectory().toString();
        Log.v(TAG, filesDir);
        try {
            server = new CBLServer(filesDir);            
        } catch (IOException e) {
            Log.e(TAG, "Error starting TDServer", e);
            Log.e(TAG,e.toString());
        }
        //install a view definition needed by the application
        CBLDatabase db = server.getDatabaseNamed(DATABASE_NAME);
        CBLView view1 = db.getViewNamed(String.format("%s/%s", dDocName, bytimecreated));
        view1.setMapReduceBlocks(new CBLViewMapBlock() {

            @Override
            public void map(Map<String, Object> document, CBLViewMapEmitBlock emitter) {
                String ctime = document.get("created_time").toString();
                int n = Integer.parseInt(ctime);
                if(n>1392879188)               
                emitter.emit(ctime, document);
                Log.v(TAG, ctime);
            }
        }, null, "1.0");        
    }

    /*public void startEktorp() {
        Log.v(TAG, "starting ektorp");

        if(httpClient != null) {
            httpClient.shutdown();
        }

        httpClient = new CBLiteHttpClient(server);
        dbInstance = new StdCouchDbInstance(httpClient);

        startupTask = new GrocerySyncEktorpAsyncTask() {

            @Override
            protected void doInBackground() {
                couchDbConnector = dbInstance.createConnector(DATABASE_NAME, true);
            }

            @Override
            protected void onSuccess() {
                //attach list adapter to the list and handle clicks
            	viewQuery = new ViewQuery().designDocId("_design/" + dDocName).viewName(bytimecreated);
                viewResult = couchDbConnector.queryView(viewQuery);                
                for(Row row : viewResult){
                	JsonNode item = row.getValueAsNode();
                	String title = item.get("title").getTextValue();
                	String word_score = item.get("word_score").getTextValue();
                	String body_value = item.get("body_value").getTextValue();
                	String user = item.get("user").getTextValue();
                	
                	int ctime = Integer.parseInt(row.getKey());
                	
                	mDbHelper.createDatabase();       
            		mDbHelper.open(); 
                	if(ctime > last_synch_pt)
                	{
                		Log.v(TAG, "Inserting " + Integer.toString(ctime) + " to sqlite");
                		
                		mDbHelper.insertRecord(ctime, title, word_score, body_value, user);
                	}
                	
                	mDbHelper.close();
                }
                
                startReplications();
            }
        };
        startupTask.execute();        
    }

    public void startReplications() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

       pushReplicationCommand = new ReplicationCommand.Builder()
                .source(DATABASE_NAME)
                .target(prefs.getString("sync_url", defaultDatabaseUrl))
                .continuous(true)
                .build();

        GrocerySyncEktorpAsyncTask pushReplication = new GrocerySyncEktorpAsyncTask() {

            @Override
            protected void doInBackground() {
                dbInstance.replicate(pushReplicationCommand);
            }
        };

        pushReplication.execute();

        pullReplicationCommand = new ReplicationCommand.Builder()
                .source(prefs.getString("sync_url", defaultDatabaseUrl))
                .target(DATABASE_NAME)
                .continuous(true)
                .build();

        GrocerySyncEktorpAsyncTask pullReplication = new GrocerySyncEktorpAsyncTask() {

            @Override
            protected void doInBackground() {
                dbInstance.replicate(pullReplicationCommand);
            }
        };

        pullReplication.execute();
    }*/ 
    public void max_created_time()
    {
    	try
    	{
	    	mDbHelper.createDatabase();       
			mDbHelper.open(); 
			last_synch_pt = mDbHelper.lastInsertedNid();
			Log.v(TAG, Integer.toString(last_synch_pt));
			mDbHelper.close();
    	}
    	catch(Exception e)
    	{
    		Log.v(TAG, e.getMessage());
    	}
    }
    public void insertInDb(String s)
    {
    	//will insert the searched term in database
    	try
    	{
	    	mDbHelper.createDatabase();
	    	mDbHelper.open();
	    	mDbHelper.insertSearchedTerm(s);
	    	mDbHelper.close();
    	}catch(Exception e)
    	{
    		Log.v(TAG,e.toString());
    	}
    }
    
    public void displayRecords(String searchText) 
    {
    	try
    	{
	    	items=new ArrayList<Record>();
		    //TestAdapter mDbHelper = new TestAdapter(this); 
			mDbHelper.createDatabase();       
			mDbHelper.open(); 
			items=mDbHelper.getImageData(searchText); 	
			mDbHelper.close();
    	}
    	catch(Exception e)
    	{
    		Log.v(TAG, e.getMessage());
    	}
	}
    
    public ArrayList<String>  getHTMLFromKeywordRecord(ArrayList<KeywordRecord> records)
    {
    	ArrayList<String> html = new ArrayList<String>();
    	for(KeywordRecord record:records)
    	{
    		html.add(record.getTerm());    		
    	}
    	return html;
    }
    
    public ArrayList<String> getRecentlySearchedTerms()
    {
    	ArrayList<String> html = new ArrayList<String>();
    	try
    	{
	    	terms = new ArrayList<KeywordRecord>();
	    	mDbHelper.createDatabase();
	    	mDbHelper.open();
	    	terms = mDbHelper.getKeywords();
	    	mDbHelper.close();
	    	
	    	html = getHTMLFromKeywordRecord(terms);
	    	
    	}catch(Exception e)
    	{
    		Log.v(TAG,e.toString());
    	}
    	return html;
    }

    public void selectTerm(final String t){
        runOnUiThread(new Runnable() {
        	@Override
        	public void run() {
        		SearchActivity.this.searchText.setText(t);
        	}
        });
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
            
            if(item.getItemId()==R.id.new_content){
                
            	Intent intent = new Intent(this,Insert_new_article.class);
            	startActivity(intent);            	
            }
            if(item.getItemId()==R.id.listener){
                
            	Intent intent = new Intent(this,WiFiDirectActivity.class);            	
            	startActivity(intent);           	
            }           
            return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if ((keyCode == KeyEvent.KEYCODE_BACK)){
            finish();
        }
        return super.onKeyDown(keyCode, event);
    } 
}

    


