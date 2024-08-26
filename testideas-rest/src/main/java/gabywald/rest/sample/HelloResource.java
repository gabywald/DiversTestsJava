package gabywald.rest.sample;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * 
 * @author Gabriel Chandesris (2024)
 */
@Path("hello")
@Produces(MediaType.TEXT_PLAIN)
public class HelloResource {
    public HelloResource() { ; }
    
    @GET
    public String getHello() {
        return "Bonjour les gens";
    }
    
    @GET
    @Path("{id}")
    public String getHello(@PathParam("id") String id,
                           @DefaultValue("votre serviteur") @HeaderParam("name") String name) {
        return "Bonjour " + id + " de la part de " + name;
    }
    
    @GET
    @Path("withheaders/{id}")
    public Response getHelloWithHeaders(@PathParam("id") String id,
                                        @DefaultValue("votre serviteur") @HeaderParam("name") String name) {
        return Response.ok().header("name", name).entity("Bonjour " + id + " de la part de (voir l'en-tête).").build();
    }

    
}
