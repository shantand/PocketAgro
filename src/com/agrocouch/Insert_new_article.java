package com.agrocouch;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.http.HttpClient;
import org.ektorp.impl.StdCouchDbInstance;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.couchbase.cblite.CBLServer;
import com.couchbase.cblite.ektorp.CBLiteHttpClient;
import com.couchbase.cblite.router.CBLURLStreamHandlerFactory;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Manager;

public class Insert_new_article extends Activity {

	protected static final int REQUEST_CAMERA = 0;
	protected static final int SELECT_FILE = 1;
	
	protected static CBLServer server;
	protected static HttpClient httpClient;
	
	public EditText ed,ed_title;
	public ImageView  iv1;
	public RelativeLayout ll4;
	protected String html,title;
	protected String user="shantanu"; 
	
	protected static Manager manager;
	protected Database database;
	public static final String DATABASE_NAME = "agrodb_no_nid";
	protected TestAdapter mDbHelper;
	
	public static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_insert_new_article);
		
		mDbHelper = new TestAdapter(this);
		
		ed_title = (EditText)findViewById(R.id.title_edit);
		ed = (EditText)findViewById(R.id.content_edit);
		ed.setSelection(ed.length());
		iv1 = (ImageView)findViewById(R.id.iv1);
		iv1.setTag("1");
		
		//startTouchDB();
        //startEktorp();
		
	}

	public void setDatabase() throws IOException, CouchbaseLiteException{
		manager = new Manager(Environment.getExternalStorageDirectory(), Manager.DEFAULT_OPTIONS);
		database = manager.getExistingDatabase(DATABASE_NAME);
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.insert_new_article, menu);
		return true;
	}
	public void InsertRecord(View view)
	{
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(ed.getWindowToken(), 0);
		
		GrocerySyncEktorpAsyncTask startupTask = new GrocerySyncEktorpAsyncTask() {

            @Override
            protected void doInBackground() {
            	try {
        			setDatabase();
        		} catch (IOException e) {
        			e.printStackTrace();
        		} catch (CouchbaseLiteException e) {
        			e.printStackTrace();
        		}                
            }

            @Override
            protected void onSuccess() {
                try	{
            	
                	//collect all 5 variables to put here from UI.
	            	int created_time = (int) (System.currentTimeMillis()/1000);
	            	String title = ed_title.getText().toString();
	            	String body = ed.getText().toString();
	            	
	            	Map<String, Object> item = new HashMap<String, Object>();            	
	            	
	                item.put("body_value", getWrittenBodyValue());
	                item.put("created_time", created_time);
	                item.put("title", getWrittenTitle());
	                item.put("user", user);
	                item.put("word_score",getWordScore(title + " " + body));
	                
	                Document document = database.createDocument();
	                document.putProperties(item);
	                
	                mDbHelper.createDatabase();       
	        		mDbHelper.open(); 
	        		mDbHelper.insertRecord(created_time, getWrittenTitle(), getWordScore(title + " " + body), getWrittenBodyValue(), user);
	        		mDbHelper.close();
	        		
	        		Toast.makeText(getApplicationContext(),"Record Inserted successfully", Toast.LENGTH_SHORT).show();
	        		(new Handler()).postDelayed(
	        				new Runnable() {
	        						public void run() {
	        								Intent in = new Intent(Insert_new_article.this, UasrMainActivity.class);
	        								startActivity(in);
	        						}
	        	    }, 40);  
	        		finish();
        		}catch(Exception e){
            		Log.v(SearchActivity.TAG,e.toString());
            	}
            }
		};
        startupTask.execute();        
	}
	
	public String getWrittenTitle()	{
		ed_title = (EditText)findViewById(R.id.title_edit);
		
		return ed_title.getText().toString();
	}
	
	public String getWrittenBodyValue()	{
		String img_src = "";
		StringBuilder s = new StringBuilder();
		s.append("<html>");
		s.append("<style> h4 {FONT-SIZE: 17pt; COLOR: #008000 }</style>");
		s.append("<center> <h4> ");
		s.append(getWrittenTitle() + " ");
		s.append(" </center></h4> <center> <body> ");		
		
		
		if (iv1.getTag().toString() != "1")
		{
			
			/*ImageView iv1 = (ImageView)findViewById(R.id.iv1);
			BitmapDrawable drawable = (BitmapDrawable) iv1.getDrawable();
			Bitmap bitmap = drawable.getBitmap();*/
			
			Bitmap bitmap = BitmapFactory.decodeFile(iv1.getTag().toString());
			ByteArrayOutputStream bos = new ByteArrayOutputStream();  
			bitmap.compress(CompressFormat.JPEG,100,bos);
			byte[] bb = bos.toByteArray();
			String image = Base64.encodeToString(bb, Base64.NO_WRAP);
			
			
			//save this image in default location in external storage /uasr/Images.
			/*
			if(iv1.getTag().toString().contains("uasr"))
			{
				//dont do anything file is already in default location
				img_src = iv1.getTag().toString();
			}
			else
			{
				
				//copied file in default location
				String path = android.os.Environment
						.getExternalStorageDirectory()
						+ File.separator
						+ "uasr" + File.separator + "Images";
				OutputStream fOut = null;
				File file = new File(path, String.valueOf(System
						.currentTimeMillis()) + ".jpg");
				try {
					fOut = new FileOutputStream(file);
					bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
					fOut.flush();
					fOut.close();
				} catch (Exception e) {
					e.printStackTrace();
				}	
				img_src = file.getAbsolutePath();
			}				
		}
		Log.v("AgroCouch",img_src);*/			
		
		s.append("<img src=" + "\"Data:image/png;base64," + image + "\" height=200 width=200 /> <br><br>");
		}
		s.append("<p> " + ed.getText().toString() + "</p>" );
		s.append(" </body> </center> </html>");
		return s.toString();
	}
	
	public String getWordScore(String s)
	{
		//Removing stop words and calculating the score
		// and return the remaining word score.
		RemoveStopWord rsw=new RemoveStopWord();
    	String searchData=rsw.removeStopWords(s);
    	
    	String res = countWordFrequency(searchData);
    	return res;
	}
	
	public String countWordFrequency(String s)
	{
		//count the word_score and return concatenated string
		Map<String, Integer> map = new HashMap<String, Integer>();
		String res = "";
		
		String[] words = s.split(" ");
	    for (String w : words) {
	        Integer n = map.get(w);
	        n = (n == null) ? 1 : ++n;
	        map.put(w, n);
	        res = res.concat("(" + w + ":" + n.toString() + ")");
	    }
		return res;
	}
	 protected void onDestroy() {
	        Log.v(SearchActivity.TAG, "onDestroy");

	        //need to stop the async task thats following the changes feed
	        //itemListViewAdapter.cancelContinuous();

	        //clean up our http client connection manager
	        if(httpClient != null) {
	            httpClient.shutdown();
	        }

	        if(server != null) {
	            server.close();
	        }

	        super.onDestroy();
	    }
	    
	    /*protected void startTouchDB() {
	        String filesDir = Environment.getExternalStorageDirectory().toString();
	        Log.v(SearchActivity.TAG, filesDir);
	        try {
	            server = new CBLServer(filesDir);
	        } catch (IOException e) {
	            Log.e(SearchActivity.TAG, "Error starting TDServer", e);
	        }        
	        
	    }

	    protected void startEktorp() {
	        Log.v(SearchActivity.TAG, "starting ektorp");

	        if(httpClient != null) {
	            httpClient.shutdown();
	        }

	        httpClient = new CBLiteHttpClient(server);
	        dbInstance = new StdCouchDbInstance(httpClient);
	    }*/
	    public void openImageDialog(View view)
	    {
	    	
	    	final CharSequence[] items = { "Take Photo", "Choose from Library",
	    				"Cancel" };

	    		AlertDialog.Builder builder = new AlertDialog.Builder(Insert_new_article.this);
	    		builder.setTitle("Add Photo!");
	    		builder.setItems(items, new DialogInterface.OnClickListener() {
	    			@Override
	    			public void onClick(DialogInterface dialog, int item) {
	    				if (items[item].equals("Take Photo")) {
	    					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    					File f = new File(android.os.Environment
	    							.getExternalStorageDirectory(), "temp.jpg");
	    					intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
	    					startActivityForResult(intent, REQUEST_CAMERA);
	    				} else if (items[item].equals("Choose from Library")) {
	    					Intent intent = new Intent(
	    							Intent.ACTION_PICK,
	    							android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
	    					intent.setType("image/*");
	    					startActivityForResult(
	    							Intent.createChooser(intent, "Select File"),
	    							SELECT_FILE);
	    				} else if (items[item].equals("Cancel")) {
	    					dialog.dismiss();
	    				}
	    			}
	    		});
	    		builder.show();
	    }
	    @Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			super.onActivityResult(requestCode, resultCode, data);
			Log.v("AgroCouch",Integer.toString(requestCode));
			if (resultCode == RESULT_OK) {
				if (requestCode == REQUEST_CAMERA) {
					File f = new File(Environment.getExternalStorageDirectory()
							.toString());
					for (File temp : f.listFiles()) {
						if (temp.getName().equals("temp.jpg")) {
							f = temp;
							break;
						}
					}
					try {
						Bitmap bm;
						BitmapFactory.Options btmapOptions = new BitmapFactory.Options();

						bm = BitmapFactory.decodeFile(f.getAbsolutePath(),
								btmapOptions);
						

						//bm = Bitmap.createScaledBitmap(bm, 70, 70, true);
						iv1.setImageBitmap(bm);
						

						String path = android.os.Environment
								.getExternalStorageDirectory()
								+ File.separator
								+ "uasr" + File.separator + "Images";
						f.delete();
						OutputStream fOut = null;
						File file = new File(path, String.valueOf(System
								.currentTimeMillis()) + ".jpg");
						iv1.setTag(file.getAbsolutePath());
						
						try {
							fOut = new FileOutputStream(file);
							bm.compress(Bitmap.CompressFormat.PNG, 85, fOut);
							fOut.flush();
							fOut.close();
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						} catch (Exception e) {
							e.printStackTrace();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if (requestCode == SELECT_FILE) {
					
					Uri selectedImageUri = data.getData();
					String tempPath = getPath(selectedImageUri, Insert_new_article.this);
					Bitmap bm;
					BitmapFactory.Options btmapOptions = new BitmapFactory.Options();
					bm = BitmapFactory.decodeFile(tempPath, btmapOptions);
					iv1.getLayoutParams().height = 200;
					iv1.getLayoutParams().width = 200;					
					
					iv1.setImageBitmap(bm);
					iv1.setTag(tempPath);
				}
			}
		}
	    
	    public String getPath(Uri uri, Activity activity) {
			String[] projection = { MediaColumns.DATA };
			Cursor cursor = activity.getContentResolver().query(uri, projection, null, null, null);
			int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		}
	    
	    public static String encodeTobase64(Bitmap image)
	    {
	        Bitmap immagex=image;
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
	        immagex.compress(Bitmap.CompressFormat.JPEG, 100, baos);
	        byte[] b = baos.toByteArray();
	        String imageEncoded = Base64.encodeToString(b,Base64.DEFAULT);

	        Log.e("LOOK", imageEncoded);
	        return imageEncoded;
	    }
}
