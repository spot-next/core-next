package at.spot.cms.service;

import at.spot.cms.restriction.RestrictionEvaluationResult;
import at.spot.itemtype.cms.model.CmsPage;

public interface CmsRestrictionService {

	RestrictionEvaluationResult checkRestrictions(CmsPage page);

}
