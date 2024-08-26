package gabywald.rest.jersey;

import java.util.Collections;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Application;

@ApplicationPath("helloworld")
@Path("hello")
public class HelloWorld extends Application {

	@Override
	public Set<Class<?>> getClasses() {
		return Collections.singleton(HelloWorld.class);
	}

	@GET
	public String sayHello() {
		return "Hello, World!";
	}
}
