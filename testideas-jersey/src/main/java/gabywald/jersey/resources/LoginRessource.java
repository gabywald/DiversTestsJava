package gabywald.jersey.resources;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import gabywald.jersey.commonresources.AuthenticationFilter;
import gabywald.jersey.services.LoginService;
import gabywald.jersey.services.TokenService;

@Path("/login")
@ApplicationScoped
public class LoginRessource {
	
	@Inject
	private LoginService loginService;
	
	@Inject
	TokenService tokenService;
	
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response authenticateUser(	@FormParam("login") String login,
										@FormParam("password") String password) {
		
		System.out.println("login: {" + login + "}");
		System.out.println("password: {" + password + "}");
		
		try {
			// ***** Authenticate User
			this.loginService.authentifier(login, password);
			// ***** Get a Token
			String token = this.tokenService.createToken(login, 120);
			// ***** Return the token
			return Response.ok()
					.header(HttpHeaders.AUTHORIZATION, AuthenticationFilter.AUTHENTICATION_SCHEME + " " + token)
					.build();
		} catch (Exception e) {
			return Response.status(Status.UNAUTHORIZED)
					.build();
		}
	}
}