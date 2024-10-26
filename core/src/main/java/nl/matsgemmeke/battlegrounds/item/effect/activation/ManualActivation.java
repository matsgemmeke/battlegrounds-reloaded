package nl.matsgemmeke.battlegrounds.item.effect.activation;

import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.deployment.Deployable;
import nl.matsgemmeke.battlegrounds.item.effect.ItemMechanism;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Activation that initiates the mechanism by a manual operation by the item holder.
 */
public class ManualActivation extends BaseItemMechanismActivation {

    public ManualActivation(@NotNull ItemMechanism mechanism) {
        super(mechanism);
    }

    public void primeDeployedObject(@NotNull ItemHolder holder, @NotNull Deployable object) {
        deployedObjects.add(object);
    }

    public void primeInHand(@NotNull ItemHolder holder, @NotNull ItemStack itemStack) {
        throw new UnsupportedOperationException("Manual mechanism activation does not support priming an object in the holder's hand");
    }
}
