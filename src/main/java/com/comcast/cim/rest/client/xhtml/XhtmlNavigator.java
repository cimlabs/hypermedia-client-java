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

/** Convenience class for moving from one application state to another
 * by following a link or submitting a form.
 */
public class XhtmlNavigator {

	private XhtmlParser parser;
	private RequestBuilder builder;
	private XhtmlHttpClient client;
	
	public XhtmlNavigator(XhtmlParser xp, RequestBuilder rb, XhtmlHttpClient xhc) {
		this.parser = xp;
		this.builder = rb;
		this.client = xhc;
	}

	/**
	 * Follow an &lt;a&gt; tag with the given link relation.
	 * @param state current application state
	 * @param rel link relation that must appear in the @rel
	 *   attribute of a link
	 * @return next application state
	 * @throws JDOMException
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public XhtmlApplicationState followLink(XhtmlApplicationState state, String rel)
	    throws JDOMException, ClientProtocolException, IOException {
		Element a = parser.getLinkWithRelation(state.getDocument(), rel);
		return traverseAnchor(state, rel, a);
	}

	/**
	 * Follow an &lt;a&gt; tag with the given link relation, starting
	 * the search from a given context in a parsed document.
	 * @param state current application state
	 * @param root element within the application state at which the
	 *   search for the link should begin
	 * @param rel link relation that must appear in the @rel
	 *   attribute of a link
	 * @return next application state
	 * @throws JDOMException
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
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

	/**
	 * Submits a form with the given @name, using the provided arguments.
	 * @param state current application state
	 * @param formName name of the form to submit
	 * @param args a map of input names to values to provide for those
	 *   inputs when submitting the form
	 * @return next application state
	 * @throws JDOMException
	 * @throws ParseException
	 * @throws IOException
	 */
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
