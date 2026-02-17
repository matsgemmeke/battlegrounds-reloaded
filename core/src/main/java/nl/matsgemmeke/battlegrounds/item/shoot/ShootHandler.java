package nl.matsgemmeke.battlegrounds.item.shoot;

import nl.matsgemmeke.battlegrounds.item.recoil.Recoil;
import nl.matsgemmeke.battlegrounds.item.reload.ResourceContainer;
import nl.matsgemmeke.battlegrounds.item.representation.ItemRepresentation;
import nl.matsgemmeke.battlegrounds.item.representation.Placeholder;
import nl.matsgemmeke.battlegrounds.item.shoot.firemode.FireMode;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.LaunchContext;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.ProjectileLauncher;
import nl.matsgemmeke.battlegrounds.item.shoot.spread.SpreadPattern;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class ShootHandler {

    private final FireMode fireMode;
    private final ItemRepresentation itemRepresentation;
    private final ProjectileLauncher projectileLauncher;
    @Nullable
    private final Recoil recoil;
    private final ResourceContainer resourceContainer;
    private final SpreadPattern spreadPattern;
    private ShotPerformer performer;

    public ShootHandler(
            FireMode fireMode,
            ProjectileLauncher projectileLauncher,
            SpreadPattern spreadPattern,
            ResourceContainer resourceContainer,
            ItemRepresentation itemRepresentation,
            @Nullable Recoil recoil
    ) {
        this.fireMode = fireMode;
        this.projectileLauncher = projectileLauncher;
        this.recoil = recoil;
        this.spreadPattern = spreadPattern;
        this.resourceContainer = resourceContainer;
        this.itemRepresentation = itemRepresentation;
    }

    public void registerObservers() {
        fireMode.addShotObserver(this::onShotActivate);
    }

    private void onShotActivate() {
        int loadedAmount = resourceContainer.getLoadedAmount();

        if (performer == null || loadedAmount <= 0) {
            return;
        }

        resourceContainer.setLoadedAmount(loadedAmount - 1);
        itemRepresentation.setPlaceholder(Placeholder.MAGAZINE_AMMO, String.valueOf(resourceContainer.getLoadedAmount()));
        // TODO: Remove this once reloading uses the ItemRepresentation
        itemRepresentation.setPlaceholder(Placeholder.RESERVE_AMMO, String.valueOf(resourceContainer.getReserveAmount()));

        Location shootingDirection = performer.getShootingDirection();
        World world = shootingDirection.getWorld();
        Supplier<Location> soundLocationSupplier = performer::getShootingDirection;
        List<Location> shotDirections = spreadPattern.getShotDirections(shootingDirection);

        for (Location shotDirection : shotDirections) {
            LaunchContext context = new LaunchContext(performer, performer, shotDirection, soundLocationSupplier, world);

            projectileLauncher.launch(context);
        }

        if (recoil != null) {
            recoil.produceRecoil(performer, shootingDirection);
        }

        ItemStack itemStack = itemRepresentation.update();
        performer.setHeldItem(itemStack);
    }

    public void cancel() {
        fireMode.cancelCycle();
        projectileLauncher.cancel();
    }

    public int getRateOfFire() {
        return fireMode.getRateOfFire();
    }

    public boolean isShooting() {
        return fireMode.isCycling();
    }

    public void shoot(ShotPerformer performer) {
        this.performer = performer;
        fireMode.startCycle();
    }
}
