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

import junit.framework.Assert;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.easymock.Capture;
import org.easymock.classextension.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.comcast.cim.rest.client.xhtml.XhtmlApplicationState;
import com.comcast.cim.rest.client.xhtml.XhtmlHttpClient;
import com.comcast.cim.rest.client.xhtml.XhtmlResponseHandler;
import com.comcast.cim.rest.client.xhtml.XhtmlResponseHandlerFactory;

public class TestXhtmlHttpClient {

	private HttpClient mockHttpClient;
	private XhtmlResponseHandlerFactory mockFactory;
	private XhtmlHttpClient impl;
	
	@Before
	public void setUp() throws Exception {
		mockHttpClient = EasyMock.createMock(HttpClient.class);
		mockFactory = EasyMock.createMock(XhtmlResponseHandlerFactory.class);
		impl = new XhtmlHttpClient(mockHttpClient, mockFactory);
	}
	
	private void replayMocks() {
		EasyMock.replay(mockHttpClient);
		EasyMock.replay(mockFactory);
	}
	
	private void verifyMocks() {
		EasyMock.verify(mockHttpClient);
		EasyMock.verify(mockFactory);
	}
	
	@Test
	public void testDelegatesRequestExecutionToHelpers() throws Exception {
		XhtmlApplicationState state = new XhtmlApplicationState(null,null,null);
		HttpGet get = new HttpGet("http://foo.example.com/");
		
		URL context = new URL("http://foo.example.com/");
		XhtmlResponseHandler rh = new XhtmlResponseHandler(context);
		
		EasyMock.expect(mockFactory.get(context)).andReturn(rh);
		EasyMock.expect(mockHttpClient.execute(get, rh))
			.andReturn(state);
		
		replayMocks();
		XhtmlApplicationState result = impl.execute(get);
		verifyMocks();
		Assert.assertSame(state, result);
	}
	
	@Test
	public void testSetsAcceptHeaderForXhtml() throws Exception {
		XhtmlApplicationState state = new XhtmlApplicationState(null,null,null);
		HttpGet get = new HttpGet("http://foo.example.com/");
		URL context = new URL("http://foo.example.com/");
		XhtmlResponseHandler rh = new XhtmlResponseHandler(context);
		
		Capture<HttpUriRequest> cap = new Capture<HttpUriRequest>();
		EasyMock.expect(mockFactory.get(context)).andReturn(rh);
		EasyMock.expect(mockHttpClient.execute(EasyMock.capture(cap), 
				EasyMock.same(rh)))
			.andReturn(state);
		
		replayMocks();
		impl.execute(get);
		verifyMocks();
		
		HttpUriRequest captured = cap.getValue();
		Assert.assertEquals("application/xhtml+xml,*/*;q=0.9",
				captured.getFirstHeader("Accept").getValue());
	}
	
	@Test
	public void testOverwritesAcceptHeaderForXhtml() throws Exception {
		XhtmlApplicationState state = new XhtmlApplicationState(null,null,null);
		HttpGet get = new HttpGet("http://foo.example.com/");
		get.setHeader("Accept","*/*");
		URL context = new URL("http://foo.example.com/");
		XhtmlResponseHandler rh = new XhtmlResponseHandler(null);
		
		Capture<HttpUriRequest> cap = new Capture<HttpUriRequest>();
		EasyMock.expect(mockFactory.get(context)).andReturn(rh);
		EasyMock.expect(mockHttpClient.execute(EasyMock.capture(cap), 
				EasyMock.same(rh)))
			.andReturn(state);
		
		replayMocks();
		impl.execute(get);
		verifyMocks();
		
		HttpUriRequest captured = cap.getValue();
		Assert.assertEquals("application/xhtml+xml,*/*;q=0.9",
				captured.getFirstHeader("Accept").getValue());
	}
	
}
