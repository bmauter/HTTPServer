package com.mauter.httpserver;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * This class detects a file's type based on its magic numbers.  This is not nearly
 * as comprehensive as UNIX's file utility which is based on libmagic.  This class
 * is just trying to help the {@linkplain SimpleFileServer} return decent mime types.
 * 
 * The name of the FileType could be used as the file extension if needed.
 */
public enum FileType {
	PNG( "image/png", 0, new byte[] { (byte)0x89, 'P', 'N', 'G', '\r', '\n', 0x1A, '\n' } ),
	JPG( "image/jpg", 0, new byte[] { (byte)0xFF, (byte)0xD8 } ),
	GIF( "image/gif", 0, "GIF89a".getBytes( StandardCharsets.UTF_8 ) ),
	gif( "image/gif", 0, "GIF87a".getBytes( StandardCharsets.UTF_8 ) ),
	PDF( "application/pdf", 0, "%PDF".getBytes( StandardCharsets.UTF_8 ) ),
	tiff( "image/tiff", 0, new byte[] { (byte)0x49, (byte)0x49, (byte)0x2A, (byte)0x00 } ),
	TIFF( "image/tiff", 0, new byte[] { (byte)0x4D, (byte)0x4D, (byte)0x00, (byte)0x2A } ),
	ZIP( "application/zip", 0, "PK".getBytes( StandardCharsets.UTF_8 ) ),
	HTML( "text/html", 0, null ),
	XML( "application/xml", 0, null ),
	CSS( "text/css", 0, null ),
	JS( "application/x-javascript", 0, null ),
	TXT( "text/plain", 0, null ),
	UNKNOWN ( "application/octet-stream", 0, null ),
	;
	
	String mimeType;
	int skip;
	byte[] magic;
	
	FileType( String mimeType, int skip, byte[] magic ) {
		this.mimeType = mimeType;
		this.skip = skip;
		this.magic = magic;
	}
	
	/**
	 * Detects the file type from the given bytes.  The entire file does not
	 * have to be loaded in the byte array, but if a file type skips the first
	 * 100 bytes and checks the next eight and you only passed this method 
	 * the first 50, then it will not be able to determine the correct file type.
	 * 
	 * @param file the byte array representing the file
	 * @return the detected FileType; UNKNOWN if some binary file not in our list
	 * and TXT if some text file not in our list
	 */
	public static FileType detect( byte[] file ) {
		for ( FileType ft : values() ) {
			if ( ft.magic == null ) continue;
			if ( file.length < ft.magic.length ) continue;
			
			boolean found = true;
			for ( int i = 0; i < ft.magic.length; i++ ) {
				if ( ft.magic[ i ] != file[ i + ft.skip ] ) {
					found = false;
					break;
				}
			}
			
			if ( found ) return ft;
		}
		
		byte[] head = Arrays.copyOf( file, Math.min( 100, file.length ) );
		
		// look for the presence of control characters except for TAB, NL, CR
		for ( byte b : head ) if ( b < 31 && b != 9 && b != 10 && b != 13 ) return UNKNOWN;
		
		// if we're here, we should have plain text of some form
		String shead = new String( head ).toLowerCase().trim();
		if ( shead.contains( "<html" ) ) return HTML;
		if ( shead.contains( "<?xml " ) ) return XML;
		if ( shead.contains( "use strict" ) ) return JS;
		if ( shead.contains( "body {" ) || shead.contains( "html {" ) 
			|| shead.contains( "html,body {" ) || shead.contains( "html, body {" ) ) return CSS;
		return TXT;
	}
}
