package at.spot.core.infrastructure.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.spot.core.infrastructure.annotation.Relation;
import at.spot.core.infrastructure.service.ModelService;
import at.spot.core.infrastructure.service.TypeService;
import at.spot.core.infrastructure.support.ItemTypePropertyDefinition;
import at.spot.core.infrastructure.support.RelationProxyList;
import at.spot.core.model.Item;
import at.spot.core.support.util.ClassUtil;

@SuppressWarnings("unchecked")
@Service
public abstract class AbstractModelService extends AbstractService implements ModelService {

	@Autowired
	protected TypeService typeService;

	@Override
	public <T extends Item> T create(final Class<T> type) {
		final String typeCode = typeService.getTypeCode(type);

		final T item = getApplicationContext().getBean(typeCode, type);
		setTypeCode(item);
		initRelationProperties(item);
		return item;
	}

	protected <T extends Item> void setTypeCode(final T item) {
		ClassUtil.setField(item, "typeCode", typeService.getTypeCode(item.getClass()));
	}

	@Override
	public <T extends Item> T createProxyModel(final Class<T> itemType, final long pk) {
		T proxyItem = null;

		proxyItem = create(itemType);
		ClassUtil.setField(proxyItem, "isProxy", true);

		proxyItem.setPk(pk);

		return proxyItem;
	}

	@Override
	public <T extends Item> T createProxyModel(final T item) {
		return createProxyModel((Class<T>) item.getClass(), item.getPk());
	}

	/**
	 * Create new {@link RelationProxyList} when accessing a relation property
	 * 
	 * @param joinPoint
	 * @param rel
	 * @return
	 * @throws Throwable
	 */
	protected <T extends Item> void initRelationProperties(final T referencingItem) {
		for (final ItemTypePropertyDefinition p : typeService.getItemTypeProperties(referencingItem.getClass())
				.values()) {

			List<Item> proxyList;

			// if the property is a relation we setup a proxy relation list
			if (p.relationDefinition != null && referencingItem.getPk() != null) {
				final Relation rel = ClassUtil.getAnnotation(referencingItem.getClass(), p.name, Relation.class);

				proxyList = new RelationProxyList<Item>(rel, referencingItem,
						typeService.isPropertyUnique(rel.referencedType(), rel.mappedTo()), p.name, () -> {
							referencingItem.markAsDirty(p.name);
						});
			} else {
				proxyList = new ArrayList<>();
			}

			ClassUtil.setField(referencingItem, p.name, proxyList);
		}
	}
}
