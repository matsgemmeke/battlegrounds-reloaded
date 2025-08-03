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
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ShootHandler {

    @NotNull
    private final AmmunitionStorage ammunitionStorage;
    @NotNull
    private final FireMode fireMode;
    @NotNull
    private final ItemRepresentation itemRepresentation;
    @NotNull
    private final ProjectileLauncher projectileLauncher;
    @Nullable
    private final Recoil recoil;
    @NotNull
    private final SpreadPattern spreadPattern;
    @Nullable
    private ShotPerformer performer;

    public ShootHandler(
            @NotNull FireMode fireMode,
            @NotNull ProjectileLauncher projectileLauncher,
            @NotNull SpreadPattern spreadPattern,
            @NotNull AmmunitionStorage ammunitionStorage,
            @NotNull ItemRepresentation itemRepresentation,
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

        Entity entity = performer.getEntity();
        Location shootingDirection = performer.getShootingDirection();
        List<Location> shotDirections = spreadPattern.getShotDirections(shootingDirection);

        for (Location shotDirection : shotDirections) {
            LaunchContext context = new LaunchContext(entity, performer, shotDirection);

            projectileLauncher.launch(context);
        }

        if (recoil != null) {
            recoil.produceRecoil(performer, shootingDirection);
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
