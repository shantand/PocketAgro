package com.agrocouch;


public class Record 
{
private String title,wordScore,bodyValue,user;
int ctime;

public int get_create_time() {
	return ctime;
}

public void set_create_time(int ctime) {
	this.ctime = ctime;
}
public String getTitle() {
	return title;
}

public void setTitle(String title) {
	this.title = title;
}

public String getWordScore() {
	return wordScore;
}

public void setWordScore(String wordScore) {
	this.wordScore = wordScore;
}

public String getBodyValue() {
	return bodyValue;
}

public void setBodyValue(String bodyValue) {
	this.bodyValue = bodyValue;
}

public String getUser()
{
	return user;
}
public void setUser(String user)
{
	this.user = user;
}


}

