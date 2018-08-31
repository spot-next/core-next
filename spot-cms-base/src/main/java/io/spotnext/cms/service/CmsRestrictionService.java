package io.spotnext.cms.service;

import io.spotnext.cms.restriction.RestrictionEvaluationResult;
import io.spotnext.itemtype.cms.AbstractCmsComponent;

/**
 * Checks CMS components for visibility restrictions.
 */
public interface CmsRestrictionService {

	/**
	 * Checks CMS components for visibility restrictions.
	 *
	 * @param component to check for restrictions
	 * @return the result of the restriction evaluation
	 */
	RestrictionEvaluationResult checkRestrictions(AbstractCmsComponent component);

}
