package nl.matsgemmeke.battlegrounds.item.mechanism.activation;

import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.deployment.Deployable;
import nl.matsgemmeke.battlegrounds.item.mechanism.ItemMechanism;
import nl.matsgemmeke.battlegrounds.item.mechanism.activation.trigger.Trigger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Activation that initiates the mechanism based of an external trigger.
 */
public class TriggerActivation extends BaseItemMechanismActivation {

    @NotNull
    private List<Trigger> triggers;

    public TriggerActivation(@NotNull ItemMechanism mechanism) {
        super(mechanism);
        this.triggers = new ArrayList<>();
    }

    public void addTrigger(@NotNull Trigger trigger) {
        triggers.add(trigger);

        trigger.addListener(this::onTrigger);
    }

    public void prime(@NotNull ItemHolder holder, @Nullable Deployable object) {
        if (object == null) {
            throw new IllegalArgumentException("Trigger mechanism activation does not support priming a deferred object");
        }

        deployedObjects.add(object);

        for (Trigger trigger : triggers) {
            trigger.checkTriggerActivation(holder, object);
        }
    }

    private void onTrigger(@NotNull ItemHolder holder, @NotNull Deployable object) {
        this.handleActivation(holder, object);
    }
}
