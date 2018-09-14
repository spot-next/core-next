module io.spotnext.support {
	exports io.spotnext.support;
	exports io.spotnext.core.support.util;
	exports io.spotnext.support.exception;
	exports io.spotnext.support.util;
	exports io.spotnext.core.support.util.data;

	requires annotations;
	requires aspectjweaver;
	requires commons.collections4;
	requires gson;
	requires junit;
	requires org.apache.commons.lang3;
	requires slf4j.api;
	requires spring.beans;
	requires spring.core;
	requires spring.expression;
}