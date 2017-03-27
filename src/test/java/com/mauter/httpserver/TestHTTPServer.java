package com.mauter.httpserver;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;

import org.junit.Assert;
import org.junit.Test;

public class TestHTTPServer {
	
	static final Charset UTF8 = Charset.forName( "UTF-8" );
	
	InputStream buildSimpleRequest() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter( baos );
		writer.println( "GET / HTTP/1.0" );
		writer.flush();
		return new ByteArrayInputStream( baos.toByteArray() );
	}

	@Test(expected=NullPointerException.class)
	public void testReadNull() throws IOException {
		HTTPServer.read( null, null );
	}
	
	@Test(expected=NullPointerException.class)
	public void testReadNullInputStream() throws IOException {
		HTTPServer.read( null, new HTTPRequest() );
	}
	
	@Test(expected=NullPointerException.class)
	public void testReadNullRequest() throws IOException {
		HTTPServer.read( new ByteArrayInputStream( "GET / HTTP/1.0\r\n".getBytes( UTF8 ) ), null );
	}
	
	@Test(expected=IOException.class)
	public void testReadEmptyInputStream() throws IOException {
		HTTPRequest request = new HTTPRequest();
		HTTPServer.read( new ByteArrayInputStream( "".getBytes( "UTF-8" ) ), request );
	}
	
	@Test
	public void testReadInputStreamOnlyInitialLine() throws IOException {
		HTTPRequest request = new HTTPRequest();
		HTTPServer.read( buildSimpleRequest(), request );
		Assert.assertEquals( "GET", request.getMethod() );
		Assert.assertEquals( "/", request.getPath() );
		Assert.assertEquals( "HTTP/1.0", request.getVersion() );
	}
	
}
