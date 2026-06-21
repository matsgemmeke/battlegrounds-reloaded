package nl.matsgemmeke.battlegrounds.item;

/**
 * Represents an item that maintains temporary or player-specific state which should be reset when a game ends or a
 * player leaves.
 */
public interface Resettable {

    /**
     * Resets the internal state of this item.
     */
    void reset();
}
