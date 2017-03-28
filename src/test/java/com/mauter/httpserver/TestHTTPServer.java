package com.mauter.httpserver;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Assert;
import org.junit.Test;

public class TestHTTPServer {
	
	InputStream buildRequest( Map<String, String> headers, byte[] body ) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		baos.write( "GET / HTTP/1.0\n".getBytes( StandardCharsets.UTF_8 ) );
		
		if ( body != null ) {
			if ( headers == null ) {
				headers = new HashMap<>( 1 );
			}
			headers.put( "Content-Length", String.valueOf( body.length ) );
		}
		
		if ( headers != null ) {
			for ( Entry<String, String> entry : headers.entrySet() ) {
				baos.write( ( entry.getKey() + ": " + entry.getValue() + "\n" ).getBytes( StandardCharsets.UTF_8 ) );
			}
		}
		
		if ( body != null ) {
			baos.write( "\n".getBytes( StandardCharsets.UTF_8 ) );
			baos.write( body );
		}
		
		return new ByteArrayInputStream( baos.toByteArray() );
	}
	
	@Test
	public void testGetPortUnset() {
		try ( HTTPServer server = new HTTPServer() ) {
			Assert.assertEquals( 0, server.getPort() ); 
		}
	}
	
	@Test
	public void testGetPortSet() {
		try ( HTTPServer server = new HTTPServer() ) {
			server.setPort( 1234 );
			Assert.assertEquals( 1234, server.getPort() ); 
		}
	}
	
	@Test
	public void testGetPortUnsetThenStart() throws IOException {
		try ( HTTPServer server = HTTPServer.always200OK() ) {
			Assert.assertFalse( server.getPort() == 0 ); 
		}
	}
	
	@Test
	public void testGetPortSetThenStart() throws IOException {
		try ( HTTPServer server = new HTTPServer() ) {
			server.setPort( 1234 );
			server.start();
			Assert.assertEquals( 1234, server.getPort() ); 
		}
	}
	
	@Test
	public void testGetRequestsUnset() {
		try ( HTTPServer server = new HTTPServer() ) {
			Assert.assertNotNull( server.getRequests() ); 
			Assert.assertTrue( server.getRequests().isEmpty() ); 
		}
	}
	
	@Test
	public void testGetRequests() throws IOException {
		try ( HTTPServer server = HTTPServer.always200OK() ) {
			
			URL url = new URL( "http://localhost:" + server.getPort() );
			HttpURLConnection con = (HttpURLConnection)url.openConnection();
			con.addRequestProperty( "1234", "5678" );
			con.connect();
			con.getResponseCode();
			con.disconnect();
			
			List<HTTPRequest> requests = server.getRequests();
			
			Assert.assertNotNull( requests ); 
			Assert.assertEquals( 1, requests.size() );
			
			HTTPRequest request = requests.get( 0 );
			Assert.assertEquals( "5678", request.getHeader( "1234" ) );
		}
	}
	
	@Test
	public void testGetResponsesUnset() {
		try ( HTTPServer server = new HTTPServer() ) {
			Assert.assertNotNull( server.getResponses() ); 
			Assert.assertTrue( server.getResponses().isEmpty() ); 
		}
	}
	
	@Test
	public void testGetResponses() throws IOException {
		try ( HTTPServer server = HTTPServer.always200OK() ) {
			
			URL url = new URL( "http://localhost:" + server.getPort() );
			HttpURLConnection con = (HttpURLConnection)url.openConnection();
			con.connect();
			con.getResponseCode();
			con.disconnect();
			
			List<HTTPResponse> responses = server.getResponses();
			
			Assert.assertNotNull( responses ); 
			Assert.assertEquals( 1, responses.size() );
			
			HTTPResponse response = responses.get( 0 );
			Assert.assertEquals( 200, response.getStatus() );
		}
	}
	
	@Test
	public void testGetHTTPRequestHandlerUnset() {
		try ( HTTPServer server = new HTTPServer() ) {
			Assert.assertNull( server.getHTTPRequestHandler() );
		}
	}

	@Test
	public void testGetHTTPRequestHandlerNull() {
		try ( HTTPServer server = new HTTPServer() ) {
			server.handler = null;
			Assert.assertNull( server.getHTTPRequestHandler() );
		}
	}
	
	@Test
	public void testGetHTTPRequestHandler() {
		HTTPRequestHandler handler = new HTTPRequestHandler() {
			@Override public void handleRequest( HTTPRequest request, HTTPResponse response ) throws IOException {}
		};
		
		try ( HTTPServer server = new HTTPServer() ) {
			server.handler = handler;
			Assert.assertEquals( handler, server.getHTTPRequestHandler() );
		}
	}
	
	@Test
	public void testSetHTTPRequestHandlerNull() {
		try ( HTTPServer server = new HTTPServer() ) {
			server.setHTTPRequestHandler( null );
			Assert.assertNull( server.handler );
		}
	}

	@Test
	public void testSetHTTPRequestHandler() {
		HTTPRequestHandler handler = new HTTPRequestHandler() {
			@Override public void handleRequest( HTTPRequest request, HTTPResponse response ) throws IOException {}
		};
		
		try ( HTTPServer server = new HTTPServer() ) {
			server.setHTTPRequestHandler( handler );
			Assert.assertEquals( handler, server.handler );
		}
	}
	
	@Test(expected=NullPointerException.class)
	public void testReadLineNull() throws IOException {
		HTTPServer.readLine( null );
	}
	
	@Test
	public void testReadLineEmpty() throws IOException {
		Assert.assertEquals( "", testReadLine( "" ) );
	}
	
	@Test
	public void testReadLineOneLine() throws IOException {
		Assert.assertEquals( "one line", testReadLine( "one line" ) );
	}
	
	@Test
	public void testReadLineTwoLines() throws IOException {
		String test = "first line\nsecond line";
		ByteArrayInputStream bais = new ByteArrayInputStream( test.getBytes( StandardCharsets.UTF_8 ) );
		BufferedInputStream bis = new BufferedInputStream( bais );
		Assert.assertEquals( "first line", HTTPServer.readLine( bis ) );
		Assert.assertEquals( "second line", HTTPServer.readLine( bis ) );
	}

	@Test
	public void testReadLine() throws IOException {
		String test = "first line\nsecond line\n\nfourth line";
		ByteArrayInputStream bais = new ByteArrayInputStream( test.getBytes( StandardCharsets.UTF_8 ) );
		BufferedInputStream bis = new BufferedInputStream( bais );
		Assert.assertEquals( "first line", HTTPServer.readLine( bis ) );
		Assert.assertEquals( "second line", HTTPServer.readLine( bis ) );
		Assert.assertEquals( "", HTTPServer.readLine( bis ) );
		Assert.assertEquals( "fourth line", HTTPServer.readLine( bis ) );
	}
	
	@Test
	public void testReadLineTwoLinesCRLF() throws IOException {
		String test = "first line\r\nsecond line";
		ByteArrayInputStream bais = new ByteArrayInputStream( test.getBytes( StandardCharsets.UTF_8 ) );
		BufferedInputStream bis = new BufferedInputStream( bais );
		Assert.assertEquals( "first line", HTTPServer.readLine( bis ) );
		Assert.assertEquals( "second line", HTTPServer.readLine( bis ) );
	}
	
	@Test
	public void testReadLineCRLF() throws IOException {
		String test = "first line\r\nsecond line\r\n\r\nfourth line";
		ByteArrayInputStream bais = new ByteArrayInputStream( test.getBytes( StandardCharsets.UTF_8 ) );
		BufferedInputStream bis = new BufferedInputStream( bais );
		Assert.assertEquals( "first line", HTTPServer.readLine( bis ) );
		Assert.assertEquals( "second line", HTTPServer.readLine( bis ) );
		Assert.assertEquals( "", HTTPServer.readLine( bis ) );
		Assert.assertEquals( "fourth line", HTTPServer.readLine( bis ) );
	}
	
	String testReadLine( String test ) throws IOException {
		return HTTPServer.readLine( new BufferedInputStream(
				new ByteArrayInputStream( test == null ? null : test.getBytes( StandardCharsets.UTF_8 ) ) ) );
	}

	@Test(expected=IOException.class)
	public void testReadNull() throws IOException, HTTPException {
		HTTPServer.read( null, null );
	}
	
	@Test(expected=IOException.class)
	public void testReadNullInputStream() throws IOException, HTTPException {
		HTTPServer.read( null, new HTTPRequest() );
	}
	
	@Test(expected=NullPointerException.class)
	public void testReadNullRequest() throws IOException, HTTPException {
		HTTPServer.read( buildRequest( null, null ), null );
	}
	
	@Test(expected=HTTPException.class)
	public void testReadEmptyInputStream() throws IOException, HTTPException {
		HTTPServer.read( new ByteArrayInputStream( "".getBytes( "UTF-8" ) ), new HTTPRequest() );
	}
	
	@Test(expected=HTTPException.class)
	public void testReadBadInputStream() throws IOException, HTTPException {
		HTTPServer.read( new ByteArrayInputStream( "bad".getBytes( "UTF-8" ) ), new HTTPRequest() );
	}
	
	@Test
	public void testReadInputStreamOnlyInitialLine() throws IOException, HTTPException {
		HTTPRequest request = new HTTPRequest();
		HTTPServer.read( buildRequest( null, null ), request );
		Assert.assertEquals( "GET", request.getMethod() );
		Assert.assertEquals( "/", request.getPath() );
		Assert.assertEquals( "HTTP/1.0", request.getVersion() );
	}
	
	@Test
	public void testReadInputStreamWithHeaders() throws IOException, HTTPException {
		Map<String, String> headers = new HashMap<>();
		headers.put( "Host", "localhost:8080" );
		headers.put( "From", "brianmauter@gmail.com" );
		headers.put( "X-blahblahblah", "more blah" );
		
		HTTPRequest request = new HTTPRequest();
		HTTPServer.read( buildRequest( headers, null ), request );
		Assert.assertEquals( "GET", request.getMethod() );
		Assert.assertEquals( "/", request.getPath() );
		Assert.assertEquals( "HTTP/1.0", request.getVersion() );
		Assert.assertEquals( headers.size(), request.getHeaders().size() );
		Assert.assertEquals( "localhost:8080", request.getHeader( "Host" ) );
		Assert.assertEquals( "brianmauter@gmail.com", request.getHeader( "From" ) );
		Assert.assertEquals( "more blah", request.getHeader( "X-blahblahblah" ) );
	}
	
	@Test
	public void testReadInputStreamWithHeadersBody() throws IOException, HTTPException {
		Map<String, String> headers = new HashMap<>();
		headers.put( "Host", "localhost:8080" );
		headers.put( "From", "brianmauter@gmail.com" );
		headers.put( "X-blahblahblah", "more blah" );
		headers.put( "Content-Length", "1" );
		
		byte[] body = "Squirrel!".getBytes( StandardCharsets.UTF_8 );
		
		HTTPRequest request = new HTTPRequest();
		HTTPServer.read( buildRequest( headers, body ), request );
		Assert.assertEquals( "GET", request.getMethod() );
		Assert.assertEquals( "/", request.getPath() );
		Assert.assertEquals( "HTTP/1.0", request.getVersion() );
		Assert.assertEquals( headers.size(), request.getHeaders().size() );
		Assert.assertEquals( "localhost:8080", request.getHeader( "Host" ) );
		Assert.assertEquals( "brianmauter@gmail.com", request.getHeader( "From" ) );
		Assert.assertEquals( "more blah", request.getHeader( "X-blahblahblah" ) );
		Assert.assertEquals( String.valueOf( body.length ), request.getHeader( "Content-Length" ) );
		Assert.assertArrayEquals( body, request.getBody() );
		Assert.assertEquals( new String( body, StandardCharsets.UTF_8 ), request.getBodyAsString() );
	}
	
}
