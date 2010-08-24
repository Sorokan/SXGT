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

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.junit.Test;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

/**
 * A integration test that checks if the dummy jmx service is available
 * 
 * @author sbaltes
 */
public class SandboxServiceIntegrationTest
		extends
			AbstractDependencyInjectionSpringContextTests {

	@Override
	protected String[] getConfigLocations() {
		return new String[]{"applicationContext.xml"};
	}

	@Test
	public void testJmxService() throws Exception {
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		ObjectName mbeanName = new ObjectName(
				"sandboxservice:name=JMXSandboxService");
		String result = (String) mbs.invoke(mbeanName, "getMessage", null, null);
		assertEquals("Hello World!", result);
	}

}
