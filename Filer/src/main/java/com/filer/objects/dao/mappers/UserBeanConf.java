package com.filer.objects.dao.mappers;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.filer.objects.dao.user.UserDAO;
import com.filer.objects.impl.user.UserDAOImpl;




@Configuration
@ComponentScan(basePackages="com.filer.objects.dao")
//@EnableWebMvc
public class UserBeanConf {//extends WebMvcConfigurerAdapter {
	
	@Bean(name="UserDAODataSource")
	public DataSource getDataSource() {
		
		
		StringBuilder dbUrlBuilder;
		DriverManagerDataSource dataSource;
		
		
		//dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource = new DriverManagerDataSource();
		
		
		dataSource.setDriverClassName("org.h2.Driver");
		
		String dbDriver = "mysql";
		String dbAddress = "//localhost";
		String dbPort = ":3306";
		String dbName = "filer.h2db";
		String dbPath = "/home/netikras/FilerDB/";
		String dbParams = "";
		
		dbPath = "/";
		dbName = "Filer";
		
		dbDriver = "h2";
		dbAddress = "tcp:localhost";
		dbPort = ":9092";
		dbPath = "/~/FilerDB/";
		dbName = "filer.h2db";
		dbParams = ";TRACE_LEVEL_SYSTEM_OUT=3";
		
		dbUrlBuilder = new StringBuilder("jdbc:");
		
		dbUrlBuilder.append(dbDriver).append(":");
		dbUrlBuilder.append(dbAddress).append(dbPort);
		dbUrlBuilder.append(dbPath).append(dbName);
		dbUrlBuilder.append(dbParams);
		
		System.out.println("Connecting to DB. URL=["+dbUrlBuilder.toString()+"]");
		
		//dataSource.setUrl("jdbc:mysql://localhost:3306/Filer");
		dataSource.setUrl(dbUrlBuilder.toString());
		dataSource.setUsername("root");
		dataSource.setPassword("p_ssw0rd");
		
		
		return dataSource;
	}
	
	
	@Bean(name="UserDAOBean")
	public UserDAO getUserDAO(){
		UserDAO userDAO = new UserDAOImpl();
		
		userDAO.setDataSource(getDataSource());
		
		return userDAO;
	}
	
	
}
