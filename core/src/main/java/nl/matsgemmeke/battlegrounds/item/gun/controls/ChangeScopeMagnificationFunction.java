package nl.matsgemmeke.battlegrounds.item.gun.controls;

import com.google.common.collect.Iterables;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.controls.ItemFunction;
import nl.matsgemmeke.battlegrounds.item.gun.GunHolder;
import nl.matsgemmeke.battlegrounds.item.scope.ScopeAttachment;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

public class ChangeScopeMagnificationFunction implements ItemFunction<GunHolder> {

    @NotNull
    private AudioEmitter audioEmitter;
    @NotNull
    private Iterable<GameSound> sounds;
    @NotNull
    private ScopeAttachment scopeAttachment;

    public ChangeScopeMagnificationFunction(@NotNull ScopeAttachment scopeAttachment, @NotNull AudioEmitter audioEmitter) {
        this.scopeAttachment = scopeAttachment;
        this.audioEmitter = audioEmitter;
        this.sounds = new HashSet<>();
    }

    public void addSounds(@NotNull Iterable<GameSound> sounds) {
        this.sounds = Iterables.concat(this.sounds, sounds);
    }

    public boolean isAvailable() {
        return scopeAttachment.isScoped();
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

    public boolean perform(@NotNull GunHolder holder) {
        if (!scopeAttachment.isScoped()) {
            return false;
        }

        audioEmitter.playSounds(sounds, holder.getEntity().getLocation());

        return scopeAttachment.nextMagnification();
    }
}
