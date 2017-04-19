package at.spot.mail.model;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import at.spot.core.infrastructure.annotation.ItemType;
import at.spot.core.infrastructure.annotation.Property;
import at.spot.core.model.Item;

@ItemType(typeCode = "mail")
public class Mail extends Item {

	private static final long serialVersionUID = 1L;

	@Property
	public String sender;

	@Property
	final public List<String> toRecipients = new ArrayList<>();

	@Property
	final public List<String> ccReceipients = new ArrayList<>();

	@Property
	final public List<String> forwardToReceipients = new ArrayList<>();

	@Property
	public String content;

	@Property
	public DateTime receivedAt;

	@Property
	public DateTime sentAt;
}
