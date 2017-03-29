# http-server

HTTPServer is a very simple embeddable HTTP server written in Java with very few dependencies.  Its original
intent is to be used in JUnit tests so one can verify that their code properly made HTTP requests and that
the request was formatted properly.

Typical use can be something like:

```
try ( HTTPServer server = HTTPServer.always200OK() ) {

	// call some code that makes an HTTP call
	SlackNotification slack = new SlackNotification();
	slack.setUrl( "http://localhost:" + server.getPort() );
	slack.setMessage( "Hello world" );
	slack.setEmoji( ":tada:" );
	...
	slack.send();
	
	Assert.assertFalse( server.getRequests().isEmpty() );
	Assert.assertTrue( server.getBodyAsString().contains( "Hello world" ) );
	...

}
```