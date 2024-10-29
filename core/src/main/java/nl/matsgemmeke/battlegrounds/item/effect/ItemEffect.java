package nl.matsgemmeke.battlegrounds.item.effect;

import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.effect.source.EffectSource;
import org.jetbrains.annotations.NotNull;

/**
 * An effect component of an item that produces a certain output when activated.
 */
public interface ItemEffect {

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
