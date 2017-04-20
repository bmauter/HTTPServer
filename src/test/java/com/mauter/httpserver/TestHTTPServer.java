package com.mauter.httpserver;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
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
			@Override public void handleRequest( HTTPRequest request, HTTPResponse response ) {}
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
			@Override public void handleRequest( HTTPRequest request, HTTPResponse response ) {}
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
	
	@Test
	public void testReadInputStreamWithMultilineHeader() throws IOException, HTTPException {
		StringBuilder test = new StringBuilder();
		test.append( "GET / HTTP/1.0\n" );
		test.append( "Colors: Red,\n" );
		test.append( " Blue,       \n" );
		test.append( "      Yellow\n" );
		ByteArrayInputStream input = new ByteArrayInputStream( test.toString().getBytes( StandardCharsets.UTF_8  ) );
		
		HTTPRequest request = new HTTPRequest();
		HTTPServer.read( input, request );
		Assert.assertEquals( "GET", request.getMethod() );
		Assert.assertEquals( "/", request.getPath() );
		Assert.assertEquals( "HTTP/1.0", request.getVersion() );
		Assert.assertEquals( 1, request.getHeaders().size() );
		Assert.assertEquals( "Red, Blue, Yellow", request.getHeader( "Colors" ) );
	}
	
	@Test
	public void testReadInputStreamWithMultilineHeaderCRLF() throws IOException, HTTPException {
		StringBuilder test = new StringBuilder();
		test.append( "GET / HTTP/1.0\r\n" );
		test.append( "Colors: Red,\r\n" );
		test.append( " Blue,       \r\n" );
		test.append( "      Yellow\r\n" );
		ByteArrayInputStream input = new ByteArrayInputStream( test.toString().getBytes( StandardCharsets.UTF_8  ) );
		
		HTTPRequest request = new HTTPRequest();
		HTTPServer.read( input, request );
		Assert.assertEquals( "GET", request.getMethod() );
		Assert.assertEquals( "/", request.getPath() );
		Assert.assertEquals( "HTTP/1.0", request.getVersion() );
		Assert.assertEquals( 1, request.getHeaders().size() );
		Assert.assertEquals( "Red, Blue, Yellow", request.getHeader( "Colors" ) );
	}
	
	@Test(expected=HTTPException.class)
	public void testReadInputStreamWithUnnamedMultilineHeader() throws IOException, HTTPException {
		StringBuilder test = new StringBuilder();
		test.append( "GET / HTTP/1.0\n" );
		test.append( " Red,\n" ); // note:  headers need to have a "Name: Value" syntax
		test.append( "      Yellow\n" );
		ByteArrayInputStream input = new ByteArrayInputStream( test.toString().getBytes( StandardCharsets.UTF_8  ) );
		
		HTTPRequest request = new HTTPRequest();
		HTTPServer.read( input, request );
		Assert.fail();
	}
	
	@Test(expected=HTTPException.class)
	public void testReadInputStreamWithBadMultilineHeader() throws IOException, HTTPException {
		StringBuilder test = new StringBuilder();
		test.append( "GET / HTTP/1.0\n" );
		test.append( "Colors: Red,\n" );
		test.append( "Blue,       \n" ); // note:  multi-line headers must start with a space
		test.append( "      Yellow\n" );
		ByteArrayInputStream input = new ByteArrayInputStream( test.toString().getBytes( StandardCharsets.UTF_8  ) );
		
		HTTPRequest request = new HTTPRequest();
		HTTPServer.read( input, request );
		Assert.fail();
	}
	
	@Test(expected=HTTPException.class)
	public void testReadInputStreamWithBadMultilineHeaderCRLF() throws IOException, HTTPException {
		StringBuilder test = new StringBuilder();
		test.append( "GET / HTTP/1.0\r\n" );
		test.append( "Colors: Red,\r\n" );
		test.append( "Blue,       \r\n" ); // note:  multi-line headers must start with a space
		test.append( "      Yellow\r\n" );
		ByteArrayInputStream input = new ByteArrayInputStream( test.toString().getBytes( StandardCharsets.UTF_8  ) );
		
		HTTPRequest request = new HTTPRequest();
		HTTPServer.read( input, request );
		Assert.fail();
	}
	
	@Test(expected=NullPointerException.class)
	public void testWriteNull() throws IOException {
		HTTPServer.write( null, null );
	}
	
	@Test(expected=NullPointerException.class)
	public void testWriteNullOutputStream() throws IOException {
		HTTPServer.write( null, new HTTPResponse() );
	}
	
	@Test(expected=NullPointerException.class)
	public void testWriteNullResponse() throws IOException {
		HTTPServer.write( new ByteArrayOutputStream(), null );
	}
	
	String testWriteResponse( HTTPResponse response ) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		HTTPServer.write( baos, response );
		return baos.toString( StandardCharsets.UTF_8.name() );
	}
	
	@Test
	public void testWriteBlankResponse() throws IOException {
		HTTPResponse response = new HTTPResponse();
		String result = testWriteResponse( response );
		Assert.assertEquals( "HTTP/1.0 0 null\r\n", result );
	}
	
	@Test
	public void testWriteSimpleResponse() throws IOException {
		HTTPResponse response = new HTTPResponse();
		response.setStatus( 200 );
		String result = testWriteResponse( response );
		Assert.assertEquals( "HTTP/1.0 200 OK\r\n", result );
	}
	
	@Test
	public void testWriteSimpleResponseNonstandardStatusMessage() throws IOException {
		HTTPResponse response = new HTTPResponse();
		response.setStatus( 200 );
		response.setStatusMessage( "asdf" );
		String result = testWriteResponse( response );
		Assert.assertEquals( "HTTP/1.0 200 asdf\r\n", result );
	}
	
	@Test
	public void testWriteSimpleResponseUnplannedStatus() throws IOException {
		HTTPResponse response = new HTTPResponse();
		response.setStatus( 111 );
		String result = testWriteResponse( response );
		Assert.assertEquals( "HTTP/1.0 111 111 Message\r\n", result );
	}
	
	@Test
	public void testWriteResponseWithHeaders() throws IOException {
		HTTPResponse response = new HTTPResponse();
		response.setStatus( 200 );
		response.setHeader( "a", "a" );
		response.setHeader( "b", "b" );
		response.setHeader( "c", "b" );
		response.setHeader( "a", "b" );
		String result = testWriteResponse( response );
		Assert.assertTrue( result.startsWith( "HTTP/1.0 200 OK\r\n" ) );
		Assert.assertTrue( result.contains( "a: b\r\n" ) );
		Assert.assertTrue( result.contains( "b: b\r\n" ) );
		Assert.assertTrue( result.contains( "c: b\r\n" ) );
	}
	
	@Test
	public void testWriteResponseWithBody() throws IOException {
		HTTPResponse response = new HTTPResponse();
		response.setStatus( 200 );
		response.setBody( "hello world" );
		String result = testWriteResponse( response );
		Assert.assertTrue( result.startsWith( "HTTP/1.0 200 OK\r\n" ) );
		Assert.assertTrue( result.toLowerCase().contains( "content-length: 11\r\n" ) );
		Assert.assertTrue( result.contains( "\r\n\r\nhello world" ) );
	}
	
	@Test
	public void testWriteResponse() throws IOException {
		HTTPResponse response = new HTTPResponse();
		response.setStatus( 200 );
		response.setHeader( "a", "a" );
		response.setHeader( "b", "b" );
		response.setHeader( "c", "b" );
		response.setHeader( "a", "b" );
		response.setHeader( "content-length", "444" );
		response.setBody( "hello world" );
		String result = testWriteResponse( response );
		Assert.assertTrue( result.startsWith( "HTTP/1.0 200 OK\r\n" ) );
		Assert.assertTrue( result.contains( "a: b\r\n" ) );
		Assert.assertTrue( result.contains( "b: b\r\n" ) );
		Assert.assertTrue( result.contains( "c: b\r\n" ) );
		Assert.assertTrue( result.toLowerCase().contains( "content-length: 11\r\n" ) );
		Assert.assertTrue( result.contains( "\r\n\r\nhello world" ) );
	}

	@Test
	public void testStopWithIOException() throws IOException {
		try ( HTTPServer server = HTTPServer.always200OK() ) {
			server.serverSocket.close();
			
			server.serverSocket = new ServerSocket( 0 ) {
				@Override
				public void close() throws IOException {
					super.close();
					throw new IOException( "test" );
				}
			};
			
			server.stop(); // note:  IOException should be logged
		}
	}

	@Test
	public void testHandlerThrowsHTTPException() throws IOException {
		try ( HTTPServer server = new HTTPServer() ) {
			server.setHTTPRequestHandler( new HTTPRequestHandler() {
				@Override public void handleRequest( HTTPRequest request, HTTPResponse response ) throws HTTPException {
					throw new HTTPException( 400, "some kind of message" );
				}
			} );
			server.start();
			
			URL url = new URL( "http://localhost:" + server.getPort() );
			HttpURLConnection con = (HttpURLConnection)url.openConnection();
			Assert.assertEquals( 400, con.getResponseCode() );
			
			try ( InputStream resp = con.getErrorStream() ) {
				byte[] buffer = new byte[ 1000 ];
				int count = resp.read( buffer, 0, buffer.length );
				String body = new String( buffer, 0, count, StandardCharsets.UTF_8 );
				Assert.assertTrue( body.startsWith( "<html><body><h1>400 - Bad Request</h1><pre>com.mauter.httpserver.HTTPException: some kind of message" ) );
				Assert.assertTrue( body.endsWith( "</pre></body></html>" ) );
			}
		}
	}
}
