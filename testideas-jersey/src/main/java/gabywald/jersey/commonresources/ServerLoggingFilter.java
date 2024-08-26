package gabywald.jersey.commonresources;

import java.io.IOException;
import java.util.logging.Logger;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

@Provider
public class ServerLoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {
	private final static Logger LOGGER = Logger.getLogger(ServerLoggingFilter.class.getName());
	
	@Override
	public void filter(ContainerRequestContext crc) throws IOException {
		ServerLoggingFilter.LOGGER.info("ContainerRequestFilter " + crc.getUriInfo().getAbsolutePath() + " " + crc.getMethod());
		for (String key : crc.getHeaders().keySet()) {
			ServerLoggingFilter.LOGGER.info(" header " + key + " : " + crc.getHeaders().get(key));
		}
	}
	
	@Override
	public void filter(ContainerRequestContext creqc, ContainerResponseContext cresc) throws IOException {
		ServerLoggingFilter.LOGGER.info("ContainerResponseFilter ");
		for (String key : cresc.getHeaders().keySet()) {
			ServerLoggingFilter.LOGGER.info(" header " + key + ": " + cresc.getHeaders().get(key));
		}
		// cresc.getHeaders().add("myHeader", "myValue");
	}
}