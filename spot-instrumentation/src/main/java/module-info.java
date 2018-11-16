module io.spotnext.instrumentation {
	exports io.spotnext.instrumentation.internal;
	exports io.spotnext.instrumentation;

	requires org.apache.commons.io;
	requires java.instrument;
	requires java.management;
	requires jdk.attach;
	requires jdk.unsupported;
//	requires jsr305;
	requires slf4j.api;
	requires spring.beans;
	requires spring.context;
	requires spring.core;
	requires spring.instrument;
}