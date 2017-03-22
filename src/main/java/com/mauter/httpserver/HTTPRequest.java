package com.mauter.httpserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This HTTP request object holds the data that is read in
 * from the client before processing and generating a response. 
 */
public class HTTPRequest implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger( HTTPServer.class );
	static final Charset UTF8 = Charset.forName( "UTF-8" );

	String method;
	String path;
	byte[] body;
	Map<String, String> headers;

	/**
	 * Gets the HTTP method of this request.
	 * 
	 * @return the HTTP method
	 */
	public String getMethod() {
		return method;
	}

	/**
	 * Sets the HTTP method of this request.
	 * 
	 * @param method the HTTP method
	 */
	public void setMethod( String method ) {
		this.method = method;
	}

	/**
	 * Gets the path of the resource for this HTTP request.
	 * 
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Sets the path of the resource for this HTTP request.
	 * 
	 * @param path the path to set
	 */
	public void setPath( String path ) {
		this.path = path;
	}

	/**
	 * Gets the body as a series of bytes.  Fancier HTTP servers
	 * would use an InputStream here, but to keep things simple
	 * we're going to stick with a byte array.
	 * 
	 * @return the body
	 */
	public byte[] getBody() {
		return body;
	}
	
	/**
	 * Gets the body as a String.
	 * 
	 * @return the body as a String
	 */
	public String getBodyAsString() {
		return this.body == null ? null : new String( this.body, UTF8 );
	}
	
	/**
	 * Sets the body from a series of bytes.
	 * 
	 * @param body the byte array to set as the body
	 */
	public void setBody( byte[] body ) { 
		this.body = body;
		if ( this.body != null ) setHeader( "Content-length", String.valueOf( body.length ) );
	}
	
	/**
	 * Sets the body from a String.  This method converts the String
	 * into a byte array and calls {@linkplain #setBody(byte[])}.
	 * 
	 * @param body the String to set as the body
	 */
	public void setBody( String body ) {
		setBody( body == null ? null : body.getBytes( UTF8 ) );
	}

	/**
	 * Gets a header with the given name or null if it does not exist.
	 * 
	 * @param header the name of the header
	 * @return the header value
	 */
	public String getHeader( String header ) {
		if ( this.headers == null ) return null;
		return this.headers.get( header.toLowerCase() );
	}

	/**
	 * Sets a header with the given name and value overwriting any
	 * previously set value having the same name regardless of case.
	 * 
	 * @param header the name of the header to set
	 * @param value the value of the header to set
	 */
	public void setHeader( String header, String value ) {
		if ( this.headers == null ) this.headers = new HashMap<>();
		this.headers.put( header.toLowerCase(), value );
	}

	/**
	 * Gets the headers as an unmodifiable Map.
	 * 
	 * @return the Map of headers
	 */
	public Map<String, String> getHeaders() {
		if ( this.headers == null ) return null;
		return Collections.unmodifiableMap( this.headers );
	}
	
	/**
	 * Reads the given InputStream into this request.
	 * 
	 * @param is the InputStream to read
	 * @throws IOException If an I/O error occurs
	 */
	public void read( InputStream is ) throws IOException {
		BufferedReader reader = new BufferedReader( new InputStreamReader( is, UTF8 ) );

		// read the first line containing method, path and version
		String line = reader.readLine();
		log.debug( "line={}", line );
		if ( line == null ) throw new IOException( "Invalid HTTP request." );
		StringTokenizer st = new StringTokenizer( line, " " );
		if ( st.countTokens() < 3 ) throw new IOException( "Invalid HTTP request." );
		setMethod( st.nextToken() );
		setPath( st.nextToken() );

		// read the headers
		while ( ( line = reader.readLine() ) != null ) {
			log.debug( "line={}", line );

			if ( "".equals( line ) ) break;

			int pos = line.indexOf( ": " );
			if ( pos < 0 ) continue;

			setHeader( line.substring( 0, pos ), line.substring( pos + ": ".length() ) );
		}

		// read the body of the request
		String sContentLength = getHeader( "Content-length" );
		if ( sContentLength != null && !sContentLength.isEmpty() ) {
			int contentLength = Integer.parseInt( sContentLength );

			if ( contentLength > 0 ) {
				byte[] body = new byte[ contentLength ];
				is.read( body, 0, contentLength );
				setBody( body );
				log.debug( "body={}", getBody() );
			}
		}
	}
}
