package nl.matsgemmeke.battlegrounds.item.effect;

import nl.matsgemmeke.battlegrounds.item.deployment.Deployable;
import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.effect.source.EffectSource;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * An effect component of an item that produces a certain output when activated.
 */
public interface ItemEffect {

    /**
     * <p>Activates the effect in the holder's hand.</p>
     *
     * <p>This method triggers the specific behavior associated with the effect directly within the {@link ItemHolder}'s
     * hand. Unlike other activation methods, this one does not target a deployed object but instead activates solely
     * based on the holder's action.</p>
     *
     * @param holder the entity or object that activates the effect
     * @param itemStack the item stack that was used to activate the effect
     */
    void activate(@NotNull ItemHolder holder, @NotNull ItemStack itemStack);

    /**
     * <p>Activates the effect with the specified item holder and deployed object.</p>
     *
     * <p>This method triggers the specific behavior associated with the effect, using the provided {@link ItemHolder}
     * to indicate which entity is activating the effect and the {@link Deployable} object to specify the target
     * instance on which the effect is applied.</p>
     *
     * @param holder the entity or object that activates the effect
     * @param object the object instance on which the effect is activated
     */
    void activate(@NotNull ItemHolder holder, @NotNull Deployable object);

    /**
     * <p>Activates the effect with the specified item holder and source.</p>
     *
     * <p>This method triggers the specific behavior associated with the effect, using the provided {@link ItemHolder}
     * to indicate which entity is activating the effect and the {@link EffectSource} to specify the source from where
     * the effect will be activated.</p>
     *
     * @param holder the entity or object that activates the effect
     * @param source the source from where the effect is activated
     */
    void activate(@NotNull ItemHolder holder, @NotNull EffectSource source);
}
