package com.filer.service.explorer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import com.filer.exceptions.AuthorizationException;
import com.filer.exceptions.IncorrectUsageException;
import com.filer.exceptions.ItemNotFoundException;
import com.filer.exceptions.NotImplementedException;
import com.filer.exceptions.NotOpenException;
import com.filer.objects.dao.file.FilesystemItem;

public interface FilesExplorerService {
	
	public static enum ArchiveFormat {
		TAR(".tar"), TAR_GZ(".tar.gz"), ZIP(".zip"), GZIP(".gzip");
		
		String value;
		
		public static ArchiveFormat digestFormat(String format) {
			ArchiveFormat retVal = null;
			
			switch (format.toLowerCase()) {
				case "tar":
					retVal = TAR;
					break;
					
				case "gz":
				case "gzip":
					retVal = GZIP;
					break;
					
				case "zip":
					retVal = ZIP;
					break;
					
				case "tar.gz":
				case "tar_gz":
				case "targz":
				case "tgz":
					retVal = TAR_GZ;
					break;
					
				default:
					break;
			}
			
			return retVal;
		}
		
		private ArchiveFormat( String value) {
			this.value = value;
		}
		
		 /**
		  * Returns suffix related to the compression format w/ a preceding dot
		  * @return
		  */
		public String getSuffix(){
			return this.value;
		}
		
	}
	
	
	
	public boolean deleteFiles(Set<FilesystemItem> items) throws ItemNotFoundException, AuthorizationException;
	
	public FilesystemItem getFile(String cwd, String filename, boolean createIfNotExists) throws AuthorizationException, ItemNotFoundException;
	
	public Set<FilesystemItem> getFiles(String cwd, Set<String> filename, boolean createIfNotExists) throws ItemNotFoundException, AuthorizationException;
	
	public Set<FilesystemItem> getDirectoryContent(String cwd, String filename) throws AuthorizationException, ItemNotFoundException, IncorrectUsageException;
	
	public FilesystemItem moveFiles(Set<FilesystemItem> files, String destinationCwd, String destFileName) throws ItemNotFoundException, AuthorizationException;
	
	public FilesystemItem saveFile(InputStream inputStream, String cwd, String filename) throws AuthorizationException, AuthorizationException, IOException;
	
	public Set<FilesystemItem> compressFiles(Set<FilesystemItem> files, FilesystemItem outputFileItem, ArchiveFormat archiveType) throws ItemNotFoundException, AuthorizationException, IncorrectUsageException, IncorrectUsageException, NotOpenException, NotImplementedException;
	
	public FilesystemItem downloadFile(String cwd, String filename) throws IncorrectUsageException, AuthorizationException, ItemNotFoundException;

	public FilesystemItem setFileOwner(FilesystemItem file, String username) throws IOException;
	
}
