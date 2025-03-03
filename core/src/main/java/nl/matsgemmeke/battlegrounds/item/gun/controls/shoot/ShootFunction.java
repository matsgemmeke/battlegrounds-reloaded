package nl.matsgemmeke.battlegrounds.item.gun.controls.shoot;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.reload.AmmunitionStorage;
import nl.matsgemmeke.battlegrounds.item.controls.ItemFunction;
import nl.matsgemmeke.battlegrounds.item.gun.GunHolder;
import nl.matsgemmeke.battlegrounds.item.shoot.FireMode;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

public class ShootFunction implements ItemFunction<GunHolder> {

    @NotNull
    private AmmunitionStorage ammunitionStorage;
    @NotNull
    private AudioEmitter audioEmitter;
    @NotNull
    private FireMode fireMode;
    @NotNull
    private Iterable<GameSound> triggerSounds;

    public ShootFunction(
            @NotNull AmmunitionStorage ammunitionStorage,
            @NotNull AudioEmitter audioEmitter,
            @NotNull FireMode fireMode
    ) {
        this.ammunitionStorage = ammunitionStorage;
        this.audioEmitter = audioEmitter;
        this.fireMode = fireMode;
        this.triggerSounds = new HashSet<>();
    }

    @NotNull
    public Iterable<GameSound> getTriggerSounds() {
        return triggerSounds;
    }

    public void setTriggerSounds(@NotNull Iterable<GameSound> triggerSounds) {
        this.triggerSounds = triggerSounds;
    }

    public boolean isAvailable() {
        return !fireMode.isCycling();
    }

    public boolean isBlocking() {
        return true;
    }

    public boolean isPerforming() {
        return fireMode.isCycling();
    }

    public boolean cancel() {
        return fireMode.cancelCycle();
    }

    public boolean perform(@NotNull GunHolder holder) {
        if (ammunitionStorage.getMagazineAmmo() <= 0) {
            audioEmitter.playSounds(triggerSounds, holder.getEntity().getLocation());
            return false;
        }

        fireMode.activateCycle();
        return true;
    }
}
