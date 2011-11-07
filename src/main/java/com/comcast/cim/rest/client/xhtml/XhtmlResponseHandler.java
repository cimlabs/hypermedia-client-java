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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;


/**
 * This class is used to attempt parsing HTTP response bodies 
 * as XML documents.
 */
public class XhtmlResponseHandler implements ResponseHandler<XhtmlApplicationState> {
	
	private static final Log defaultLog = 
		LogFactory.getLog(XhtmlResponseHandler.class);
	private Log logger = defaultLog;
	
	private static final String VALIDATION_FEATURE = 
		"http://xml.org/sax/features/validation";
	private static final String LOAD_EXTERNAL_DTD_FEATURE = 
		"http://apache.org/xml/features/nonvalidating/load-external-dtd";
	
	private URL context;
	
	public XhtmlResponseHandler(URL context) {
		this.context = context;
	}
	
	public XhtmlApplicationState handleResponse(HttpResponse resp) 
		throws ClientProtocolException, IOException {
		SAXBuilder builder = getBuilder();
		
		HttpEntity entity = null;
		try {
			entity = resp.getEntity();
			Document doc = null;
			if (entity != null) { 
				doc = builder.build(entity.getContent());
				entity.consumeContent();
			}
			return new XhtmlApplicationState(context, resp, doc);
		} catch (JDOMException e) {
			logger.warn("unparseable XML response",e);
			entity.consumeContent();
			return new XhtmlApplicationState(context, resp, null);
		}
	}

	protected static SAXBuilder getBuilder() {
		SAXBuilder builder = new SAXBuilder();
		builder.setFeature(VALIDATION_FEATURE, false);
		builder.setFeature(LOAD_EXTERNAL_DTD_FEATURE, false);
		return builder;
	}
	
	protected void setLogger(Log logger) {
		this.logger = logger;
	}

	protected URL getContext() {
		return context;
	}
}