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
import java.io.InputStream;

public class InterruptedInputStream extends InputStream {
	private InputStream in;
	public InterruptedInputStream(InputStream in) {
		this.in = in;
	}
	
	@Override
	public int available() throws IOException {
		return in.available();
	}

	@Override
	public void close() throws IOException {
		in.close();
	}

	@Override
	public synchronized void mark(int arg0) {
		in.mark(arg0);
	}

	@Override
	public boolean markSupported() {
		return in.markSupported();
	}

	@Override
	public int read(byte[] arg0, int arg1, int arg2) throws IOException {
		int result = in.read(arg0,arg1,arg2);
		if (result == -1) throw new IOException();
		return result;
	}

	@Override
	public int read(byte[] arg0) throws IOException {
		int result = in.read(arg0);
		if (result == -1) throw new IOException();
		return result;
	}

	@Override
	public synchronized void reset() throws IOException {
		in.reset();
	}

	@Override
	public long skip(long arg0) throws IOException {
		return in.skip(arg0);
	}

	@Override
	public int read() throws IOException {
		int result = in.read();
		if (result == -1) throw new IOException();
		return result;
	}
}