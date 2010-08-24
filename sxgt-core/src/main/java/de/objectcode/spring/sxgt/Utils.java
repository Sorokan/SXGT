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
package de.objectcode.spring.sxgt;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Utils used in templates and GroovyBeanDefinitionTransformer
 * 
 * @author <a href="mailto:baltes@objectcode.de">Sebastian Baltes</a>
 * @copyright (c) 2010 ObjectCode GmbH
 */
public class Utils {

	/**
	 * @param _strings
	 *          one or more qualified (with package) java class or ressource names
	 * @return appended unqualified names (without package)
	 */
	public static String unqualifiedName(String... _strings) {
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < _strings.length; i++) {
			b.append(StringUtils.substringAfterLast(_strings[i], "."));
		}
		return b.toString();
	}

	/**
	 * wraps an exception as a runtime exception (if it's not allready a runtime
	 * exception) with an addtitional message text
	 * 
	 * @param _e
	 * @param _additionalMessage
	 * @return original exception wrapped in a runtime exception
	 */
	public static RuntimeException wrapped(Exception _e, String _additionalMessage) {
		if (_e == null && _e instanceof RuntimeException) {
			return (RuntimeException) _e;
		}
		RuntimeException re = new RuntimeException(_e.getMessage() + " "
				+ _additionalMessage, _e);
		re.setStackTrace(_e.getStackTrace());
		return re;
	}

	/**
	 * wraps an exception as a runtime exception (if it's not allready a runtime
	 * exception)
	 * 
	 * @param _e
	 * @return original exception wrapped in a runtime exception
	 */
	public static RuntimeException wrapped(Exception _e) {
		return wrapped(_e, null);
	}

	/**
	 * dumps some xml node as as a pretty formatted text
	 * 
	 * @param _node
	 * @param withXmlDeclaration
	 *          if a xml declaration should be printed
	 * @return dumped xml text
	 * @throws TransformerFactoryConfigurationError
	 * @throws TransformerException
	 */
	public static String xmlToString(Node _node, boolean withXmlDeclaration)
			throws TransformerFactoryConfigurationError, TransformerException {
		if (_node == null) {
			return "";
		}
		DOMSource source = new DOMSource(_node);
		StreamResult result = new StreamResult(new StringWriter());
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		if (!withXmlDeclaration) {
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		}
		transformer.transform(source, result);
		String xmlString = result.getWriter().toString();
		return xmlString;
	}

	/**
	 * parses a xml string into a document
	 * 
	 * @param xml
	 *          string
	 * @return xml dom document
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static Document parseXmlString(String xml)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setNamespaceAware(true);
		DocumentBuilder parser = factory.newDocumentBuilder();
		Document doc = parser.parse(new InputSource(new StringReader(xml)));
		return doc;
	}

	/**
	 * gets a resource as an input stream
	 * 
	 * @param (qualified) name of the resource
	 * @return input stream of the resource
	 */
	public static InputStream getResource(String name) {
		return Thread.currentThread().getContextClassLoader().getResourceAsStream(
				name);
	}

}
