package gabywald.rest.httpclient;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * A simple Java REST GET example using the Apache HTTP library.
 * This executes a call against the Yahoo Weather API service, which is
 * actually an RSS service (http://developer.yahoo.com/weather/).
 * 
 * Try this Twitter API URL for another example (it returns JSON results):
 * http://search.twitter.com/search.json?q=%40apple
 * (see this url for more twitter info: https://dev.twitter.com/docs/using-search)
 * 
 * Apache HttpClient: http://hc.apache.org/httpclient-3.x/
 *
 */
public class ApacheHttpRestClient1v2 {

	public static void main(String[] args) {
		// https://news.google.com/?hl=fr&gl=FR&ceid=FR:fr
		CloseableHttpClient httpclient		= HttpClients.createDefault();
		CloseableHttpResponse httpResponse	= null;

		// CoreConnectionPNames.
		// ContentType.
		
		try {
//			URI uri							= new URIBuilder()
//					.setScheme("http")
//					.setHost("news.google.com")
//					.setPath("/")
//					.setPort(80)
//					.setParameter("hl", "fr")
//					.setParameter("gl", "FR")
//					.setParameter("ceid", "FR:fr")
//					.build();
			
			URI uri							= new URIBuilder()
					.setScheme("http")
					.setHost("gabriel.chandesris.free.fr")
					.setPath("/index.php")
					.setPort(80)
					.build();
			
			HttpGet httpget = new HttpGet(uri);
			System.out.println(httpget.getURI());

			// HttpGet httpget					= new HttpGet("http://localhost/");
			httpResponse		= httpclient.execute(httpget);
			HttpEntity entity	= httpResponse.getEntity();
			
			System.out.println("----------------------------------------");
			System.out.println(httpResponse.getStatusLine());
			Header[] headers = httpResponse.getAllHeaders();
			for (int i = 0; i < headers.length; i++) {
				System.out.println(headers[i]);
			}
			System.out.println("----------------------------------------");

			if (entity != null) {
				System.out.println(EntityUtils.toString(entity));
			}

			// System.out.println("executing request to " + uri);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (httpResponse != null) { 
				try {
					httpResponse.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}
}
