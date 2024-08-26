package gabywald.jersey.commonresources;

import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;

@Path("/asyncres")
public class AsyncRessource {
	private final static Logger LOGGER = Logger.getLogger(AsyncRessource.class.getName());
	
	@Resource
	ManagedExecutorService executorService;
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public void asyncGet(@Suspended final AsyncResponse asyncResponse) {
		AsyncRessource.LOGGER.info("BEGIN asyncGet");
		this.executorService.execute(() -> {
			String result = this.executerTraitement();
			asyncResponse.resume(result);
		});
		AsyncRessource.LOGGER.info("END-- asyncGet");
	}
	
	private String executerTraitement() {
		try {
			Thread.sleep(10_000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return "Succès !";
	}
}
