package gabywald.jersey.commonresources;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

@Produces(MediaType.TEXT_PLAIN)
@Provider
public class DateWriter implements MessageBodyWriter<Date> {
	public boolean isWriteable(	Class<?> type, Type genericType,
								Annotation[] annotations, MediaType mediaType) {
		return Date.class.isAssignableFrom(type);
	}
	public long getSize(	Date t, Class<?> type, Type genericType,
							Annotation[] annotations, MediaType mediaType) {
		return 19;
	}
	public void writeTo(Date t, Class<?> type, Type genericType,
						Annotation[] annotations, 
						MediaType mediaType,
						MultivaluedMap<String, Object> httpHeaders, 
						OutputStream entityStream)
					throws IOException, WebApplicationException {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd:hh.mm.ss");
		entityStream.write(df.format(t).getBytes());
	}
}
