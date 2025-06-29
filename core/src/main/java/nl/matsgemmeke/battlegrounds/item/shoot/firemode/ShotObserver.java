package nl.matsgemmeke.battlegrounds.item.shoot.firemode;

/**
 * Observer interface for receiving shot events from a {@link FireMode}.
 */
public interface ShotObserver {

    /**
     * Called when a shot is fired as part of the firing cycle.
     */
    void onShotFired();
}
