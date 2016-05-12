package com.agrocouch;

import java.io.IOException; 
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.agrocouch.Record;



import android.R.bool;
import android.content.ContentValues;
import android.content.Context; 
import android.database.Cursor; 
import android.database.SQLException; 
import android.database.sqlite.SQLiteDatabase; 
import android.util.Log; 
 
public class TestAdapter  
{ 
    protected static final String TAG = "DataAdapter"; 
 
    private final Context mContext; 
    private SQLiteDatabase mDb; 
    private DataBaseHelper mDbHelper; 
    int startIndex, endIndex;

    public TestAdapter(Context context)  
    { 
        this.mContext = context; 
        mDbHelper = new DataBaseHelper(mContext); 
    } 
    public TestAdapter createDatabase() throws SQLException  
    { 
    	
        try  
        { 
            mDbHelper.createDataBase(); 
        }  
        catch (IOException mIOException)  
        { 
            Log.e(TAG, mIOException.toString() + "  UnableToCreateDatabase"); 
            throw new Error("UnableToCreateDatabase"); 
        } 
        return this; 
    } 
    public TestAdapter open() throws SQLException  
    { 
        try  
        { 
            mDbHelper.openDataBase(); 
            mDbHelper.close(); 
            mDb = mDbHelper.getReadableDatabase(); 
        }  
        catch (SQLException mSQLException)  
        { 
            Log.e(TAG, "open >>"+ mSQLException.toString()); 
            throw mSQLException; 
        } 
        return this; 
    } 
 
    public void close()  
    { 
        mDbHelper.close(); 
    } 
    
    
    
    
    public int browseAllRecord() 
    { 
    	int count=0;
        try 
        { 
        	String sql ="SELECT count(*) from node_data"; 
            //Cursor cursor = mDb.rawQuery(sql, new String[] { "%"+searchText+"%" }); 
        	Cursor cursor = mDb.rawQuery(sql,null); 
        	cursor.moveToFirst();
        	count=cursor.getInt(0);
        	System.out.println("Total record="+count);
            cursor.close();            
            return count;
        } 
        catch (SQLException mSQLException)  
        { 
            Log.e(TAG, "getTestData >>"+ mSQLException.toString()); 
            throw mSQLException; 
        } 
        
    }
    
    
    
    public ArrayList<Record> browseAllText(String index) 
    { 
    	ArrayList<Record> records=new ArrayList<Record>();
        try 
        { 
        	String sql ="SELECT title,body_value,created_time,user from node_data limit 50 offset ?"; 
        	//String sql ="select nid,title from node_data where nid in(select nid from agrodb_taxonomy_index where tid=33174) limit 50 offset ?"; 

            //Cursor cursor = mDb.rawQuery(sql, new String[] { "%"+searchText+"%" }); 
        	Cursor cursor = mDb.rawQuery(sql, new String[] { index }); 
            if (cursor.moveToFirst())
            {
           	   do
           	   {   
           		 Record record = new Record(); 
          	      String ctime=cursor.getString(cursor.getColumnIndex("created_time"));
          	      String title = cursor.getString(cursor.getColumnIndex("title"));
          	      //String wordScore = cursor.getString(cursor.getColumnIndex("word_score"));
          	      String bodyValue = cursor.getString(cursor.getColumnIndex("body_value"));
          	      String user = cursor.getString(cursor.getColumnIndex("user"));
          	      //adding to ArrayList
          	      record.set_create_time(Integer.parseInt(ctime));
          	      record.setTitle(title);
          	      record.setUser(user);
          	      //record.setWordScore(wordScore);
          	      record.setBodyValue(bodyValue);
          	      
          	      records.add(record);
           	     
           	   }while(cursor.moveToNext());
             }
            cursor.close();            
            return records;

        } 
        catch (SQLException mSQLException)  
        { 
            Log.e(TAG, "getTestData >>"+ mSQLException.toString()); 
            throw mSQLException; 
        } 
    }   
    
    public ArrayList<BookmarkRecord> displayBookmark()
    {
    	ArrayList<BookmarkRecord> bookmarkRecords=new ArrayList<BookmarkRecord>();
        try 
        { 
        	String sql ="SELECT * from bookmark"; 

            //Cursor cursor = mDb.rawQuery(sql, new String[] { "%"+searchText+"%" }); 
        	Cursor cursor = mDb.rawQuery(sql,null); 
            if (cursor.moveToFirst())
            {
           	   do
           	   {   
           		  BookmarkRecord record = new BookmarkRecord(); 
           	      //int id=cursor.getInt(cursor.getColumnIndex("_id"));
           	      String ctime = cursor.getString(cursor.getColumnIndex("node_created"));
           	      String record_user = cursor.getString(cursor.getColumnIndex("record_user"));
           	      String date = cursor.getString(cursor.getColumnIndex("date"));
           	      String record_title = cursor.getString(cursor.getColumnIndex("record_title"));
  
           	      //adding to ArrayList
           	      //record.setId(id);
           	      record.setCreate_time(ctime);
           	      record.setRecord_user(record_user);
           	      record.setDate(date);
           	      record.setRecord_title(record_title);
           	      
           	      bookmarkRecords.add(record);
           	     
           	   }while(cursor.moveToNext());
             }
            cursor.close();
        } 
        catch (SQLException mSQLException)  
        { 
            Log.e(TAG, "getTestData >>"+ mSQLException.toString()); 
            throw mSQLException; 
        } 
        return bookmarkRecords;
    }
    
    public ArrayList<Record> getImageData(String searchText) 
    { 
    	ArrayList<Record> records=new ArrayList<Record>();
        try 
        { 
     	
        	String sql ="SELECT * from node_data where word_score LIKE ?"; 

            //Cursor cursor = mDb.rawQuery(sql, new String[] { "%"+searchText+"%" }); 
        	Log.v(SearchActivity.TAG,sql);  
        	Cursor cursor = mDb.rawQuery(sql, new String[] { "% "+searchText+" %" }); 
            if (cursor!= null && cursor.moveToFirst())
            {
           	   do
           	   {   
           		  try
           		  {
	           		  Record record = new Record(); 
	           	      String ctime=cursor.getString(cursor.getColumnIndex("created_time"));
	           	      String title = cursor.getString(cursor.getColumnIndex("title"));
	           	      //String wordScore = cursor.getString(cursor.getColumnIndex("word_score"));
	           	      String bodyValue = cursor.getString(cursor.getColumnIndex("body_value"));
	           	      String user = cursor.getString(cursor.getColumnIndex("user"));
	           	      //adding to ArrayList
	           	      record.set_create_time(Integer.parseInt(ctime));
	           	      record.setTitle(title);
	           	      record.setUser(user);
	           	      //record.setWordScore(wordScore);
	           	      record.setBodyValue(bodyValue);
	           	      
	           	      records.add(record);
           		}
              	catch(Exception e)
              	{
              		Log.v(SearchActivity.TAG, e.getMessage());
              	}
           	     
           	   }while(cursor.moveToNext());
             }
            cursor.close();
            
            if(records.size()<=0)
            {
            	RemoveStopWord rsw=new RemoveStopWord();
            	String searchData=rsw.removeStopWords(searchText);
            	String sql2=sqlQuery(searchData);
                Log.v(SearchActivity.TAG,sql2);      	
            	Cursor cursor2 = mDb.rawQuery(sql2,arrayOfString(searchData)); 
                if (cursor2!=null && cursor2.moveToFirst())
                {
               	   do
               	   {  
               		  try
               		  {
               		  Record record = new Record(); 
               	      String ctime=cursor2.getString(cursor2.getColumnIndex("created_time"));
               	      String title = cursor2.getString(cursor2.getColumnIndex("title"));
               	      //String wordScore = cursor2.getString(cursor2.getColumnIndex("word_score"));
               	      String bodyValue = cursor2.getString(cursor2.getColumnIndex("body_value"));
               	      String user = cursor2.getString(cursor.getColumnIndex("user"));
      
               	      //adding to ArrayList
               	      record.set_create_time(Integer.parseInt(ctime));
               	      record.setTitle(title);
               	      record.setUser(user);
               	      //record.setWordScore(wordScore);
               	      record.setBodyValue(bodyValue);
               	      
               	      records.add(record);
               		}
                  	catch(Exception e)
                  	{
                  		Log.v(SearchActivity.TAG, e.getMessage());
                  	}
               	     
               	   }while(cursor2.moveToNext());
                 }
                cursor2.close();
            }
            return records;

        } 
        catch (SQLException mSQLException)  
        { 
            Log.e(TAG, "getTestData >>"+ mSQLException.toString()); 
            throw mSQLException; 
        } 
    }
    
    public void insertBookmark(String title,int ctime, String user) 
    {
    	String TABLE_NAME="bookmark";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();

        try 
        { 
	        ContentValues initialValues = new ContentValues();
	
	         // convert date to string
	        String dateString = dateFormat.format(date);
	
	        initialValues.put("record_title",title);
	        initialValues.put("date",dateString);
	        initialValues.put("node_created",ctime);
	        initialValues.put("record_user",user);
	        
	
	        mDb.insert(TABLE_NAME, null, initialValues);
	        System.out.println("Record Inserted");
        } catch (SQLException mSQLException)  
        { 
            Log.e(TAG, "Inserting Bookmark"+ mSQLException.toString()); 
            throw mSQLException; 
        } 
    }
   
    
    /***********************************************************************Fetching one records********************************************************************/
    public Record searchRecordById(String id)
    {
    	Record record = new Record(); 
        try 
        { 
     	
        	String sql ="SELECT * from node_data where nid=?"; 

            Cursor cursor = mDb.rawQuery(sql, new String[] {id}); 
            if (cursor.moveToFirst())
            {
           	   do
           	   {   
           	      String title = cursor.getString(cursor.getColumnIndex("title"));
           	      String bodyValue = cursor.getString(cursor.getColumnIndex("body_value"));
  
           	      //adding to ArrayList
           	      record.setTitle(title);
           	      record.setBodyValue(bodyValue);
          	     
           	   }while(cursor.moveToNext());
             }
            cursor.close();
        } 
        catch (SQLException mSQLException)  
        { 
            Log.e(TAG, "getTestData >>"+ mSQLException.toString()); 
            throw mSQLException; 
        } 
        return record;
    }
    
    /***********************************************************************Deleting one records********************************************************************/
    public void deleteBookmark(String title, String ctime, String user) 
    {
    	//mDb.delete("bookmark","_id" + " = ?",new String[] {String.valueOf(id)});
    	mDb.delete("bookmark", "record_title = ? and node_created = ? and record_user = ?" , new String[] {title,ctime,user});
    }
    
    
    public String sqlQuery(String str)
    {
    	String newSplit[] = str.split(" ");
		StringBuilder sb = new StringBuilder();
		sb.append("select * from node_data where ");
		
		//searching data in word score
		for(int i=0;i<newSplit.length;i++)
	    {
	    	if(i<newSplit.length-1)
	    	sb.append("word_score LIKE ? and ");
	    	else
	    	sb.append("word_score LIKE ?");
	    }
		
		return sb.toString();
    }
    
    public String[] arrayOfString(String str)
    {
    	String newSplit[] = str.split(" ");
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<newSplit.length;i++)
	    {
	    	sb.append("%"+newSplit[i]+"% ");
	    }
		return sb.toString().split(" ");
    }
    
    public int lastInsertedNid()
    {  
    	int max_val=0;
    	try
    	{
    		String sql = "select max(created_time) from node_data";
        	Cursor cursor = mDb.rawQuery(sql, null);   
        	if (cursor.moveToFirst())
            {
                do
                {           
                	max_val = cursor.getInt(0);                  
                } while(cursor.moveToNext());           
            }
        	
    	}
    	catch(SQLException mSQLException)
    	{
    		Log.e(TAG, "getTestData >>"+ mSQLException.toString()); 
            throw mSQLException;
    	}
    	return max_val;
    } 
    
    public void insertRecord(int ctime,String title,String word_score, String body_value, String user) 
    {
    	String TABLE_NAME="node_data";
        
        try 
        { 
        ContentValues initialValues = new ContentValues();

         // inserting arguments
        initialValues.put("created_time",ctime);
        initialValues.put("title",title);
        initialValues.put("word_score",word_score);
        initialValues.put("body_value",body_value);
        initialValues.put("user",user);

        
        mDb.insert(TABLE_NAME, null, initialValues);
        Log.v(TAG, "Record Inserted");
        } catch (SQLException mSQLException)  
        { 
            Log.e(TAG, "Inserting Record"+ mSQLException.toString()); 
            throw mSQLException; 
        } 
    }
    
    public boolean isPresentNodeData(String title,int ctime, String user)
    {
    	try
    	{
	    	String sql = "SELECT * FROM node_data where title = ? and created_time = ? and user = ?" ;
	    	Cursor cursor = mDb.rawQuery(sql,new String[] {title,Integer.toString(ctime),user});
	    	
	    	//Log.v(SearchActivity.TAG, Integer.toString(cursor.getCount()));
	    	if(cursor != null && cursor.moveToFirst()) {
	    		cursor.close();
	    		return true;
	    	}
	    	cursor.close();
	    	return false;
	    	
	    	
    	}
    	catch(Exception e)
    	{
    		Log.v(SearchActivity.TAG,e.toString());
    	}    	
    	return false;
    }
    
    
    public boolean isPresent(String title,int ctime, String user)
    {
    	try
    	{
	    	String sql = "SELECT * FROM bookmark where record_title = ? and node_created = ? and record_user = ?" ;
	    	Cursor cursor = mDb.rawQuery(sql,new String[] {title,Integer.toString(ctime),user});
	    	
	    	Log.v(SearchActivity.TAG, Integer.toString(cursor.getCount()));
	    	if(cursor != null && cursor.moveToFirst()) {
	    		cursor.close();
	    		return true;
	    	}
	    	cursor.close();
	    	return false;
	    	
	    	
    	}
    	catch(Exception e)
    	{
    		Log.v(SearchActivity.TAG,e.toString());
    	}    	
    	return false;
    }
    
    public Record searchRecordByTitle(String title,String ctime, String user)
    {
    	Record record = new Record(); 
    	try
    	{
	    	String sql = "SELECT * FROM node_data where title = ? and created_time = ? and user = ?" ;
	    	Cursor cursor = mDb.rawQuery(sql,new String[] {title,ctime,user});
	    	
	    	if(cursor!=null && cursor.moveToFirst())
	    	{
	    		String t = cursor.getString(cursor.getColumnIndex("title"));
         	    String bodyValue = cursor.getString(cursor.getColumnIndex("body_value"));

         	    //adding to ArrayList
         	    record.setTitle(t);
         	    record.setBodyValue(bodyValue);
	    	}
	    	cursor.close();
    	}
    	catch (SQLException mSQLException)  
        { 
            Log.e(TAG, "get bookmark data >>"+ mSQLException.toString()); 
            throw mSQLException; 
        } 
    	 
    	return record;
    }
    
    public void insertSearchedTerm(String t)
    {
    	String TABLE_NAME="searched_terms";
    	        
        try 
        {
        	
        String sql = "SELECT term FROM searched_terms order by tid desc limit  10" ;
        Cursor cursor = mDb.rawQuery(sql,null);
        if(cursor != null && cursor.moveToFirst())
    	{
        	do
        	{
	        	String term = cursor.getString(cursor.getColumnIndex("term"));
	        	if(t==term) return;  
        	
        	}while(cursor.moveToNext());
    	}
        
        ContentValues initialValues = new ContentValues();

         // inserting arguments
        initialValues.put("term",t);
        mDb.insert(TABLE_NAME, null, initialValues);
        Log.v(SearchActivity.TAG,"Inserted record in searched terms");
        
        cursor.close();
        }catch (SQLException mSQLException)  
        { 
            Log.e(TAG, "Inserting Record"+ mSQLException.toString()); 
            throw mSQLException; 
        }        
    }
    
    public ArrayList<KeywordRecord> getKeywords()
    {
    	ArrayList<KeywordRecord> ak = new ArrayList<KeywordRecord>();
    	
    	String sql = "SELECT distinct(term) FROM searched_terms order by tid desc" ;
        Cursor cursor = mDb.rawQuery(sql,null);
    	
        if(cursor != null && cursor.moveToFirst())
    	{
        	do
        	{
        		KeywordRecord rec = new KeywordRecord();
        		String t = cursor.getString(cursor.getColumnIndex("term"));
        		rec.setTerm(t);
        		ak.add(rec);
        		
        	}while(cursor.moveToNext());
        	cursor.close();
    	}
        return ak;
    }
    
}



