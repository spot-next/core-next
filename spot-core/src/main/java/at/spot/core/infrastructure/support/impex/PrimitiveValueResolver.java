package at.spot.core.infrastructure.support.impex;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;

public class PrimitiveValueResolver implements ImpexValueResolver {

	@Override
	public Object resolve(String value) {
		Object retVal = null;

		Number numberVal = toNumber(value);
		Boolean boolVal = toBoolean(value);

		if (numberVal != null) {
			retVal = numberVal;
		} else if (boolVal != null) {
			retVal = numberVal;
		} else {
			retVal = value;
		}

		return retVal;
	}

	private Boolean toBoolean(String value) {
		return BooleanUtils.toBooleanObject(value);
	}

	private Number toNumber(String value) {
		if (NumberUtils.isCreatable(value)) {
			return NumberUtils.createNumber(value);
		}

		return null;
	}
}
