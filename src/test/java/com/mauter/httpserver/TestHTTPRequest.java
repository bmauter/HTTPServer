package com.mauter.httpserver;

import java.io.UnsupportedEncodingException;

import org.junit.Assert;
import org.junit.Test;

public class TestHTTPRequest {
	
	@Test
	public void testGetMethodNull() {
		HTTPRequest request = new HTTPRequest();
		request.method = null;
		Assert.assertNull( request.getMethod() );
	}
	
	@Test
	public void testGetMethodBlank() {
		HTTPRequest request = new HTTPRequest();
		request.method = "";
		Assert.assertEquals( "", request.getMethod() );
	}
	
	@Test
	public void testGetMethod() {
		HTTPRequest request = new HTTPRequest();
		request.method = "1234";
		Assert.assertEquals( "1234", request.getMethod() );
	}
	
	@Test
	public void testSetMethodNull() {
		HTTPRequest request = new HTTPRequest();
		request.setMethod( null );
		Assert.assertNull( request.method );
	}

	@Test
	public void testSetMethodBlank() {
		HTTPRequest request = new HTTPRequest();
		request.setMethod( "" );
		Assert.assertEquals( "", request.method );
	}

	@Test
	public void testSetMethod() {
		HTTPRequest request = new HTTPRequest();
		request.setMethod( "1234" );
		Assert.assertEquals( "1234", request.method );
	}

	@Test
	public void testGetPathNull() {
		HTTPRequest request = new HTTPRequest();
		request.path = null;
		Assert.assertNull( request.getPath() );
	}
	
	@Test
	public void testGetPathBlank() {
		HTTPRequest request = new HTTPRequest();
		request.path = "";
		Assert.assertEquals( "", request.getPath() );
	}
	
	@Test
	public void testGetPath() {
		HTTPRequest request = new HTTPRequest();
		request.path = "1234";
		Assert.assertEquals( "1234", request.getPath() );
	}
	
	@Test
	public void testSetPathNull() {
		HTTPRequest request = new HTTPRequest();
		request.setPath( null );
		Assert.assertNull( request.path );
	}
	
	@Test
	public void testSetPathBlank() {
		HTTPRequest request = new HTTPRequest();
		request.setPath( "" );
		Assert.assertEquals( "", request.path );
	}
	
	@Test
	public void testSetPath() {
		HTTPRequest request = new HTTPRequest();
		request.setPath( "1234" );
		Assert.assertEquals( "1234", request.path );
	}

	@Test
	public void testGetBodyNull() {
		HTTPRequest request = new HTTPRequest();
		request.body = null;
		Assert.assertNull( request.getBody() );
	}
	
	@Test
	public void testGetBodyBlank() {
		HTTPRequest request = new HTTPRequest();
		request.body = new byte[] {};
		Assert.assertArrayEquals( new byte[] {}, request.getBody() );
	}
	
	@Test
	public void testGetBody() {
		HTTPRequest request = new HTTPRequest();
		request.body = new byte[] { 1, 2, 3, 4 };
		Assert.assertArrayEquals( new byte[] { 1, 2, 3, 4 }, request.getBody() );
	}

	@Test
	public void testGetBodyAsStringNull() {
		HTTPRequest request = new HTTPRequest();
		request.body = null;
		Assert.assertNull( request.getBodyAsString() );
	}
	
	@Test
	public void testGetBodyAsStringBlank() throws UnsupportedEncodingException {
		HTTPRequest request = new HTTPRequest();
		request.body = "".getBytes( "UTF-8" );
		Assert.assertEquals( "", request.getBodyAsString() );
	}
	
	@Test
	public void testGetBodyAsString() throws UnsupportedEncodingException {
		HTTPRequest request = new HTTPRequest();
		request.body = "1234".getBytes( "UTF-8" );
		Assert.assertEquals( "1234", request.getBodyAsString() );
	}
	
	@Test
	public void testSetBodyBytesNull() {
		HTTPRequest request = new HTTPRequest();
		request.setBody( (byte[])null );
		Assert.assertNull( request.body );
	}
	
	@Test
	public void testSetBodyBytesBlank() {
		HTTPRequest request = new HTTPRequest();
		request.setBody( new byte[] {} );
		Assert.assertArrayEquals( new byte[] {}, request.body );
	}
	
	@Test
	public void testSetBodyBytes() {
		HTTPRequest request = new HTTPRequest();
		request.setBody( new byte[] { 1, 2, 3, 4 } );
		Assert.assertArrayEquals( new byte[] { 1, 2, 3, 4 }, request.body );
	}
	
	@Test
	public void testSetBodyStringNull() {
		HTTPRequest request = new HTTPRequest();
		request.setBody( (String)null );
		Assert.assertNull( request.body );
	}
	
	@Test
	public void testSetBodyStringBlank() throws UnsupportedEncodingException {
		HTTPRequest request = new HTTPRequest();
		request.setBody( "" );
		Assert.assertArrayEquals( "".getBytes( "UTF-8" ), request.body );
	}
	
	@Test
	public void testSetBodyString() throws UnsupportedEncodingException {
		HTTPRequest request = new HTTPRequest();
		request.setBody( "1234" );
		Assert.assertArrayEquals( "1234".getBytes( "UTF-8" ), request.body );
	}
	
	@Test
	public void testGetHeaderNone() {
		HTTPRequest request = new HTTPRequest();
		Assert.assertNull( request.getHeader( "a" ) );
	}
	
	@Test(expected=NullPointerException.class)
	public void testGetHeaderNull() {
		HTTPRequest request = new HTTPRequest();
		request.setHeader( "asdf", "qwer" );
		Assert.assertNull( request.getHeader( null ) );
	}
	
	@Test
	public void testGetHeaderBlank() {
		HTTPRequest request = new HTTPRequest();
		request.setHeader( "asdf", "qwer" );
		Assert.assertNull( request.getHeader( "" ) );
	}
	
	@Test
	public void testGetHeaderNotThere() {
		HTTPRequest request = new HTTPRequest();
		request.setHeader( "asdf", "qwer" );
		Assert.assertNull( request.getHeader( "a" ) );
	}
	
	@Test
	public void testGetHeader() {
		HTTPRequest request = new HTTPRequest();
		request.setHeader( "asdf", "qwer" );
		Assert.assertEquals( "qwer", request.getHeader( "asdf" ) );
	}
	
	@Test(expected=NullPointerException.class)
	public void testSetHeaderNullKey() {
		HTTPRequest request = new HTTPRequest();
		request.setHeader( null, "qwer" );
	}
	
	@Test
	public void testSetHeaderNullValue() {
		HTTPRequest request = new HTTPRequest();
		request.setHeader( "asdf", null );
		Assert.assertNull( request.getHeader( "asdf" ) );
	}
	
	@Test
	public void testSetHeaderBlankKey() {
		HTTPRequest request = new HTTPRequest();
		request.setHeader( "", "qwer" );
		Assert.assertEquals( "qwer", request.getHeader( "" ) );
	}
	
	@Test
	public void testSetHeaderBlankValue() {
		HTTPRequest request = new HTTPRequest();
		request.setHeader( "asdf", "" );
		Assert.assertEquals( "", request.getHeader( "asdf" ) );
	}
	
	@Test
	public void testSetHeader() {
		HTTPRequest request = new HTTPRequest();
		request.setHeader( "asdf", "qwer" );
		Assert.assertEquals( "qwer", request.getHeader( "asdf" ) );
	}
	
	@Test
	public void testSetHeaderDifferentCaseKey() {
		HTTPRequest request = new HTTPRequest();
		request.setHeader( "asdf", "qwer" );
		Assert.assertEquals( "qwer", request.getHeader( "ASDF" ) );
	}
	
	@Test
	public void testSetSameHeaderTwice() {
		HTTPRequest request = new HTTPRequest();
		request.setHeader( "asdf", "qwer" );
		request.setHeader( "asdf", "ghjk" );
		Assert.assertNotSame( "qwer", request.getHeader( "asdf" ) );
		Assert.assertEquals( "ghjk", request.getHeader( "asdf" ) );
	}
	
	@Test
	public void testGetHeadersNone() {
		HTTPRequest request = new HTTPRequest();
		Assert.assertNull( request.getHeaders() );
	}
	
	@Test
	public void testGetHeadersOne() {
		HTTPRequest request = new HTTPRequest();
		request.setHeader( "asdf", "qwer" );
		Assert.assertEquals( 1, request.getHeaders().size() );
	}
	
	@Test
	public void testGetHeadersTwo() {
		HTTPRequest request = new HTTPRequest();
		request.setHeader( "asdf1", "qwer" );
		request.setHeader( "asdf2", "qwer" );
		Assert.assertEquals( 2, request.getHeaders().size() );
	}
	
	@Test
	public void testGetHeadersOneSetTwice() {
		HTTPRequest request = new HTTPRequest();
		request.setHeader( "asdf", "qwer1" );
		request.setHeader( "asdf", "qwer2" );
		Assert.assertEquals( 1, request.getHeaders().size() );
		Assert.assertEquals( "qwer2", request.getHeaders().get( "asdf" ) );
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void testGetHeadersNotModifiable() {
		HTTPRequest request = new HTTPRequest();
		request.setHeader( "asdf", "qwer" );
		request.getHeaders().put( "zxcv", "dfgh" );
	}
}
