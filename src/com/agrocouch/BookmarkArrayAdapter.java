package com.agrocouch;

import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
 
public class BookmarkArrayAdapter extends BaseAdapter 
{
	private final List<BookmarkRecord> rows;
	public BookmarkArrayAdapter(final Context context,final List<BookmarkRecord> items) 
	{
		this.rows = items;
	}
	public int getCount() 
	{
		return this.rows.size();
	}
	public Object getItem(int position) 
	{
		return this.rows.get(position);
	}
	public long getItemId(int position) 
	{
		return position;
	}
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		final BookmarkRecord row = this.rows.get(position);
		View itemView = null;
		if (convertView == null) 
		{
			LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			itemView = inflater.inflate(R.layout.list_bookmark, null);
		} 
		else 
		{
			itemView = convertView;
		}
		TextView textView = (TextView) itemView.findViewById(R.id.label);
		ImageView imageView = (ImageView) itemView.findViewById(R.id.logo);
		ImageButton imgButton = (ImageButton) itemView.findViewById(R.id.icon);
		imgButton.setTag(row);
		imgButton.setFocusable(false);
		
		textView.setText(row.getRecord_title());
		
		
		//imageView.setImageResource(R.drawable.textlogo);		
		
		return itemView;
	}
	public void deleteRow(BookmarkRecord row) 
	{
		if(this.rows.contains(row)) 
		{
			this.rows.remove(row);
		}
	}
}