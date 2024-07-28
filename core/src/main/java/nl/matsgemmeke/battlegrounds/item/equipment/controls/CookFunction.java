package nl.matsgemmeke.battlegrounds.item.equipment.controls;

import com.google.common.collect.Iterables;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.controls.ItemFunction;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import nl.matsgemmeke.battlegrounds.item.mechanism.activation.ItemMechanismActivation;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

public class CookFunction implements ItemFunction<EquipmentHolder> {

    @NotNull
    private AudioEmitter audioEmitter;
    @NotNull
    private ItemMechanismActivation mechanismActivation;
    @NotNull
    private Iterable<GameSound> sounds;

    public CookFunction(@NotNull ItemMechanismActivation mechanismActivation, @NotNull AudioEmitter audioEmitter) {
        this.mechanismActivation = mechanismActivation;
        this.audioEmitter = audioEmitter;
        this.sounds = new HashSet<>();
    }

    public void addSounds(@NotNull Iterable<GameSound> sounds) {
        this.sounds = Iterables.concat(this.sounds, sounds);
    }

    public boolean isAvailable() {
        return !mechanismActivation.isPrimed();
    }

    public boolean isBlocking() {
        return false;
    }

    public boolean isPerforming() {
        return false;
    }

    public boolean cancel() {
        return false;
    }

    public boolean perform(@NotNull EquipmentHolder holder) {
        audioEmitter.playSounds(sounds, holder.getEntity().getLocation());
        mechanismActivation.prime(holder);
        return true;
    }
}
