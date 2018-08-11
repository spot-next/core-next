package io.spotnext.mail.service;

import io.spotnext.core.model.user.User;
import io.spotnext.mail.model.MailBox;

/**
 * Offers {@link MailBox} related functionality.
 */
public interface MailBoxService {

	/**
	 * Returns the {@link MailBox} for a given {@link User}.
	 * 
	 * @param owner
	 * @return
	 */
	MailBox getMailBoxForUser(User owner);

	/**
	 * Returns the {@link MailBox} for the given email address.å
	 * 
	 * @param emailAddress
	 * @return
	 */
	MailBox getMailBoxByAlias(String alias);
}
