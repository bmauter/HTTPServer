package com.mauter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class HTTPResponse implements Serializable {
	private static final long serialVersionUID = 1L;
	static final Charset UTF8 = Charset.forName( "UTF-8" );
	
	int status = 200;
	String statusMessage = "OK";
	String body;
	Map<String, String> headers;

	public int getStatus() { return status; }
	public void setStatus( int status ) { this.status = status; }

	public String getStatusMessage() { return statusMessage; }
	public void setStatusMessage( String statusMessage ) { this.statusMessage = statusMessage; }

	public String getBody() { return body; }
	public void setBody( String body ) { 
		this.body = body;
		if ( this.body != null ) setHeader( "Content-length", String.valueOf( body.length() ) );
	}

	public String getHeader( String header ) {
		if ( this.headers == null ) return null;
		return this.headers.get( header.toLowerCase() );
	}

	public void setHeader( String header, String value ) {
		if ( this.headers == null ) this.headers = new HashMap<>();
		this.headers.put( header.toLowerCase(), value );
	}

	public Map<String, String> getHeaders() {
		if ( this.headers == null ) return null;
		return Collections.unmodifiableMap( this.headers );
	}

	public void write( OutputStream os ) throws IOException {
		Writer writer = new OutputStreamWriter( os, UTF8 );
		writer.write( "HTTP/1.0 " );
		writer.write( status );
		writer.write( " " );
		writer.write( statusMessage );
		writer.write( "\n" );
		if ( headers != null ) {
			for ( Entry<String, String> header : headers.entrySet() ) {
				writer.write( header.getKey() );
				writer.write( ": " );
				writer.write( header.getValue() );
				writer.write( "\n" );
			}
		}
		if ( body != null ) {
			writer.write( "\n" );
			writer.write( body );
		}
		writer.flush();
	}
}
