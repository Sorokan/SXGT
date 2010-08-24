/**
 * SXGT - SXGT is a Spring XML Groovy Template system
 * 
 * Copyright (c) 2010 ObjectCode GmbH
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version
 * 2.1, as published by the Free Software Foundation.
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
package de.objectcode.spring.sxgt.example;

import de.objectcode.spring.sxgt.SxgtNamespaceHandler;

/**
 * Example for a SXGT Namespace Handler. Each project that uses sxgt must have
 * its own version of this class. It is used to bind your xml schema to the
 * package of your groovy templates: for every xml element from the spring
 * config that has the namespaceUrl of your xml schema (here
 * "http://localhost/sxgt/example"), a template with the same name as the xml
 * element must be found in the given package (here
 * "de.objectcode.spring.sxgt.example.templates").
 * 
 * @author sbaltes
 */
public class ExampleSxgtNamespaceHandler extends SxgtNamespaceHandler {

	public ExampleSxgtNamespaceHandler() {
		super("http://localhost/sxgt", "de.objectcode.spring.sxgt.example");
	}

}
