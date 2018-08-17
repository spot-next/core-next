package io.spotnext.sample.interceptor;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import io.spotnext.core.infrastructure.exception.ModelValidationException;
import io.spotnext.core.infrastructure.interceptor.ItemValidateInterceptor;
import io.spotnext.core.infrastructure.interceptor.impl.AbstractItemInterceptor;
import io.spotnext.sample.types.itemtypes.Party;

@Service
public class PartyValidateInterceptor extends AbstractItemInterceptor<Party> implements ItemValidateInterceptor<Party> {

	@Override
	public void onValidate(final Party item) throws ModelValidationException {
		if (item.isFixed()
				&& (item.getLocation() == null || (item.getDate() == null || item.getDate().isBefore(LocalDate.now()))
						|| item.getInvitedGuests().size() == 0)) {
			throw new ModelValidationException(
					"Party cannot be fixed as not all necessary properties (date, location, invitedGuests) are defined yet.");
		}
	}

	@Override
	public Class<Party> getItemType() {
		return Party.class;
	}
}
