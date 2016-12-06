package at.spot.spring.web.message;

import java.util.Locale;

import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

public class NestedReloadingMessageSource extends ReloadableResourceBundleMessageSource {

	@Override
	protected String getMessageInternal(final String code, final Object[] args, final Locale locale) {
		String message = null;

		try {
			message = super.getMessageInternal(code, args, locale);
		} catch (final NoSuchMessageException e) {
			// catch exceptions from the parent message source and ignore it
		}

		return message;
	}
}
