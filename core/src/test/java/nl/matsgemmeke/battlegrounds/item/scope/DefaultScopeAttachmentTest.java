package nl.matsgemmeke.battlegrounds.item.scope;

import nl.matsgemmeke.battlegrounds.item.ItemEffect;
import nl.matsgemmeke.battlegrounds.sound.SoundEmitter;
import nl.matsgemmeke.battlegrounds.sound.SoundSequence;
import org.bukkit.Location;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultScopeAttachmentTest {

    private static final List<Float> MAGNIFICATIONS = List.of(-0.1f, -0.15f, -0.2f);
    private static final SoundSequence USE_SOUND_SEQUENCE = new SoundSequence("use", null);
    private static final SoundSequence STOP_SOUND_SEQUENCE = new SoundSequence("stop", null);
    private static final SoundSequence CHANGE_MAGNIFICATION_SOUND_SEQUENCE = new SoundSequence("change", null);

    private static final ScopeProperties SCOPE_PROPERTIES_DEFAULT = new ScopeProperties(MAGNIFICATIONS, USE_SOUND_SEQUENCE, STOP_SOUND_SEQUENCE, CHANGE_MAGNIFICATION_SOUND_SEQUENCE);
    private static final ScopeProperties SCOPE_PROPERTIES_NO_MAGNIFICATIONS = new ScopeProperties(List.of(), USE_SOUND_SEQUENCE, STOP_SOUND_SEQUENCE, CHANGE_MAGNIFICATION_SOUND_SEQUENCE);
    private static final ScopeProperties SCOPE_PROPERTIES_NO_SOUND_SEQUENCES = new ScopeProperties(MAGNIFICATIONS, null, null, null);

    @Mock
    private SoundEmitter soundEmitter;

    @Test
    @DisplayName("applyEffect does nothing when already in use")
    void applyEffect_alreadyInUse() {
        ScopeUser scopeUser = mock(ScopeUser.class);

        DefaultScopeAttachment scopeAttachment = new DefaultScopeAttachment(soundEmitter, SCOPE_PROPERTIES_DEFAULT);
        scopeAttachment.applyEffect(scopeUser);
        boolean applied = scopeAttachment.applyEffect(scopeUser);

        assertThat(applied).isFalse();

        verify(scopeUser).addEffect(any(ScopeZoomEffect.class));
    }

    @Test
    @DisplayName("applyEffect creates new zoom effect and adds it to the user")
    void applyEffect_createsZoomEffect() {
        Location userLocation = new Location(null, 1, 1, 1);

        ScopeUser scopeUser = mock(ScopeUser.class);
        when(scopeUser.addEffect(any(ScopeZoomEffect.class))).thenReturn(true);
        when(scopeUser.getLocation()).thenReturn(userLocation);

        DefaultScopeAttachment scopeAttachment = new DefaultScopeAttachment(soundEmitter, SCOPE_PROPERTIES_DEFAULT);
        boolean applied = scopeAttachment.applyEffect(scopeUser);

        assertThat(applied).isTrue();

        verify(soundEmitter).playSoundSequence(eq(USE_SOUND_SEQUENCE), Mockito.<Supplier<Location>>argThat(supplier -> supplier.get() == userLocation));
        verify(scopeUser).addEffect(any(ScopeZoomEffect.class));
    }

    @Test
    @DisplayName("isScoped returns false when effect is not applied")
    void isScope_effectNotApplied() {
        DefaultScopeAttachment scopeAttachment = new DefaultScopeAttachment(soundEmitter, SCOPE_PROPERTIES_DEFAULT);
        boolean scoped = scopeAttachment.isScoped();

        assertThat(scoped).isFalse();
    }

    @Test
    @DisplayName("isScope returns true when effect is applied")
    void isScope_effectApplied() {
        ScopeUser scopeUser = mock(ScopeUser.class);

        DefaultScopeAttachment scopeAttachment = new DefaultScopeAttachment(soundEmitter, SCOPE_PROPERTIES_DEFAULT);
        scopeAttachment.applyEffect(scopeUser);
        boolean scoped = scopeAttachment.isScoped();

        assertThat(scoped).isTrue();
    }

    @Test
    @DisplayName("removeEffect does nothing when not in use")
    void removeEffect_notInUse() {
        DefaultScopeAttachment scopeAttachment = new DefaultScopeAttachment(soundEmitter, SCOPE_PROPERTIES_DEFAULT);
        boolean removed = scopeAttachment.removeEffect();

        assertThat(removed).isFalse();
    }

    @Test
    @DisplayName("removeEffect removes current effect from current user")
    void removeEffect_inUse() {
        Location userLocation = new Location(null, 1, 1, 1);

        ScopeUser scopeUser = mock(ScopeUser.class);
        when(scopeUser.getLocation()).thenReturn(userLocation);

        DefaultScopeAttachment scopeAttachment = new DefaultScopeAttachment(soundEmitter, SCOPE_PROPERTIES_DEFAULT);
        scopeAttachment.applyEffect(scopeUser);
        boolean removed = scopeAttachment.removeEffect();

        assertThat(removed).isTrue();

        verify(soundEmitter).playSoundSequence(eq(STOP_SOUND_SEQUENCE), Mockito.<Supplier<Location>>argThat(supplier -> supplier.get() == userLocation));
        verify(scopeUser).removeEffect(any(ItemEffect.class));
    }

    @Test
    @DisplayName("nextMagnification returns false when no magnifications are configured")
    void nextMagnification_noConfiguredMagnifications() {
        DefaultScopeAttachment scopeAttachment = new DefaultScopeAttachment(soundEmitter, SCOPE_PROPERTIES_NO_MAGNIFICATIONS);
        boolean result = scopeAttachment.nextMagnification();

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("nextMagnification returns true and changes magnification to the next value")
    void nextMagnification_changeToNextValue() {
        ScopeUser scopeUser = mock(ScopeUser.class);

        DefaultScopeAttachment scopeAttachment = new DefaultScopeAttachment(soundEmitter, SCOPE_PROPERTIES_DEFAULT);
        boolean result = scopeAttachment.nextMagnification();
        scopeAttachment.applyEffect(scopeUser);

        assertThat(result).isTrue();

        verify(scopeUser).applyViewMagnification(-0.15f);
    }

    @Test
    @DisplayName("nextMagnification resets magnification when all values have been selected once")
    void nextMagnification_resetAfterAllValues() {
        Location userLocation = new Location(null, 1, 1, 1);

        ScopeUser scopeUser = mock(ScopeUser.class);
        when(scopeUser.getLocation()).thenReturn(userLocation);

        DefaultScopeAttachment scopeAttachment = new DefaultScopeAttachment(soundEmitter, SCOPE_PROPERTIES_DEFAULT);
        scopeAttachment.applyEffect(scopeUser);
        scopeAttachment.nextMagnification();
        scopeAttachment.nextMagnification();
        scopeAttachment.nextMagnification();

        verify(soundEmitter, times(3)).playSoundSequence(eq(CHANGE_MAGNIFICATION_SOUND_SEQUENCE), Mockito.<Supplier<Location>>argThat(supplier -> supplier.get() == userLocation));
        verify(scopeUser, times(2)).applyViewMagnification(-0.1f);
        verify(scopeUser).applyViewMagnification(-0.15f);
        verify(scopeUser).applyViewMagnification(-0.2f);
    }
}
