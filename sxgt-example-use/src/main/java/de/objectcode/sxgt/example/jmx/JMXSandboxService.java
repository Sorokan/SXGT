/**
 * Copyright (c) 2003-2009 GD GameDuell GmbH, All Rights Reserved
 * This document is strictly confidential and sole property of
 * GD GameDuell GmbH, Berlin, Germany
 *
 * JMXAccountingServiceSwitch.java
 * 
 * created at 10:03:28 12.03.2009
 *
 */
package de.objectcode.sxgt.example.jmx;

import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

@ManagedResource(
		objectName = "sandboxservice:name=JMXSandboxService", 
		description = "A dummy JMX bean")
public class JMXSandboxService {
	
	@ManagedAttribute(description = "returns greeting string")
	public String getMessage() {
		return "Hello World!";
	}
}
