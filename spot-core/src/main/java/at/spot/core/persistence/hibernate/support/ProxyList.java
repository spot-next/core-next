package at.spot.core.persistence.hibernate.support;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import org.hibernate.collection.internal.PersistentList;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

public class ProxyList extends PersistentList {
	private static final long serialVersionUID = 1L;

	public ProxyList(final SharedSessionContractImplementor session) {
		super(session);
	}

	public ProxyList(final SharedSessionContractImplementor session, final List collection) {
		super(session, collection);
	}

	private void setRelationMapping(final Object value) {

	}

	private void removeRelationMapping(final Object value) {

	}

	@Override
	public void add(final int index, final Object value) {
		super.add(index, value);
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
	public boolean addAll(final int index, final Collection coll) {
		return super.addAll(index, coll);
	}

	@Override
	public void clear() {
		super.clear();
	}

	@Override
	public Object remove(final int index) {
		return super.remove(index);
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

	@Override
	public Object set(final int index, final Object value) {
		return super.set(index, value);
	}

}
