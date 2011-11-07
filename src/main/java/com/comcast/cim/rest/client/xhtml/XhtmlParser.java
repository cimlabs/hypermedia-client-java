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

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.xpath.XPath;

/**
 * Used for locating specific links and forms within a parsed
 * XHTML response body.
 */
public class XhtmlParser {

	public static final String XHTML_NS_URI = "http://www.w3.org/1999/xhtml";
	public static final Namespace XHTML_NS = Namespace.getNamespace(XHTML_NS_URI);

	/**
	 * Generate an XPath search against an XHTML document (using the proper
	 * XHTML namespace).
	 * @param xhtmlPrefix This is the prefix to use when describing elements
	 *   in the XHTML XML namespace. For example, provide &quot;xhtml&quot;
	 *   here if you are going to refer to &quot;xhtml:a&quot; in your 
	 *   XPath expression.
	 * @param xpathExpression the XPath search term to use
	 * @return executable XPath search
	 * @throws JDOMException
	 */
	public static XPath getXPath(String xhtmlPrefix, String xpathExpression)
			throws JDOMException {
		XPath xpath = XPath.newInstance(xpathExpression);
		xpath.addNamespace(Namespace.getNamespace(xhtmlPrefix, XhtmlParser.XHTML_NS_URI));
		return xpath;
	}

	/**
	 * Find a descendant of the given element that is an &lt;a&gt;
	 * tag with the given link relation in its @rel attribute. 
	 * @param elt root element of the search
	 * @param rel link relation to find
	 * @return desired &lt;a&gt; element, or {@code null} if no
	 *   such element exists
	 * @throws JDOMException
	 */
	public Element getLinkWithRelation(Element elt, String rel) throws JDOMException {
		XPath xpath = getXPath("xhtml", String.format(".//xhtml:a[@rel='%s']", rel));
		return (Element)xpath.selectSingleNode(elt);
	}

	/**
     * Find in the given XML document an &lt;a&gt;
     * tag with the given link relation in its @rel attribute. 
     * @param doc XML document to search
     * @param rel link relation to find
     * @return desired &lt;a&gt; element, or {@code null} if no
     *   such element exists
     * @throws JDOMException
     */
	public Element getLinkWithRelation(Document doc, String rel) throws JDOMException {
		return getLinkWithRelation(doc.getRootElement(), rel);
	}

    /**
     * Find a descendant of the given element that is a &lt;form&gt;
     * tag with the given @name attribute. 
     * @param elt root element of the search
     * @param formName @name attribute value to look for
     * @return desired &lt;form&gt; element, or {@code null} if no
     *   such element exists
     * @throws JDOMException
     */
	public Element getFormWithName(Element elt, String formName) throws JDOMException {
		XPath xpath = getXPath("xhtml", String.format(".//xhtml:form[@name='%s']", formName));
		return (Element)xpath.selectSingleNode(elt);
	}

    /**
     * Find in the given XML document a &lt;form&gt;
     * tag with the given @name attribute. 
     * @param doc XML document to search
     * @param formName @name attribute value to look for
     * @return desired &lt;form&gt; element, or {@code null} if no
     *   such element exists
     * @throws JDOMException
     */
	public Element getFormWithName(Document doc, String formName) throws JDOMException {
		return getFormWithName(doc.getRootElement(), formName);
	}	

}