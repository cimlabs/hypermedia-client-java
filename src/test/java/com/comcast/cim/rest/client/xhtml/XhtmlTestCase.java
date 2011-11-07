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

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.comcast.cim.rest.client.xhtml.XhtmlParser;
import com.comcast.cim.rest.client.xhtml.XhtmlResponseHandler;

public abstract class XhtmlTestCase {
	
	protected static final String publicId = "-//W3C//DTD XHTML 1.0 Transitional//EN";
	protected static final String systemId = "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd";

	protected String buildXhtml(String content) {
		final String fmt = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML " +
		"1.0 Transitional//EN\" " +
		"\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">" +
		"<html xmlns=\"http://www.w3.org/1999/xhtml\" " +
		"xml:lang=\"en\" lang=\"en\"><head/><body>%s</body></html>";
		return String.format(fmt, content);
	}

	protected Document getDocument(String xhtml) throws JDOMException,
			IOException {
				SAXBuilder builder = XhtmlResponseHandler.getBuilder();
				ByteArrayInputStream buf = new ByteArrayInputStream(xhtml.getBytes());
				Document doc = builder.build(buf);
				return doc;
			}

	protected Document getDocument(Element content) {
		Element html = new Element("html", XhtmlParser.XHTML_NS);
		html.addContent(new Element("head", XhtmlParser.XHTML_NS));
		Element body = new Element("body", XhtmlParser.XHTML_NS);
		body.addContent(content);
		html.addContent(body);
		Document doc = new Document();
		doc.setDocType(new DocType("html",XhtmlTestCase.publicId,XhtmlTestCase.systemId));
		doc.addContent(html);
		return doc;
	}
	
	protected void buildDocument(Element content) {
		getDocument(content);
	}

}