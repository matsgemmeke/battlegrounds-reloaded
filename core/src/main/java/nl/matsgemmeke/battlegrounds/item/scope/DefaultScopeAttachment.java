package nl.matsgemmeke.battlegrounds.item.scope;

import com.google.common.collect.Iterables;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

public class DefaultScopeAttachment implements ScopeAttachment {

    @NotNull
    private final AudioEmitter audioEmitter;
    private float currentMagnification;
    @NotNull
    private Iterator<Float> settingsCycle;
    @NotNull
    private final ScopeProperties properties;
    @Nullable
    private ScopeUser currentUser;
    @Nullable
    private ScopeZoomEffect currentEffect;

    public DefaultScopeAttachment(@NotNull ScopeProperties properties, @NotNull AudioEmitter audioEmitter) {
        this.properties = properties;
        this.audioEmitter = audioEmitter;
        this.settingsCycle = properties.magnificationSettings().iterator();
        this.currentMagnification = settingsCycle.next();
    }

    public boolean applyEffect(@NotNull ScopeUser scopeUser) {
        // Do not apply the effect if one is already being used
        if (currentEffect != null) {
            return false;
        }

        audioEmitter.playSounds(properties.scopeUseSounds(), scopeUser.getLocation());

        currentEffect = new ScopeZoomEffect(scopeUser, currentMagnification);
        currentEffect.apply();

        currentUser = scopeUser;

        return scopeUser.addEffect(currentEffect);
    }

    public boolean isScoped() {
        return currentUser != null;
    }

    public boolean nextMagnification() {
        if (Iterables.size(properties.magnificationSettings()) <= 1) {
            return false;
        }

        // Obtain a new iterator if all values have been used
        if (!settingsCycle.hasNext()) {
            settingsCycle = properties.magnificationSettings().iterator();
        }

        currentMagnification = settingsCycle.next();

        if (currentEffect != null) {
            currentEffect.setMagnification(currentMagnification);
            currentEffect.update();
        }

        return true;
    }

    public boolean removeEffect() {
        if (currentEffect == null || currentUser == null) {
            return false;
        }

        audioEmitter.playSounds(properties.scopeStopSounds(), currentUser.getLocation());

        currentEffect.remove();
        currentUser.removeEffect(currentEffect);

        currentEffect = null;
        currentUser = null;

        return true;
    }
}
