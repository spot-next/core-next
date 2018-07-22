package at.spot.core.persistence.hibernate.support;

import java.util.Collection;
import java.util.function.Predicate;

import org.hibernate.collection.internal.PersistentBag;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

public class ProxyBag extends PersistentBag {
	private static final long serialVersionUID = 1L;

	public ProxyBag(final SharedSessionContractImplementor session) {
		super(session);
	}

	public ProxyBag(final SharedSessionContractImplementor session, final Collection collection) {
		super(session, collection);
	}

	private void setRelationMapping(final Object value) {

	}

	private void removeRelationMapping(final Object value) {

	}

	@Override
	public boolean addAll(final Collection values) {
		return super.addAll(values);
	}

	@Override
	public boolean add(final Object object) {
		return super.add(object);
	}

	@Override
	public void clear() {
		super.clear();
	}

	@Override
	public boolean remove(final Object value) {
		return super.remove(value);
	}

	@Override
	public boolean removeAll(final Collection coll) {
		return super.removeAll(coll);
	}

	@Override
	public boolean removeIf(final Predicate filter) {
		return super.removeIf(filter);
	}

}
