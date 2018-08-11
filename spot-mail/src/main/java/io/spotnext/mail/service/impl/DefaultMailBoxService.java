package io.spotnext.mail.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.spotnext.core.infrastructure.exception.ModelSaveException;
import io.spotnext.core.infrastructure.exception.ModelValidationException;
import io.spotnext.core.infrastructure.service.ModelService;
import io.spotnext.core.model.user.User;
import io.spotnext.core.persistence.exception.ModelNotUniqueException;
import io.spotnext.mail.model.MailBox;
import io.spotnext.mail.service.MailBoxService;

@Service
public class DefaultMailBoxService implements MailBoxService {

	@Autowired
	protected ModelService modelService;

	@Override
	public MailBox getMailBoxForUser(final User owner) {
		final Map<String, Comparable<?>> condition = new HashMap<>();
		condition.put("owner.uid", owner.getId());

		MailBox mailbox = null;

		try {
			mailbox = modelService.get(MailBox.class, condition);

			if (mailbox == null) {
				mailbox = modelService.create(MailBox.class);
				mailbox.owner = owner;
				modelService.save(mailbox);
			}
		} catch (ModelSaveException | ModelNotUniqueException | ModelValidationException e) {
			e.printStackTrace();
		}

		return mailbox;

	}

	@Override
	public MailBox getMailBoxByAlias(final String alias) {
		// TODO Auto-generated method stub
		return null;
	}

}
