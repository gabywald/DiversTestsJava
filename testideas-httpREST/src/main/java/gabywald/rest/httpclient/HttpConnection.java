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

import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;

import gabywald.utilities.logger.Logger;
import gabywald.utilities.logger.Logger.LoggerLevel;

public class HttpConnection {
	public final static String USER_AGENT = "Mozilla/5.0";
	
	private boolean isGet					= false;
	private String url						= null;
	private Map<String, String> reqProps	= new HashMap<String, String>();
	
	private String response	= null;
	private String error	= null;
	
	public HttpConnection(boolean isGet, String url) {
		this.isGet	= isGet;
		this.url	= url;
	}
	
	/**
	 * see HttpHeaders constants
	 */
	public String setRequestProperty(String key, String value) {
		return this.reqProps.put(key, value);
	}
	
	/**
	 * see HttpHeaders constants
	 */
	public String getRequestProperty(String key) {
		return this.reqProps.get(key);
	}
	
	public String setContentType(String type) {
		return this.setRequestProperty(HttpHeaders.CONTENT_TYPE, type);
	}
	
	public String getContentType() {
		return this.getRequestProperty(HttpHeaders.CONTENT_TYPE);
	}
	
	public String setUserAgent(String type) {
		return this.setRequestProperty(HttpHeaders.USER_AGENT, type);
	}
	
	public String getUserAgent() {
		return this.getRequestProperty(HttpHeaders.USER_AGENT);
	}

	public boolean isGet() {
		return this.isGet;
	}

	public String getUrl() {
		return this.url;
	}
	
	public int sendRequest(String data, Map<String, String> params) {
		
		if (this.url == null) { return -1; }
		
		int toReturn  = -1;
		
		// Map<String, String> params = new HashMap<String, String>();
		// params.put(key, value);
		// params.put("Accept", "*/*");
		
		try {
			StringBuilder postData = new StringBuilder();
			if (params != null) {
				for (Map.Entry<String, String> param : params.entrySet()) {
					if (postData.length() != 0) {
						postData.append('&');
					}
					postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
					postData.append('=');
					postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
				} // END "for (Map.Entry<String, String> param : params.entrySet())"
			} // END "if (params != null)"
			else if (data != null) {
				postData.append(data);
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
				
				for (String propKey : this.reqProps.keySet()) 
					{ connection.setRequestProperty( propKey, this.reqProps.get(propKey) ); }
				
				connection.setUseCaches(false);
				connection.setDoInput(true);
				connection.setDoOutput(true);
				
//				DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
//				wr.writeBytes(urlParameters);
//				wr.flush();
//				wr.close();
				
				try( DataOutputStream wr = new DataOutputStream( connection.getOutputStream())) 
					{ wr.write( postDataBytes ); }
				
				toReturn = this.showOutputFrom(connection);
				
			} // END "if (this.isGet)"
			
		} catch (MalformedURLException e) {
			Logger.printlnLog(LoggerLevel.LL_ERROR, "MalformedURLException: {" + e.getMessage() + "}");
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			Logger.printlnLog(LoggerLevel.LL_ERROR, "UnsupportedEncodingException: {" + e.getMessage() + "}");
			e.printStackTrace();
		} catch (IOException e) {
			Logger.printlnLog(LoggerLevel.LL_ERROR, "IOException: {" + e.getMessage() + "}");
			e.printStackTrace();
		}
		
		return toReturn;
	}
	
	private int showOutputFrom(HttpURLConnection connection) throws IOException {
		Logger.printlnLog(LoggerLevel.LL_DEBUG, "getContentLength: [" + connection.getContentLength() + "]" );
		
		int responseCode	= connection.getResponseCode();
		String outMessage	= connection.getResponseMessage();
		
		Logger.printlnLog(LoggerLevel.LL_DEBUG, "responseCode: [" + responseCode + "]" );
		Logger.printlnLog(LoggerLevel.LL_DEBUG, "outMessage--: {" + outMessage + "}" );
		
		// TODO make better HTTP codes recognition !
		if (responseCode != HttpStatus.SC_OK) {
			Logger.printlnLog(LoggerLevel.LL_WARNING, "responseCode: [" + responseCode + "]" );
			Logger.printlnLog(LoggerLevel.LL_WARNING, "outMessage--: {" + outMessage + "}" );
			
			this.error = new String("");
			BufferedReader in	= new BufferedReader(new InputStreamReader(connection.getErrorStream()));
			String inputLine	= null;
			// StringBuffer response = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				Logger.printlnLog(LoggerLevel.LL_WARNING, inputLine );
				this.error += inputLine;
			}
			in.close();
		} // END "if (responseCode != HttpStatus.SC_OK)"
		
		Map<String, List<String> > responseHeaders	= connection.getHeaderFields();
		Iterator<String> iteOnMapKeys				= responseHeaders.keySet().iterator();
		while (iteOnMapKeys.hasNext()) {
			String currentKey = iteOnMapKeys.next();
			Logger.printlnLog(LoggerLevel.LL_DEBUG, "\t KEY: {" + currentKey + "}" );
			List<String> values = responseHeaders.get( currentKey );
			for (int i = 0 ; i < values.size() ; i++) {
				Logger.printlnLog(LoggerLevel.LL_DEBUG, "\t\t (" + i + "): {" + values.get( i ) + "}" );
			}
		} // END "while (iteOnMapKeys.hasNext())"
		
		// TODO make better HTTP codes recognition !
		if (responseCode != HttpStatus.SC_OK) {
			return responseCode;
		}
		
		// ***** result *****
		this.response = new String("");
		BufferedReader in	= new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String inputLine	= null;
		// StringBuffer response = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			Logger.printlnLog(LoggerLevel.LL_DEBUG, inputLine );
			this.response += inputLine;
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
