package at.spot.mail.model;

import java.util.List;

import org.joda.time.DateTime;

import at.spot.core.infrastructure.annotation.model.ItemType;
import at.spot.core.infrastructure.annotation.model.Property;
import at.spot.core.model.Item;

@ItemType
public class Mail extends Item {

	private static final long serialVersionUID = 1L;

	@Property
	public String sender;

	@Property
	public List<String> toRecipients;

	@Property
	public List<String> ccReceipients;

	@Property
	public List<String> forwardToReceipients;

	@Property
	public String content;

	@Property
	public DateTime receivedAt;

	@Property
	public DateTime sentAt;
}
