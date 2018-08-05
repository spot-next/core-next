package io.spotnext.cms.service;

import io.spotnext.cms.restriction.RestrictionEvaluationResult;
import io.spotnext.itemtype.cms.model.CmsPage;

public interface CmsRestrictionService {

	RestrictionEvaluationResult checkRestrictions(CmsPage page);

}
