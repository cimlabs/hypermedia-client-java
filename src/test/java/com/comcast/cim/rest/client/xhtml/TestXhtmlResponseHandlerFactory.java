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


import java.net.URL;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.comcast.cim.rest.client.xhtml.XhtmlResponseHandler;
import com.comcast.cim.rest.client.xhtml.XhtmlResponseHandlerFactory;

public class TestXhtmlResponseHandlerFactory {

	private XhtmlResponseHandlerFactory impl;
	
	@Before
	public void setUp() throws Exception {
		impl = new XhtmlResponseHandlerFactory();
	}
	
	@Test
	public void testPopulatesContextInRequestHandler() throws Exception {
		URL context = new URL("http://foo.example.com/");
		XhtmlResponseHandler result = impl.get(context);
		Assert.assertSame(context, result.getContext());
	}

}
