package com.filer.utils;

import java.util.Calendar;
import java.util.Random;


public class RANDOM {
	
	
	public static final String LOWER = "qwertyuiopasdfghjklzxcvbnm";
	public static final String UPPER = "QWERTYUIOPASDFGHJKLZXCVBNM";
	public static final String DIGIT = "0123456789";
	public static final String SPECIAL = "!@#$%^&*()_-=+[]{}\\|;:'\"/><.,~`";
	
	
	private static Random rand = new Random();
	
	public static int nextInt(){
		return rand.nextInt();
	}
	
	public static int nextInt(int max){
		return rand.nextInt(max);
	}
	
	public static int nextInt(int min, int max){
		return rand.nextInt(max - min) + min;
	}
	
	
	
	public static String nextString(int length){
		return nextString(LOWER+UPPER+DIGIT, length);
	}
	
	public static String nextString(String dictionary, int length){
		String retVal = "";
		
		for(int i=0; i<length; i++)
			retVal += dictionary.charAt(rand.nextInt(dictionary.length()));
		
		return retVal;
	}
	
	public static String nextString(String dictionary, int length_MIN, int length_MAX){
		return nextString(dictionary, nextInt(length_MIN, length_MAX));
	}
	
	public static long nextLong(long min, long max){
		long retVal = 0;
		long gap = max - min;
		long nextLong = rand.nextLong() & 0xffffffffL;
		
		if(gap == 0) retVal = max;
		else 		 retVal = min + ( nextLong % gap );
		
		
		return retVal;
	}
	
	public static Calendar nextDate(Calendar between_MIN, Calendar between_MAX){
		Calendar retVal = Calendar.getInstance();
		
		retVal.setTimeInMillis(
				nextLong(
						between_MIN.getTimeInMillis(), 
						between_MAX.getTimeInMillis()
					)
				);
		
		return retVal;
	}
	
	
	
}
