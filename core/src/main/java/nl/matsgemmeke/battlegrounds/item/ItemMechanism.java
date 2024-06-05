package nl.matsgemmeke.battlegrounds.item;

/**
 * A mechanism that is part of an {@link Item}, that can be activated or operated by its holder.
 */
public interface ItemMechanism {

    /**
     * Attempts to cancel the operation.
     *
     * @return whether the operation was cancelled
     */
    boolean cancel();
}
