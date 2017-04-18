package com.mauter.httpserver;

import org.junit.Assert;
import org.junit.Test;

public class TestHTTPException {
	
	@Test
	public void testHTTPExceptionIntStringNull() {
		HTTPException he = new HTTPException( 1234, (String)null );
		Assert.assertNull( he.getMessage() );
		Assert.assertEquals( 1234, he.getStatus() );
	}

	@Test
	public void testHTTPExceptionIntString() {
		HTTPException he = new HTTPException( 1234, "some kind of message" );
		Assert.assertEquals( "some kind of message", he.getMessage() );
		Assert.assertEquals( 1234, he.getStatus() );
	}
	
	@Test
	public void testHTTPExceptionThrowableNull() {
		HTTPException he = new HTTPException( null );
		Assert.assertNull( he.getCause() );
		Assert.assertNull( he.getMessage() );
		Assert.assertEquals( 500, he.getStatus() );
	}
	
	@Test
	public void testHTTPExceptionThrowable() {
		Exception e = new Exception();
		HTTPException he = new HTTPException( e );
		Assert.assertEquals( e, he.getCause() );
		Assert.assertEquals( "java.lang.Exception", he.getMessage() );
		Assert.assertEquals( 500, he.getStatus() );
	}

	@Test
	public void testHTTPExceptionIntThrowableNull() {
		HTTPException he = new HTTPException( 1234, (Throwable)null );
		Assert.assertNull( he.getCause() );
		Assert.assertNull( he.getMessage() );
		Assert.assertEquals( 1234, he.getStatus() );
	}
	
	@Test
	public void testHTTPExceptionIntThrowable() {
		Exception e = new Exception();
		HTTPException he = new HTTPException( 1234, e );
		Assert.assertEquals( e, he.getCause() );
		Assert.assertEquals( "java.lang.Exception", he.getMessage() );
		Assert.assertEquals( 1234, he.getStatus() );
	}
	
	@Test
	public void testHTTPExceptionIntStringNullThrowableNull() {
		HTTPException he = new HTTPException( 1234, null, null );
		Assert.assertNull( he.getCause() );
		Assert.assertNull( he.getMessage() );
		Assert.assertEquals( 1234, he.getStatus() );
	}
	
	@Test
	public void testHTTPExceptionIntStringNullThrowable() {
		Exception e = new Exception();
		HTTPException he = new HTTPException( 1234, null, e );
		Assert.assertEquals( e, he.getCause() );
		Assert.assertNull( he.getMessage() );
		Assert.assertEquals( 1234, he.getStatus() );
	}
	
	@Test
	public void testHTTPExceptionIntStringThrowableNull() {
		HTTPException he = new HTTPException( 1234, "some kind of message", null );
		Assert.assertNull( he.getCause() );
		Assert.assertEquals( "some kind of message", he.getMessage() );
		Assert.assertEquals( 1234, he.getStatus() );
	}
	
	@Test
	public void testHTTPExceptionIntStringThrowable() {
		Exception e = new Exception();
		HTTPException he = new HTTPException( 1234, "some kind of message", e );
		Assert.assertEquals( e, he.getCause() );
		Assert.assertEquals( "some kind of message", he.getMessage() );
		Assert.assertEquals( 1234, he.getStatus() );
	}
}
