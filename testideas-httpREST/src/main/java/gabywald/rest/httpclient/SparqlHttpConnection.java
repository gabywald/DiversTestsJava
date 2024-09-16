package gabywald.rest.httpclient;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

//import gabywald.utilities.logger.Logger;
//import gabywald.utilities.logger.Logger.LoggerLevel;

/**
 * 
 * @author Gabriel Chandesris (20180217+)
 * see : https://www.mkyong.com/java/how-to-send-http-request-getpost-in-java/
 * see : http://hc.apache.org/
 * see : http://www.mkyong.com/java/apache-httpclient-examples/
 * 
 * https://stackoverflow.com/questions/4205980/java-sending-http-parameters-via-post-method-easily
 */
public class SparqlHttpConnection {
	
	private final static String USER_AGENT = "Mozilla/5.0";
	
	private boolean isGet	= false;
	private String url		= null;
	
	private String response	= null;
	private String error	= null;
	
	public SparqlHttpConnection(boolean isGet, String url) {
		this.isGet	= isGet;
		this.url	= url;
	}
	
	public static String getUserAgent() {
		return SparqlHttpConnection.USER_AGENT;
	}

	public boolean isGet() {
		return this.isGet;
	}

	public String getUrl() {
		return this.url;
	}

/*
String urlParameters  = "param1=a&param2=b&param3=c";
byte[] postData       = urlParameters.getBytes( StandardCharsets.UTF_8 );
int    postDataLength = postData.length;
String request        = "http://example.com/index.php";
URL    url            = new URL( request );
HttpURLConnection conn= (HttpURLConnection) url.openConnection();           
conn.setDoOutput( true );
conn.setInstanceFollowRedirects( false );
conn.setRequestMethod( "POST" );
conn.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded"); 
conn.setRequestProperty( "charset", "utf-8");
conn.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
conn.setUseCaches( false );
try( DataOutputStream wr = new DataOutputStream( conn.getOutputStream())) {
   wr.write( postData );
}
 */
	
/*
<option value="auto" >Auto</option>
<option value="text/html" selected="selected">HTML</option>
<option value="text/x-html+tr" >HTML (Basic Browsing Links)</option>
<option value="application/vnd.ms-excel" >Spreadsheet</option>
<option value="application/sparql-results+xml" >XML</option>
<option value="application/sparql-results+json" >JSON</option>
<option value="application/javascript" >Javascript</option>
<option value="text/turtle" >Turtle</option>
<option value="application/rdf+xml" >RDF/XML</option>
<option value="text/plain" >N-Triples</option>
<option value="text/csv" >CSV</option>
<option value="text/tab-separated-values" >TSV</option>
 */

	public int sendRequest(String sparql) {
		
		if (this.url == null) { return -1; }
		
		int toReturn  = -1;
		
		Map<String, String> params = new HashMap<String, String>();
		// params.put(key, value);
		params.put("format", "application/sparql-results+json");
		params.put("output-format", "application/sparql-results+json");
		params.put("Accept", "*/*");
		params.put("charset", "utf-8");
		params.put("default-graph-uri", "");
		params.put("timeout", "0");
		params.put("debug", "checked");
		params.put("log_debug_info", "");
		params.put("query", sparql);
		
		try {

			StringBuilder postData = new StringBuilder();
			for (Map.Entry<String, String> param : params.entrySet()) {
				if (postData.length() != 0) {
					postData.append('&');
				}
				postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
				postData.append('=');
				postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
			}
			byte[] postDataBytes = postData.toString().getBytes("UTF-8");
			
			String requestMethod = (this.isGet?"GET":"POST");
			
			if (this.isGet) {
				// ***** GET  *****
				// System.out.println( this.url + "?" + postData.toString() + "\n" + "*******************");
				
				URL urlObj						= new URL( this.url + "?" + postData.toString() );
				HttpURLConnection connection	= (HttpURLConnection) urlObj.openConnection();
				
				connection.setRequestMethod( requestMethod );
				
				toReturn = this.showOutputFrom(connection);
				
			} else {
				// ***** POST *****
				
				URL urlObj						= new URL( this.url );
				HttpURLConnection connection	= (HttpURLConnection) urlObj.openConnection();
				
				connection.setRequestMethod( requestMethod );
				
				connection.setRequestProperty("User-Agent", SparqlHttpConnection.USER_AGENT);
				
				connection.setRequestProperty( "Content-Language", "en-US" );  
				
				connection.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded" );

				connection.setRequestProperty( "Content-Length", String.valueOf( postDataBytes.length ) );
				
				connection.setUseCaches(false);
				connection.setDoInput(true);
				connection.setDoOutput(true);
				
//				DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
//				wr.writeBytes(urlParameters);
//				wr.flush();
//				wr.close();
				
				try( DataOutputStream wr = new DataOutputStream( connection.getOutputStream())) 
				{
					wr.write( postDataBytes );
				}
				
				toReturn = this.showOutputFrom(connection);
				
			} // END "if (this.isGet)"
			
		} catch (MalformedURLException e) {
			// Logger.printlnLog(LoggerLevel.LL_ERROR, "MalformedURLException: {" + e.getMessage() + "}");
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// Logger.printlnLog(LoggerLevel.LL_ERROR, "UnsupportedEncodingException: {" + e.getMessage() + "}");
			e.printStackTrace();
		} catch (IOException e) {
			// Logger.printlnLog(LoggerLevel.LL_ERROR, "IOException: {" + e.getMessage() + "}");
			e.printStackTrace();
		}
		
		return toReturn;
	}
	
	private int showOutputFrom(HttpURLConnection connection) throws IOException {
		// Logger.printlnLog(LoggerLevel.LL_DEBUG, "getContentLength: [" + connection.getContentLength() + "]" );
		
		int responseCode	= connection.getResponseCode();
		String outMessage	= connection.getResponseMessage();
		
		// Logger.printlnLog(LoggerLevel.LL_DEBUG, "responseCode: [" + responseCode + "]" );
		// Logger.printlnLog(LoggerLevel.LL_DEBUG, "outMessage--: {" + outMessage + "}" );
		
		// TODO make better HTTP codes recognition !
		if (responseCode != 200) {
			// Logger.printlnLog(LoggerLevel.LL_WARNING, "responseCode: [" + responseCode + "]" );
			// Logger.printlnLog(LoggerLevel.LL_WARNING, "outMessage--: {" + outMessage + "}" );
			
			this.error = new String("");
			BufferedReader in	= new BufferedReader(new InputStreamReader(connection.getErrorStream()));
			String inputLine	= null;
			// StringBuffer response = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				// Logger.printlnLog(LoggerLevel.LL_WARNING, inputLine );
				this.error += inputLine;
			}
			in.close();
		} // END "if (responseCode != 200)"
		
		Map<String, List<String> > responseHeaders	= connection.getHeaderFields();
		Iterator<String> iteOnMapKeys				= responseHeaders.keySet().iterator();
		while (iteOnMapKeys.hasNext()) {
			String currentKey = iteOnMapKeys.next();
			// Logger.printlnLog(LoggerLevel.LL_DEBUG, "\t KEY: {" + currentKey + "}" );
			List<String> values = responseHeaders.get( currentKey );
			for (int i = 0 ; i < values.size() ; i++) {
				// Logger.printlnLog(LoggerLevel.LL_DEBUG, "\t\t (" + i + "): {" + values.get( i ) + "}" );
			}
		} // END "while (iteOnMapKeys.hasNext())"
		
		// TODO make better HTTP codes recognition !
		if (responseCode != 200) {
			return responseCode;
		}
		
		// ***** result *****
		this.response = new String("");
		BufferedReader in	= new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String inputLine	= null;
		// StringBuffer response = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			// Logger.printlnLog(LoggerLevel.LL_DEBUG, inputLine );
			this.response += inputLine;
			// TODO JProgressBar
			System.out.print( "." );
		}
		in.close();
		
		System.out.println( "." );
		
		return responseCode;
	}
	
	public String getResponse() { 
		return this.response;
	}
	
	public String getError() { 
		return this.error;
	}
	
}
