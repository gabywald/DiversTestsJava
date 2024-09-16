package gabywald.rest.jersey;

import java.util.HashSet;

import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

// import org.glassfish.jersey.servlet.ServletContainer;

@ApplicationPath("/test")
public class MyApplication extends Application {

	@Override
	public Set<Class<?>> getClasses() {
		Set<Class<?>> set	= new HashSet<Class<?>>();

		set.add(TestResources.class);

		return set;
	}

	@Override
	public Set<Object> getSingletons() {
		Set<Object> set	= new HashSet<Object>();

		// set.add(new TestResources());

		return set;
		// return super.getSingletons();
	}

//	public static void main(String[] args) throws Exception  {
//		Server server = new Server(0);
//		ServletContextHandler handler = new ServletContextHandler(server, "/");
//		handler.setAttribute("main.args", ImmutableList.copyOf(args));
//		ServletHolder holder = new ServletHolder(new HttpServletDispatcher());
//		holder.setInitParameter("javax.ws.rs.Application", MyApplication.getName());
//		handler.addServlet(holder, "/*");
//		server.start();
//		server.join();
//	}
//
//	public class MyApplication extends Application {
//		public PubSubApplication(@Context ServletContext servletContext) throws Exception {
//			String[] args = servletContext.getAttribute("main.args");
//			// do stuff with args...
//		}
//	}

}
