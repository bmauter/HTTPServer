# HTTP Server

HTTP Server is a very simple embeddable HTTP server written in Java with very few dependencies.  Its original intent is to be used in JUnit tests so one can verify that their code properly made HTTP requests and that the request was formatted properly.

## Background

I wrote a simple SlackNotification class for work, but I had no easy way to test that it was working properly.  At first, I sent a message on Slack telling everyone I was testing and to ignore the next 50 messages or so.  That works, but a better test wouldn't bother all of my coworkers and could be run as many times as I wanted, preferably when the rest of our stuff is built.

## Typical Use

HTTP Server implements `Closeable`. Use a `try-with-resources` statement to keeps things simple and tidy.  After `start()` is called, HTTP server bind to an open port by default  Then call your code to be tested passing in "localhost" and the port that HTTP Server bound to.  After your code makes its call, you can inspect the requests and responses to make sure things were put together correctly.

```java
try ( HTTPServer server = HTTPServer.always200OK() ) {

	// call some code you wrote that makes an HTTP call
	SlackNotification slack = new SlackNotification();
	slack.setUrl( "http://localhost:" + server.getPort() );
	slack.setMessage( "Hello world" );
	slack.setEmoji( ":tada:" );
	
	// more setup
	
	slack.send();
	
	// now assert everything looks like it should
	List<HTTPRequest> requests = server.getRequests();
	Assert.assertFalse( requests.isEmpty() );
	Assert.assertTrue( requests.get(0).getBodyAsString().contains( "Hello world" ) );
	
	// more asserts

}
```
## More Advanced Use

## Concurrency

Using TestNG so you can run concurrent tests?  Run as many of these servers as you like simultaneously as long as you have enough memory and ports open (there are 65,535 of them).

## Repeatability

Don't want to disturb your coworkers or have to clean up a website after your unit tests run?  Test against this server.  You can even implement the `HTTPRequestHandler` interface so the server responds exactly like you want.

For example, what if you want to test how your code handles errors from the server?

```java
try ( HTTPServer server = new HTTPServer() ) {

	// make the server tell us that we sent bad data
	server.setHTTPRequestHandler( new HTTPRequestHandler() {
		public void handleRequest( HTTPRequest request, HTTPResponse response ) {
			response.setStatus( 400 );
		}
	} );
	server.start();

	// call some code you wrote that makes an HTTP call
	SlackNotification slack = new SlackNotification();
	slack.setUrl( "http://localhost:" + server.getPort() );
	slack.setToken( "obviously not a real slack token" );
	slack.setMessage( "Hello world" );
	slack.setEmoji( ":tada:" );
	
	// more setup
	
	slack.send();
	
	// now assert everything looks like it should
	List<HTTPResponse> responses = server.getResponses();
	Assert.assertFalse( responses.isEmpty() );
	Assert.assertEquals( 400, responses.get(0).getStatus() );
	
	// more asserts

}
```
