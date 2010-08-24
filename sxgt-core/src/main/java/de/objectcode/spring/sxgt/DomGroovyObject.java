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
package de.objectcode.spring.sxgt;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.MetaClassImpl;
import groovy.lang.MetaMethod;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.jxpath.JXPathContext;
import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.ReflectionCache;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A simple wrapper for w3c dom which makes it "groovy". You can access the
 * child element or attributes simply by typing domNodeGroovyObject.element or
 * domNodeGroovyObject.attribute, or you can use xpath like
 * domNodeGroovyObject.xpath("*")<br/>
 * 
 * Based on code from <a
 * href="http://today.java.net/pub/a/today/2004/08/12/groovyxml.html"
 * >http://today.java.net/pub/a/today/2004/08/12/groovyxml.html</a>
 * 
 * @author <a href="mailto:baltes@objectcode.de">Sebastian Baltes</a>
 * @copyright (c) 2010 ObjectCode GmbH
 */
public class DomGroovyObject implements GroovyObject, Iterable<DomGroovyObject> {

	private final Element element;
	private MetaClass metaClass;

	public DomGroovyObject(Element _element) {
		element = _element;
	}

	public Object invokeMethod(String _methodName, Object _parameters) {
		if ("getValue".equals(_methodName)) {
			return element.getNodeValue();
		}

		if ("xpath".equals(_methodName)) {
			String s = (String) _parameters;
			return xpath(s);
		}

		if ("toString".equals(_methodName)) {
			return toString();
		}

		return null;
	}

	public Object getProperty(String _name) {
		if (element.getNodeType() == Node.ELEMENT_NODE) {
			Element elem = element;
			if (elem.hasAttribute(_name)) {
				return elem.getAttribute(_name);
			}
			try {
				return listChildren(_name);
			} catch (Exception e) {
				e.printStackTrace();
				// nothing...
			}
		}
		return null;
	}

	public void setProperty(String _name, Object _value) {
		// immutable, so do nothing
	}

	public MetaClass getMetaClass() {
		if (metaClass == null) {
			List<MetaMethod> propertyMethods = new ArrayList<MetaMethod>();
			NodeList nodes = element.getChildNodes();
			for (int i = 0; i < nodes.getLength(); i++) {
				Node child = nodes.item(i);
				if (child.getNodeType() == Node.ELEMENT_NODE
						|| child.getNodeType() == Node.ATTRIBUTE_NODE) {
					MetaMethod method = new PropertyMetaMethod(child.getLocalName(),
							child.getNodeType() == Node.ELEMENT_NODE
									? DomGroovyObject.class
									: String.class);
					propertyMethods.add(method);
				}
			}
			metaClass = new MetaClassImpl(this.getClass(), propertyMethods
					.toArray(new MetaMethod[propertyMethods.size()]));
			metaClass.initialize();
		}
		return metaClass;
	}

	public void setMetaClass(MetaClass _metaClass) {
		metaClass = _metaClass;
	}

	@Override
	public String toString() {
		try {
			return Utils.xmlToString(element, false);
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	private List<DomGroovyObject> xpath(String path) {
		List<DomGroovyObject> children = new ArrayList<DomGroovyObject>();
		List<?> result = JXPathContext.newContext(element).selectNodes(path);
		for (Object child : result) {
			if (child instanceof Element) {
				children.add(new DomGroovyObject((Element) child));
			}
		}
		return children;
	}

	private List<DomGroovyObject> listChildren(String name) {
		List<DomGroovyObject> children = new ArrayList<DomGroovyObject>();
		NodeList nodes = element.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node child = nodes.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				if (name == null || name.equals(child.getLocalName())) {
					children.add(new DomGroovyObject((Element) child));
				}
			}
		}
		return children;
	}

	private class PropertyMetaMethod extends MetaMethod {

		private final String name;
		private final Class<?> type;

		public PropertyMetaMethod(String _name, Class<?> _type) {
			name = _name;
			type = _type;
			setParametersTypes(new CachedClass[0]);
		}

		@Override
		public int getModifiers() {
			return Modifier.PUBLIC;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public Class<?> getReturnType() {
			return type;
		}

		@Override
		public CachedClass getDeclaringClass() {
			return ReflectionCache.getCachedClass(DomGroovyObject.class);
		}

		@Override
		public Object invoke(Object object, Object[] arguments) {
			return ((DomGroovyObject) object).getProperty(name);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<DomGroovyObject> iterator() {
		return listChildren(null).iterator();
	}
}
