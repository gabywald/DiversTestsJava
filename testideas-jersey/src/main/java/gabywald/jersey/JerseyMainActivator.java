package gabywald.jersey;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("main")
public class JerseyMainActivator extends ResourceConfig { // Application {

	public JerseyMainActivator(String... packages) {
//		BeanConfig beanConfig = new BeanConfig();
//		beanConfig.setVersion("1.0.0");
//		beanConfig.setSchemes(new String[] { "http" });
//		beanConfig.setBasePath("main");
//		beanConfig.setResourcePackage("gabywald.jersey.resources");
//		beanConfig.setScan(true);
//		beanConfig.setTitle("Documentation de l'API <locale>");
		
//		this.register(new BinderAuthentication2Token());
//		this.register(new BinderAuthentication2Login());
		
		this.packages(packages);
	}
	
//	@Override
//	public Set<Class<?>> getClasses() {
//		Set<Class<?>> resources = new HashSet<>();
//		
//		resources.add(HelloRessource.class);
//		
//		resources.add(ConstraintViolationExceptionMapper.class);
//		resources.add(ServerLoggingFilter.class);
//		resources.add(LoginRessource.class);
//		resources.add(RessourceSample.class);
//		
//		resources.add(CORSFilter.class);
//		resources.add(DateWriter.class);
//		
//		resources.add(AsyncRessource.class);
//		
//		resources.add(ApiListingResource.class);
//		resources.add(SwaggerSerializers.class);
//		
//		return resources;
//	}
}
