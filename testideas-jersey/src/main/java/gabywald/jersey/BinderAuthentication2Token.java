package gabywald.jersey;


import org.glassfish.hk2.utilities.binding.AbstractBinder;

import gabywald.jersey.services.TokenService;

public class BinderAuthentication2Token extends AbstractBinder {
	@Override
	protected void configure() {
		this.bind(TokenService.class).to(TokenService.class);
	}
}
