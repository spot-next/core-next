package io.spotnext.core.infrastructure.resolver.impex.impl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import io.spotnext.core.infrastructure.exception.ValueResolverException;
import io.spotnext.core.infrastructure.resolver.impex.ImpexValueResolver;
import io.spotnext.core.infrastructure.service.TypeService;
import io.spotnext.core.infrastructure.service.impl.AbstractService;
import io.spotnext.core.infrastructure.support.impex.ColumnDefinition;
import io.spotnext.core.infrastructure.support.spring.Registry;
import io.spotnext.core.persistence.service.QueryService;

/**
 * <p>
 * ReferenceValueResolver class.
 * </p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@Service
public class FileValueResolver extends AbstractService implements ImpexValueResolver<Object> {

	@Resource
	private TypeService typeService;

	@Resource
	private QueryService queryService;

	/** {@inheritDoc} */
	@Override
	public Object resolve(final String value, final Class<Object> targetType, final List<Class<?>> genericArguments,
			final ColumnDefinition columnDefinition) throws ValueResolverException {

		if (StringUtils.isBlank(value)) {
			return null;
		}

		Object fileContent = null;

		try {
			byte[] fileByteContent = null;

			Path path = Paths.get(value);

			// if the path is aboslute, we can just laod it from the file system
			if (path.isAbsolute()) {
				fileByteContent = Files.readAllBytes(path);
			} else {
				// otherwise it is a file from the classpath
				fileByteContent = IOUtils.toByteArray(Registry.getMainClass().getResourceAsStream("/" + value));
			}

			if (String.class.isAssignableFrom(targetType)) {
				fileContent = new String(fileByteContent, StandardCharsets.UTF_8);
			} else if (char[].class.isAssignableFrom(targetType)) {
				fileContent = new String(fileByteContent, StandardCharsets.UTF_8).toCharArray();
			} else if (byte[].class.isAssignableFrom(targetType)) {
				fileContent = fileByteContent;
			} else {
				throw new ValueResolverException(String.format("Cannot load file into object of type '%s'", targetType.getClass()));
			}
		} catch (IOException e) {
			throw new ValueResolverException(String.format("Could not read file: %s", value), e);
		}

		return fileContent;
	}
}
