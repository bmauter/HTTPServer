package com.mauter;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HTTPServer implements Runnable, Closeable {
	
	private static final Logger log = LoggerFactory.getLogger( HTTPServer.class );
	
	static final Charset UTF8 = Charset.forName( "UTF-8" );
	
	Thread thread;
	ServerSocket serverSocket;
	boolean isRunning = true;
	HTTPRequestHandler handler;
	List<HTTPRequest> requests = new ArrayList<>();
	List<HTTPResponse> responses = new ArrayList<>();
	
	public int getPort() { return serverSocket.getLocalPort(); }
	
	public List<HTTPRequest> getRequests() { return this.requests; }
	public List<HTTPResponse> getResponses() { return this.responses; }
	
	public HTTPRequestHandler getHTTPRequestHandler() {
		return this.handler;
	}
	
	public void setHTTPRequestHandler( HTTPRequestHandler handler ) {
		this.handler = handler;
	}
	
	public static HTTPServer always200OK() throws IOException {
		HTTPServer server = new HTTPServer();
		server.start();
		return server;
	}
	
	public void start() throws IOException {
		isRunning = true;
		reset();
		
		serverSocket = new ServerSocket( 0 );
		log.info( "bound to port {}", getPort() );
		
		thread = new Thread( this, "HTTPServerThread" );
		thread.start();
	}
	
	public void reset() { 
		requests.clear();
		responses.clear();
	}
	
	public void stop() {
		if ( !isRunning ) return;
		isRunning = false;
		
		try {
			serverSocket.close();
			serverSocket = null;
		}
		catch ( IOException ioe ) {
			log.error( "Unable to close server socket.", ioe );
		}
		
		try {
			thread.join( 1000 );
			thread = null;
		}
		catch ( InterruptedException ie ) {
			log.error( "Interrupted when waiting for server to stop.", ie );
		}
	}
	
	public void close() {
		stop();
	}
	
	@Override
	public void run() {
		while ( isRunning ) {
			try ( Socket socket = serverSocket.accept();
					InputStream is = socket.getInputStream();
					OutputStream os = socket.getOutputStream() ) {
				log.debug( "socket={}, is={}, os={}", socket, is, os );
				
				HTTPRequest request = new HTTPRequest();
				this.requests.add( request );
				
				HTTPResponse response = new HTTPResponse();
				this.responses.add( response );
				
				request.read( is );
				if ( this.handler != null ) this.handler.handleRequest( request, response );
				response.write( os );
			}
			catch ( IOException ioe ) {
				// only log the exception if we're running.  closing the serverSocket
				// always throws an exception, so ignore that.
				if ( isRunning ) log.error( "Unable to process request.", ioe );
			}
		}
	}
	
	public class HTTPRequest implements Serializable {
		private static final long serialVersionUID = 1L;
		
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
	
	public class HTTPResponse implements Serializable {
		private static final long serialVersionUID = 1L;
		
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
	
	/**
	 * Handles HTTP requests.  Implement this class and pass it to {@link #setHTTPRequestHandler(HTTPRequestHandler)}
	 * so that you can control what happens based on the request.  The default implementation simply returns
	 * a 200 OK to the caller.
	 */
	public interface HTTPRequestHandler {
		public void handleRequest( HTTPRequest request, HTTPResponse response ) throws IOException;
	}
}
