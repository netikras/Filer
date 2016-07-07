package com.filer.objects.impl.user;

import java.io.Serializable;


public class Bookmark implements Serializable{
	
	private static final long serialVersionUID = -1815446373372734488L;
	

	
	String username;
	String alias;
	String path;
	
	
	
	public Bookmark(){}
	
	public Bookmark(String alias, String path){
		setAlias(alias);
		setPath(path);
	}
	
	
	
	public String getUsername() {
		return this.username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getAlias() {
		return this.alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public String getPath() {
		return this.path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	
	
	
	
	@Override
	public String toString() {
		return "Fav [ alias=" + getAlias() + ", path=" + getPath() + "]";
	}
	
	
	
}
