package gabywald.jersey.resources;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import javax.inject.Singleton;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

@Path("hello")
@Singleton
public class HelloRessource {

	// http://localhost:8080/RESTProjectBase/hello
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getHello() {
		return "Hello from REST @ " + LocalDateTime.now();
	}

	@GET
	@Produces("text/plain")
	@Path("again")
	public String getHelloAgain() {
		return "Hello again from REST @ " + LocalDateTime.now();
	}

	@POST
	@Produces("text/plain")
	public String postHello() {
		System.out.println(" -- POST received");
		return "Ok";
	}
	
	@GET
	@Path("name/{name}")
	@Produces("text/plain")
	public String getHello(@PathParam("name") String name) {
		return "Hello " + name + " @" + LocalDateTime.now();
	}
	
	@GET
	@Path("param")
	@Produces("text/plain")
	public String getHelloParam(@DefaultValue("World") @QueryParam("name") String name) {
		return "Hello " + name + " from REST @ " + LocalDateTime.now();
	}
	
	@GET
	@Path("params")
	@Produces("text/plain")
	public String getHelloAllParams(@Context UriInfo ui) {
		MultivaluedMap<String, String> queryParams = ui.getQueryParameters();
		StringBuilder sb = new StringBuilder();
		sb.append("Hello @ " + LocalDateTime.now());
		for (Entry<String, List<String>> entry : queryParams.entrySet()) {
			sb.append("\n").append(entry.getKey()).append("=");
			for (String value : entry.getValue()) {
				sb.append(value).append(";");
			}
		}
		return sb.toString();
	}
	
	
	/*
curl -i -X GET http://localhost:8080/RESTProjectBase/main/hello/validation
curl -i -X GET "http://localhost:8080/RESTProjectBase/main/hello/validation?name=Jeff&version=2"
	 */
	@GET
	@Path("validation")
	@Produces(MediaType.TEXT_PLAIN)
	public String getHelloValidation(	@NotNull @Size(min = 3)
										@QueryParam("name") String name,
										@Min(1) @Max(10) @QueryParam("version") int version) {
		return "Hello " + name + " v" + version;
	}
	
	/*
curl -i -X GET http://localhost:8080/RESTProjectBase/main/hello/conneg
curl -i -H "accept:text/plain" -X GET http://localhost:8080/RESTProjectBase/main/hello/conneg
curl -i -H "accept:text/html" -X GET http://localhost:8080/RESTProjectBase/main/hello/conneg
	 */
	
	@GET @Path("conneg") @Produces("text/plain")
	public String getHelloString() {
		return "Hello, "+ LocalDateTime.now();
	}
	
	
	@GET @Path("conneg") @Produces("text/html")
	public String getHelloHTML() {
		return "<html><body><h1>Hello, "+ LocalDateTime.now()
		+"</h1></body></html>";
	}
	
	/*
curl -i -X GET http://localhost:8080/RESTProjectBase/main/hello/date-bis
	 */
	
	@GET
	@Path("date-bis")
	public Response getDateBis() {
		ResponseBuilder rb = Response.ok();
		rb.header("headerName", "headerValue")
			.type(MediaType.TEXT_PLAIN)
			.entity(LocalDateTime.now().toString());
		return rb.build();
	}
	

	/*
curl -i -X GET http://localhost:8080/RESTProjectBase/main/hello/date
	 */
	@GET
	@Path("date")
	public Date getDate() {
		return new Date();
	}
	
	/*
curl -i -X POST http://localhost:8080/RESTProjectBase/main/hello/name?value=gaby	
	 */
	@POST
	@Path("name")
	public Response creerHello(	@QueryParam("value") String value,
								@Context HttpHeaders headerParams,
								@Context UriInfo ui) {
		System.out.println("Creation ");
		System.out.println("header : " + headerParams.getRequestHeaders());
		UriBuilder ub	= ui.getAbsolutePathBuilder();
		URI uri			= ub.path(value).build();
		return Response.created(uri).build();
	}
	
	/*
curl -i -X GET http://localhost:8080/RESTProjectBase/main/hello/validation
curl -i -X GET "http://localhost:8080/RESTProjectBase/main/hello/validation?nom=Jeff&version=2"

curl -i -X GET http://localhost:8080/RESTProjectBase/main/hello/conneg
curl -i -H "accept:text/plain" -X GET http://localhost:8080/RESTProjectBase/main/hello/conneg
curl -i -H "accept:text/html" -X GET http://localhost:8080/RESTProjectBase/main/hello/conneg

curl -i -X POST -H "Content-Type: application/x-www-form-urlencoded" -d "login=user&password=pwd" http://localhost:8080/RESTProjectBase/main/login
curl -i -X POST -H "Content-Type: application/x-www-form-urlencoded" -d "login=user&password=rest" http://localhost:8080/RESTProjectBase/main/login
	 */
	
}