package nl.matsgemmeke.battlegrounds.item.scope;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class DefaultScopeAttachment implements ScopeAttachment {

    @NotNull
    private final AudioEmitter audioEmitter;
    private float currentMagnification;
    private int magnificationIndex;
    @NotNull
    private List<Float> magnifications;
    @NotNull
    private List<GameSound> useSounds;
    @NotNull
    private List<GameSound> stopSounds;
    @NotNull
    private List<GameSound> changeMagnificationSounds;
    @Nullable
    private ScopeUser currentUser;
    @Nullable
    private ScopeZoomEffect currentEffect;

    @Inject
    public DefaultScopeAttachment(@NotNull AudioEmitter audioEmitter) {
        this.audioEmitter = audioEmitter;
        this.useSounds = Collections.emptyList();
        this.stopSounds = Collections.emptyList();
        this.changeMagnificationSounds = Collections.emptyList();
        this.magnifications = Collections.emptyList();
        this.magnificationIndex = 0;
    }

    public void configureChangeMagnificationSounds(List<GameSound> changeMagnificationSounds) {
        this.changeMagnificationSounds = changeMagnificationSounds;
    }

    public void configureMagnifications(List<Float> magnifications) {
        this.magnifications = magnifications;

        currentMagnification = magnifications.get(0);
    }

    public void configureStopSounds(List<GameSound> stopSounds) {
        this.stopSounds = stopSounds;
    }

    public void configureUseSounds(List<GameSound> useSounds) {
        this.useSounds = useSounds;
    }

    public boolean applyEffect(@NotNull ScopeUser scopeUser) {
        if (magnifications.isEmpty()) {
            return false;
        }

        // Do not apply the effect if one is already being used
        if (currentEffect != null) {
            return false;
        }

        audioEmitter.playSounds(useSounds, scopeUser.getLocation());

        currentEffect = new ScopeZoomEffect(scopeUser, currentMagnification);
        currentEffect.apply();

        currentUser = scopeUser;

        return scopeUser.addEffect(currentEffect);
    }

    public boolean isScoped() {
        return currentUser != null;
    }

    public boolean nextMagnification() {
        if (magnifications.size() <= 1) {
            return false;
        }

        magnificationIndex = (magnificationIndex + 1) % magnifications.size();
        currentMagnification = magnifications.get(magnificationIndex);

        if (currentEffect != null && currentUser != null) {
            audioEmitter.playSounds(changeMagnificationSounds, currentUser.getLocation());
            currentEffect.setMagnification(currentMagnification);
            currentEffect.update();
        }

        return true;
    }

    public boolean removeEffect() {
        if (currentEffect == null || currentUser == null) {
            return false;
        }

        audioEmitter.playSounds(stopSounds, currentUser.getLocation());

        currentEffect.remove();
        currentUser.removeEffect(currentEffect);

        currentEffect = null;
        currentUser = null;

        return true;
    }
}
