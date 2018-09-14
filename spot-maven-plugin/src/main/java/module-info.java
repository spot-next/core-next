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

	requires io.spotnext.infrastructure;
	requires annotations;
	requires com.fasterxml.jackson.core;
	requires com.fasterxml.jackson.databind;
	requires commons.collections4;
	requires commons.lang3;
	requires jalopy;
	requires java.instrument;
	requires logback.core;
	requires maven.artifact;
	requires maven.core;
	requires maven.model;
	requires maven.plugin.annotations;
	requires maven.plugin.api;
	requires maven.project;
	requires org.apache.commons.io;
	requires plexus.build.api;
	requires spring.core;
	requires velocity.engine.core;
	requires java.base;
}