package de.objectcode.sxgt.example.jmx;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.junit.Test;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

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
