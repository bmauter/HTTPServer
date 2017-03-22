package com.mauter.httpserver;

import java.io.IOException;

/**
 * Handles HTTP requests.  Implement this class and pass it to
 * {@link #setHTTPRequestHandler(HTTPRequestHandler)} so that
 * you can control what happens based on the request.
 */
public interface HTTPRequestHandler {
	
	/**
	 * Called when the HTTP server wants to process the request
	 * and generate a response.  Implementors are responsible
	 * for setting the HTTP status and status message at the
	 * bare minimum to make proper HTTP clients return properly.
	 * 
	 * @param request the HTTPRequest read from the socket's InputStream
	 * @param response the HTTPResponse that will be written to the
	 * socket's OutputStream
	 * @throws IOException if an I/O error occurs
	 */
	public void handleRequest( HTTPRequest request, HTTPResponse response ) throws IOException;
}
