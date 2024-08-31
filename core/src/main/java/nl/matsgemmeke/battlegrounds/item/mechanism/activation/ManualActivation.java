package nl.matsgemmeke.battlegrounds.item.mechanism.activation;

import nl.matsgemmeke.battlegrounds.item.deployment.Deployable;
import nl.matsgemmeke.battlegrounds.item.holder.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.mechanism.ItemMechanism;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Activation that triggers the mechanism by a manual operation by the item holder.
 */
public class ManualActivation extends BaseItemMechanismActivation {

    public ManualActivation(@NotNull ItemMechanism mechanism) {
        super(mechanism);
    }

    public void prime(@NotNull ItemHolder holder, @Nullable Deployable object) {
        if (object == null) {
            throw new IllegalArgumentException("Manual mechanism activation does support priming a deferred object");
        }

        deployedObjects.add(object);
    }
}
