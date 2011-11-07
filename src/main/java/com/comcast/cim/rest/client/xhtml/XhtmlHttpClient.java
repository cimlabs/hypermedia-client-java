/*
   Copyright (C) 2011 Comcast Interactive Media, LLC ("Licensor").

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package com.comcast.cim.rest.client.xhtml;

import java.io.IOException;
import java.net.URL;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;

/**
 * Convenience wrapper around {@link HttpClient} that asks for and parses
 * XHTML responses from the server.
 */
public class XhtmlHttpClient {
	
	private static final String ACCEPT_HEADER = "application/xhtml+xml,*/*;q=0.9";
	
	private HttpClient httpClient;
	private XhtmlResponseHandlerFactory xhtmlResponseHandlerFactory;

	public XhtmlHttpClient(HttpClient hc, XhtmlResponseHandlerFactory xrhf) {
		this.httpClient = hc;
		this.xhtmlResponseHandlerFactory = xrhf;
	}

	/**
	 * Executes the given HTTP request and returns the next
	 * application state.
	 * @param req HTTP request to execute
	 * @return new application state
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public XhtmlApplicationState execute(HttpUriRequest req) 
			throws ClientProtocolException, IOException {
		req.setHeader("Accept",ACCEPT_HEADER);
		URL context = new URL(req.getURI().toString());
		XhtmlResponseHandler rh = xhtmlResponseHandlerFactory.get(context);
		XhtmlApplicationState state = httpClient.execute(req, rh);
		return state;
	}

}
