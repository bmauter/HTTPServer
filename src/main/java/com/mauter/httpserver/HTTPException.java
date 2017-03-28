package com.mauter.httpserver;

public class HTTPException extends Exception {

	private static final long serialVersionUID = 1L;
	
	int status;
	
	/**
	 * Gets the status for this exception.
	 * 
	 * @return the status for this exception.
	 */
	public int getStatus() {
		return this.status;
	}
	
	public HTTPException( int status, String message ) {
		super( message );
		this.status = status;
	}

	public HTTPException( Throwable t ) {
		this( 500, t );
	}

	public HTTPException( String message, Throwable t ) {
		this( 500, message, t );
	}
	
	public HTTPException( int status, Throwable t ) {
		super( t );
		this.status = status;
	}
	
	public HTTPException( int status, String message, Throwable t ) {
		super( message, t );
		this.status = status;
	}
}
