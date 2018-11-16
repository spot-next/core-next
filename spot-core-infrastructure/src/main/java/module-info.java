module io.spotnext.infrastructure {
	exports io.spotnext.infrastructure.maven.xml;
	exports io.spotnext.infrastructure;
	exports io.spotnext.infrastructure.instrumentation;
	exports io.spotnext.infrastructure.maven;
	exports io.spotnext.infrastructure.annotation;
	exports io.spotnext.infrastructure.constants;
	exports io.spotnext.infrastructure.type;
	exports io.spotnext.infrastructure.handler;

	requires annotations;
	requires com.fasterxml.jackson.databind;
	requires io.spotnext.support;
	requires jackson.annotations;
	requires java.instrument;
	requires java.persistence;
	requires java.validation;
	requires java.xml.bind;
	requires javassist;
	requires org.apache.commons.collections4;
	requires org.apache.commons.lang3;
	requires org.aspectj.weaver;
	requires org.hibernate.orm.core;
	requires org.hibernate.commons.annotations;
	requires slf4j.api;
	requires spring.beans;
	requires spring.context;
	requires spring.data.commons;
	requires spring.data.jpa;
}