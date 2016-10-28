package at.spot.mail.model;

import java.util.ArrayList;
import java.util.List;

import at.spot.core.infrastructure.annotation.model.ItemType;
import at.spot.core.infrastructure.annotation.model.Property;
import at.spot.core.infrastructure.type.collection.ObservableList;
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
	final public List<Mail> mails = new ObservableList<Mail>(ArrayList.class, this, "mails");

}
