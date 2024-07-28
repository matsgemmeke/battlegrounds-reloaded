package nl.matsgemmeke.battlegrounds.item.gun.controls;

import com.google.common.collect.Iterables;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.item.AmmunitionHolder;
import nl.matsgemmeke.battlegrounds.item.controls.ItemFunction;
import nl.matsgemmeke.battlegrounds.item.gun.GunHolder;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadSystem;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

public class ReloadFunction implements ItemFunction<GunHolder> {

    @NotNull
    private AmmunitionHolder ammunitionHolder;
    @NotNull
    private Iterable<GameSound> reloadSounds;
    @NotNull
    private ReloadSystem reloadSystem;

    public ReloadFunction(@NotNull AmmunitionHolder ammunitionHolder, @NotNull ReloadSystem reloadSystem) {
        this.ammunitionHolder = ammunitionHolder;
        this.reloadSystem = reloadSystem;
        this.reloadSounds = new HashSet<>();
    }

    public void addReloadSounds(@NotNull Iterable<GameSound> reloadSounds) {
        this.reloadSounds = Iterables.concat(this.reloadSounds, reloadSounds);
    }

    public boolean isAvailable() {
        return !reloadSystem.isPerforming();
    }

    public boolean isBlocking() {
        return true;
    }

    public boolean isPerforming() {
        return reloadSystem.isPerforming();
    }

    public boolean cancel() {
        return reloadSystem.cancel();
    }

    public boolean perform(@NotNull GunHolder holder) {
        if (ammunitionHolder.getMagazineAmmo() >= ammunitionHolder.getMagazineSize() || ammunitionHolder.getReserveAmmo() <= 0) {
            return false;
        }

        return reloadSystem.performReload(holder);
    }
}
