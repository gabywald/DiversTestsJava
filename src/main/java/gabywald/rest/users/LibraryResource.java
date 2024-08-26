package gabywald.rest.users;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

/**
 * 
 * @author Gabriel Chandesris (2024)
 */
@Path("/library")
public class LibraryResource {
    
    @Context
    SecurityContext securityContext;
    
    @RolesAllowed("LIBRARIAN")
    @POST
    @Path("/addBook")
    public Response addBook(@HeaderParam("book") String newBook) {
        
        // Authorization check passed, a librarian can add a book
        // Add book logic here
        
        return Response.ok("NEW BOOK: {" + newBook + "}").build();
        // return Response.status(Status.NOT_IMPLEMENTED).build();
    }
    
    @RolesAllowed("MEMBER")
    @GET
    @Path("/viewBooks")
    public Response viewBooks() {
        // Authorization check passed, a member can view books
        // Return book list
        
        return Response.ok("VIEW BOOKS").build();
        // return Response.status(Status.NOT_IMPLEMENTED).build();
    }
    
    @PermitAll // @DenyAll
    @GET
    @Path("/book/{isbn}")
    public Response findBook(@PathParam("isbn") String isbn) {
        // Authorization check passed, a member can view books
        // FIND book 
        
        return Response.ok("FIND BOOK: {" + isbn + "}").build();
        // return Response.status(Status.NOT_IMPLEMENTED).build();
    }
    
}
