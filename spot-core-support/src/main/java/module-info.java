module io.spotnext.support {
	exports io.spotnext.support;
	exports io.spotnext.support.weaving;
	exports io.spotnext.support.exception;
	exports io.spotnext.support.util;

	requires java.xml;
	requires org.assertj.core;
	requires org.aspectj.weaver;
	requires java.instrument;
	requires android.json;
	requires annotations;
	requires javassist;
	requires junit;
	requires org.apache.commons.collections4;
	requires org.apache.commons.lang3;
	requires slf4j.api;
	requires spring.beans;
	requires spring.core;
	requires spring.expression;
}