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

import java.net.URL;

import org.apache.http.HttpResponse;
import org.jdom.Document;

/** 
 * This class encapsulates an {@link HttpResponse} and the result of parsing 
 * its response body into an XML {@link Document}.
 */
public class XhtmlApplicationState {

	private URL context;
	private HttpResponse httpResponse;
	private Document document;
	
	public XhtmlApplicationState(URL context, HttpResponse resp, Document doc) {
		this.context = context;
		this.httpResponse = resp;
		this.document = doc;
	}
	
	/**
	 * Returns the URL that was accessed to return this response. Any
	 * relative links in the returned document should be interpreted
	 * relative to this context.
	 * @return {@link URL} request context
	 */
	public URL getContext() { return context; }

	/** 
	 * Returns the parsed response body. For invalid XML bodies,
	 * will return <code>null</code>.
	 * @return {@link Document} if response body was successfully
	 * parsed; <code>null</code> otherwise.
	 */
	public Document getDocument() { return document; }
	
	/** 
	 * Returns the consumed {@link HttpResponse} whose body we
	 * attempted to parse. Note that the body via {@link HttpResponse#getEntity()}
	 * will already have been consumed, but the headers and 
	 * {@link org.apache.http.StatusLine} are available.
	 * @return {@link HttpResponse}
	 */
	public HttpResponse getHttpResponse() { return httpResponse; }

	public boolean succeeded() {
		int status = httpResponse.getStatusLine().getStatusCode();
		return (status >= 200 && status <= 299);
	}

}
