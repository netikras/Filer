package com.filer.service.user;

import java.util.Set;

import com.filer.objects.dao.user.User;
import com.filer.objects.impl.user.Bookmark;



public interface BookmarksService {
	
	
	
	
	public Bookmark getUserBookmark(User user, String bookmarkName);
	
	public Set<Bookmark> getUserBookmarks(User user);
	
	public Bookmark createUserBookmark(User user, String bookmarkName, String bookmarkValue);
	
	public Bookmark updateUserBookmark(User user, String bookmarkName, String bookmarkValue);
	
	public boolean deleteUserBookmark(User user, String bookmarkName);
	
	
}
