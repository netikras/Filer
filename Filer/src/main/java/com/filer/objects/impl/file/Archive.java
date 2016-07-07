package com.filer.objects.impl.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;

import com.filer.exceptions.IncorrectUsageException;
import com.filer.exceptions.NotImplementedException;
import com.filer.exceptions.NotOpenException;
import com.filer.objects.dao.file.FilesystemItem;
import com.filer.service.explorer.FilesExplorerService.ArchiveFormat;

public class Archive extends FilesystemItem {
	
	
	
	
	
	private static final long serialVersionUID = 7238764431594664695L;
	
	private final ArchiveFormat TYPE;
	
	byte[] buffer = new byte[1024];
	private FileOutputStream fileOutputStream;
	private OutputStream archiveOutputStream;
	
	private int filesAdded;
	
	/*
	
	public Archive(ArchiveFormat type, String output_cwd, String output_filename, String suffix) {
		super(output_cwd, output_filename + (suffix.startsWith(".")?"":".")+suffix);
		
		this.TYPE = type;
		this.filesAdded = 0;
	}*/
	
	public Archive(ArchiveFormat type, String outputFileAddress) {
		this(type, outputFileAddress, type.getSuffix());
	}
	
	public Archive(ArchiveFormat type, String outputFileAddress, String suffix) {
		super(outputFileAddress + (suffix.startsWith(".")?"":".")+suffix);
		
		this.TYPE = type;
		this.filesAdded = 0;
	}
	
	
	
	public void openArchive() throws IOException, NotImplementedException{
		this.fileOutputStream = new FileOutputStream(this);

		switch (TYPE) {
			case TAR:
				archiveOutputStream = new TarArchiveOutputStream(fileOutputStream);
				break;
				
			case TAR_GZ:
				throw new NotImplementedException("Unsupported archive type");
				
			case ZIP:
				archiveOutputStream = new ZipOutputStream(fileOutputStream);
				break;
				
			case GZIP:
				archiveOutputStream = new GZIPOutputStream(fileOutputStream);
				break;
				
				
			default:
				throw new NotImplementedException("Unsupported archive type");
		}
	}
	
	
	
	public void addFile(String file) throws NotOpenException, IOException, IncorrectUsageException{
		addFile(new File(file));
	}
	
	public void addFile(File file) throws NotOpenException, IOException, IncorrectUsageException{
		
		if (archiveOutputStream == null){
			throw new NotOpenException("Cannot add entry to archive that is not open");
		}
		
//		FileInputStream fileInputStream = new FileInputStream(file);
		
		switch (TYPE) {
			case TAR:
				
				addTarEntry((TarArchiveOutputStream)archiveOutputStream, file);
				
				break;
				
			case TAR_GZ:
				
				break;
				
			case ZIP:
				
				addZipEntry((ZipOutputStream)archiveOutputStream, file);

				break;
				
			case GZIP:
				
				addGzipEntry((GZIPOutputStream)archiveOutputStream, file);
				
				break;
				
				
			default:
				
				break;
		}
		
		filesAdded++;
		
	}
	
	
	public void closeArchive() throws IOException{
		archiveOutputStream.close();
		archiveOutputStream = null;
//		this.finalize();
	}
	
	
	
	
	
	
	
	
	private void addTarEntry (TarArchiveOutputStream tarArchiveOutputStream, File file) throws IOException{
		FileInputStream fileInputStream = new FileInputStream(file);
		
		TarArchiveEntry tarArchiveEntry = new TarArchiveEntry(file);
		tarArchiveOutputStream.putArchiveEntry(tarArchiveEntry);
		try {
			writeFileToArchive(fileInputStream, tarArchiveOutputStream);
		} finally {
			fileInputStream.close();
			tarArchiveOutputStream.closeArchiveEntry();
		}
	}
	
	
	private void addZipEntry (ZipOutputStream tarArchiveOutputStream, File file) throws IOException{
		FileInputStream fileInputStream = new FileInputStream(file);
		
		ZipEntry zipEntry = new ZipEntry(file.getName());
		ZipOutputStream zipOutputStream = (ZipOutputStream) archiveOutputStream;
		zipOutputStream.putNextEntry(zipEntry);
		try {
			writeFileToArchive(fileInputStream, zipOutputStream);
		} finally {
			fileInputStream.close();
			zipOutputStream.closeEntry();
		}
	}
	
	
	private void addGzipEntry (GZIPOutputStream tarArchiveOutputStream, File file) throws IOException, IncorrectUsageException{
		
		if (getFilesAdded() > 0){
			throw new IncorrectUsageException("GZip archives can have only one root element. "
					+ "Current archive already has "+getFilesAdded()
					+". Cannot add another one");
		}
		
		FileInputStream fileInputStream = new FileInputStream(file);
		
		GZIPOutputStream gzipOutputStream = (GZIPOutputStream) archiveOutputStream;
		
		try {
			writeFileToArchive(fileInputStream, gzipOutputStream);
		} finally {
			gzipOutputStream.finish();
			fileInputStream.close();
			//gzipOutputStream.close();
		}
	}
	
	
	
	
	
	
	private void writeFileToArchive(FileInputStream fileInputStream, OutputStream archiveEntryOutputStream) throws IOException{
		int len = 0;
		
		try {
			while ((len = fileInputStream.read(buffer)) > 0 ) {
				archiveEntryOutputStream.write(buffer, 0, len);
			}
		} finally {
			fileInputStream.close();
		}
	}
	
	
	
	public int getFilesAdded(){
		return this.filesAdded;
	}
	
	
	
}
