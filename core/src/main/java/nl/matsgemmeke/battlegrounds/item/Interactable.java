package nl.matsgemmeke.battlegrounds.item;

/**
 * An item that performs actions when being interacted with.
 *
 * @param <T> the type of user that utilizes the item
 */
public interface Interactable<T extends ItemUser> {

    /**
     * Handles a performed item change from this item to another item.
     */
    void onChangeFrom();

    /**
     * Handles a performed item change from another item to this item.
     */
    void onChangeTo();

    /**
     * Handles a performed drop on the item.
     */
    void onDrop();

    /**
     * Handles a performed left click on the item.
     */
    void onLeftClick();

    /**
     * Handles an item pickup.
     *
     * @param user the user who picked up the item
     */
    void onPickUp(T user);

    /**
     * Handles a performed right click on the item.
     */
    void onRightClick();

    /**
     * Handles a performed item swap from this item to another item.
     */
    void onSwapFrom();

    /**
     * Handles a performed item swap from another item to this item.
     */
    void onSwapTo();
}
