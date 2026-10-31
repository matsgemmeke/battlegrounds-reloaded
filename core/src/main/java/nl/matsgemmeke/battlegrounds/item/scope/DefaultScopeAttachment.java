package nl.matsgemmeke.battlegrounds.item.scope;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import nl.matsgemmeke.battlegrounds.sound.SoundEmitter;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class DefaultScopeAttachment implements ScopeAttachment {

    private final SoundEmitter soundEmitter;
    private final ScopeProperties scopeProperties;
    private float currentMagnification;
    private int magnificationIndex;
    @Nullable
    private ScopeUser currentUser;
    @Nullable
    private ScopeZoomEffect currentEffect;

    @Inject
    public DefaultScopeAttachment(SoundEmitter soundEmitter, @Assisted ScopeProperties scopeProperties) {
        this.soundEmitter = soundEmitter;
        this.scopeProperties = scopeProperties;
        this.magnificationIndex = 0;
        this.currentMagnification = Float.MIN_VALUE;
    }

    @Override
    public boolean applyEffect(ScopeUser scopeUser) {
        if (scopeProperties.magnifications().isEmpty()) {
            return false;
        }

        // Do not apply the effect if one is already being used
        if (currentEffect != null) {
            return false;
        }

        if (currentMagnification == Float.MIN_VALUE) {
            currentMagnification = scopeProperties.magnifications().getFirst();
        }

        soundEmitter.playSoundSequence(scopeProperties.useSoundSequence(), scopeUser::getLocation);

        currentEffect = new ScopeZoomEffect(scopeUser, currentMagnification);
        currentEffect.apply();

        currentUser = scopeUser;

        return scopeUser.addEffect(currentEffect);
    }

    @Override
    public boolean isScoped() {
        return currentUser != null;
    }

    @Override
    public boolean nextMagnification() {
        List<Float> magnifications = scopeProperties.magnifications();

        if (magnifications.size() <= 1) {
            return false;
        }

        magnificationIndex = (magnificationIndex + 1) % magnifications.size();
        currentMagnification = magnifications.get(magnificationIndex);

        if (currentEffect != null && currentUser != null) {
            soundEmitter.playSoundSequence(scopeProperties.changeMagnificationSoundSequence(), currentUser::getLocation);

            currentEffect.setMagnification(currentMagnification);
            currentEffect.update();
        }

        return true;
    }

    @Override
    public boolean removeEffect() {
        if (currentEffect == null || currentUser == null) {
            return false;
        }

        soundEmitter.playSoundSequence(scopeProperties.stopSoundSequence(), currentUser::getLocation);

        currentEffect.remove();
        currentUser.removeEffect(currentEffect);

        currentEffect = null;
        currentUser = null;

        return true;
    }
}
