package gabywald.jersey.resources;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import gabywald.jersey.commonresources.Secured;

@Path("ressources")
@Singleton
public class RessourceSample {
	
	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response get(@PathParam("id") Long id) {
		return Response.ok()
				.entity("{\"message\":\"acces methode publique\"}")
				.build();
	}
	
	@POST
	@Secured
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response mySecuredMethod(@PathParam("id") Long id) {
		return Response.ok()
				.entity("{\"message\":\"acces methode securisee\"}")
				.build();
	}
}