package gabywald.jersey;

import org.glassfish.jersey.server.ResourceConfig;

import gabywald.designpatterns.usage.BuilderFluently;
import gabywald.global.exceptions.MessageException;

public class JerseyLauncherBuilder extends BuilderFluently<JerseyLauncher> {
	
	public enum AttemptedParameters {
		PORT("port"), 
		URL("url"), 
		PACKAGES("packages"), 
		START("start");
		
		private String name = null;
		
		private AttemptedParameters(String name) {
			this.name = name;
		}
		
		public String getName() {
			return this.name;
		}
	}
	
	public static class JerseyLauncherBuilderException extends MessageException {

		private static final long serialVersionUID = -726255812570533907L;

		public JerseyLauncherBuilderException(String message) {
			super(message);
		}
		
		public JerseyLauncherBuilderException(AttemptedParameters ap) {
			super(JerseyLauncherBuilderException.missingParameterMessage(ap.getName()));
		}
		
		public static String missingParameterMessage(String name) {
			StringBuilder sb = new StringBuilder();
			sb.append("Missing parameter {");
			sb.append(name);
			sb.append("}");
			return sb.toString();
		}
	}
	
	public JerseyLauncherBuilder addPort(int port) {
		return (JerseyLauncherBuilder) this.addParameter(AttemptedParameters.PORT.getName(), port);
	}
	
	public JerseyLauncherBuilder addURL(String url) {
		return (JerseyLauncherBuilder) this.addParameter(AttemptedParameters.URL.getName(), url);
	}
	
	public JerseyLauncherBuilder addPackages(String packages) {
		return (JerseyLauncherBuilder) this.addParameter(AttemptedParameters.PACKAGES.getName(), packages);
	}
	
	public JerseyLauncherBuilder addStart(boolean start) {
		return (JerseyLauncherBuilder) this.addParameter(AttemptedParameters.START.getName(), start);
	}
	
	public JerseyLauncher buildWithException() throws JerseyLauncherBuilderException {
		// mandatory : PORT
		if (!this.map.containsKey(AttemptedParameters.PORT.getName())) {
			throw new JerseyLauncherBuilderException(AttemptedParameters.PORT.getName());
		}
		
		// mandatory : URL
		if (!this.map.containsKey(AttemptedParameters.URL.getName())) {
			throw new JerseyLauncherBuilderException(AttemptedParameters.URL.getName());
		}
		
		// mandatory : PACKAGES
		if (!this.map.containsKey(AttemptedParameters.PACKAGES.getName())) {
			throw new JerseyLauncherBuilderException(AttemptedParameters.PACKAGES.getName());
		}
		
		int port = Integer.parseInt(this.map.get(AttemptedParameters.PORT.getName()).second);
		String url = this.map.get(AttemptedParameters.URL.getName()).second;
		String packages = this.map.get(AttemptedParameters.PACKAGES.getName()).second;
		
		// START parameter is optional, true by default
		boolean start = true;
		if (this.map.containsKey(AttemptedParameters.START.getName())) {
			start = this.map.get(AttemptedParameters.START.getName()).second.equals(Boolean.TRUE.toString());
		}
		
		ResourceConfig config = new ResourceConfig();
		config.packages(packages.split(";"));
		config.register(new BinderAuthentication2Token());
		config.register(new BinderAuthentication2Login());
		
		JerseyLauncher jlToReturn = new JerseyLauncher(port, url, start, config);
		
		return jlToReturn;
	}

	@Override
	public JerseyLauncher build() {
		try {
			return this.buildWithException();
		} catch (JerseyLauncherBuilderException e) {
			System.out.println(e.getMessage());
			return null;
		}
	}

}


