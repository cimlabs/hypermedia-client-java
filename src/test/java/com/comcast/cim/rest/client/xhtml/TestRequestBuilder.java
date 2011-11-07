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


import static org.junit.Assert.*;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.util.EntityUtils;
import org.jdom.Element;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.comcast.cim.rest.client.xhtml.RequestBuilder;
import com.comcast.cim.rest.client.xhtml.XhtmlParser;

public class TestRequestBuilder extends XhtmlTestCase {

	private RequestBuilder impl;
	private Map<String,String> args;
	
	@Before
	public void setUp() throws Exception {
		impl = new RequestBuilder();
		args = new HashMap<String,String>();
	}

	@Test
	public void testCanBuildRequestForAbsoluteLinkHref() throws Exception {
		Element a = new Element("a", XhtmlParser.XHTML_NS_URI);
		String absoluteUri = "http://www.example.com/foo";
		a.setAttribute("href", absoluteUri);
		buildDocument(a);
		URL context = new URL("http://bar.example.com/");
		HttpUriRequest result = impl.followLink(a, context);
		Assert.assertNotNull(result);
		Assert.assertEquals("GET", result.getMethod());
		Assert.assertEquals(absoluteUri, result.getURI().toString());
	}
	
	@Test
	public void testCanBuildRequestForRelativeLinkHref() throws Exception {
		Element a = new Element("a", XhtmlParser.XHTML_NS_URI);
		a.setAttribute("href", "/foo");
		buildDocument(a);
		URL context = new URL("http://www.example.com/bar");
		
		HttpUriRequest result = impl.followLink(a, context);
		Assert.assertNotNull(result);
		Assert.assertEquals("GET", result.getMethod());
		Assert.assertEquals("http://www.example.com/foo", result.getURI().toString());
	}
	
	@Test
	public void testThrowsExceptionIfLinkElementIsNotAnAnchorTag() throws Exception {
		Element a = new Element("p", XhtmlParser.XHTML_NS_URI);
		buildDocument(a);
		URL context = new URL("http://foo.example.com/");
		try {
			impl.followLink(a, context);
			Assert.fail("should have thrown IllegalArgumentException");
		} catch (IllegalArgumentException expected) {
		}
	}
	
	@Test
	public void testThrowsExceptionIfLinkElementHasNoHrefAttribute() throws Exception {
		Element a = new Element("a", XhtmlParser.XHTML_NS_URI);
		buildDocument(a);
		URL context = new URL("http://foo.example.com/");
		try {
			impl.followLink(a, context);
			Assert.fail("should have thrown IllegalArgumentException");
		} catch (IllegalArgumentException expected) {
		}
	}
	
	@Test
	public void testBuildsAGetRequestIfFormMethodSaysSo() throws Exception {
		Element form = new Element("form", XhtmlParser.XHTML_NS_URI);
		form.setAttribute("method","get");
		form.setAttribute("action","http://foo.example.com/");
		buildDocument(form);
		URL context = new URL("http://www.example.com/");
		HttpUriRequest result = impl.submitForm(form, context, args);
		Assert.assertEquals("GET", result.getMethod());
	}
	
	@Test
	public void testBuildsAPostRequestIfFormMethodSaysSo() throws Exception {
		Element form = new Element("form", XhtmlParser.XHTML_NS_URI);
		form.setAttribute("method","post");
		form.setAttribute("action","http://foo.example.com/");
		buildDocument(form);
		URL context = new URL("http://www.example.com/");
		HttpUriRequest result = impl.submitForm(form, context, args);
		Assert.assertEquals("POST", result.getMethod());
	}
	
	@Test
	public void testThrowsExceptionIfFormMethodIsNotGetOrPost() throws Exception {
		Element form = new Element("form", XhtmlParser.XHTML_NS_URI);
		form.setAttribute("method","PUT");
		form.setAttribute("action","http://foo.example.com/");
		buildDocument(form);
		URL context = new URL("http://www.example.com/");
		try {
			impl.submitForm(form, context, args);
			Assert.fail("should have thrown IllegalArgumentException");
		} catch (IllegalArgumentException expected) {
		}
	}
	
	@Test
	public void testThrowsExceptionIfFormDoesNotHaveMethod() throws Exception {
		Element form = new Element("form", XhtmlParser.XHTML_NS_URI);
		form.setAttribute("action","http://foo.example.com/");
		buildDocument(form);
		URL context = new URL("http://www.example.com/");
		
		try {
			impl.submitForm(form, context, args);
			Assert.fail("should have thrown IllegalArgumentException");
		} catch (IllegalArgumentException expected) {
		}
	}
	
	@Test
	public void testBuildsRequestWithAbsoluteURLInFormAction() throws Exception {
		Element form = new Element("form", XhtmlParser.XHTML_NS_URI);
		form.setAttribute("method", "GET");
		String absoluteUri = "http://foo.example.com/";
		form.setAttribute("action", absoluteUri);
		buildDocument(form);
		URL context = new URL("http://www.example.com");
		
		HttpUriRequest result = impl.submitForm(form, context, args);
		Assert.assertEquals(absoluteUri, result.getURI().toString());
	}
	
	@Test
	public void testBuildsRequestWithRelativeURLInFormAction() throws Exception {
		Element form = new Element("form", XhtmlParser.XHTML_NS_URI);
		form.setAttribute("method", "POST");
		form.setAttribute("action", "./baz");
		buildDocument(form);
		URL context = new URL("http://www.example.com/foo/bar");
		
		HttpUriRequest result = impl.submitForm(form, context, args);
		Assert.assertEquals("http://www.example.com/foo/baz", 
							result.getURI().toString());
	}
	
	@Test
	public void testAddsGetInputToUriAsQueryParameter() throws Exception {
		Element form = new Element("form", XhtmlParser.XHTML_NS_URI);
		form.setAttribute("method", "GET");
		form.setAttribute("action", "http://foo.example.com/");
		Element input = new Element("input", XhtmlParser.XHTML_NS_URI);
		input.setAttribute("type","text");
		input.setAttribute("name","arg0");
		form.addContent(input);
		buildDocument(form);
		URL context = new URL("http://www.example.com/foo/bar");
		args.put("arg0","val0");
		
		HttpUriRequest result = impl.submitForm(form, context, args);
		Assert.assertEquals("http://foo.example.com/?arg0=val0", 
							result.getURI().toString());
	}
	
	@Test
	public void testSkipsInputsWithNoName() throws Exception {
		Element form = new Element("form", XhtmlParser.XHTML_NS_URI);
		form.setAttribute("method", "GET");
		form.setAttribute("action", "http://foo.example.com/");
		Element input = new Element("input", XhtmlParser.XHTML_NS_URI);
		input.setAttribute("type","text");
		input.setAttribute("name","arg0");
		form.addContent(input);
		Element input2 = new Element("input", XhtmlParser.XHTML_NS_URI);
		input2.setAttribute("type","submit");
		form.addContent(input2);
		buildDocument(form);
		URL context = new URL("http://www.example.com/foo/bar");
		args.put("arg0","val0");
		
		HttpUriRequest result = impl.submitForm(form, context, args);
		Assert.assertEquals("http://foo.example.com/?arg0=val0", 
							result.getURI().toString());
	}
	
	@Test
	public void testAddsMultipleInstancesOfInputNamesToQueryParams() throws Exception {
		Element form = new Element("form", XhtmlParser.XHTML_NS_URI);
		form.setAttribute("method", "GET");
		form.setAttribute("action", "http://foo.example.com/");
		Element input = new Element("input", XhtmlParser.XHTML_NS_URI);
		input.setAttribute("type","text");
		input.setAttribute("name","arg0");
		form.addContent(input);
		Element input2 = new Element("input", XhtmlParser.XHTML_NS_URI);
		input2.setAttribute("type","text");
		input2.setAttribute("name","arg0");
		form.addContent(input2);
		buildDocument(form);
		URL context = new URL("http://www.example.com/foo/bar");
		args.put("arg0","val0");
		
		HttpUriRequest result = impl.submitForm(form, context, args);
		Assert.assertEquals("http://foo.example.com/?arg0=val0&arg0=val0", 
							result.getURI().toString());
	}
	
	@Test
	public void testAddsHiddenInputsToQueryParams() throws Exception {
		Element form = new Element("form", XhtmlParser.XHTML_NS_URI);
		form.setAttribute("method", "GET");
		form.setAttribute("action", "http://foo.example.com/");
		Element input = new Element("input", XhtmlParser.XHTML_NS_URI);
		input.setAttribute("type","hidden");
		input.setAttribute("name","arg0");
		form.addContent(input);
		Element input2 = new Element("input", XhtmlParser.XHTML_NS_URI);
		input2.setAttribute("type","submit");
		form.addContent(input2);
		buildDocument(form);
		URL context = new URL("http://www.example.com/foo/bar");
		args.put("arg0","val0");
		
		HttpUriRequest result = impl.submitForm(form, context, args);
		Assert.assertEquals("http://foo.example.com/?arg0=val0", 
							result.getURI().toString());
	}
	
	@Test
	public void testRetainsExistingInputValueIfNotSpecifiedInMap() throws Exception {
		Element form = new Element("form", XhtmlParser.XHTML_NS_URI);
		form.setAttribute("method", "GET");
		form.setAttribute("action", "http://foo.example.com/");
		Element input = new Element("input", XhtmlParser.XHTML_NS_URI);
		input.setAttribute("type","hidden");
		input.setAttribute("name","arg0");
		input.setAttribute("value", "val1");
		form.addContent(input);
		Element input2 = new Element("input", XhtmlParser.XHTML_NS_URI);
		input2.setAttribute("type","submit");
		form.addContent(input2);
		buildDocument(form);
		URL context = new URL("http://www.example.com/foo/bar");
		
		HttpUriRequest result = impl.submitForm(form, context, args);
		Assert.assertEquals("http://foo.example.com/?arg0=val1", 
							result.getURI().toString());
	}
	
	@Test
	public void testBuildsAPostRequestWithFormUrlEncodingForMethodPost()
		throws Exception {
		Element form = new Element("form", XhtmlParser.XHTML_NS_URI);
		form.setAttribute("method", "POST");
		form.setAttribute("action", "http://foo.example.com/");
		Element input = new Element("input", XhtmlParser.XHTML_NS_URI);
		input.setAttribute("type","text");
		input.setAttribute("name","arg0");
		form.addContent(input);
		Element input2 = new Element("input", XhtmlParser.XHTML_NS_URI);
		input2.setAttribute("type","submit");
		form.addContent(input2);
		buildDocument(form);
		URL context = new URL("http://www.example.com/foo/bar");
		args.put("arg0","val0");
		
		HttpUriRequest result = impl.submitForm(form, context, args);
		Assert.assertEquals("POST", result.getMethod());
		HttpEntity entity = ((HttpPost)result).getEntity();
		Assert.assertNotNull(entity);
		Assert.assertEquals("arg0=val0", EntityUtils.toString(entity));
	}
	
	@Test
	public void testCanSubmitAFormWithASelectInput() throws Exception {
		Element form = new Element("form", XhtmlParser.XHTML_NS_URI);
		form.setAttribute("method", "POST");
		form.setAttribute("action", "http://foo.example.com/");
		Element select = new Element("select", XhtmlParser.XHTML_NS_URI);
		select.setAttribute("name","arg0");
		Element option1 = new Element("option", XhtmlParser.XHTML_NS_URI);
		option1.setAttribute("value","val0");
		option1.setText("Description of val0");
		select.addContent(option1);
		Element option2 = new Element("option", XhtmlParser.XHTML_NS_URI);
		option2.setAttribute("value","val1");
		option2.setAttribute("selected","true");
		option2.setText("Description of val1");
		select.addContent(option2);
		form.addContent(select);
		buildDocument(form);
		URL context = new URL("http://www.example.com/foo/bar");
		args.put("arg0","val0");
		
		HttpUriRequest result = impl.submitForm(form, context, args);
		Assert.assertEquals("POST", result.getMethod());
		HttpEntity entity = ((HttpPost)result).getEntity();
		Assert.assertNotNull(entity);
		Assert.assertEquals("arg0=val0", EntityUtils.toString(entity));
	}
	
	@Test
	public void testWillSubmitADefaultSelectionIfNotSpecified() throws Exception {
		Element form = new Element("form", XhtmlParser.XHTML_NS_URI);
		form.setAttribute("method", "POST");
		form.setAttribute("action", "http://foo.example.com/");
		Element select = new Element("select", XhtmlParser.XHTML_NS_URI);
		select.setAttribute("name","arg0");
		Element option = new Element("option", XhtmlParser.XHTML_NS_URI);
		option.setAttribute("value","val0");
		option.setAttribute("selected","true");
		option.setText("Description of val0");
		select.addContent(option);
		form.addContent(select);
		buildDocument(form);
		URL context = new URL("http://www.example.com/foo/bar");
		
		HttpUriRequest result = impl.submitForm(form, context, args);
		Assert.assertEquals("POST", result.getMethod());
		HttpEntity entity = ((HttpPost)result).getEntity();
		Assert.assertNotNull(entity);
		Assert.assertEquals("arg0=val0", EntityUtils.toString(entity));
	}
	
	@Test
	public void testWillNotLetMeSubmitANonAvailableOptionForASelect() throws Exception {
		Element form = new Element("form", XhtmlParser.XHTML_NS_URI);
		form.setAttribute("method", "POST");
		form.setAttribute("action", "http://foo.example.com/");
		Element select = new Element("select", XhtmlParser.XHTML_NS_URI);
		select.setAttribute("name","arg0");
		Element option = new Element("option", XhtmlParser.XHTML_NS_URI);
		option.setAttribute("value","val0");
		option.setText("Description of val0");
		select.addContent(option);
		form.addContent(select);
		buildDocument(form);
		args.put("arg0", "notAnOption");
		URL context = new URL("http://www.example.com/foo/bar");
		
		try {
			impl.submitForm(form, context, args);
			fail("should have thrown exception");
		} catch (IllegalArgumentException expected) {
		}
	}
}
