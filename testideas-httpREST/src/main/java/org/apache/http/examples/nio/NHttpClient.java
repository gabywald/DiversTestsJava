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
package org.apache.http.examples.nio;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.concurrent.CountDownLatch;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.impl.nio.DefaultHttpClientIODispatch;
import org.apache.http.impl.nio.pool.BasicNIOConnPool;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.nio.protocol.BasicAsyncRequestProducer;
import org.apache.http.nio.protocol.BasicAsyncResponseConsumer;
import org.apache.http.nio.protocol.HttpAsyncRequestExecutor;
import org.apache.http.nio.protocol.HttpAsyncRequester;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.nio.reactor.IOEventDispatch;
import org.apache.http.protocol.HttpCoreContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpProcessorBuilder;
import org.apache.http.protocol.RequestConnControl;
import org.apache.http.protocol.RequestContent;
import org.apache.http.protocol.RequestExpectContinue;
import org.apache.http.protocol.RequestTargetHost;
import org.apache.http.protocol.RequestUserAgent;

/**
 * Minimal asynchronous HTTP/1.1 client.
 * <p>
 * Please note that this example represents a minimal HTTP client implementation.
 * It does not support HTTPS as is.
 * You either need to provide BasicNIOConnPool with a connection factory
 * that supports SSL or use a more complex HttpAsyncClient.
 *
 * @see BasicNIOConnPool#BasicNIOConnPool(org.apache.http.nio.reactor.ConnectingIOReactor,
 *   org.apache.http.nio.pool.NIOConnFactory, int)
 * @see org.apache.http.impl.nio.pool.BasicNIOConnFactory
 */
public class NHttpClient {

	public static void main(final String[] args) throws Exception {
		// Create HTTP protocol processing chain
		final HttpProcessor httpproc = HttpProcessorBuilder.create()
				// Use standard client-side protocol interceptors
				.add(new RequestContent())
				.add(new RequestTargetHost())
				.add(new RequestConnControl())
				.add(new RequestUserAgent("Test/1.1"))
				.add(new RequestExpectContinue(true)).build();
		// Create client-side HTTP protocol handler
		final HttpAsyncRequestExecutor protocolHandler = new HttpAsyncRequestExecutor();
		// Create client-side I/O event dispatch
		final IOEventDispatch ioEventDispatch = new DefaultHttpClientIODispatch(protocolHandler,
				ConnectionConfig.DEFAULT);
		// Create client-side I/O reactor
		final ConnectingIOReactor ioReactor = new DefaultConnectingIOReactor();
		// Create HTTP connection pool
		final BasicNIOConnPool pool = new BasicNIOConnPool(ioReactor, ConnectionConfig.DEFAULT);
		// Limit total number of connections to just two
		pool.setDefaultMaxPerRoute(2);
		pool.setMaxTotal(2);
		// Run the I/O reactor in a separate thread
		final Thread t = new Thread(new Runnable() {

			public void run() {
				try {
					// Ready to go!
					ioReactor.execute(ioEventDispatch);
				} catch (final InterruptedIOException ex) {
					System.err.println("Interrupted");
				} catch (final IOException e) {
					System.err.println("I/O error: " + e.getMessage());
				}
				System.out.println("Shutdown");
			}

		});
		// Start the client thread
		t.start();
		// Create HTTP requester
		final HttpAsyncRequester requester = new HttpAsyncRequester(httpproc);
		// Execute HTTP GETs to the following hosts and
		final HttpHost[] targets = new HttpHost[] {
				new HttpHost("www.apache.org", 80, "http"),
				new HttpHost("www.verisign.com", 443, "https"),
				new HttpHost("www.google.com", 80, "http")
		};
		final CountDownLatch latch = new CountDownLatch(targets.length);
		for (final HttpHost target: targets) {
			final BasicHttpRequest request = new BasicHttpRequest("GET", "/");
			final HttpCoreContext coreContext = HttpCoreContext.create();
			requester.execute(
					new BasicAsyncRequestProducer(target, request),
					new BasicAsyncResponseConsumer(),
					pool,
					coreContext,
					// Handle HTTP response from a callback
					new FutureCallback<HttpResponse>() {

						public void completed(final HttpResponse response) {
							latch.countDown();
							System.out.println(target + "->" + response.getStatusLine());
						}

						public void failed(final Exception ex) {
							latch.countDown();
							System.out.println(target + "->" + ex);
						}

						public void cancelled() {
							latch.countDown();
							System.out.println(target + " cancelled");
						}

					});
		}
		latch.await();
		System.out.println("Shutting down I/O reactor");
		ioReactor.shutdown();
		System.out.println("Done");
	}

}
