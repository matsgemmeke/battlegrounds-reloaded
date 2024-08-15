package nl.matsgemmeke.battlegrounds.item.holder;

/**
 * An entity capable of throwing items.
 */
public interface ItemThrower {

    /**
     * Gets whether the entity is currently able to throw items.
     *
     * @return whether the entity can throw items
     */
    boolean isAbleToThrow();

    /**
     * Sets whether the entity is currently able to throw items.
     *
     * @param ableToThrow whether the entity can throw items
     */
    void setAbleToThrow(boolean ableToThrow);
}
