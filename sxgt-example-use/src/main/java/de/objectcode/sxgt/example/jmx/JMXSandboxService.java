/**
 * SXGT - SXGT is a Spring XML Groovy Template system
 * 
 * Copyright (c) 2010 ObjectCode GmbH
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version
 * 3, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License 3 for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License version 3 along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 **/
package de.objectcode.sxgt.example.jmx;

import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

/**
 * A dummy implementation of a jmx service with a getMessage method that returns
 * "Hello World!"
 * 
 * @author sbaltes
 */
@ManagedResource(
		objectName = "sandboxservice:name=JMXSandboxService", 
		description = "A dummy JMX bean")
public class JMXSandboxService {
	
	@ManagedAttribute(description = "returns greeting string")
	public String getMessage() {
		return "Hello World!";
	}
}
