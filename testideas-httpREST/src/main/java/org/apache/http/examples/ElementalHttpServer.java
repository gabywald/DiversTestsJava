/*
 * ==================================================================== 
 * Licensed to the Apache Software Foundation (ASF) under one 
 * or more contributor license agreements.  See the NOTICE file 
 * distributed with this work for additional information 
 * regarding copyright ownership.  The ASF licenses this file 
 * to you under the Apache License, Version 2.0 (the 
 * "License"); you may not use this file except in compliance 
 * with the License.  You may obtain a copy of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on an 
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY 
 * KIND, either express or implied.  See the License for the 
 * specific language governing permissions and limitations 
 * under the License. 
 * ==================================================================== 
 * 
 * This software consists of voluntary contributions made by many 
 * individuals on behalf of the Apache Software Foundation.  For more 
 * information on the Apache Software Foundation, please see 
 * <http://www.apache.org/>. 
 * 
 */

package org.apache.http.examples; 

import java.io.File; 
import java.io.IOException; 
import java.io.InterruptedIOException; 
import java.net.ServerSocket; 
import java.net.Socket; 
import java.net.URLDecoder; 
import java.nio.charset.Charset; 
import java.util.Locale; 

import org.apache.http.ConnectionClosedException; 
import org.apache.http.HttpEntity; 
import org.apache.http.HttpEntityEnclosingRequest; 
import org.apache.http.HttpException; 
import org.apache.http.HttpRequest; 
import org.apache.http.HttpResponse; 
import org.apache.http.HttpResponseInterceptor; 
import org.apache.http.HttpServerConnection; 
import org.apache.http.HttpStatus; 
import org.apache.http.MethodNotSupportedException; 
import org.apache.http.entity.ContentType; 
import org.apache.http.entity.FileEntity; 
import org.apache.http.entity.StringEntity; 
import org.apache.http.impl.DefaultBHttpServerConnection; 
import org.apache.http.protocol.BasicHttpContext; 
import org.apache.http.protocol.HttpContext; 
import org.apache.http.protocol.HttpProcessor; 
import org.apache.http.protocol.HttpRequestHandler; 
import org.apache.http.protocol.HttpService; 
import org.apache.http.protocol.ImmutableHttpProcessor; 
import org.apache.http.protocol.ResponseConnControl; 
import org.apache.http.protocol.ResponseContent; 
import org.apache.http.protocol.ResponseDate; 
import org.apache.http.protocol.ResponseServer; 
import org.apache.http.protocol.UriHttpRequestHandlerMapper; 
import org.apache.http.util.EntityUtils; 

/**
 * Basic, yet fully functional and spec compliant, HTTP/1.1 file server. 
 * <p> 
 * Please note the purpose of this application is demonstrate the usage of HttpCore APIs. 
 * It is NOT intended to demonstrate the most efficient way of building an HTTP file server. 
 * 
 * 
 */ 
public class ElementalHttpServer { 

	public static void main(String[] args) throws Exception { 
		if (args.length < 1) { 
			System.err.println("Please specify document root directory"); 
			System.exit(1); 
		} 
		Thread t = new RequestListenerThread(8080, args[0]); 
		t.setDaemon(false); 
		t.start(); 
	} 

	static class HttpFileHandler implements HttpRequestHandler  { 

		private final String docRoot; 

		public HttpFileHandler(final String docRoot) { 
			super(); 
			this.docRoot = docRoot; 
		} 

		public void handle( 
				final HttpRequest request, 
				final HttpResponse response, 
				final HttpContext context) throws HttpException, IOException { 

			String method = request.getRequestLine().getMethod().toUpperCase(Locale.ENGLISH); 
			if (!method.equals("GET") && !method.equals("HEAD") && !method.equals("POST")) { 
				throw new MethodNotSupportedException(method + " method not supported"); 
			} 
			String target = request.getRequestLine().getUri(); 

			if (request instanceof HttpEntityEnclosingRequest) { 
				HttpEntity entity = ((HttpEntityEnclosingRequest) request).getEntity(); 
				byte[] entityContent = EntityUtils.toByteArray(entity); 
				System.out.println("Incoming entity content (bytes): " + entityContent.length); 
			} 

			final File file = new File(this.docRoot, URLDecoder.decode(target, "UTF-8")); 
			if (!file.exists()) { 

				response.setStatusCode(HttpStatus.SC_NOT_FOUND); 
				StringEntity entity = new StringEntity( 
						"<html><body><h1>File" + file.getPath() + 
						" not found</h1></body></html>", 
						ContentType.create("text/html", "UTF-8")); 
				response.setEntity(entity); 
				System.out.println("File " + file.getPath() + " not found"); 

			} else if (!file.canRead() || file.isDirectory()) { 

				response.setStatusCode(HttpStatus.SC_FORBIDDEN); 
				StringEntity entity = new StringEntity( 
						"<html><body><h1>Access denied</h1></body></html>", 
						ContentType.create("text/html", "UTF-8")); 
				response.setEntity(entity); 
				System.out.println("Cannot read file " + file.getPath()); 

			} else { 

				response.setStatusCode(HttpStatus.SC_OK); 
				FileEntity body = new FileEntity(file, ContentType.create("text/html", (Charset) null)); 
				response.setEntity(body); 
				System.out.println("Serving file " + file.getPath()); 
			} 
		} 

	} 

	static class RequestListenerThread extends Thread { 

		private final ServerSocket serversocket; 
		private final HttpService httpService; 

		public RequestListenerThread(int port, final String docroot) throws IOException { 
			this.serversocket = new ServerSocket(port); 

			// Set up the HTTP protocol processor 
			HttpProcessor httpproc = new ImmutableHttpProcessor(new HttpResponseInterceptor[] { 
					new ResponseDate(), 
					new ResponseServer("Test/1.1"), 
					new ResponseContent(), 
					new ResponseConnControl() 
			}); 

			// Set up request handlers 
			UriHttpRequestHandlerMapper reqistry = new UriHttpRequestHandlerMapper(); 
			reqistry.register("*", new HttpFileHandler(docroot)); 

			// Set up the HTTP service 
			this.httpService = new HttpService(httpproc, reqistry); 
		}

		@Override 
		public void run() { 
			System.out.println("Listening on port " + this.serversocket.getLocalPort()); 
			while (!Thread.interrupted()) { 
				try { 
					// Set up HTTP connection 
					Socket socket = this.serversocket.accept(); 
					DefaultBHttpServerConnection conn = new DefaultBHttpServerConnection(8 * 1024); 
					System.out.println("Incoming connection from " + socket.getInetAddress()); 
					conn.bind(socket); 

					// Start worker thread 
					Thread t = new WorkerThread(this.httpService, conn); 
					t.setDaemon(true); 
					t.start(); 
				} catch (InterruptedIOException ex) { 
					break; 
				} catch (IOException e) { 
					System.err.println("I/O error initialising connection thread: " 
							+ e.getMessage()); 
					break; 
				} 
			} 
		} 
	} 

	static class WorkerThread extends Thread { 

		private final HttpService httpservice; 
		private final HttpServerConnection conn; 

		public WorkerThread( 
				final HttpService httpservice, 
				final HttpServerConnection conn) { 
			super(); 
			this.httpservice = httpservice; 
			this.conn = conn; 
		} 

		@Override 
		public void run() { 
			System.out.println("New connection thread"); 
			HttpContext context = new BasicHttpContext(null); 
			try { 
				while (!Thread.interrupted() && this.conn.isOpen()) { 
					this.httpservice.handleRequest(this.conn, context); 
				} 
			} catch (ConnectionClosedException ex) { 
				System.err.println("Client closed connection"); 
			} catch (IOException ex) { 
				System.err.println("I/O error: " + ex.getMessage()); 
			} catch (HttpException ex) { 
				System.err.println("Unrecoverable HTTP protocol violation: " + ex.getMessage()); 
			} finally { 
				try { 
					this.conn.shutdown(); 
				} catch (IOException ignore) {} 
			} 
		} 

	} 

}