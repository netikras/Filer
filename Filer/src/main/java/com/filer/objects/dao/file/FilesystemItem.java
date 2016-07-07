package com.filer.objects.dao.file;

import java.io.File;
import java.nio.file.attribute.PosixFilePermission;
import java.util.List;
import java.util.Set;

import com.filer.objects.dao.user.User;




/*
{
    "name": "Dokumentai",
    "type": "directory",
    "uid": "netikras",
    "gid": "netikras",
    "mode": "0755",
    "size": "4096",
    "dAcc": "1456843062",
    "dMod": "1456843062",
    "dChg": "1456843062",
    "actions": ["open", "download", "info", "rename", "copy", "move", "delete", "bookmark", "compress_zip", "compress_targz", "extract"]

}
 */


public class FilesystemItem extends File{
	



	private static final long serialVersionUID = 3106118240358224146L;
	
	
	String cwd;
	String name;
	String type;
	String uid;
	String gid;
	int mode;
	long size;
	long dAcc;
	long dMod;
	Set<PosixFilePermission> permissions;
	List<String> actions;
	
	long dChg;
	
	
	/* How to find whether some user can write to some file:
	 * 	1. check whether anyone can write to that file;
	 * 		y) check whether user is the OWNER:
	 * 			y) check whether owner can WRITE
	 * 				y) TRUE
	 * 				n) FALSE
	 * 			n) check whether user is a member of GROUP:
	 * 				y) check whether GROUP can write:
	 * 					y) TRUE
	 * 					n) FALSE
	 * 				n) check whether OTHER can write
	 * 					y) TRUE
	 * 					n) FALSE
	 * 
	 */
	
	
	
	public FilesystemItem(String cwd, String filename) {
		this(cwd+separator+filename);
		
	}
	
	public FilesystemItem(String filepath) {
		super(filepath);
		
	}
	
	
	public List<String> getActions() {
		return actions;
	}
	public void setActions(List<String> actions) {
		this.actions = actions;
	}
	/*public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}*/
	public Set<PosixFilePermission> getPermissions() {
		return permissions;
	}
	public void setPermissions(Set<PosixFilePermission> permissions) {
		this.permissions = permissions;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getGid() {
		return gid;
	}
	public void setGid(String gid) {
		this.gid = gid;
	}
	public int getMode() {
		return mode;
	}
	public void setMode(int mode) {
		this.mode = mode;
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	public long getdAcc() {
		return dAcc;
	}
	public void setdAcc(long dAcc) {
		this.dAcc = dAcc;
	}
	public long getdMod() {
		return dMod;
	}
	public void setdMod(long dMod) {
		this.dMod = dMod;
	}
	public long getdChg() {
		return dChg;
	}
	public void setdChg(long dChg) {
		this.dChg = dChg;
	}
	
	
	/*public String getFullPath(){
		return ""+getCwd()+File.separator+getName();
	}*/
	
	/*public File getFile(){
		return new File(getFullPath());
	}*/
	
	
	
	
	
	
	public boolean canRead(User user){
		boolean retVal = false;
		
		if (getUid().equals(user.getUsername())){
			if (getPermissions().contains(PosixFilePermission.OWNER_READ)) {
				retVal = true;
			}
		} else 
		if (user.getGroups().contains(getGid())){
			if (getPermissions().contains(PosixFilePermission.GROUP_READ)) {
				retVal = true;
			}
		} else 
		if (getPermissions().contains(PosixFilePermission.OTHERS_READ)) {
			retVal = true;
		}
		
		return retVal;
	}
	
	public boolean canWrite(User user){
		boolean retVal = false;
		
		if (getUid().equals(user.getUsername())){
			if (getPermissions().contains(PosixFilePermission.OWNER_WRITE)) {
				retVal = true;
			}
		} else 
		if (user.getGroups().contains(getGid())){
			if (getPermissions().contains(PosixFilePermission.GROUP_WRITE)) {
				retVal = true;
			}
		} else 
		if (getPermissions().contains(PosixFilePermission.OTHERS_WRITE)) {
			retVal = true;
		}
		
		return retVal;
	}
	
	public boolean canExecute(User user){
		boolean retVal = false;
		
		if (getUid().equals(user.getUsername())){
			if (getPermissions().contains(PosixFilePermission.OWNER_EXECUTE)) {
				retVal = true;
			}
		} else 
		if (user.getGroups().contains(getGid())){
			if (getPermissions().contains(PosixFilePermission.GROUP_EXECUTE)) {
				retVal = true;
			}
		} else 
		if (getPermissions().contains(PosixFilePermission.OTHERS_EXECUTE)) {
			retVal = true;
		}
		
		return retVal;
	}

	
	
	
}
