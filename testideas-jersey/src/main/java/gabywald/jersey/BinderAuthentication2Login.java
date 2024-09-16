package gabywald.jersey;


import org.glassfish.hk2.utilities.binding.AbstractBinder;

import gabywald.jersey.services.LoginService;

public class BinderAuthentication2Login extends AbstractBinder {
	@Override
	protected void configure() {
		this.bind(LoginService.class).to(LoginService.class);
	}
}
