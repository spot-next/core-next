open module io.spotnext.cms {
	exports io.spotnext.cms.rendering.provider;
	exports io.spotnext.cms.annotations;
	exports io.spotnext.cms.rendering.transformers;
	exports io.spotnext.cms.strategy.impl;
	exports io.spotnext.cms;
	exports io.spotnext.cms.restriction;
	exports io.spotnext.cms.service;
	exports io.spotnext.cms.rendering.view;
	exports io.spotnext.cms.strategy;
	exports io.spotnext.cms.endpoints;
	exports io.spotnext.cms.rendering.resolver;
	exports io.spotnext.cms.service.impl;
	exports io.spotnext.cms.exception;

	requires io.spotnext.core;
	requires io.spotnext.infrastructure;
	requires io.spotnext.support;
	requires com.fasterxml.jackson.core;
	requires com.fasterxml.jackson.databind;
	requires java.annotation;
	requires java.validation;
	requires javax.servlet.api;
	requires org.apache.commons.lang3;
	requires org.hibernate.validator;
	requires spark.core;
	requires spring.beans;
	requires spring.boot.autoconfigure;
	requires spring.context;
	requires spring.core;
	requires thymeleaf;
	requires thymeleaf.extras.java8time;
	requires thymeleaf.spring5;
}