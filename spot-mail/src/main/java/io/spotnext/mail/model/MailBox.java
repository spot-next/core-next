package io.spotnext.mail.model;

import java.util.List;

import io.spotnext.core.infrastructure.annotation.ItemType;
import io.spotnext.core.infrastructure.annotation.Property;
import io.spotnext.core.model.Item;
import io.spotnext.core.model.user.User;
import io.spotnext.mail.type.MailBoxType;

@ItemType(typeCode = "mailBox")
public class MailBox extends Item {

	private static final long serialVersionUID = 1L;

	@Property(unique = true)
	public User owner;

	@Property
	public String alias;

	@Property
	public MailBoxType type;

	@Property
	public List<Mail> mails;

}
