package com.mauter.httpserver;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This HTTP response object holds the data that is written
 * out to the client after processing the incoming request. 
 */
public class HTTPResponse implements Serializable {
	private static final long serialVersionUID = 1L;
	
	int status;
	String statusMessage;
	byte[] body;
	Map<String, String> headers;

	/**
	 * Gets the HTTP status to be returned to the caller.
	 * 
	 * @return the HTTP status
	 */
	public int getStatus() {
		return status;
	}
	
	/**
	 * Sets the HTTP status to be returned to the caller.
	 * 
	 * @param status the HTTP status
	 */
	public void setStatus( int status ) {
		this.status = status;
		
		if ( 200 == status ) this.statusMessage = "OK";
		else if ( 400 == status ) this.statusMessage = "Bad Request";
		else if ( 404 == status ) this.statusMessage = "Not Found";
		else if ( 500 == status ) this.statusMessage = "Server Error";
		else this.statusMessage = status + " Message";
	}
	
	/**
	 * Gets the HTTP status message to be returned to the caller.
	 * For example, an HTTP 200 will have a status message of "OK".
	 * 
	 * @return the HTTP status message
	 */
	public String getStatusMessage() {
		return statusMessage;
	}
	
	/**
	 * Sets the HTTP status message to be returned to the caller.
	 * For example, if your status code is 200, then an appropriate
	 * status message would be "OK".
	 * 
	 * @param statusMessage the HTTP status message
	 */
	public void setStatusMessage( String statusMessage ) {
		this.statusMessage = statusMessage;
	}

	/**
	 * Gets the body as a series of bytes.  Fancier HTTP servers
	 * would use an OutputStream here, but to keep things simple
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
		return this.body == null ? null : new String( this.body, StandardCharsets.UTF_8 );
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
		setBody( body == null ? null : body.getBytes( StandardCharsets.UTF_8 ) );
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
	 * Changes this response object into a standard response.  For
	 * example if the caller requests a resource that does not exist
	 * and you wish to return a 404, call this method on your response
	 * passing in the status code and message.
	 * 
	 * @param status the status code
	 * @param statusMessage the status message
	 */
	public void buildStandardResponse( int status ) {
		headers = null;
		setStatus( status );
		setHeader( "Content-Type", FileType.HTML.mimeType );
		
		StringBuilder body = new StringBuilder( 100 );
		body.append( "<html><body><h1>" );
		body.append( status ).append( " - " ).append( statusMessage );
		body.append( "</h1></body></html>" );
		
		setBody( body.toString() );
	}
}
