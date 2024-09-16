package gabywald.jersey.services;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LoginService {
	
	public void authentifier(String login, String password) 
			throws SecurityException {
		// TODO get Pair of user / pwd from file / DB / ...
		if ( ! "user".equals(login) || ! "rest".equals(password)) {
			throw new SecurityException("Invalid user/password");
		}
	}
}