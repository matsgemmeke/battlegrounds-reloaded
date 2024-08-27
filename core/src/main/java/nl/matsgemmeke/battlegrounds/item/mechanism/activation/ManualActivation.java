package nl.matsgemmeke.battlegrounds.item.mechanism.activation;

import nl.matsgemmeke.battlegrounds.item.Droppable;
import nl.matsgemmeke.battlegrounds.item.holder.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.mechanism.ItemMechanism;
import org.jetbrains.annotations.NotNull;

/**
 * Activation that triggers the mechanism by a manual operation by the item holder.
 */
public class ManualActivation implements ItemMechanismActivation {

    private boolean primed;
    @NotNull
    private Droppable item;
    @NotNull
    private ItemMechanism mechanism;

    public ManualActivation(@NotNull Droppable item, @NotNull ItemMechanism mechanism) {
        this.item = item;
        this.mechanism = mechanism;
        this.primed = false;
    }

    public void activate(@NotNull ItemHolder holder) {
        mechanism.activate(holder);
    }

    public boolean isPrimed() {
        return primed;
    }

    public void prime(@NotNull ItemHolder holder) {
        primed = true;
    }
}
