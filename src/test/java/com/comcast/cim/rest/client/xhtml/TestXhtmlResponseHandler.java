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
import java.net.URL;

import org.apache.commons.logging.impl.NoOpLog;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.message.BasicHttpResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.comcast.cim.rest.client.xhtml.XhtmlApplicationState;
import com.comcast.cim.rest.client.xhtml.XhtmlResponseHandler;

public class TestXhtmlResponseHandler {

	private XhtmlResponseHandler impl;
	private URL context;
	
	@Before
	public void setUp() throws Exception {
		context = new URL("http://foo.example.com/");
		impl = new XhtmlResponseHandler(context);
		impl.setLogger(new NoOpLog());
	}

	@Test
	public void testDigestsValidXhtml() throws Exception {
		HttpResponse resp = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK");
		resp.setHeader("Content-Type","application/xhtml+xml;charset=utf-8");
		String xhtml = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML " +
				"1.0 Transitional//EN\" " +
				"\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">" +
				"<html xmlns=\"http://www.w3.org/1999/xhtml\" " +
				"xml:lang=\"en\" lang=\"en\"><head/><body/></html>";
		byte[] bytes = xhtml.getBytes();
		MockHttpEntity entity = new MockHttpEntity(bytes);
		resp.setEntity(entity);
		resp.setHeader("Content-Length","" + bytes.length);
		
		XhtmlApplicationState result = impl.handleResponse(resp);
		Assert.assertNotNull(result);
		Assert.assertNotNull(result.getDocument());
		Assert.assertSame(resp, result.getHttpResponse());
		Assert.assertTrue(entity.fullyConsumed());
	}
	
	@Test
	public void testReturnsNullDocumentForInvalidXhtml() throws Exception {
		HttpResponse resp = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK");
		resp.setHeader("Content-Type","application/xhtml+xml;charset=utf-8");
		String xhtml = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML " +
				"1.0 Transitional//EN\" " +
				"\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">" +
				"<html xmlns=\"http://www.w3.org/1999/xhtml\" " +
				"xml:lang=\"en\" lang=\"en\"><head><body/></html>";
		byte[] bytes = xhtml.getBytes();
		MockHttpEntity entity = new MockHttpEntity(bytes);
		resp.setEntity(entity);
		resp.setHeader("Content-Length","" + bytes.length);
		
		XhtmlApplicationState result = impl.handleResponse(resp);
		Assert.assertNotNull(result);
		Assert.assertNull(result.getDocument());
		Assert.assertSame(resp, result.getHttpResponse());
		Assert.assertTrue(entity.fullyConsumed());
	}
	
	@Test
	public void testReturnsNullDocumentWithNoBody() throws Exception {
		HttpResponse resp = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_TEMPORARY_REDIRECT, "Temporary Redirect");
		resp.setHeader("Location","http://www.example.com/foo");
		
		XhtmlApplicationState result = impl.handleResponse(resp);
		Assert.assertNotNull(result);
		Assert.assertNull(result.getDocument());
		Assert.assertSame(resp, result.getHttpResponse());
	}
	
	@Test
	public void testPassesUpIOExceptionIfThrown() {
		HttpResponse resp = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK");
		resp.setHeader("Content-Type","application/xhtml+xml;charset=utf-8");
		String xhtml = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML " +
				"1.0 Transitional//EN\" " +
				"\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">" +
				"<html xmlns=\"http://www.w3.org/1999/xhtml\" " +
				"xml:lang=\"en\" lang=\"en\"><head/><body/>";
		byte[] bytes = xhtml.getBytes();
		long purportedLength = bytes.length + 6;
		resp.setHeader("Content-Length","" + purportedLength);
		
		ByteArrayInputStream buf = new ByteArrayInputStream(bytes);
		InterruptedInputStream iis = new InterruptedInputStream(buf);
		resp.setEntity(new InputStreamEntity(iis,purportedLength));
		
		try {
			impl.handleResponse(resp);
			Assert.fail("should have thrown IOException");
		} catch (IOException expected) {
		}
	}

	@Test
	public void testPopulatesRequestContextInResult() throws Exception {
		HttpResponse resp = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK");
		resp.setHeader("Content-Type","application/xhtml+xml;charset=utf-8");
		String xhtml = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML " +
				"1.0 Transitional//EN\" " +
				"\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">" +
				"<html xmlns=\"http://www.w3.org/1999/xhtml\" " +
				"xml:lang=\"en\" lang=\"en\"><head/><body/></html>";
		byte[] bytes = xhtml.getBytes();
		resp.setEntity(new ByteArrayEntity(bytes));
		resp.setHeader("Content-Length", "" + bytes.length);
		
		XhtmlApplicationState result = impl.handleResponse(resp);
		Assert.assertSame(context, result.getContext());
	}
}
