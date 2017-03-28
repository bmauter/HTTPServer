package com.mauter.httpserver;

import java.io.UnsupportedEncodingException;

import org.junit.Assert;
import org.junit.Test;

public class TestHTTPResponse {
	
	@Test
	public void testGetStatusUnset() {
		HTTPResponse response = new HTTPResponse();
		Assert.assertEquals( 0, response.getStatus() );
	}
	
	@Test
	public void testGetStatus() {
		HTTPResponse response = new HTTPResponse();
		response.status = 1234;
		Assert.assertEquals( 1234, response.getStatus() );
	}
	
	@Test
	public void testSetStatus() {
		HTTPResponse response = new HTTPResponse();
		response.setStatus( 1234 );
		Assert.assertEquals( 1234, response.status );
	}

	@Test
	public void testGetStatusMessageNull() {
		HTTPResponse response = new HTTPResponse();
		response.statusMessage = null;
		Assert.assertNull( response.getStatusMessage() );
	}
	
	@Test
	public void testGetStatusMessageBlank() {
		HTTPResponse response = new HTTPResponse();
		response.statusMessage = "";
		Assert.assertEquals( "", response.getStatusMessage() );
	}
	
	@Test
	public void testGetStatusMessage() {
		HTTPResponse response = new HTTPResponse();
		response.statusMessage = "1234";
		Assert.assertEquals( "1234", response.getStatusMessage() );
	}
	
	@Test
	public void testSetStatusMessageNull() {
		HTTPResponse response = new HTTPResponse();
		response.setStatusMessage( null );
		Assert.assertNull( response.statusMessage );
	}

	@Test
	public void testSetStatusMessageBlank() {
		HTTPResponse response = new HTTPResponse();
		response.setStatusMessage( "" );
		Assert.assertEquals( "", response.statusMessage );
	}

	@Test
	public void testSetStatusMessage() {
		HTTPResponse response = new HTTPResponse();
		response.setStatusMessage( "1234" );
		Assert.assertEquals( "1234", response.statusMessage );
	}
	
	@Test
	public void testGetBodyNull() {
		HTTPResponse response = new HTTPResponse();
		response.body = null;
		Assert.assertNull( response.getBody() );
	}
	
	@Test
	public void testGetBodyBlank() {
		HTTPResponse response = new HTTPResponse();
		response.body = new byte[] {};
		Assert.assertArrayEquals( new byte[] {}, response.getBody() );
	}
	
	@Test
	public void testGetBody() {
		HTTPResponse response = new HTTPResponse();
		response.body = new byte[] { 1, 2, 3, 4 };
		Assert.assertArrayEquals( new byte[] { 1, 2, 3, 4 }, response.getBody() );
	}

	@Test
	public void testGetBodyAsStringNull() {
		HTTPResponse response = new HTTPResponse();
		response.body = null;
		Assert.assertNull( response.getBodyAsString() );
	}
	
	@Test
	public void testGetBodyAsStringBlank() throws UnsupportedEncodingException {
		HTTPResponse response = new HTTPResponse();
		response.body = "".getBytes( "UTF-8" );
		Assert.assertEquals( "", response.getBodyAsString() );
	}
	
	@Test
	public void testGetBodyAsString() throws UnsupportedEncodingException {
		HTTPResponse response = new HTTPResponse();
		response.body = "1234".getBytes( "UTF-8" );
		Assert.assertEquals( "1234", response.getBodyAsString() );
	}
	
	@Test
	public void testSetBodyBytesNull() {
		HTTPResponse response = new HTTPResponse();
		response.setBody( (byte[])null );
		Assert.assertNull( response.body );
	}
	
	@Test
	public void testSetBodyBytesBlank() {
		HTTPResponse response = new HTTPResponse();
		response.setBody( new byte[] {} );
		Assert.assertArrayEquals( new byte[] {}, response.body );
	}
	
	@Test
	public void testSetBodyBytes() {
		HTTPResponse response = new HTTPResponse();
		response.setBody( new byte[] { 1, 2, 3, 4 } );
		Assert.assertArrayEquals( new byte[] { 1, 2, 3, 4 }, response.body );
	}
	
	@Test
	public void testSetBodyStringNull() {
		HTTPResponse response = new HTTPResponse();
		response.setBody( (String)null );
		Assert.assertNull( response.body );
	}
	
	@Test
	public void testSetBodyStringBlank() throws UnsupportedEncodingException {
		HTTPResponse response = new HTTPResponse();
		response.setBody( "" );
		Assert.assertArrayEquals( "".getBytes( "UTF-8" ), response.body );
	}
	
	@Test
	public void testSetBodyString() throws UnsupportedEncodingException {
		HTTPResponse response = new HTTPResponse();
		response.setBody( "1234" );
		Assert.assertArrayEquals( "1234".getBytes( "UTF-8" ), response.body );
	}
	
	@Test
	public void testGetHeaderNone() {
		HTTPResponse response = new HTTPResponse();
		Assert.assertNull( response.getHeader( "a" ) );
	}
	
	@Test(expected=NullPointerException.class)
	public void testGetHeaderNull() {
		HTTPResponse response = new HTTPResponse();
		response.setHeader( "asdf", "qwer" );
		Assert.assertNull( response.getHeader( null ) );
	}
	
	@Test
	public void testGetHeaderBlank() {
		HTTPResponse response = new HTTPResponse();
		response.setHeader( "asdf", "qwer" );
		Assert.assertNull( response.getHeader( "" ) );
	}
	
	@Test
	public void testGetHeaderNotThere() {
		HTTPResponse response = new HTTPResponse();
		response.setHeader( "asdf", "qwer" );
		Assert.assertNull( response.getHeader( "a" ) );
	}
	
	@Test
	public void testGetHeader() {
		HTTPResponse response = new HTTPResponse();
		response.setHeader( "asdf", "qwer" );
		Assert.assertEquals( "qwer", response.getHeader( "asdf" ) );
	}
	
	@Test(expected=NullPointerException.class)
	public void testSetHeaderNullKey() {
		HTTPResponse response = new HTTPResponse();
		response.setHeader( null, "qwer" );
	}
	
	@Test
	public void testSetHeaderNullValue() {
		HTTPResponse response = new HTTPResponse();
		response.setHeader( "asdf", null );
		Assert.assertNull( response.getHeader( "asdf" ) );
	}
	
	@Test
	public void testSetHeaderBlankKey() {
		HTTPResponse response = new HTTPResponse();
		response.setHeader( "", "qwer" );
		Assert.assertEquals( "qwer", response.getHeader( "" ) );
	}
	
	@Test
	public void testSetHeaderBlankValue() {
		HTTPResponse response = new HTTPResponse();
		response.setHeader( "asdf", "" );
		Assert.assertEquals( "", response.getHeader( "asdf" ) );
	}
	
	@Test
	public void testSetHeader() {
		HTTPResponse response = new HTTPResponse();
		response.setHeader( "asdf", "qwer" );
		Assert.assertEquals( "qwer", response.getHeader( "asdf" ) );
	}
	
	@Test
	public void testSetHeaderDifferentCaseKey() {
		HTTPResponse response = new HTTPResponse();
		response.setHeader( "asdf", "qwer" );
		Assert.assertEquals( "qwer", response.getHeader( "ASDF" ) );
	}
	
	@Test
	public void testSetSameHeaderTwice() {
		HTTPResponse response = new HTTPResponse();
		response.setHeader( "asdf", "qwer" );
		response.setHeader( "asdf", "ghjk" );
		Assert.assertNotSame( "qwer", response.getHeader( "asdf" ) );
		Assert.assertEquals( "ghjk", response.getHeader( "asdf" ) );
	}
	
	@Test
	public void testGetHeadersNone() {
		HTTPResponse response = new HTTPResponse();
		Assert.assertNull( response.getHeaders() );
	}
	
	@Test
	public void testGetHeadersOne() {
		HTTPResponse response = new HTTPResponse();
		response.setHeader( "asdf", "qwer" );
		Assert.assertEquals( 1, response.getHeaders().size() );
	}
	
	@Test
	public void testGetHeadersTwo() {
		HTTPResponse response = new HTTPResponse();
		response.setHeader( "asdf1", "qwer" );
		response.setHeader( "asdf2", "qwer" );
		Assert.assertEquals( 2, response.getHeaders().size() );
	}
	
	@Test
	public void testGetHeadersOneSetTwice() {
		HTTPResponse response = new HTTPResponse();
		response.setHeader( "asdf", "qwer1" );
		response.setHeader( "asdf", "qwer2" );
		Assert.assertEquals( 1, response.getHeaders().size() );
		Assert.assertEquals( "qwer2", response.getHeaders().get( "asdf" ) );
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void testGetHeadersNotModifiable() {
		HTTPResponse response = new HTTPResponse();
		response.setHeader( "asdf", "qwer" );
		response.getHeaders().put( "zxcv", "dfgh" );
	}
}
