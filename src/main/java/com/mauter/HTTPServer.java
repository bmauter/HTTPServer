package com.mauter;

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

public class HTTPServer implements Runnable, Closeable {

	private static final Logger log = LoggerFactory.getLogger( HTTPServer.class );

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
}
