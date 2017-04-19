package at.spot.spring.web.message;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

public class NestedReloadingMessageSource extends ReloadableResourceBundleMessageSource {

	protected boolean ignoreMissingMessageCode = false;

	@Override
	protected String getMessageInternal(final String code, final Object[] args, final Locale locale) {
		String message = null;

		try {
			message = super.getMessageInternal(code, args, locale);
		} catch (final NoSuchMessageException e) {
			// catch exceptions from the parent message source and ignore it
		}

		if (ignoreMissingMessageCode && StringUtils.isBlank(message)) {
			message = String.format("<%s>", code);
		}

		return message;
	}

	// @Override
	// public final String getMessage(final MessageSourceResolvable resolvable,
	// final Locale locale) {
	// String message = null;
	//
	// for (final String code : resolvable.getCodes()) {
	// message = getMessageInternal(code, resolvable.getArguments(), locale);
	// }
	//
	// return message
	// }

	/**
	 * If no message is found and this is set to true, then the key will be
	 * returned as message. Otherwise a {@link NoSuchMessageException} is
	 * thrown.
	 * 
	 * @param ignoreMissingMessageCode
	 */
	public void setIgnoreMissingMessageCode(final boolean ignoreMissingMessageCode) {
		this.ignoreMissingMessageCode = ignoreMissingMessageCode;
	}
}
