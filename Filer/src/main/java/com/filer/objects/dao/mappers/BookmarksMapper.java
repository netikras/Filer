package com.filer.objects.dao.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.filer.objects.impl.user.Bookmark;

public class BookmarksMapper implements RowMapper<Bookmark>{

	@Override
	public Bookmark mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		
		Bookmark bookmark = new Bookmark();
		
		bookmark.setUsername(rs.getString("username"));
		bookmark.setAlias(rs.getString("alias"));
		bookmark.setPath(rs.getString("path"));
		
		return bookmark;
	}
	
}
