package com.filer.objects.dao.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;


import org.springframework.jdbc.core.RowMapper;


public class UserGroupsMapper implements RowMapper<String> {

	@Override
	public String mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		/*Set<String> groupsSet = new HashSet<String>();
		
		while (rs.next()){
			groupsSet.add(rs.getString("name"));
		}*/
		
		String group = rs.getString("name");
		
		
		return group;
	}
	
}