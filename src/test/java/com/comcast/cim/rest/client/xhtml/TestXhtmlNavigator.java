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
import static org.easymock.classextension.EasyMock.*;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicHttpResponse;
import org.jdom.Document;
import org.jdom.Element;
import org.junit.Before;
import org.junit.Test;

import com.comcast.cim.rest.client.xhtml.RequestBuilder;
import com.comcast.cim.rest.client.xhtml.XhtmlApplicationState;
import com.comcast.cim.rest.client.xhtml.XhtmlHttpClient;
import com.comcast.cim.rest.client.xhtml.XhtmlNavigator;
import com.comcast.cim.rest.client.xhtml.XhtmlParser;

public class TestXhtmlNavigator {

	private XhtmlParser mockParser;
	private RequestBuilder mockBuilder;
	private XhtmlHttpClient mockClient;
	private XhtmlNavigator impl;
	private Document doc;
	private URL context;
	private XhtmlApplicationState initState;
	private XhtmlApplicationState newState;
	private HttpResponse success;
	
	@Before
	public void setUp() throws Exception {
		mockParser = createMock(XhtmlParser.class);
		mockBuilder = createMock(RequestBuilder.class);
		mockClient = createMock(XhtmlHttpClient.class);
		impl = new XhtmlNavigator(mockParser, mockBuilder, mockClient);
		doc = new Document();
		context = new URL("http://foo.example.com/");
		initState = new XhtmlApplicationState(context, null, doc);
		success = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK");
		newState = new XhtmlApplicationState(null, success, null);
	}
	
	private void replayMocks() {
		replay(mockParser);
		replay(mockBuilder);
		replay(mockClient);
	}
	
	private void verifyMocks() {
		verify(mockParser);
		verify(mockBuilder);
		verify(mockClient);
	}
	
	@Test
	public void testCanFollowLink() throws Exception {
		String rel = "relation";
		Element a = new Element("a");
		expect(mockParser.getLinkWithRelation(doc, rel))
			.andReturn(a);
		HttpGet req = new HttpGet("http://foo.example.com/");
		expect(mockBuilder.followLink(a, context))
			.andReturn(req);
		expect(mockClient.execute(req))
			.andReturn(newState);
		
		replayMocks();
		XhtmlApplicationState result = impl.followLink(initState, rel);
		verifyMocks();
		assertSame(newState, result);
	}
	
	@Test
	public void testCanFollowLinkFromSpecificRoot() throws Exception {
		String rel = "relation";
		Element outer = new Element("div", XhtmlParser.XHTML_NS);
		Element wrong = new Element("a", XhtmlParser.XHTML_NS);
		wrong.setAttribute("rel",rel);
		wrong.setAttribute("href","/bad");
		wrong.setText("Bad Link");
		outer.addContent(wrong);
		Element inner = new Element("div", XhtmlParser.XHTML_NS);
		Element right = new Element("a", XhtmlParser.XHTML_NS);
		right.setAttribute("rel",rel);
		right.setAttribute("href","/good");
		right.setText("Good Link");
		inner.addContent(right);
		outer.addContent(inner);
		
		expect(mockParser.getLinkWithRelation(inner, rel))
			.andReturn(right);
		HttpGet req = new HttpGet("http://foo.example.com/");
		expect(mockBuilder.followLink(right, context))
			.andReturn(req);
		expect(mockClient.execute(req))
			.andReturn(newState);
		
		replayMocks();
		XhtmlApplicationState result = impl.followLink(initState, inner, rel);
		verifyMocks();
		assertSame(newState, result);
	}
	
	@Test
	public void testThrowsRelationNotFoundExceptionIfLinkNotPresent() throws Exception {
		String rel = "relation";
		expect(mockParser.getLinkWithRelation(doc, rel))
			.andReturn(null);
		
		replayMocks();
		try {
			impl.followLink(initState, rel);
			Assert.fail("should have thrown exception");
		} catch (RelationNotFoundException expected) {
		}
		verifyMocks();
	}

	@Test
	public void testCanSubmitForm() throws Exception {
		String name = "formName";
		Element form = new Element("form");
		expect(mockParser.getFormWithName(doc, name))
			.andReturn(form);
		HttpPost req = new HttpPost("http://foo.example.com/");
		Map<String,String> args = new HashMap<String, String>();
		expect(mockBuilder.submitForm(form, context, args))
			.andReturn(req);
		expect(mockClient.execute(req))
			.andReturn(newState);
		
		replayMocks();
		XhtmlApplicationState result = impl.submitForm(initState, name, args);
		verifyMocks();
		assertSame(newState, result);
	}
	
	@Test
	public void testThrowsRelationNotFoundExceptionIfFormNotPresent() throws Exception {
		String formName = "formName";
		expect(mockParser.getFormWithName(doc, formName))
			.andReturn(null);
		
		replayMocks();
		try {
			impl.submitForm(initState, formName, null);
			fail("should have thrown exception");
		} catch (RelationNotFoundException expected) {
		}
		verifyMocks();
	}

}
