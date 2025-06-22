package nl.matsgemmeke.battlegrounds.item.shoot;

import nl.matsgemmeke.battlegrounds.item.representation.ItemRepresentation;
import nl.matsgemmeke.battlegrounds.item.shoot.firemode.FireMode;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.ProjectileLauncher;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ShootHandler {

    @NotNull
    private final FireMode fireMode;
    @NotNull
    private final ItemRepresentation itemRepresentation;
    @NotNull
    private final ProjectileLauncher projectileLauncher;
    @Nullable
    private ShotPerformer performer;

    public ShootHandler(
            @NotNull FireMode fireMode,
            @NotNull ProjectileLauncher projectileLauncher,
            @NotNull ItemRepresentation itemRepresentation
    ) {
        this.fireMode = fireMode;
        this.projectileLauncher = projectileLauncher;
        this.itemRepresentation = itemRepresentation;
    }

    public void registerObservers() {
        fireMode.addShotObserver(this::onShotActivate);
    }

    private void onShotActivate() {
        if (performer == null) {
            return;
        }

        Location launchDirection = performer.getShootingDirection();
        projectileLauncher.launch(launchDirection);

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
