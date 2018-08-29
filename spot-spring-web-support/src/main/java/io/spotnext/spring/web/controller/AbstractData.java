package io.spotnext.spring.web.controller;

import java.io.Serializable;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

/**
 * <p>Abstract AbstractData class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public abstract class AbstractData extends ModelAndView implements Serializable {

    private static final long serialVersionUID = 1L;

    protected ModelMap model = null;

    /** {@inheritDoc} */
    @Override
    public ModelMap getModelMap() {
        if (this.model == null) {
            this.model = new DataContext(this);
        }

        return this.model;
    }
}
