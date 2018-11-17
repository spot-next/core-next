package io.spotnext.spring.web.session;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

import io.spotnext.core.infrastructure.http.Session;
//import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * <p>WebSession class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
//@SuppressFBWarnings("UWF_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD")
public class WebSession extends Session implements HttpSession {

	protected ServletContext servletContext;
	protected HttpSessionContext sessionContext;

	/**
	 * <p>Constructor for WebSession.</p>
	 *
	 * @param id a {@link java.lang.String} object.
	 */
	public WebSession(final String id) {
		super(id);
	}

	/** {@inheritDoc} */
	@Override
	public ServletContext getServletContext() {
		return servletContext;
	}

	/** {@inheritDoc} */
	@Override
	public HttpSessionContext getSessionContext() {
		return sessionContext;
	}

	/** {@inheritDoc} */
	@Override
	public Object getValue(final String key) {
		return getAttribute(key);
	}

	/** {@inheritDoc} */
	@Override
	public String[] getValueNames() {
		return attributes.keySet().toArray(new String[0]);
	}

	/** {@inheritDoc} */
	@Override
	public void putValue(final String key, final Object value) {
		setAttribute(key, value);
	}

	/** {@inheritDoc} */
	@Override
	public void removeValue(final String key) {
		removeAttribute(key);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isNew() {
		// TODO Auto-generated method stub
		return false;
	}

}
