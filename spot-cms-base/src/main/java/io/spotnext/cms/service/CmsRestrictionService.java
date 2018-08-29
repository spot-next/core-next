package io.spotnext.cms.service;

import io.spotnext.cms.restriction.RestrictionEvaluationResult;
import io.spotnext.itemtype.cms.model.CmsPage;

/**
 * <p>CmsRestrictionService interface.</p>
 */
public interface CmsRestrictionService {

	/**
	 * <p>checkRestrictions.</p>
	 *
	 * @param page a {@link io.spotnext.itemtype.cms.model.CmsPage} object.
	 * @return a {@link io.spotnext.cms.restriction.RestrictionEvaluationResult} object.
	 */
	RestrictionEvaluationResult checkRestrictions(CmsPage page);

}
