# HTTP Server

HTTP Server is a very simple embeddable HTTP server written in Java with very few dependencies.  Its original
intent is to be used in JUnit tests so one can verify that their code properly made HTTP requests and that
the request was formatted properly.

## Background

I wrote a simple SlackNotification class for work, but I had no easy way to test that it was working properly.
At first, I sent a message on Slack telling everyone I was testing and to ignore the next 50 messages or so.
That works, but a better test wouldn't bother all of my coworkers and could be run as many times as I wanted,
preferably when the rest of our stuff is built.

## Typical use

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
	Assert.assertFalse( server.getRequests().isEmpty() );
	Assert.assertTrue( server.getBodyAsString().contains( "Hello world" ) );
	
	// more asserts

}
```

Since HTTP Server implements `Closeable`, using a `try-with-resources` statement keeps things simple and tidy.
Already using port 8080?  That's not going to cause a conflict.  By default, the server binds to an available
port so you don't have to worry with that.  Using TestNG so you can run concurrent tests?  Run as many of these
servers as you like simultaneously as long as you have enough ports open (there are 65,535 of them).  Don't
want to disturb your coworkers or have to clean up a website after your unit tests run?  Test against this
server.  You can even implement the `HTTPRequestHandler` interface so the server responds exactly like you
want.

