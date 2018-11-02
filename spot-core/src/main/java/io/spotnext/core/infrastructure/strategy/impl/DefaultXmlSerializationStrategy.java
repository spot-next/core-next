package io.spotnext.core.infrastructure.strategy.impl;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;

/**
 * <p>
 * DefaultXmlSerializationStrategy class.
 * </p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@Service
public class DefaultXmlSerializationStrategy extends AbstractJacksonSerializationStrategy {
	@Override
	protected ObjectMapper createMapper() {
		// enables serialization to XML
		final JacksonXmlModule xmlModule = new JacksonXmlModule();
//		xmlModule.setDefaultUseWrapper(false);
		
		final ObjectMapper objectMapper = new XmlMapper(xmlModule);
		
//		objectMapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);
//		objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
//		objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);

//		objectMapper.configure(MapperFeature.USE_WRAPPER_NAME_AS_PROPERTY_NAME, true);
//		objectMapper.configure(MapperFeature.USE_ANNOTATIONS, true);
		
		// support JAXB annotations
		objectMapper.registerModule(new JaxbAnnotationModule());

		return objectMapper;
	}
}
