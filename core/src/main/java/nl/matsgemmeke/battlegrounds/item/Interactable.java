package nl.matsgemmeke.battlegrounds.item;

import org.jetbrains.annotations.NotNull;

/**
 * An item that performs actions when being interacted with.
 *
 * @param <T> the type of holder that utilizes the item
 */
public interface Interactable<T extends ItemHolder> {

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
     * @param holder the entity who picked up the item
     */
    void onPickUp(@NotNull T holder);

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
