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
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;


public class RequestBuilder {

	public HttpUriRequest followLink(Element a, URL context) throws MalformedURLException {
		if (!"a".equalsIgnoreCase(a.getName())) {
			throw new IllegalArgumentException("can only follow links on <a> tags");
		}
		String href = a.getAttributeValue("href");
		if (href == null) {
			throw new IllegalArgumentException("<a> tag had no @href attribute");
		}
		URL derived = new URL(context, href);
		return new HttpGet(derived.toString());
	}

	public HttpUriRequest submitForm(Element form, URL context, Map<String, String> args)
		throws JDOMException, ParseException, IOException {
		String formMethod = form.getAttributeValue("method");
		String formAction = form.getAttributeValue("action");
		URL derived = new URL(context, formAction);
		
		UrlEncodedFormEntity e = marshalArguments(form, args);
		
		if ("GET".equalsIgnoreCase(formMethod)) {
			String reqUri = null;
			if (e != null) {
				String queryParams = EntityUtils.toString(e);
				reqUri = String.format("%s?%s", derived.toString(), queryParams);
			} else {
				reqUri = derived.toString();
			}
			return new HttpGet(reqUri);
		} else if ("POST".equalsIgnoreCase(formMethod)) {
			HttpPost post = new HttpPost(derived.toString());
			post.setEntity(e);
			return post;
		}
		throw new IllegalArgumentException("form method must be GET or POST");
	}

	protected UrlEncodedFormEntity marshalArguments(Element form,
			Map<String, String> args) throws JDOMException,
			UnsupportedEncodingException {
		List<NameValuePair> keyvals = new ArrayList<NameValuePair>();
		marshalInputArguments(form, args, keyvals);
		marshalSelectArguments(form, args, keyvals);
		return (keyvals.size() > 0) ? new UrlEncodedFormEntity(keyvals, "UTF-8") : null;
	}

	private void marshalInputArguments(Element form, Map<String, String> args,
			List<NameValuePair> keyvals) throws JDOMException {
		XPath xpath = XhtmlParser.getXPath("xhtml", "//xhtml:input[@name]");
		for(Object o : xpath.selectNodes(form)) {
			Element input = (Element)o;
			String inputName = input.getAttributeValue("name");
			if (args.containsKey(inputName)) {
				keyvals.add(new BasicNameValuePair(inputName, args.get(inputName)));
			} else {
				String currValue = input.getAttributeValue("value");
				if (currValue != null) {
					keyvals.add(new BasicNameValuePair(inputName, currValue));
				}
			}
		}
	}
	
	private void marshalSelectArguments(Element form, Map<String, String> args,
			List<NameValuePair> keyvals) throws JDOMException {
		XPath xpath = XhtmlParser.getXPath("xhtml", "//xhtml:select");
		for(Object o : xpath.selectNodes(form)) {
			Element select = (Element)o;
			String selectName = select.getAttributeValue("name");
			if (selectName != null && args.containsKey(selectName)) {
				addAvailableOption(select, args.get(selectName), keyvals);
			} else if (selectName != null) {
				addDefaultOption(select, keyvals);
			}
		}
	}

	private void addAvailableOption(Element select, String chosenValue,
			List<NameValuePair> keyvals)
			throws JDOMException {
		String selectName = select.getAttributeValue("name");
		String expr = String.format("//xhtml:option[@value='%s']",chosenValue);
		XPath optionPath = XhtmlParser.getXPath("xhtml", expr);
		if (optionPath.selectSingleNode(select) == null) {
			String msg = String.format("value '%s' was not one of the available options for select '%s'",
										chosenValue, selectName);
			throw new IllegalArgumentException(msg);
		}
		keyvals.add(new BasicNameValuePair(selectName, chosenValue));
	}

	private void addDefaultOption(Element select, List<NameValuePair> keyvals) 
			throws JDOMException {
		String selectName = select.getAttributeValue("name");
		XPath defaultPath = XhtmlParser.getXPath("xhtml","//xhtml:option[@selected]");
		Element defaultOption = (Element)defaultPath.selectSingleNode(select);
		if (defaultOption != null) {
			keyvals.add(new BasicNameValuePair(selectName, defaultOption.getAttributeValue("value","")));
		}
	}

}
