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

import org.apache.http.HttpResponse;

/**
 * Exception that wraps a server response whose status code
 * does not reflect success (e.g. a 500 error).
 */
public class ServerErrorException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	private HttpResponse resp;
	public ServerErrorException() {
		super();
	}
	
	public ServerErrorException(HttpResponse resp) {
		super(resp.getStatusLine().toString());
		this.resp = resp;
	}

	/**  
	 * @return the server response indicating an error
	 */
	public HttpResponse getResponse() { return resp; }
}
