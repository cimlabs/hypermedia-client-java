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
import java.net.MalformedURLException;
import java.util.Map;

import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpUriRequest;
import org.jdom.Element;
import org.jdom.JDOMException;

public class XhtmlNavigator {

	private XhtmlParser parser;
	private RequestBuilder builder;
	private XhtmlHttpClient client;
	
	public XhtmlNavigator(XhtmlParser xp, RequestBuilder rb, XhtmlHttpClient xhc) {
		this.parser = xp;
		this.builder = rb;
		this.client = xhc;
	}

	public XhtmlApplicationState followLink(XhtmlApplicationState state, String rel)
	    throws JDOMException, ClientProtocolException, IOException {
		Element a = parser.getLinkWithRelation(state.getDocument(), rel);
		return traverseAnchor(state, rel, a);
	}

	public XhtmlApplicationState followLink(XhtmlApplicationState state, Element root,
			String rel) throws JDOMException, ClientProtocolException,
			IOException {
		Element a = parser.getLinkWithRelation(root, rel);
		return traverseAnchor(state, rel, a);
	
	}
	private XhtmlApplicationState traverseAnchor(XhtmlApplicationState state,
			String rel, Element a)
	    throws MalformedURLException, ClientProtocolException, IOException {
		if (a == null) {
			throw new RelationNotFoundException("no link with relation \"" + rel + "\" found");
		}
		HttpUriRequest req = builder.followLink(a, state.getContext());
		XhtmlApplicationState result = client.execute(req);
		if (!result.succeeded()) {
			throw new ServerErrorException(result.getHttpResponse());
		}
		return result;
	}

	public XhtmlApplicationState submitForm(XhtmlApplicationState state, String formName,
			Map<String, String> args)
	    throws JDOMException, ParseException, IOException {
		Element form = parser.getFormWithName(state.getDocument(), formName);
		if (form == null) {
			throw new RelationNotFoundException("no form with name \"" + formName + "\" found");
		}
		HttpUriRequest req = builder.submitForm(form, state.getContext(), args);
		XhtmlApplicationState result = client.execute(req);
		if (!result.succeeded()) {
			throw new ServerErrorException(result.getHttpResponse());
		}
		return result;
	}


}
