package com.mauter.httpserver;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class SimpleFileServer implements HTTPRequestHandler {
	
	File root;
	
	public SimpleFileServer( File root ) throws IOException {
		if ( root == null ) throw new NullPointerException( "Root directory cannot be null." );
		if ( !root.exists() ) throw new IOException( "Root directory does not exist." );
		if ( !root.isDirectory() ) throw new IOException( "Root must be a directory." );
		
		this.root = root;
	}
	
	@Override
	public void handleRequest( HTTPRequest request, HTTPResponse response ) throws IOException {
		String path = request.getPath();
		if ( "/".equals( path ) ) path = "index.html";
		
		response.setHeader( "Server", getClass().getSimpleName()
				+ "/" + getClass().getPackage().getImplementationVersion() );
		
		File file = new File( root, path );
		if ( !file.exists() ) {
			response.setStatus( 404 );
			response.setStatusMessage( "Not Found" );
			return;
		}
		
		try ( FileInputStream fis = new FileInputStream( file ) ) {
			ByteArrayOutputStream body = new ByteArrayOutputStream();
			byte[] buffer = new byte[ 1024 ];
			int c;
			while( ( c = fis.read( buffer, 0, buffer.length ) ) > 0 ) {
				body.write( buffer, 0, c );
			}
			response.setStatus( 200 );
			response.setStatusMessage( "OK" );
			response.setHeader( "Content-Type", "application/octet-stream" );
			response.setBody( body.toByteArray() );
		}
	}
}
