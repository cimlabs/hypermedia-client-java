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

/**
 * This exception is raised when the client is asked to follow a
 * link with a certain link relation or to submit a form with a
 * given class, but no such link or form can be found in the
 * current application state.
 */
public class RelationNotFoundException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public RelationNotFoundException() {
		super();
	}
	
	public RelationNotFoundException(String msg) {
		super(msg);
	}
	
	public RelationNotFoundException(Throwable cause) {
		super(cause);
	}
	
	public RelationNotFoundException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
