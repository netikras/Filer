package com.filer.objects.factories;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.filer.objects.dao.file.FilesystemItem;

public class FilesystemItemFactory {
	
	
	
	public Set<FilesystemItem> getItems(String dirPath){
		
		Set<FilesystemItem> retVal = new HashSet<FilesystemItem>();
		
		File dir = new File(dirPath);
		
		
		
		if (dir.exists()){
			if (dir.isDirectory()){
				for (File f: dir.listFiles()){
					retVal.add(getItem(f.getAbsolutePath()));
				}
			} else {
				retVal.add(getItem(dir.getAbsolutePath()));
			}
		}
		
		
		return retVal;
		
		
	}
	
	public FilesystemItem getItem(String fileAddress){
		//return getItem(new File(filepath));
		
		
		FilesystemItem item = null;
		BasicFileAttributes attr = null;
		PosixFileAttributes posixAttr = null;
		Set<PosixFilePermission> posixPermissions = null;
		Path filePath = null;
		
		
		
		item = new FilesystemItem(fileAddress);
		
		
		filePath = Paths.get(item.getAbsolutePath());
		
		List<String> actionsList = new ArrayList<String>();
			actionsList.add("open");
			actionsList.add("delete");
			actionsList.add("move");
			actionsList.add("download");
			actionsList.add("info");
			actionsList.add("rename");
			actionsList.add("compress_zip");
			actionsList.add("compress_tgz");
		
		item.setType(getItemtype(item));
		item.setSize(item.length());
		
		item.setActions(actionsList);
		
		try {
			attr = Files.readAttributes(filePath, BasicFileAttributes.class);
			
			item.setdAcc(attr.lastAccessTime().toMillis());
			item.setdMod(attr.lastModifiedTime().toMillis());
			item.setdChg(attr.creationTime().toMillis());
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		
		try {
			posixAttr = Files.getFileAttributeView(filePath, PosixFileAttributeView.class).readAttributes();
			
			item.setUid(posixAttr.owner().getName());
			item.setGid(posixAttr.group().getName());
			posixAttr.permissions();
			//item.setMode(posixAttr.permissions());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
		try {
			posixPermissions = posixAttr.permissions();
			
			item.setPermissions(posixPermissions);
			
			int perm_u=0;
			int perm_g=0;
			int perm_o=0;
			
			if (posixPermissions.contains(PosixFilePermission.OWNER_READ))     perm_u+=4;
			if (posixPermissions.contains(PosixFilePermission.OWNER_WRITE))    perm_u+=2;
			if (posixPermissions.contains(PosixFilePermission.OWNER_EXECUTE))  perm_u+=1;
			
			if (posixPermissions.contains(PosixFilePermission.GROUP_READ))     perm_g+=4;
			if (posixPermissions.contains(PosixFilePermission.GROUP_WRITE))    perm_g+=2;
			if (posixPermissions.contains(PosixFilePermission.GROUP_EXECUTE))  perm_g+=1;
			
			if (posixPermissions.contains(PosixFilePermission.OTHERS_READ))    perm_o+=4;
			if (posixPermissions.contains(PosixFilePermission.OTHERS_WRITE))   perm_o+=2;
			if (posixPermissions.contains(PosixFilePermission.OTHERS_EXECUTE)) perm_o+=1;
			
			item.setMode(
					  perm_u*100
					+ perm_g*10
					+ perm_o
					);
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		
		return item;
	}
	
	public FilesystemItem getItem(String cwd, String filename){
		return this.getItem(cwd+File.separator + filename);
	}
	
	
	/*public FilesystemItem getItem(File file){
		
		
		
	}*/
	
	
	String getItemtype(File file){
		String retVal = "file";
		
		if (file.isDirectory()) return "directory";
		
		
		return retVal;
		
		
	}
	
	

}
