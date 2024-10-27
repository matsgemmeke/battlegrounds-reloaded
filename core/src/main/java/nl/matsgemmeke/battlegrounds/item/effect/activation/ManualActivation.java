package nl.matsgemmeke.battlegrounds.item.effect.activation;

import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.deployment.Deployable;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.source.EffectSource;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Activation that initiates the effect by a manual operation by the item holder.
 */
public class ManualActivation extends BaseItemEffectActivation {

    public ManualActivation(@NotNull ItemEffect effect) {
        super(effect);
    }

    public void prime(@NotNull ItemHolder holder, @NotNull EffectSource source) {
        sources.add(source);
    }

    public void primeDeployedObject(@NotNull ItemHolder holder, @NotNull Deployable object) {
        deployedObjects.add(object);
    }

    public void primeInHand(@NotNull ItemHolder holder, @NotNull ItemStack itemStack) {
        throw new UnsupportedOperationException("Manual effect activation does not support priming an object in the holder's hand");
    }
}
