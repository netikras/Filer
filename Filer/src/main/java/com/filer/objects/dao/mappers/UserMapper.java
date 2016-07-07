package com.filer.objects.dao.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.filer.objects.dao.user.User;
import com.filer.objects.impl.user.UserImpl;

public class UserMapper implements RowMapper<User> {

	@Override
	public User mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		User user = new UserImpl();
		
		user.setUsername(rs.getString("name"));
		user.setPasswordHash(rs.getString("pswd"));
		user.setEmail(rs.getString("email"));
		
		
		
		return user;
	}
	
	
	
	
	
}
