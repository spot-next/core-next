package at.spot.core.infrastructure.type;

public class ItemPropertyDefinition {

	public final String name;
	public final String returnType;
	public final boolean isReadable;
	public final boolean isWritable;
	public final boolean isInitial;
	public final boolean isUnique;
	public final String itemValueProvider;

	public ItemPropertyDefinition(String name, String returnType, boolean isReadable, boolean isWritable,
			boolean isInitial, boolean isUnique, String itemValueProvider) {

		this.name = name;
		this.returnType = returnType;
		this.isReadable = isReadable;
		this.isWritable = isWritable;
		this.isInitial = isInitial;
		this.isUnique = isUnique;
		this.itemValueProvider = itemValueProvider;
	}
}
