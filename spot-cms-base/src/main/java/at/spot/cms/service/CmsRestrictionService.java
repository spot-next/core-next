package at.spot.cms.service;

import at.spot.cms.model.CmsPage;
import at.spot.cms.restriction.RestrictionEvaluationResult;

public interface CmsRestrictionService {

	RestrictionEvaluationResult checkRestrictions(CmsPage page);

}
