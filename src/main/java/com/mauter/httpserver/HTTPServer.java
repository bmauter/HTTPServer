package com.mauter.httpserver;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This simple HTTP server is intended to be embedded in unit tests.  
 * It sports very few dependencies so your classpath isn't cluttered
 * just so you can exercise a few HTTP calls.  Start the server with
 * a try-with-resources statement.  Before you exit the try, inspect
 * the requests and responses to verify that your code works. 
 */
public class HTTPServer implements Runnable, Closeable {

	private static final Logger log = LoggerFactory.getLogger( HTTPServer.class );

	Thread thread;
	ServerSocket serverSocket;
	boolean isRunning = true;

	int port = 0;
	List<HTTPRequest> requests = new ArrayList<>();
	List<HTTPResponse> responses = new ArrayList<>();
	HTTPRequestHandler handler;

	/**
	 * Gets the port number bound by the listening socket.  The default
	 * is 0 so that the socket will find an open port automatically.  After
	 * the socket acquires the port, this value will be updated with the
	 * actual port number used.
	 * 
	 * @return the port number
	 */
	public int getPort() { return this.port; }
	
	/**
	 * Sets the port number to be bound by the listening socket.  Passing
	 * in 0 here causes the socket to find an open port automatically.
	 * 
	 * @param port the port number to use
	 */
	public void setPort( int port ) { this.port = port; }

	/**
	 * Gets the list of requests that have been made since startup
	 * or the last call to {@linkplain #reset()}.
	 * 
	 * @return the List of HTTPRequest objects
	 */
	public List<HTTPRequest> getRequests() { return this.requests; }
	
	/**
	 * Gets the list of responses that have been returned since startup
	 * or the last call to {@linkplain #reset()}.
	 * 
	 * @return the List of HTTPResponse objects
	 */
	public List<HTTPResponse> getResponses() { return this.responses; }

	/**
	 * Gets the request handler used by the server to handle requests.
	 * 
	 * @return the HTTPRequestHandler that handles all requests to this server
	 */
	public HTTPRequestHandler getHTTPRequestHandler() { return this.handler; }
	
	/**
	 * Sets the request handler used by the server to handle requests.
	 * 
	 * @param handler the HTTPRequestHandler used to handle all requests to this server
	 */
	public void setHTTPRequestHandler( HTTPRequestHandler handler ) { this.handler = handler; }

	/**
	 * Creates and starts a server that always returns a 200 OK response no
	 * matter the request.
	 * 
	 * @return a new HTTPServer that's already started and listening for requests
	 * @throws IOException if an I/O error occurs when opening the socket
	 */
	public static HTTPServer always200OK() throws IOException {
		HTTPServer server = new HTTPServer();
		server.setHTTPRequestHandler( new HTTPRequestHandler() {
			@Override public void handleRequest( HTTPRequest request, HTTPResponse response ) throws IOException {
				response.setStatus( 200 );
				response.setStatusMessage( "OK" );
			}
		} );
		server.start();
		return server;
	}

	/**
	 * Starts the server if not already running.  During this call
	 * the socket is created and bound to the port.  If the default
	 * port of 0 is used, the socket will find an open port, bind
	 * to it and report it back to the server's port variable.
	 * 
	 * @throws IOException if an I/O error occurs when opening the socket
	 */
	public void start() throws IOException {
		if ( isRunning ) return;
		
		isRunning = true;
		reset();

		serverSocket = new ServerSocket( this.port );
		this.port = serverSocket.getLocalPort();
		log.info( "bound to port {}", this.port );

		thread = new Thread( this, "HTTPServerThread" );
		thread.start();
	}

	/**
	 * Clears the stored requests and responses.
	 */
	public void reset() { 
		requests.clear();
		responses.clear();
	}

	/**
	 * Stops the server and closes the socket.
	 */
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

	/**
	 * Stops the server and closes the socket.
	 * @see #stop()
	 */
	public void close() {
		stop();
	}

	/**
	 * Listens for client connections, reads input and writes output.
	 * This method is what calls {@linkplain HTTPRequestHandler#handleRequest(HTTPRequest, HTTPResponse)}.
	 */
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
				this.handler.handleRequest( request, response );
				response.write( os );
			}
			catch ( IOException ioe ) {
				// only log the exception if we're running.  closing the serverSocket
				// always throws an exception, so ignore that.
				if ( isRunning ) log.error( "Unable to process request.", ioe );
			}
		}
	}
}
