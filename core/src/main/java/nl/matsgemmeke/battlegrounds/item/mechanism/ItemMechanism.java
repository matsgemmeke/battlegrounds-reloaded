package nl.matsgemmeke.battlegrounds.item.mechanism;

import nl.matsgemmeke.battlegrounds.item.deployment.Deployable;
import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * A mechanism component of an item. Produces a certain effect when activated.
 */
public interface ItemMechanism {

    /**
     * <p>Activates the mechanism in the holder's hand.</p>
     *
     * <p>This method triggers the specific behavior associated with the mechanism directly within the
     * {@link ItemHolder}'s hand. Unlike other activation methods, this one does not target a deployed object but
     * instead activates solely based on the holder's action.</p>
     *
     * @param holder the entity or object that activates the mechanism
     * @param itemStack the item stack that was used to activate the mechanism
     */
    void activate(@NotNull ItemHolder holder, @NotNull ItemStack itemStack);

    /**
     * <p>Activates the mechanism with the specified item holder and deployed object.</p>
     *
     * <p>This method triggers the specific behavior associated with the mechanism, using the provided
     * {@link ItemHolder} to indicate which entity is activating the mechanism and the {@link Deployable} object to
     * specify the target instance on which the mechanism is applied.</p>
     *
     * @param holder the entity or object that activates the mechanism
     * @param object the object instance on which the mechanism is activated
     */
    void activate(@NotNull ItemHolder holder, @NotNull Deployable object);
}
