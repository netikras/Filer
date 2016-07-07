package com.filer.service.explorer;

import java.io.File;
import java.util.HashSet;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.filer.objects.meta.JSONResponse;


@RestController
public class Controller_moveFile {
	
	@RequestMapping(value="/move/{srcFilename}", method=RequestMethod.POST, produces="application/json")
	@ResponseStatus(code=HttpStatus.CREATED, reason="File has been moved")
	@ResponseBody public void moveFile(
			@PathVariable(value="srcFilename")                  HashSet<String> srcFilename,
			@RequestParam(value="dstFilename", required=false)  String dstFilename,
			@RequestParam(value="srcCwd",      required=true )  String srcCwd,
			@RequestParam(value="dstCwd",      required=false)  String dstCwd
			) {
		
		JSONResponse retVal = new JSONResponse();
		
		if (dstFilename != null && srcFilename != null && srcFilename.size() > 1){
			retVal.setCode(1);
			retVal.setMessage1("Cannot move multiple files to a single destination file");
		} else {
			if (dstCwd == null) dstCwd = srcCwd;
			if (dstCwd == null) {
				retVal.setCode(2);
				retVal.setMessage1("Source cwd not known");
			} else {
				for (String filename : srcFilename){
					File srcFile = new File(srcCwd+File.separator+filename);
					File dstFile = new File(dstCwd+File.separator+filename);
					
					// check if 
					
				}
			}
		}
		
//		return retVal;
	}
	
	
}
