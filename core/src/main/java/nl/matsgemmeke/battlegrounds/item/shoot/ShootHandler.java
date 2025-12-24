package nl.matsgemmeke.battlegrounds.item.shoot;

import nl.matsgemmeke.battlegrounds.item.recoil.Recoil;
import nl.matsgemmeke.battlegrounds.item.reload.AmmunitionStorage;
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

    private final AmmunitionStorage ammunitionStorage;
    private final FireMode fireMode;
    private final ItemRepresentation itemRepresentation;
    private final ProjectileLauncher projectileLauncher;
    @Nullable
    private final Recoil recoil;
    private final SpreadPattern spreadPattern;
    private ShotPerformer performer;

    public ShootHandler(
            FireMode fireMode,
            ProjectileLauncher projectileLauncher,
            SpreadPattern spreadPattern,
            AmmunitionStorage ammunitionStorage,
            ItemRepresentation itemRepresentation,
            @Nullable Recoil recoil
    ) {
        this.fireMode = fireMode;
        this.projectileLauncher = projectileLauncher;
        this.recoil = recoil;
        this.spreadPattern = spreadPattern;
        this.ammunitionStorage = ammunitionStorage;
        this.itemRepresentation = itemRepresentation;
    }

    public void registerObservers() {
        fireMode.addShotObserver(this::onShotActivate);
    }

    private void onShotActivate() {
        int magazineAmmo = ammunitionStorage.getMagazineAmmo();

        if (performer == null || magazineAmmo <= 0) {
            return;
        }

        ammunitionStorage.setMagazineAmmo(magazineAmmo - 1);
        itemRepresentation.setPlaceholder(Placeholder.MAGAZINE_AMMO, String.valueOf(ammunitionStorage.getMagazineAmmo()));
        // TODO: Remove this once reloading uses the ItemRepresentation
        itemRepresentation.setPlaceholder(Placeholder.RESERVE_AMMO, String.valueOf(ammunitionStorage.getReserveAmmo()));

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
