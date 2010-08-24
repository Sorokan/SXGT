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

import static de.objectcode.spring.sxgt.Utils.parseXmlString;
import static de.objectcode.spring.sxgt.Utils.wrapped;
import groovy.text.SimpleTemplateEngine;
import groovy.text.Template;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.DescriptiveResource;
import org.springframework.core.io.Resource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * For every SXGT element this class looks up a matching template and outputs a
 * xml containing standard bean definitions. This generated xml is then imported
 * into the spring context.
 * 
 * @author <a href="mailto:baltes@objectcode.de">Sebastian Baltes</a>
 * @copyright (c) 2010 ObjectCode GmbH
 */
public class GroovyBeanDefinitionTransformer implements BeanDefinitionParser {

	private static final Log LOG = LogFactory
			.getLog(GroovyBeanDefinitionTransformer.class);

	public static final String SXGT_PROFILE = "sxgt.profile";

	private final String templatePackageName;
	private String profilePostfixUsed;
	private Element element;
	private ParserContext parserContext;
	private StringBuilder errorExplanation;
	private String generatedXml;

	public GroovyBeanDefinitionTransformer(String templatePackageName) {
		this.templatePackageName = templatePackageName;
	}

	@Override
	public BeanDefinition parse(Element _element, ParserContext _parserContext) {
		element = _element;
		parserContext = _parserContext;
		try {
			if ("generator".equals(element.getLocalName())) {
				setGeneratorSystemProperties();
			} else {
				expandSpringConfig();
			}
			return null;
		} catch (Exception e) {
			throw wrapped(e);
		}
	}

	private void expandSpringConfig()
			throws TransformerFactoryConfigurationError, TransformerException {
		errorExplanation = new StringBuilder();
		errorExplanation
				.append(String
						.format(
								" assigned to element [%s] in document [%s]. %s transforms custom spring config xml extensions "
										+ "by the use of groovy .tmpl scripts into ordinary spring bean xml definitions. "
										+ "For each spring element from the gd namespace must exist a similar named "
										+ ".tmpl file in classpath.", getDescriptiveId(),
								getDescriptiveDocumentURI(), getClass().getSimpleName()));
		Document xmlResult = transform();
		XmlBeanDefinitionReader reader = parserContext.getReaderContext()
				.getReader();
		Resource resource = new DescriptiveResource("[" + getTmplName()
				+ "] assigned to element [" + getDescriptiveId() + "] in document ["
				+ getDescriptiveDocumentURI() + "]");
		try {
			reader.registerBeanDefinitions(xmlResult, resource);
		} catch (Exception e) {
			try {
				LOG
						.error("SXGT SPRING TEMPLATE ERROR:\n"
								+ e
								+ "\n"
								+ "while registering the following on-the-fly generated spring config into the spring bean context:"
								+ "\n\n" + generatedXml + "\n\n"
								+ "this xml was generated from the template file "
								+ getTmplName() + " for the spring config file "
								+ getDescriptiveDocumentURI()
								+ ", in detail for the element:\n\n"
								+ Utils.xmlToString(element, true));
			} catch (Throwable ex) {
				// nothing
			}
			throw wrapped(e, "(error in " + errorExplanation + ")");
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug("transformed the following input node:");
			LOG.debug(Utils.xmlToString(element, true));
			LOG.debug("into the following bean definition (using template file "
					+ getTmplName() + "):");
			LOG.debug(Utils.xmlToString(xmlResult, true));
		}
	}

	private Document transform() {
		InputStream in = getInputStream();
		if (in == null) {
			throw new RuntimeException("Could not load groovy template file "
					+ getTmplName() + errorExplanation);
		}
		errorExplanation.insert(0, String.format("in template file [%s]",
				getTmplName()));
		String templateContent = null;
		try {
			in = getInputStream();
			templateContent = IOUtils.toString(in);
			Template template;
			try {
				Map<String, Object> binding = new HashMap<String, Object>();
				binding.put("element", new DomGroovyObject(element));
				binding.put("Utils", new Utils());
				binding.put("profilePostfix", getProfilePostfixFromSystemProperty());
				SimpleTemplateEngine engine = new SimpleTemplateEngine();
				if (LOG.isTraceEnabled()) {
					engine.setVerbose(true);
				}
				template = engine.createTemplate(templateContent);
				generatedXml = template.make(binding).toString();
			} catch (Exception e) {
				try {
					LOG.error("SXGT SPRING TEMPLATE ERROR:\n" + e + "\n"
							+ "while executing the template file " + getTmplName()
							+ ", content is:\n\n" + templateContent + "\n\n"
							+ "the template is needed for the spring config file "
							+ getDescriptiveDocumentURI()
							+ ", in detail for the element:\n\n"
							+ Utils.xmlToString(element, true));
				} catch (Throwable ex) {
					// nothing
				}
				throw e;
			}
			try {
				Document doc = parseXmlString(generatedXml);
				return doc;
			} catch (Exception e) {
				try {
					LOG
							.error("SXGT SPRING TEMPLATE ERROR:\n"
									+ e
									+ "\n"
									+ "while parsing the following on-the-fly generated spring config:"
									+ "\n\n" + generatedXml + "\n\n"
									+ "this xml was generated from the template file "
									+ getTmplName() + " for the spring config file "
									+ getDescriptiveDocumentURI()
									+ ", in detail for the element:\n\n"
									+ Utils.xmlToString(element, true));
				} catch (Throwable ex) {
					// nothing
				}
				throw e;
			}
		} catch (Exception e) {
			throw wrapped(e, "(error in " + errorExplanation + ")");
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	/**
	 * reads in "{element.name}-{profile}.tmpl" or as a fallback
	 * "{element.name}.tmpl"
	 */
	private InputStream getInputStream() {
		profilePostfixUsed = "-" + getSxgtProfile();
		InputStream in = Utils.getResource(getTmplName());
		if (in == null) {
			profilePostfixUsed = "";
			in = Utils.getResource(getTmplName());
		}
		return in;
	}

	private String getSxgtProfile() {
		// "", "unitTests" or other
		return System.getProperty(SXGT_PROFILE, "");
	}

	private void setGeneratorSystemProperties() {
		String value = element.getAttribute("profile");
		if (System.getProperty(SXGT_PROFILE) == null) {
			LOG.debug("setting system property " + SXGT_PROFILE + "=" + value);
			System.setProperty(SXGT_PROFILE, value);
		} else {
			LOG
					.debug("generator profile setting is ignored because system property already set to "
							+ SXGT_PROFILE + "=" + System.getProperty(SXGT_PROFILE));
		}
	}

	private String getProfilePostfixFromSystemProperty() {
		return "-" + getSxgtProfile();
	}

	private String getTmplName() {
		String tmplName = getTemplatePackageName().replace('.', '/');
		return tmplName + (tmplName.endsWith("/") ? "" : "/")
				+ element.getLocalName()
				+ profilePostfixUsed + ".tmpl";
	}

	public String plot(Element _element) {
		try {
			element = _element;
			Document xmlResult = transform();
			return Utils.xmlToString(xmlResult, true);
		} catch (Exception e) {
			throw wrapped(e);
		}
	}

	private String getDescriptiveId() {
		String id = element.getAttribute("id");
		if (!StringUtils.isEmpty(id)) {
			return "<" + element.getNodeName() + " id='" + id + "' ...>";
		}
		id = element.getAttribute("name");
		if (!StringUtils.isEmpty(id)) {
			return "<" + element.getNodeName() + " name='" + id + "' ...>";
		}
		NamedNodeMap attributes = element.getAttributes();
		if (attributes != null) {
			if (attributes.getLength() > 0) {
				Node attribute = attributes.item(0);
				return "<" + element.getNodeName() + " " + attribute.getNodeName()
						+ "='" + attribute.getNodeValue() + "' ...>";
			}
		}
		return "<" + element.getNodeName() + "' ...>";
	}

	private String getDescriptiveDocumentURI() {
		return parserContext.getReaderContext().getResource().getDescription();
	}

	public String getTemplatePackageName() {
		return templatePackageName;
	}

}
