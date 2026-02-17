package nl.matsgemmeke.battlegrounds.item.reload;

/**
 * An item that is can be reloaded.
 */
public interface Reloadable {

    /**
     * Gets the resource container of the item.
     *
     * @return the item resource container
     */
    ResourceContainer getResourceContainer();

    /**
     * Sets the resource container of the item.
     *
     * @param resourceContainer the item resource container
     */
    void setResourceContainer(ResourceContainer resourceContainer);

    /**
     * Attempts to cancel the current reload operation. Returns {@code false} if there was no ongoing reload operation
     * to cancel.
     *
     * @return true if the reload was cancelled, otherwise false
     */
    boolean cancelReload();

    /**
     * Gets whether the item is available to perform a reload.
     *
     * @return whether the item can perform a reload
     */
    boolean isReloadAvailable();

    /**
     * Gets whether the item is currently performing a reload operation.
     *
     * @return whether the item is reloading
     */
    boolean isReloading();

    /**
     * Starts a reload operation.
     *
     * @param performer the entity who performs the reload
     */
    void reload(ReloadPerformer performer);
}
