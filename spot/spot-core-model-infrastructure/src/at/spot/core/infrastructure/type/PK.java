package at.spot.core.infrastructure.type;

import java.math.BigInteger;

import at.spot.core.model.Item;
import java.util.Objects;

public class PK extends BigInteger {

    private static final long serialVersionUID = 1L;

    protected Class<? extends Item> type;

    public PK(long pk, Class<? extends Item> type) {
        super(String.valueOf(pk));
        this.type = type;
    }

    public Class<? extends Item> getType() {
        return type;
    }

    public void setType(Class<? extends Item> type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object x) {
        if (x == null || !(x instanceof PK)) {
            return false;
        }

        if (this == x) {
            return true;
        }

        return type.equals(((PK) x).getType()) && super.equals((BigInteger) x);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, doubleValue());
    }

}
