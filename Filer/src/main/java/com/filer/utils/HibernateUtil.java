/*package com.filer.utils;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

import com.filer.objects.impl.user.UserImpl;

public class HibernateUtil {
	
	private static Map<String, Session> sessions = new HashMap<String, Session>();
	
	
	public static final String config_user = "User.hbm.xml";
	public static final String config_favs = "Fav.hbm.xml" ;
	
	
	
	public static Session getDBSession() {
		Session retVal = null;
		//SessionFactory factory;
		
		try{
			
			factory = new AnnotationConfiguration().
	                   configure().
	                   addPackage("com.filer.objects.dao"). //add package if used.
	                   addAnnotatedClass(User.class).
	                   buildSessionFactory();
			
			
			Configuration configuration = new Configuration();
			System.out.println("Loading HBM configuration");
			configuration.configure();
			configuration.addPackage("com.filer.objects.dao");
			configuration.addAnnotatedClass(UserImpl.class);
			
			System.out.println("Building srb");
			StandardServiceRegistryBuilder ssrb = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
			System.out.println("building the factory");
			SessionFactory sessionFactory = configuration.buildSessionFactory(ssrb.build());
			System.out.println("Opening session");
			retVal = sessionFactory.openSession();
			
			
		} catch (HibernateException e){
			System.err.println("Hibernate error while opening session ");
			e.printStackTrace();
			
		} catch (Exception e) {
			System.err.println("Common error while opening session");
			e.printStackTrace();
			
		}
		
		
		
	
	return retVal;
	}
	
	public static Session getDBSessionByConfig(String config) {
		Session retVal = null;
		
			if (sessions.containsKey(config)){
				System.out.println("sessions.containsKey(config)");
				retVal = sessions.get(config);
			} else {
				try{
					
					Configuration configuration = new Configuration();
					System.out.println("Loading HBM configuration: "+config);
					//configuration.configure(config);
					configuration.configure();
					System.out.println("Building srb");
					StandardServiceRegistryBuilder ssrb = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
					System.out.println("building the factory");
					SessionFactory sessionFactory = configuration.buildSessionFactory(ssrb.build());
					System.out.println("Opening session");
					retVal = sessionFactory.openSession();
					
					sessions.put(config, retVal);
					
				} catch (HibernateException e){
					System.err.println("Hibernate error while opening session for config: "+config);
					e.printStackTrace();
					
				} catch (Exception e) {
					System.err.println("Common error while opening session for config: "+config);
					e.printStackTrace();
					
				}
			}
			
			
			
		
		return retVal;
	}
	
	
}
*/