package nl.matsgemmeke.battlegrounds.item.scope;

import com.google.common.collect.Iterables;
import nl.matsgemmeke.battlegrounds.entity.ScopeUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

public class DefaultScopeAttachment implements ScopeAttachment {

    private float currentMagnification;
    @NotNull
    private Iterable<Float> magnificationSettings;
    @NotNull
    private Iterator<Float> settingsCycle;
    @Nullable
    private ScopeUser currentUser;
    @Nullable
    private ScopeZoomEffect currentEffect;

    public DefaultScopeAttachment(@NotNull Iterable<Float> magnificationSettings) {
        this.magnificationSettings = magnificationSettings;
        this.settingsCycle = magnificationSettings.iterator();
        this.currentMagnification = settingsCycle.next();
    }

    public boolean applyEffect(@NotNull ScopeUser user) {
        // Do not apply the effect if one is already being used
        if (currentEffect != null) {
            return false;
        }

        currentEffect = new ScopeZoomEffect(user, currentMagnification);
        currentEffect.apply();

        currentUser = user;

        return user.addEffect(currentEffect);
    }

    public boolean isScoped() {
        return currentUser != null;
    }

    public boolean nextMagnification() {
        if (Iterables.size(magnificationSettings) <= 1) {
            return false;
        }

        // Obtain a new iterator if all values have been used
        if (!settingsCycle.hasNext()) {
            settingsCycle = magnificationSettings.iterator();
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

        currentEffect.remove();
        currentUser.removeEffect(currentEffect);

        currentEffect = null;
        currentUser = null;

        return true;
    }
}
