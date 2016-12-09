package at.spot.mail.model;

import java.util.List;

import at.spot.core.infrastructure.annotation.ItemType;
import at.spot.core.infrastructure.annotation.Property;
import at.spot.core.model.Item;
import at.spot.core.model.user.User;
import at.spot.mail.type.MailBoxType;

@ItemType
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
