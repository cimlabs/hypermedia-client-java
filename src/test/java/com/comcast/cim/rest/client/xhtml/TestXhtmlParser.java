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

import org.jdom.Document;
import org.jdom.Element;
import org.junit.Before;
import org.junit.Test;

import com.comcast.cim.rest.client.xhtml.XhtmlParser;

public class TestXhtmlParser extends XhtmlTestCase {

	private XhtmlParser impl;
	
	@Before
	public void setUp() throws Exception {
		impl = new XhtmlParser();
	}

	@Test
	public void testCanLocateLinkWithRelation() throws Exception {
		String xhtml = buildXhtml("<p><a rel=\"test_relation\" " +
				"href=\"/foo\">Link</a></p>");
		Document doc = getDocument(xhtml);
		Element result = impl.getLinkWithRelation(doc,"test_relation");
		assertNotNull(result);
	}
	
	@Test
	public void testReturnsNullIfNoLinkWithRelation() throws Exception {
		String xhtml = buildXhtml("<p><a rel=\"test_relation\" " +
				"href=\"/foo\">Link</a></p>");
		Document doc = getDocument(xhtml);
		
		Element result = impl.getLinkWithRelation(doc,"notfound_relation");
		assertNull(result);
	}
	
	@Test
	public void testReturnsFirstLinkWithRelationIfMultiple() throws Exception {
		String xhtml = buildXhtml("<p><a rel=\"test_relation\" " +
				"href=\"/foo\">Link</a>" +
				"<a rel=\"test_relation\" href=\"/bar\">Link 2</a></p>");
		Document doc = getDocument(xhtml);
		
		Element result = impl.getLinkWithRelation(doc,"test_relation");
		assertEquals("Link",result.getText());
	}
	
	@Test
	public void testCanFindFormWithClass() throws Exception {
		String xhtml = buildXhtml("<p><form name=\"test_name\"></form></p>");
		Document doc = getDocument(xhtml);
		
		Element result = impl.getFormWithName(doc,"test_name");
		assertNotNull(result);
	}
	
	@Test
	public void testReturnsFirstMatchingForm() throws Exception {
		String xhtml = buildXhtml("<p><form name=\"test_name\">Form 1</form>" +
		"<form name=\"test_name\">Form 2</form></p>");
		Document doc = getDocument(xhtml);
		
		Element result = impl.getFormWithName(doc,"test_name");
		assertEquals("Form 1", result.getText());
	}

	@Test
	public void testReturnsNullIfNoFormWithClass() throws Exception {
		String xhtml = buildXhtml("<p><form name=\"test_name\"></form></p>");
		Document doc = getDocument(xhtml);
		
		Element result = impl.getFormWithName(doc,"notfound_name");
		assertNull(result);
	}
	
	@Test
	public void testFindsCorrectNamedFormFromContext() throws Exception {
		final String name = "formName";
		Element outer = new Element("div", XhtmlParser.XHTML_NS);
		Element wrong = new Element("form", XhtmlParser.XHTML_NS);
		wrong.setAttribute("name",name);
		wrong.setAttribute("action","/bad");
		wrong.setAttribute("method","POST");
		outer.addContent(wrong);
		Element inner = new Element("div", XhtmlParser.XHTML_NS);
		Element right = new Element("form", XhtmlParser.XHTML_NS);
		right.setAttribute("name",name);
		right.setAttribute("action","/good");
		right.setAttribute("method","POST");
		inner.addContent(right);
		outer.addContent(inner);
		getDocument(outer);
		
		Element result = impl.getFormWithName(inner, name);
		assertSame(right, result);
	}
	
	@Test
	public void testFindsCorrectLinkRelationFromContext() throws Exception {
		final String rel = "relation";
		Element outer = new Element("div", XhtmlParser.XHTML_NS);
		Element wrong = new Element("a", XhtmlParser.XHTML_NS);
		wrong.setAttribute("rel",rel);
		wrong.setAttribute("href","/bad");
		outer.addContent(wrong);
		Element inner = new Element("div", XhtmlParser.XHTML_NS);
		Element right = new Element("a", XhtmlParser.XHTML_NS);
		right.setAttribute("rel",rel);
		right.setAttribute("href","/good");
		inner.addContent(right);
		outer.addContent(inner);
		getDocument(outer);
		
		Element result = impl.getLinkWithRelation(inner, rel);
		assertSame(right, result);
	}
}
