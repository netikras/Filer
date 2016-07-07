package com.filer.objects.dao.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;


import org.springframework.jdbc.core.RowMapper;

import com.filer.objects.impl.user.Bookmark;

public class UserBookmarksMapper implements RowMapper<Bookmark> {

	@Override
	public Bookmark mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		/*Set<Bookmark> bookmarksSet = new HashSet<Bookmark>();
		
		while (rs.next()){
			bookmarksSet.add(new Bookmark(rs.getString("alias"), rs.getString("path")));
		}*/
		
		Bookmark bookmark = new Bookmark();
		bookmark.setAlias(rs.getString("alias"));
		bookmark.setPath(rs.getString("path"));
		
		
		return bookmark;
	}
	
}
