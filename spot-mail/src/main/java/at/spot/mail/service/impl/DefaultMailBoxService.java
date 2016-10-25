package at.spot.mail.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.spot.core.infrastructure.exception.ModelNotFoundException;
import at.spot.core.infrastructure.exception.ModelSaveException;
import at.spot.core.infrastructure.service.ModelService;
import at.spot.core.model.user.User;
import at.spot.mail.model.MailBox;
import at.spot.mail.service.MailBoxService;

@Service
public class DefaultMailBoxService implements MailBoxService {

	@Autowired
	protected ModelService modelService;

	@Override
	public MailBox getMailBoxForUser(User owner) {
		Map<String, Object> condition = new HashMap<>();
		condition.put("owner", owner);

		MailBox mailbox = null;

		try {
			mailbox = modelService.get(MailBox.class, condition);

			if (mailbox == null) {
				mailbox = modelService.create(MailBox.class);
				mailbox.owner = owner;
				modelService.save(mailbox);
			}
		} catch (ModelNotFoundException | ModelSaveException e) {
			e.printStackTrace();
		}

		return mailbox;

	}

	@Override
	public MailBox getMailBoxByAlias(String alias) {
		// TODO Auto-generated method stub
		return null;
	}

}
