package at.spot.core.model.user;

import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;

import at.spot.core.infrastructure.annotation.ItemType;
import at.spot.core.infrastructure.annotation.Property;
import at.spot.core.model.Item;
import at.spot.core.model.internationalization.Country;

@ItemType
public class Address extends Item {
	private static final long serialVersionUID = 1L;

	// @Relation(type = RelationType.OneToMany, endType =
	// RelationEndType.Source, relationItemType = AddressToUserRelation.class)
	@NotNull
	@Property(unique = true)
	public Item owner;

	@NotNull
	@Property(unique = true)
	public AddressType type;

	@Property
	public String streetName;

	@Property
	public String streetNumber;

	/**
	 * In most cases this represents the "state", "province" or "region".
	 */
	@Property
	public String administrativeArea;

	/**
	 * In most cases this represents the "county" or "district".
	 */
	@Property
	public String subAdministrativeArea;

	/**
	 * In most cases this represents the "city" or "town".
	 */
	@Property
	public String locality;

	/**
	 * In most cases this represents the "district" in a
	 * {@link Address#locality}.
	 */
	@Property
	public String dependentLocality;

	@Property
	public String postalCode;

	@Property
	public Country country;

	@Property
	public Map<ContactDetailsType, String> phoneNumbers = new HashMap<>();

	@Property
	public Map<ContactDetailsType, String> emailAddresses = new HashMap<>();

}
