package at.spot.core.model;

import at.spot.core.infrastructure.annotation.Property;

public abstract class Relation<SOURCE extends Item, TARGET extends Item> extends Item {

    private static final long serialVersionUID = 1L;

    @Property(unique = true)
    protected SOURCE source;

    @Property
    protected TARGET target;

    public SOURCE getSource() {
        return source;
    }

    public void setSource(SOURCE source) {
        this.source = source;
    }

    public TARGET getTarget() {
        return target;
    }

    public void setTarget(TARGET target) {
        this.target = target;
    }
}
