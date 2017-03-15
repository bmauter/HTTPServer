package com.mauter;

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

public class HTTPRequest implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger( HTTPServer.class );
	static final Charset UTF8 = Charset.forName( "UTF-8" );

	String method;
	String path;
	String body;
	Map<String, String> headers;

	public void read( InputStream is ) throws IOException {
		BufferedReader reader = new BufferedReader( new InputStreamReader( is, UTF8 ) );

		// read the first line containing method, path and version
		String line = reader.readLine();
		log.debug( "line={}", line );
		if ( line == null ) throw new IOException( "Invalid HTTP request." );
		StringTokenizer st = new StringTokenizer( line, " " );
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
				char[] body = new char[ contentLength ];
				reader.read( body, 0, contentLength );
				setBody( new String( body ) );
				log.debug( "body={}", getBody() );
			}
		}
	}

	public String getMethod() { return method; }
	public void setMethod( String method ) { this.method = method; }

	public String getPath() { return path; }
	public void setPath( String path ) { this.path = path; }

	public String getBody() { return body; }
	public void setBody( String body ) { this.body = body; }

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
}
