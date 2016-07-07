package com.filer.controllers.explorer;


import org.springframework.http.MediaType;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.filer.exceptions.AuthenticationException;
import com.filer.exceptions.AuthorizationException;
import com.filer.exceptions.IncorrectUsageException;
import com.filer.exceptions.ItemNotFoundException;
import com.filer.exceptions.NotImplementedException;
import com.filer.exceptions.NotOpenException;
import com.filer.objects.dao.file.FilesystemItem;
import com.filer.objects.dao.user.User;
import com.filer.objects.meta.JSONResponse;
import com.filer.service.auth.AuthenticationService;
import com.filer.service.explorer.FilesExplorerService;
import com.filer.service.explorer.FilesExplorerService.ArchiveFormat;
import com.filer.service.explorer.FilesExplorerServiceImpl;
import com.filer.utils.GlobalFactory;
	


@RestController
@RequestMapping(value="/explorer")
public class FilesExplorerController {
	
	
	@RequestMapping(value="/delete", method=RequestMethod.DELETE, produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseStatus(code=HttpStatus.OK, reason="Files have been deleted")
	@ResponseBody public JSONResponse deleteFileSystemItem(
			@RequestParam (value="fileNames", required=true) Set<String> fileNames,
			@RequestParam (value="cwd", required=true)       String cwd,
			HttpServletRequest request
			) throws ItemNotFoundException, AuthorizationException, AuthenticationException {
		
		
		JSONResponse retVal = new JSONResponse();
		FilesExplorerService filesExplorerService = FilesExplorerServiceImpl.getInstance();
		AuthenticationService authenticationService = GlobalFactory.getAuthenticationService_singlet();
		
		authenticationService.assumeIsAuthenticated(request);
		
		filesExplorerService.deleteFiles(filesExplorerService.getFiles(cwd, fileNames, false));
		
		return retVal;
	}
	
	
	
	
	@RequestMapping(value="/open", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseStatus(code=HttpStatus.OK)
	@ResponseBody public Set<FilesystemItem> listDirectory(
				@RequestParam(value="cwd"  , required=false ) String       cwd,
				@RequestParam(value="file" , required=true  ) String file,
				HttpServletRequest request
			) throws ItemNotFoundException, AuthorizationException, IncorrectUsageException, AuthenticationException {
		
		Set<FilesystemItem> retVal = null;
		FilesExplorerService filesExplorerService = FilesExplorerServiceImpl.getInstance();
		AuthenticationService authenticationService = GlobalFactory.getAuthenticationService_singlet();
		
		authenticationService.assumeIsAuthenticated(request);
		
		retVal = new HashSet<FilesystemItem>();
		
		System.out.println("Requested file for OPEN: cwd="+cwd+";");
		
		
		
		if (file != null && ! file.isEmpty()){
//			retVal = filesExplorerService.getFiles(cwd, files, false);
			retVal = filesExplorerService.getDirectoryContent(cwd, file);
		} else {
			System.out.println("RequestParam('file') == "+file);
		}
		
		
		
		
		return retVal;
	}
	
	
	
	
	@RequestMapping(value="/upload", method=RequestMethod.POST, produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseStatus(code=HttpStatus.CREATED, reason="File has been saved")
	@ResponseBody public List<FilesystemItem> uploadFile(
				@RequestParam(value="cwd"      , required=true ) String        cwd,
				//@RequestParam(value="file"     , required=true ) MultipartFile file
				@RequestPart(name="file", required=true) List<MultipartFile> files,
				HttpServletRequest request
			) throws AuthorizationException, IOException, AuthenticationException{
		
		List<FilesystemItem> retVal = new ArrayList<FilesystemItem>();
		FilesExplorerService filesExplorerService = FilesExplorerServiceImpl.getInstance();
		FilesystemItem filesystemItem;
		User user;
		
		AuthenticationService authenticationService;
		
		authenticationService = GlobalFactory.getAuthenticationService_singlet();
		user = authenticationService.assumeIsAuthenticated(request);
		
		System.out.println("Requested file for UPLOAD: cwd="+cwd+"; "+files);
		
		if (files != null) {
			for(MultipartFile file: files) {
				if (file != null && !file.isEmpty()) {
					System.out.println("Uploaded a file: "+file.getOriginalFilename());
					
					filesystemItem = filesExplorerService.saveFile(file.getInputStream(), cwd, file.getOriginalFilename());
					filesExplorerService.setFileOwner(filesystemItem, user.getUsername());
					retVal.add(filesystemItem);
				}
			}
		}
		
		
		return retVal;
	}
	
	
	
	
	
	@RequestMapping(value="/download", method=RequestMethod.GET, produces=MediaType.APPLICATION_OCTET_STREAM_VALUE)
	@ResponseStatus(code=HttpStatus.OK)
	@ResponseBody public FileSystemResource downloadFile(
				@RequestParam(value="cwd"  , required=false ) String      cwd,
				@RequestParam(value="file" , required=true  ) String filename,
				HttpServletResponse response
			) throws ItemNotFoundException, IncorrectUsageException, AuthorizationException{
		
		FileSystemResource retVal = null;
		FilesExplorerService filesExplorerService = FilesExplorerServiceImpl.getInstance();
		
		
		System.out.println("Requested file for DOWNLOAD: cwd="+cwd+"; name="+filename);
		
		
		retVal = new FileSystemResource(filesExplorerService.downloadFile(cwd, filename));
		response.setHeader("Content-Disposition", "attachment; filename='"+filename+"'");
		
		
		return retVal;
	}
	
	
	
	
	@RequestMapping(value="/compress/{format}", method=RequestMethod.POST, produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseStatus(code=HttpStatus.CREATED, reason="Files have been copressed")
	@ResponseBody public Set<FilesystemItem> compressFiles(
			@PathVariable(value="format") String format,
			@RequestParam(value="outputFileName", required=false)         String outputFileName,
			@RequestParam(value="cwd"  , required=false, defaultValue="") String cwd,
			@RequestParam(value="file" , required=true  )                 Set<String> inputFileNames
			) throws FileNotFoundException, ItemNotFoundException, AuthorizationException, IncorrectUsageException, NotOpenException, NotImplementedException {
		
		Set<FilesystemItem> retVal = null;
		FilesExplorerService filesExplorerService = FilesExplorerServiceImpl.getInstance();
		
		System.out.println("Requested file for COMPRESS_"+format+": cwd="+cwd);
		
		filesExplorerService.compressFiles(
					filesExplorerService.getFiles(cwd, inputFileNames, false), 
					filesExplorerService.getFile(cwd, outputFileName, true), 
					//filesExplorerService.resolveArchiveFormatFromString(format)
					ArchiveFormat.digestFormat(format)
				);
		
		
		
		return retVal;
	}
	
	
	
	
	
	
	@RequestMapping(value="/delete", method=RequestMethod.PUT, produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseStatus(code=HttpStatus.OK, reason="Files have been deleted")
	@ResponseBody public JSONResponse deleteFile(
				@RequestParam(value="cwd"      , required=true ) String       cwd,
				@RequestParam(value="file"     , required=true ) Set<String> files
			) throws AuthorizationException, ItemNotFoundException{
		JSONResponse retVal = new JSONResponse();
		FilesExplorerService filesExplorerService = FilesExplorerServiceImpl.getInstance();
		
		System.out.println("Requested file for DELETE: cwd="+cwd+";");
		
		if (files != null && files.size() > 0) {
			filesExplorerService.deleteFiles(
						filesExplorerService.getFiles(cwd, files, false)
					);
		}
		
		
		return retVal;
	}
	
	
	
	
}
