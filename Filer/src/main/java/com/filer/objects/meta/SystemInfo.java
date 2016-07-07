package com.filer.objects.meta;

import java.io.File;

public class SystemInfo {
	
	
	String os_name;
	String os_ver;
	String os_arch;
	String separator;
	String lineSeparator;
	String homedir;
	String rootdir;
	int sessionTimeout_s;
	String sessionSeed;
	
	
	

	static SystemInfo systemInfo = null;
	
	public SystemInfo() {
		// http://stackoverflow.com/questions/228477/how-do-i-programmatically-determine-operating-system-in-java
		
		setOs_name(System.getProperty("os.name"));
		setOs_ver(System.getProperty("os.version"));
		setOs_arch(System.getProperty("os.arch"));
		setSeparator(File.separator);
		setLineSeparator(System.getProperty("line.separator"));
		
		setHomedir(System.getProperty("user.home"));
		setRootdir(getHomedir().substring(0, getHomedir().indexOf(File.separator)+1));
		
		setSessionTimeout_s(60*10); // 10 minutes
	}
	
	
	
	public SystemInfo(String sessionSeed) {
		// http://stackoverflow.com/questions/228477/how-do-i-programmatically-determine-operating-system-in-java
		this();
		setSessionSeed(sessionSeed);
	}
	
	
	
	
	public static SystemInfo getSystemInfo(){
		
		if (systemInfo == null){
			systemInfo = new SystemInfo();
		}

		
		return systemInfo;
	}
	
	
	
	public static SystemInfo getSystemInfo(String sessionSeed){
		
		SystemInfo sessionSystemInfo;
		
		sessionSystemInfo = new SystemInfo();
		sessionSystemInfo.setSessionSeed(sessionSeed);
		
		return sessionSystemInfo;
	}
	
	
	public String getSessionSeed() {
		return sessionSeed;
	}

	public void setSessionSeed(String sessionSeed) {
		this.sessionSeed = sessionSeed;
	}
	

	public String getOs_name() {
		return os_name;
	}

	public void setOs_name(String os_name) {
		this.os_name = os_name;
	}

	public String getOs_ver() {
		return os_ver;
	}

	public void setOs_ver(String os_ver) {
		this.os_ver = os_ver;
	}

	public String getOs_arch() {
		return os_arch;
	}

	public void setOs_arch(String os_arch) {
		this.os_arch = os_arch;
	}

	public String getSeparator() {
		return separator;
	}

	public void setSeparator(String separator) {
		this.separator = separator;
	}

	public String getLineSeparator() {
		return lineSeparator;
	}

	public void setLineSeparator(String lineSeparator) {
		this.lineSeparator = lineSeparator;
	}

	public static void setSystemInfo(SystemInfo systemInfo) {
		SystemInfo.systemInfo = systemInfo;
	}

	public int getSessionTimeout_s() {
		return sessionTimeout_s;
	}

	public void setSessionTimeout_s(int sessionTimeout_s) {
		this.sessionTimeout_s = sessionTimeout_s;
	}



	public String getHomedir() {
		return homedir;
	}



	public void setHomedir(String homedir) {
		this.homedir = homedir;
	}



	public String getRootdir() {
		return rootdir;
	}



	public void setRootdir(String rootdir) {
		this.rootdir = rootdir;
	}
	
	
	
	
	
	
	
	
}
