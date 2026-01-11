package nl.matsgemmeke.battlegrounds.item.throwing;

public interface ThrowableItem {

    /**
     * Perform a throw on the item.
     *
     * @param performer the entity who performs the throw
     */
    void performThrow(ThrowPerformer performer);
}
