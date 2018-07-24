package at.spot.core.persistence.hibernate.support.usertypes;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import org.hibernate.collection.internal.PersistentSet;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.persister.collection.CollectionPersister;

import at.spot.core.persistence.hibernate.support.proxy.ProxyPersistentSet;
import at.spot.core.support.util.ClassUtil;
import at.spot.core.types.Item;

public class RelationshipMaintainingSetType extends AbstractCollectionType {
	@Override
	public PersistentCollection instantiate(final SharedSessionContractImplementor session,
			final CollectionPersister persister) {

		final PersistentSet set = new PersistentSet(session);

		return new ProxyPersistentSet(set, getOwnershipSetter(set), getOwnershipClearer(set));
	}

	@Override
	public Object instantiate(final int anticipatedSize) {
		return new HashSet<>();
	}

	@Override
	public PersistentCollection wrap(final SharedSessionContractImplementor session, final Object collection) {
		final PersistentSet set = new PersistentSet(session, (Set) unwrap((Collection<?>) collection));

		return new ProxyPersistentSet(set, getOwnershipSetter(set), getOwnershipClearer(set));
	}

	private RelationshipReferenceUpdater getOwnershipSetter(final PersistentCollection owner) {
		return new RelationshipReferenceUpdater(owner) {
			@Override
			public void accept(final Item t) {
				final String role = getOwner().getRole();
				final String property = role.substring(role.lastIndexOf("."), role.length());
				ClassUtil.setField(t, property, getOwner());
			}
		};
	}

	private RelationshipReferenceUpdater getOwnershipClearer(final PersistentCollection owner) {
		return new RelationshipReferenceUpdater(owner) {
			@Override
			public void accept(final Item t) {
				final String role = getOwner().getRole();
				final String property = role.substring(role.lastIndexOf("."), role.length());
				ClassUtil.setField(t, property, null);
			}
		};
	}

	private abstract class RelationshipReferenceUpdater implements Consumer<Item> {
		private final PersistentCollection owner;

		public RelationshipReferenceUpdater(final PersistentCollection owner) {
			this.owner = owner;
		}

		public PersistentCollection getOwner() {
			return owner;
		};
	}
}