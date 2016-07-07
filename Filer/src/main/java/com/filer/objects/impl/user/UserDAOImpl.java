package com.filer.objects.impl.user;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import com.filer.exceptions.AuthenticationException;
import com.filer.exceptions.ItemNotFoundException;
import com.filer.exceptions.UserUpdateException;
import com.filer.objects.dao.mappers.UserBookmarksMapper;
import com.filer.objects.dao.mappers.UserGroupsMapper;
import com.filer.objects.dao.mappers.UserMapper;
import com.filer.objects.dao.mappers.UsernamesMapper;
import com.filer.objects.dao.user.User;
import com.filer.objects.dao.user.UserDAO;
import com.filer.utils.HttpStatus;
import org.h2.Driver;

public class UserDAOImpl implements UserDAO {
	
	
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplateObject;
	
	
	@Override
	public void setDataSource(DataSource ds){
		this.dataSource = ds;
		this.jdbcTemplateObject = new JdbcTemplate(dataSource);
	}
	
	
	@Override
	/**
	 *  Not implemented yet
	 */
	public Set<String> getUsersBySubstring(String substring) {
		HashSet<String> users;
		List<String> usersList;
		String SQL = "select name from T_users where name like ?";
		
		Object[] args = new Object[]{"%"+substring+"%"};
		usersList = jdbcTemplateObject.query(SQL, args, new UsernamesMapper());
		
		users = new HashSet<String>(usersList);
		
		
		return users;
	}
	
	
	
	@Override
	public User getUser(String username) throws ItemNotFoundException {
		String SQL;
		User user;
		
		Object[] args = new Object[]{username};
		ItemNotFoundException itemNotFoundException;
		
		
		try {
			System.out.println("Loading user "+username);
			
			
			/*Connection connection = null;
			ResultSet resultSet = null;
			Statement statement = null;

			try {
				Class.forName("org.h2.Driver");
				connection = DriverManager.getConnection(
						"jdbc:h2:tcp:localhost:9092/~/FilerDB/filer.h2db.mv.db;TRACE_LEVEL_FILE=3", "root", "p_ssw0rd");
				statement = connection.createStatement();
				resultSet = statement
						.executeQuery("SELECT * FROM T_users");
				user = new UserImpl();
				if (resultSet.next()) {
					user.setUsername(resultSet.getString("name"));
					user.setEmail(resultSet.getString("email"));
					user.setPasswordHash(resultSet.getString("pswd"));
					
					System.out.println(user);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					resultSet.close();
					statement.close();
					connection.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}*/
			
			
			SQL = "select * from T_users where name = ?";
			user = jdbcTemplateObject.queryForObject(SQL, args, new UserMapper());
			
			
		} catch(EmptyResultDataAccessException e) {
			itemNotFoundException = new ItemNotFoundException();
			itemNotFoundException.setMessage1("Cannot load user");
			itemNotFoundException.setMessage2("User not found");
			itemNotFoundException.setProbableCause(username);
			itemNotFoundException.setDeveloperMessage(e.getMessage());
			itemNotFoundException.setStatusCode(HttpStatus.NOT_FOUND);
			
			throw itemNotFoundException;
		}
		
		
		System.out.println("Loading user groups");
		SQL = "select * from T_groups where username = ?";
		
		user.setGroups(new HashSet<String>(jdbcTemplateObject.query(SQL, args, new UserGroupsMapper())));
		
		System.out.println("Loading user bookmarks");
		SQL = "select * from T_favs where username = ?";
		user.setBookmarks(new HashSet<Bookmark>(jdbcTemplateObject.query(SQL, args, new UserBookmarksMapper())));
		
		
		return user;
	}

	@Override
	public User create(User user) {
		String SQL;
		
		System.out.println("Saving user to DB: "+user.getUsername());
		SQL = "insert into T_users (name, pswd, email) values (?, ?, ?)";
		jdbcTemplateObject.update(SQL, 
				user.getUsername(), 
				user.getPasswordHash(), 
				user.getEmail()
			);
		
		
	System.out.println("Saving groups to database: "+user.getGroups());
		SQL = "insert into T_groups (name, username) values (?, ?)";
		for (String group: user.getGroups()) {
			System.out.println("Group: "+group);
			jdbcTemplateObject.update(SQL, 
					group, 
					user.getUsername()
			);
		}
		
		
		System.out.println("Saving bookmarks to database");
		SQL = "insert into T_favs (username, alias, path) values (?, ?, ?)";
		for (Bookmark bookmark: user.getBookmarks()) {
			jdbcTemplateObject.update(SQL, 
					user.getUsername(), 
					bookmark.getAlias(),
					bookmark.getPath()
			);
		}
		
		System.out.println("User has been saved");
		return user;
		
	}

	@Override
	public User update(User user) {
		String SQL_upd = "";
		String SQL_ins = "";
		String SQL_del = "";
		String SQL_mrg = "";
		
		
		
		SQL_upd = "update T_users set pswd = ?, email = ? where name = ?";
		SQL_mrg = "MERGE INTO T_users (name, pswd, email) KEY(name) VALUES (?, ?, ?)";
		jdbcTemplateObject.update(SQL_mrg, 
				user.getPasswordHash(), 
				user.getEmail(),
				user.getUsername()
			);
		
		
		
		// TODO fix all this mess. This is a very ugly workaround..
		// TODO track user changes by saving them to a separate arrayList _dirty. Compare entries and update only what differs
		SQL_ins = "insert into T_groups (name, username) values (?, ?)";
		//SQL_upd = "update T_groups set name = ? where username = ?";
		SQL_del = "delete from T_groups where username = ?";
		SQL_mrg = "MERGE INTO T_groups (name, username) KEY(name, username) VALUES (?, ?)";
		//jdbcTemplateObject.update(SQL_del, user.getUsername());
		for (String group: user.getGroups()){
				jdbcTemplateObject.update(SQL_mrg,
						group,
						user.getUsername()
					);
		}
		
		
		
		SQL_del = "delete from T_favs where username = ?";
		SQL_ins = "insert into T_favs (alias, path, username) values (?, ?, ?)";
		SQL_upd = "update T_favs set path = ? where username = ? and alias = ?";
		SQL_mrg = "MERGE INTO T_favs (username, alias, path) KEY(username, alias, path) VALUES (?, ?, ?)";
		//jdbcTemplateObject.update(SQL_del, user.getUsername());
		for (Bookmark bookmark: user.getBookmarks()){
			
			jdbcTemplateObject.update(SQL_mrg,
					user.getUsername(),
					bookmark.getAlias(),
					bookmark.getPath()
				);
			System.out.println("Inserting new bookmark: "+bookmark.getAlias()+" :: "+bookmark.getPath()	);
			
		}
		
		
		return user;
		
	}

	@Override
	public boolean delete(User user) {
		//if (user == null) return false;
		
		return delete(user.getUsername());
	}

	@Override
	public boolean delete(String username)  {
		int rowsUpdated = 0;
		String SQL;
		
		SQL = "delete from T_users where name = ?";
		rowsUpdated = jdbcTemplateObject.update(SQL, username);
		
		SQL = "delete from T_groups where username = ?";
		rowsUpdated+= jdbcTemplateObject.update(SQL, username);
		
		SQL = "delete from T_favs where username = ?";
		rowsUpdated+= jdbcTemplateObject.update(SQL, username);
		
		return rowsUpdated > 0;
	}
	
	
	@Override
	public boolean deleteBookmark(User user, String bookmarkName) throws ItemNotFoundException, UserUpdateException {
		
		boolean deleted = false;
		
		int rowsUpdated = 0;
		String SQL;
		
		SQL = "delete from T_favs where username = ? and alias = ?";
		
		rowsUpdated = jdbcTemplateObject.update(SQL, user.getUsername(), bookmarkName);
		
		deleted = rowsUpdated > 0;
		
		return deleted;
	}
	
	@Override
	public boolean deleteGroup(User user, String group) throws ItemNotFoundException, UserUpdateException {
		
		boolean deleted = false;
		
		int rowsUpdated = 0;
		String SQL;
		
		SQL = "delete from T_groups where username = ? and name = ?";
		
		rowsUpdated = jdbcTemplateObject.update(SQL, user.getUsername(), group);
		
		if (rowsUpdated > 0) {
			deleted = true;
		}
		
		
		
		/*try {
			Connection connection = DriverManager.getConnection("localhost/filer", "netikras", "p_ssw0rd");
			connection.setAutoCommit(false);
			
			PreparedStatement preparedStatement = connection.prepareStatement(SQL);
			preparedStatement.setString(0, user.getUsername());
			preparedStatement.setString(1, group);
			
			preparedStatement.executeUpdate();
			
			
			connection.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		
		
		return deleted;
		
		
	}
	
	
	

	@Override
	public List<User> listUsers()  {
		List<User> usersPartial = null;
		List<User> usersLoaded = new ArrayList<User>();
		String SQL;
		
		SQL = "select * from T_users";
		usersPartial = jdbcTemplateObject.query(SQL, new UserMapper());
		
		
		
		for (User user: usersPartial) {
			try {
				usersLoaded.add(getUser(user.getUsername()));
			} catch(Exception e){
				
			}
		}
		
		return usersLoaded;
	}
	
	
	
	
	

	
	@Override
	public boolean login(User user, String passwd, int sessionTimeout) throws AuthenticationException {
		
		AuthenticationException authenticationException;
		
		if (user != null) {
			
			user.login(passwd, sessionTimeout);
			user.addGroup("root");
		} else {
			System.out.println("E: login() :: User is null");
			
			authenticationException = new AuthenticationException();
			authenticationException.setMessage1("Cannot login a user");
			authenticationException.setMessage1("User is NULL");
			authenticationException.setStatusCode(HttpStatus.UNAUTHORIZED);
			throw authenticationException;
			
			//return false;
		}
		
		return user.isLoggedIn();
	}





	@Override
	public boolean logout(User user) {
		if (user != null) {
			user.logout();
		}
		return isLoggedIn(user);
	}





	@Override
	public boolean isLoggedIn(User user) {
		boolean loggedIn = false;
		
		if (user != null) {
			loggedIn = user.isLoggedIn();
		} else {
			return false;
		}
		return loggedIn;
	}
	
	
	@Override
	public void setLock(User user, LOCK_MODE mode){
		
	}
	
	
	@Override
	public LOCK_MODE getLock(User user) {
		LOCK_MODE lock = LOCK_MODE.L_NONE;
		
		
		
		return lock;
	}
	
	
	
}
