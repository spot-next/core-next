module io.spotnext.maven {
	exports io.spotnext.maven.mojo;
	exports io.spotnext.maven;
	exports io.spotnext.maven.velocity.type.parts;
	exports io.spotnext.maven.velocity.util;
	exports io.spotnext.maven.util;
	exports io.spotnext.maven.velocity;
	exports io.spotnext.maven.velocity.type.base;
	exports io.spotnext.maven.exception;
	exports io.spotnext.maven.velocity.type;
	exports io.spotnext.maven.velocity.type.annotation;

	requires annotations;
	requires com.fasterxml.jackson.core;
	requires com.fasterxml.jackson.databind;
	requires io.spotnext.infrastructure;
	requires io.spotnext.support;
	requires java.instrument;
	requires java.xml.bind;
	requires logback.core;
	requires org.apache.commons.collections4;
	requires org.apache.commons.io;
	requires org.apache.commons.lang3;
	requires plexus.build.api;
	requires spring.core;
	requires velocity.engine.core;
	requires roaster.api;
	requires maven.artifact;
	requires maven.core;
	requires maven.model;
	requires maven.plugin.annotations;
	requires maven.plugin.api;
	requires maven.plugin.registry;
	requires maven.project;
}