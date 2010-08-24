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

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.xml.NamespaceHandler;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * A NamespaceHandler that maps a GroovyBeanDefinitionTransformer to each spring
 * xml config element which namespace is matching.
 * 
 * This class is an abstract class. Subclasses must provide a default
 * constructor and set the super constructor parameters as hard coded strings.
 * 
 * @author <a href="mailto:baltes@objectcode.de">Sebastian Baltes</a>
 * @copyright (c) 2010 ObjectCode GmbH
 */
public abstract class SxgtNamespaceHandler implements NamespaceHandler {

	private final String namespaceUrl;
	private final String templatePackageName;

	/**
	 * Subclasses must provide a default constructor and set the super constructor
	 * parameters as hard coded strings.
	 * 
	 * @param namespaceUrl
	 *          the namespaceUrl is used to recognise the xml elements that you
	 *          defined in your own schema
	 * @param templatePackageName
	 *          the templatePackageName is the full qualified package name where
	 *          the groovy template files for your own schema can be found
	 */
	public SxgtNamespaceHandler(String namespaceUrl, String templatePackageName) {
		this.namespaceUrl = namespaceUrl;
		this.templatePackageName = templatePackageName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BeanDefinitionHolder decorate(Node _source,
			BeanDefinitionHolder _definition, ParserContext _parserContext) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BeanDefinition parse(Element _element, ParserContext _parserContext) {
		if (namespaceUrl.equals(_element.getNamespaceURI())) {
			return new GroovyBeanDefinitionTransformer(templatePackageName).parse(
					_element,
					_parserContext);
		}
		return null;
	}

}
