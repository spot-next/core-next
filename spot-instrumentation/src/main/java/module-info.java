module io.spotnext.instrumentation {
	exports io.spotnext.instrumentation.internal;
	exports io.spotnext.instrumentation;
	exports io.spotnext.instrumentation.transformer;

	requires annotations;
	requires assertj.core;
	requires fast.classpath.scanner;
	requires java.instrument;
	requires java.management;
	requires javassist;
	requires jdk.unsupported;
	requires jsr305;
	requires org.apache.commons.io;
	requires org.apache.commons.lang3;
	requires org.slf4j;
	requires spring.beans;
	requires spring.context;
	requires spring.core;
	requires spring.instrument;
	requires zt.exec;
}