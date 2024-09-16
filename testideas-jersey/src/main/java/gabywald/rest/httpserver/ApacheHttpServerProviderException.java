package gabywald.rest.httpserver;

import gabywald.global.exceptions.GenericException;

@SuppressWarnings("serial")
public class ApacheHttpServerProviderException extends GenericException {

	public ApacheHttpServerProviderException(String request) 
		{ super(request); }

}
