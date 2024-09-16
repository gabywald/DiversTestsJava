package gabywald.rest.httpclient;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
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

/**
 * 
 * @author gchandesris (20200220)
 */
public class HttpConnection {
	public final static String USER_AGENT = "Mozilla/5.0";

	private boolean isGet					= false;
	private String url						= null;
	private Map<String, String> reqProps	= new HashMap<String, String>();

	private String response	= null;
	private String error	= null;
	
	private HttpURLConnection connection = null;

	public HttpConnection(boolean isGet, String url) {
		this.isGet	= isGet;
		this.url	= url;
	}

	/**
	 * See HttpHeaders constants
	 * @param key (String)
	 * @param value (String)
	 * @return (String)
	 */
	public String setRequestProperty(String key, String value) {
		return this.reqProps.put(key, value);
	}

	/**
	 * See HttpHeaders constants
	 * @param key (String)
	 * @return (String)
	 */
	public String getRequestProperty(String key) {
		return this.reqProps.get(key);
	}
	
	/**
	 * See HttpHeaders constants
	 * @param key (String)
	 * @return (List&lt;String&gt;)
	 */
	public List<String> getResponseProperty(String key) {
		return this.connection.getHeaderFields().get(key);
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

				URL urlObj		= new URL( this.url + "?" + postData.toString() );
				this.connection	= (HttpURLConnection) urlObj.openConnection();

				this.connection.setRequestMethod( requestMethod );

				toReturn = this.showOutputFrom(this.connection);

			} else {
				// ***** POST *****

				URL urlObj		= new URL( this.url );
				this.connection	= (HttpURLConnection) urlObj.openConnection();

				this.connection.setRequestMethod( requestMethod );

				for (String propKey : this.reqProps.keySet()) 
				{ this.connection.setRequestProperty( propKey, this.reqProps.get(propKey) ); }

				this.connection.setUseCaches(false);
				this.connection.setDoInput(true);
				this.connection.setDoOutput(true);
				
				OutputStream outStream = this.connection.getOutputStream();
				OutputStreamWriter outStreamWriter = new OutputStreamWriter(outStream, "UTF-8");
				outStreamWriter.write(postData.toString());
				outStreamWriter.flush();
				outStreamWriter.close();
				outStream.close();
				
				Logger.printlnLog(LoggerLevel.LL_DEBUG, "postData.toString(): {" + postData.toString() + "}");

				//				DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
				//				wr.writeBytes(urlParameters);
				//				wr.flush();
				//				wr.close();

				try( DataOutputStream wr = new DataOutputStream( this.connection.getOutputStream())) 
				{ wr.write( postDataBytes ); }

				toReturn = this.showOutputFrom(this.connection);

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

			InputStream iStream = null;
			try {
				iStream = connection.getErrorStream();
			} catch(NullPointerException npe) {
				Logger.printlnLog(LoggerLevel.LL_WARNING, "ErrorStream Null" );
				iStream = connection.getInputStream();
			}
			// NOTE : connection.getContentLength() == 0 !?
			if (iStream != null) {
				this.error = new String("");
				BufferedReader in	= new BufferedReader(new InputStreamReader(iStream));
				String inputLine	= null;
				// StringBuffer response = new StringBuffer();
				while ((inputLine = in.readLine()) != null) {
					Logger.printlnLog(LoggerLevel.LL_WARNING, inputLine );
					this.error += inputLine;
				}
				in.close();
			} else {
				Logger.printlnLog(LoggerLevel.LL_WARNING, "(responseCode != HttpStatus.SC_OK): no Stream !" );
			}
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
			connection.disconnect();
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
		connection.disconnect();

		System.out.println( "." );

		return responseCode;
	}

	public static String getFullResponse(HttpConnection conBase) throws IOException{
		
		HttpURLConnection con = conBase.connection;
		
		StringBuilder fullResponseBuilder = new StringBuilder();

		// read status and message
		fullResponseBuilder.append(con.getResponseCode())
		.append(" ")
		.append(con.getResponseMessage())
		.append("\n");

		// read headers
		con.getHeaderFields().entrySet().stream()
		.filter(entry -> entry.getKey() != null)
		.forEach(entry -> {
			fullResponseBuilder.append(entry.getKey()).append(": ");
			List<String> headerValues = entry.getValue();
			Iterator<String> it = headerValues.iterator();
			if (it.hasNext()) {
				fullResponseBuilder.append(it.next());
				while (it.hasNext()) {
					fullResponseBuilder.append(", ").append(it.next());
				}
			}
			fullResponseBuilder.append("\n");
		});

		// read response content
		int status = con.getResponseCode();
		InputStreamReader streamReader = null;
		if (status > 299) {
			streamReader = new InputStreamReader(con.getErrorStream());
		} else {
			streamReader = new InputStreamReader(con.getInputStream());
		}
		
		try {
			BufferedReader in = new BufferedReader(streamReader);
			String inputLine;
			StringBuffer content = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				content.append(inputLine);
			}
			in.close();
			fullResponseBuilder.append(content.toString());
		} catch (IOException e) {
			// e.printStackTrace();
		}


		return fullResponseBuilder.toString();
	}

	public String getResponse() { 
		return this.response;
	}

	public String getError() { 
		return this.error;
	}

}
