package gabywald.jersey.commonresources;

import java.util.StringJoiner;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
@Produces(MediaType.APPLICATION_JSON)
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {
	@Override
	public Response toResponse(final ConstraintViolationException exception) {
		return Response.status(Response.Status.BAD_REQUEST)
							.type(MediaType.APPLICATION_JSON)
							.entity(creerMessage(exception)).build();
	}
	
	private String creerMessage(ConstraintViolationException exception) {
		StringBuilder msg	= new StringBuilder("{ message: \"Violation of validation constraints\", violations=");
		StringJoiner sj		= new StringJoiner(", ", "[", "]");
		for (ConstraintViolation<?> cv : exception.getConstraintViolations()) {
			sj.add(cv.getPropertyPath().toString() + ": \"" + cv.getMessage() + "\"");
		}
		msg.append(sj.toString());
		msg.append("}");
		return msg.toString();
	}
}