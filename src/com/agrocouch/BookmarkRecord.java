package com.agrocouch;

public class BookmarkRecord 
{
int id;
String ctime,record_user,record_title,date;
public int getId() {
	return id;
}
public void setId(int id) {
	this.id = id;
}
public String getCreate_time() {
	return ctime;
}
public void setCreate_time(String  ctime) {
	this.ctime = ctime;
}
public String getRecord_user() {
	return record_user;
}
public void setRecord_user(String record_user) {
	this.record_user = record_user;
}
public String getDate() {
	return date;
}
public void setDate(String date) {
	this.date = date;
}
public String getRecord_title() {
	return record_title;
}
public void setRecord_title(String record_title) {
	this.record_title = record_title;
}
}
