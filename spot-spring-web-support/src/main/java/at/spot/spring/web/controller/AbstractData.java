package at.spot.spring.web.controller;

import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

public abstract class AbstractData extends ModelAndView {

	protected ModelMap model = null;

	@Override
	public ModelMap getModelMap() {
		if (this.model == null) {
			this.model = new DataContext(this);
		}

		return this.model;
	}
}
