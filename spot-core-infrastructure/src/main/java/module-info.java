module io.spotnext.infrastructure {
	exports io.spotnext.infrastructure.maven.xml;
	exports io.spotnext.infrastructure.spring;
	exports io.spotnext.infrastructure;
	exports io.spotnext.infrastructure.instrumentation;
	exports io.spotnext.infrastructure.maven;
	exports io.spotnext.infrastructure.annotation;
	exports io.spotnext.infrastructure.constants;
	exports io.spotnext.infrastructure.type;
	exports io.spotnext.infrastructure.handler;

	requires java.base;
	requires annotations;
	requires aspectjweaver;
	requires com.fasterxml.jackson.databind;
	requires commons.collections4;
	requires hibernate.core;
	requires hibernate.jpa;
	requires jackson.annotations;
	requires java.instrument;
	requires java.validation;
	requires javassist;
	requires org.apache.commons.lang3;
	requires org.slf4j;
	requires io.spotnext.support;
	requires io.spotnext.instrumentation;
	requires spring.beans;
	requires spring.context;
	requires spring.data.commons;
	requires spring.data.jpa;
	requires java.se.ee;
	requires java.xml.bind;
	
}