package gabywald.jersey.commonresources;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import gabywald.jersey.services.TokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

@ApplicationScoped
@Provider
@Priority(Priorities.AUTHENTICATION)
@Secured
public class AuthenticationFilter implements ContainerRequestFilter {
	
	public static final String AUTHENTICATION_SCHEME = "Bearer"; // "BlackBird"; // "Bearer";
	
	private final static Logger LOGGER = Logger.getLogger(AuthenticationFilter.class.getName());
	
	@Inject
	private TokenService tokenService;
	
	@Override
	public void filter(ContainerRequestContext requestContext) 
			throws IOException {
		// ***** Extract value of Authorization Header
		String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
		AuthenticationFilter.LOGGER.info("authorizationHeader=" + authorizationHeader);
		
		// ***** Validate Authorization Header
		if ( ! this.isHeaderAuthenticationValid(authorizationHeader) ) {
			this.interruptionWithUnauthorized(requestContext);
			return;
		}
		
		// ***** Extract Token from header
		String token = authorizationHeader.substring(AuthenticationFilter.AUTHENTICATION_SCHEME.length())
				.trim();
		
		AuthenticationFilter.LOGGER.info("token=" + token);
		try {
			// validerToken(token);
			// ***** Token Validation
			Jws<Claims> jws = this.tokenService.validToken(token);
			// ***** Extract User from Token
			AuthenticationFilter.LOGGER.info("subject = " + jws.getBody().get("sub"));
		} catch (Exception e) {
			AuthenticationFilter.LOGGER.log(Level.WARNING, "Error when validating token", e);
			this.interruptionWithUnauthorized(requestContext);
		}
	}
	
	/**
	 * Check if Authorization Header is valid. 
	 * @param authorizationHeader (String)
	 * @return (boolean)
	 */
	private boolean isHeaderAuthenticationValid(String authorizationHeader) {
		return ( (authorizationHeader != null)
					&& (authorizationHeader.toLowerCase()
							.startsWith(AuthenticationFilter.AUTHENTICATION_SCHEME.toLowerCase() + " ") ) );
	}

	/**
	 * Stop filter treatment with error 401 (unauthorized)
	 * @param requestContext (ContainerRequestContext)
	 */
	private void interruptionWithUnauthorized(ContainerRequestContext requestContext) {
		requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
				.header(HttpHeaders.WWW_AUTHENTICATE, AuthenticationFilter.AUTHENTICATION_SCHEME + " realm=\"token\"").build());
	}
	
//	/**
//	 * Token validation
//	 * @param token
//	 * @throws Exception
//	 */
//	private void validerToken(String token) 
//			throws Exception {
//
//	}

}
