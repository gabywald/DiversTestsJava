package gabywald.jersey.commonresources;

import java.io.IOException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

@Provider
public class CORSFilter implements ContainerResponseFilter {
	
	@Override
	public void filter(	final ContainerRequestContext requestContext,
						final ContainerResponseContext responseContext)
					throws IOException {
		MultivaluedMap<String, Object> headers = responseContext.getHeaders();
		headers.add("Access-Control-Allow-Origin", 			"*"); // XXX !!
		headers.add("Access-Control-Allow-Headers", 		"origin, contenttype, accept, authorization");
		headers.add("Access-Control-Allow-Credentials", 	"true");
		headers.add("Access-Control-Allow-Methods", 		"GET, POST, PUT, DELETE, OPTIONS, HEAD");
		headers.add("Access-Control-Max-Age", 				"1209600");
	}
}