package com.agrocouch;


import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
 
public class MobileArrayAdapter extends ArrayAdapter<String> {
	private final Context context;
	private final ArrayList<String> titles;
	private final ArrayList<String> user;
	
 
	public MobileArrayAdapter(Context context, ArrayList<String> titles, ArrayList<String> user) {
		super(context, R.layout.display_row, titles);
		this.context = context;
		this.titles = titles;
		this.user = user;
		
	}
 
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 
		View rowView = inflater.inflate(R.layout.display_row, parent, false);
		TextView textView1 = (TextView) rowView.findViewById(R.id.title_line);
		TextView textView2 = (TextView) rowView.findViewById(R.id.userLine);		
		
		textView1.setText(titles.get(position));
		textView2.setText(user.get(position));
				
		return rowView;
	}
}
