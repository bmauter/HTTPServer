package com.mauter;

import java.io.IOException;

/**
 * Handles HTTP requests.  Implement this class and pass it to {@link #setHTTPRequestHandler(HTTPRequestHandler)}
 * so that you can control what happens based on the request.  The default implementation simply returns
 * a 200 OK to the caller.
 */
public interface HTTPRequestHandler {
	public void handleRequest( HTTPRequest request, HTTPResponse response ) throws IOException;
}
