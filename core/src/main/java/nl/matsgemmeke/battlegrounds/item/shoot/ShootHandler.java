package nl.matsgemmeke.battlegrounds.item.shoot;

import nl.matsgemmeke.battlegrounds.item.representation.ItemRepresentation;
import nl.matsgemmeke.battlegrounds.item.shoot.firemode.FireMode;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ShootHandler {

    @NotNull
    private final FireMode fireMode;
    @NotNull
    private final ItemRepresentation itemRepresentation;
    @Nullable
    private ShotPerformer performer;

    public ShootHandler(
            @NotNull FireMode fireMode,
            @NotNull ItemRepresentation itemRepresentation
    ) {
        this.fireMode = fireMode;
        this.itemRepresentation = itemRepresentation;
    }

    public void registerObservers() {
        fireMode.addShotObserver(this::onShotActivate);
    }

    private void onShotActivate() {
        if (performer == null) {
            return;
        }

        ItemStack itemStack = itemRepresentation.update();
        performer.setHeldItem(itemStack);
    }

    public boolean cancel() {
        return fireMode.cancelCycle();
    }

    public int getRateOfFire() {
        return fireMode.getRateOfFire();
    }

    public boolean isShooting() {
        return fireMode.isCycling();
    }

    public void shoot(@NotNull ShotPerformer performer) {
        this.performer = performer;
        fireMode.startCycle();
    }
}
