package com.filer.service.explorer;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.UserPrincipal;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.util.FileCopyUtils;

import com.filer.exceptions.AuthorizationException;
import com.filer.exceptions.IncorrectUsageException;
import com.filer.exceptions.ItemNotFoundException;
import com.filer.exceptions.NotImplementedException;
import com.filer.exceptions.NotOpenException;
import com.filer.objects.dao.file.FilesystemItem;
import com.filer.objects.factories.FilesystemItemFactory;
import com.filer.objects.impl.file.Archive;

public class FilesExplorerServiceImpl implements FilesExplorerService {

	
	
	
	public static FilesExplorerService getInstance() {
		FilesExplorerService filesExplorerService = new FilesExplorerServiceImpl();
		
		return filesExplorerService;
	}
	
	
	
	
	
	
	
	
	
	@Override
	public boolean deleteFiles(Set<FilesystemItem> items) throws ItemNotFoundException, AuthorizationException {
		boolean deleted = false;
		ItemNotFoundException itemNotFoundException;
		
		
		if (items != null && items.size() > 0) {
			deleted = true;
			for (FilesystemItem item : items) {
				if (item.exists()) {
					try {
						System.out.println("Deleting file: "+item);
						item.delete();
					} catch(Exception e) {
						deleted = false;
					}
				} else {
					itemNotFoundException = new ItemNotFoundException();
					
					itemNotFoundException.setMessage1("Cannot delete file");
					itemNotFoundException.setMessage2("File does not exist");
					itemNotFoundException.setProbableCause(item.getAbsolutePath());
					itemNotFoundException.setStatusCode(HttpStatus.NOT_FOUND);
					
					throw itemNotFoundException;
				}
			}
			
		}
		
		return deleted;
	}
	
	
	
	
	
	
	
	
	@Override
	public FilesystemItem getFile(String cwd, String filename, boolean createIfNotExists) throws AuthorizationException {
		FilesystemItem filesystemItem = null;
		
		FilesystemItemFactory factory = new FilesystemItemFactory();
		File file;
		
		file = new File(cwd+File.separator+filename);
		
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch(IOException e) {
				System.err.println("Cannot create a new file: "+file.getAbsolutePath());
				e.printStackTrace();
			}
		}
		
		
		filesystemItem = factory.getItem(cwd, filename);
		
		
		return filesystemItem;
	}

	@Override
	public Set<FilesystemItem> getFiles(String cwd, Set<String> filenames, boolean createIfNotExists) throws ItemNotFoundException,
			AuthorizationException {
		
		Set<FilesystemItem> retVal = new HashSet<FilesystemItem>();
		
		if (filenames != null && filenames.size() > 0) {
			for (String filename : filenames) {
				retVal.add(getFile(cwd, filename, createIfNotExists));
			}
		} else {
			System.err.println("getFiles("+cwd+", "+filenames+", "+createIfNotExists+")");
		}
		
		return retVal;
	}
	
	
	public Set<FilesystemItem> getDirectoryContent(String cwd, String filename) throws AuthorizationException, ItemNotFoundException, IncorrectUsageException {
		
		Set<FilesystemItem> retVal = new HashSet<FilesystemItem>();
		
		FilesystemItem file = null;
		IncorrectUsageException incorrectUsageException;
		ItemNotFoundException itemNotFoundException;
		
		file = getFile(cwd, filename, false);
		
		if (file.exists()) {
			if (file.isDirectory()) {
				retVal = getFiles(
							file.getAbsolutePath(), 
							new HashSet<String>(
									Arrays.asList(file.list())), 
							false
						);
			} else {
				incorrectUsageException = new IncorrectUsageException();
				incorrectUsageException.setMessage1("Cannot list directory contents");
				incorrectUsageException.setMessage2("Item is not a directory");
				incorrectUsageException.setProbableCause(file.getAbsolutePath());
				incorrectUsageException.setStatusCode(HttpStatus.CONFLICT);
				
				throw incorrectUsageException;
			}
		} else {
			itemNotFoundException = new ItemNotFoundException();
			itemNotFoundException.setMessage1("Cannot list directory contents");
			itemNotFoundException.setMessage2("Directory cannot be found");
			itemNotFoundException.setProbableCause(file.getAbsolutePath());
			itemNotFoundException.setStatusCode(HttpStatus.NOT_FOUND);
			
			throw itemNotFoundException;
		}
		
		return retVal;
	}
	
	
	
	@Override
	public FilesystemItem moveFiles(Set<FilesystemItem> files, String destinationCwd, String destFileName)
			throws ItemNotFoundException, AuthorizationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FilesystemItem saveFile(InputStream inputStream, String cwd, String filename) throws AuthorizationException, IOException {
		System.out.println("Saving file "+ filename +" in "+cwd+"");
		FilesystemItem outputFile = getFile(cwd, filename, true);
		
		BufferedOutputStream outputFileOutputStream = new BufferedOutputStream(new FileOutputStream(outputFile));
		//FileCopyUtils.copy(inputStream, outputFileOutputStream);
		
		byte[] buffer = new byte[1024];
		int len;
		int iterations = 0;
		while ((len = inputStream.read(buffer)) != -1) {
			outputFileOutputStream.write(buffer, 0, len);
			iterations++;
		}
		outputFileOutputStream.close();
		
		System.out.println("Saved a file: "+outputFile.getAbsolutePath()+" . Approx size: " + (iterations*buffer.length/1024) + " KB");
		
		return outputFile;
	}
	
	
	@Override
	public FilesystemItem setFileOwner(FilesystemItem file, String username) throws IOException {
		Path path;
		UserPrincipalLookupService userPrincipalLookupService = FileSystems.getDefault().getUserPrincipalLookupService();
		UserPrincipal userPrincipal = userPrincipalLookupService.lookupPrincipalByName(username);
		
		if (file != null && file.exists()) {
			path = Paths.get(file.getAbsolutePath());
			Files.setOwner(path, userPrincipal);
		}
		
		return file;
	};
	

	@Override
	public Set<FilesystemItem> compressFiles(Set<FilesystemItem> files, FilesystemItem outputFileItem, ArchiveFormat archiveType)
			throws ItemNotFoundException, AuthorizationException, IncorrectUsageException, NotOpenException, NotImplementedException {
		
		
		HashSet<FilesystemItem> results = new HashSet<FilesystemItem>();
		FilesystemItemFactory filesystemItemFactory = new FilesystemItemFactory();
		IncorrectUsageException incorrectUsageException;
		Archive archive;
		
		
		
		if (files != null && files.size() > 0) {
			
			if (archiveType != null) {
				
				if (outputFileItem == null || outputFileItem.getName() == null || outputFileItem.getName().isEmpty()) {
					
					outputFileItem = null;
					
					for (FilesystemItem item : files) {
						archive = new Archive(archiveType, item.getAbsolutePath());
						
						try {
							
							archive.openArchive();
							archive.addFile(item);
							archive.closeArchive();
							
						} catch (IOException e) {
							// TODO: handle exception
							e.printStackTrace();
						}
						
						results.add(filesystemItemFactory.getItem(archive.getAbsolutePath()));
					}
					
				} else {
					
					archive = new Archive(archiveType, outputFileItem.getAbsolutePath());
					
					try {
						archive.openArchive();
						
						for (FilesystemItem item : files) {
							archive.addFile(item);
						}
						
						archive.closeArchive();
					} catch (IOException e) {
						// TODO: handle exception
						e.printStackTrace();
					}
					
					results.add(archive);
				}
				
			} else {
				incorrectUsageException = new IncorrectUsageException();
				incorrectUsageException.setMessage1("Cannot create an archive");
				incorrectUsageException.setMessage2("Archive Format not provided");
				incorrectUsageException.setStatusCode(HttpStatus.BAD_REQUEST);
				incorrectUsageException.setProbableCause(outputFileItem.getAbsolutePath());
				
				throw incorrectUsageException;
			}
			
		}
		
		
		return results;
	}




	@Override
	public FilesystemItem downloadFile(String cwd, String filename) throws IncorrectUsageException,
			AuthorizationException, ItemNotFoundException {
		
		IncorrectUsageException incorrectUsageException;
		ItemNotFoundException itemNotFoundException;
		//AuthorizationException authorizationException;
		
		FilesystemItem file = getFile(cwd, filename, false);
		
		
		
		if (file.exists()){
			
			if (file.isDirectory()) {
				incorrectUsageException = new IncorrectUsageException();
				incorrectUsageException.setStatusCode(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
				incorrectUsageException.setMessage1("Cannot download a file");
				incorrectUsageException.setMessage2("Requested file is a directory. Directories cannot be transferred");
				incorrectUsageException.setProbableCause(filename);
				
				throw incorrectUsageException;
				
			}
			
		} else {
			itemNotFoundException = new ItemNotFoundException();
			itemNotFoundException.setStatusCode(HttpStatus.NOT_FOUND);
			itemNotFoundException.setMessage1("Cannot download a file");
			itemNotFoundException.setMessage2("File not found");
			itemNotFoundException.setProbableCause(filename);
			
			throw itemNotFoundException;
		}
		
		return file;
	}
	
}
