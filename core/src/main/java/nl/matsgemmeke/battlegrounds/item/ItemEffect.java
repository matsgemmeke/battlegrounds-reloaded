package nl.matsgemmeke.battlegrounds.item;

/**
 * Represents an effect on a {@link Item}.
 */
public interface ItemEffect {

    /**
     * Applies the effect.
     */
    void apply();

    /**
     * Removes the effect.
     */
    void remove();

    /**
     * Attempts to update the effect on the player.
     *
     * @return whether the effect was updated
     */
    boolean update();
}
