package nl.matsgemmeke.battlegrounds.item.scope;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.ItemEffect;
import org.bukkit.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class DefaultScopeAttachmentTest {

    private static final List<Float> MAGNIFICATIONS = List.of(-0.1f, -0.15f, -0.2f);
    private static final List<GameSound> USE_SOUNDS = List.of(mock(GameSound.class));
    private static final List<GameSound> STOP_SOUNDS = List.of(mock(GameSound.class));
    private static final List<GameSound> CHANGE_MAGNIFICATION_SOUNDS = List.of(mock(GameSound.class));

    private AudioEmitter audioEmitter;

    @BeforeEach
    public void setUp() {
        audioEmitter = mock(AudioEmitter.class);
    }

    @Test
    public void applyEffectDoesNothingWhenAlreadyInUse() {
        ScopeUser scopeUser = mock(ScopeUser.class);

        DefaultScopeAttachment scopeAttachment = new DefaultScopeAttachment(audioEmitter);
        scopeAttachment.configureMagnifications(MAGNIFICATIONS);
        scopeAttachment.applyEffect(scopeUser);
        boolean applied = scopeAttachment.applyEffect(scopeUser);

        assertThat(applied).isFalse();

        verify(scopeUser).addEffect(any(ScopeZoomEffect.class));
    }

    @Test
    public void applyEffectCreatesNewZoomEffectAndAddsItToUser() {
        Location userLocation = new Location(null, 1, 1, 1);

        ScopeUser scopeUser = mock(ScopeUser.class);
        when(scopeUser.addEffect(any(ScopeZoomEffect.class))).thenReturn(true);
        when(scopeUser.getLocation()).thenReturn(userLocation);

        DefaultScopeAttachment scopeAttachment = new DefaultScopeAttachment(audioEmitter);
        scopeAttachment.configureMagnifications(MAGNIFICATIONS);
        scopeAttachment.configureUseSounds(USE_SOUNDS);
        boolean applied = scopeAttachment.applyEffect(scopeUser);

        assertThat(applied).isTrue();

        verify(audioEmitter).playSounds(USE_SOUNDS, userLocation);
        verify(scopeUser).addEffect(any(ScopeZoomEffect.class));
    }

    @Test
    public void isNotScopedWhenEffectNotApplied() {
        DefaultScopeAttachment scopeAttachment = new DefaultScopeAttachment(audioEmitter);
        boolean scoped = scopeAttachment.isScoped();

        assertThat(scoped).isFalse();
    }

    @Test
    public void isScopedWhenEffectApplied() {
        ScopeUser scopeUser = mock(ScopeUser.class);

        DefaultScopeAttachment scopeAttachment = new DefaultScopeAttachment(audioEmitter);
        scopeAttachment.configureMagnifications(MAGNIFICATIONS);
        scopeAttachment.applyEffect(scopeUser);
        boolean scoped = scopeAttachment.isScoped();

        assertThat(scoped).isTrue();
    }

    @Test
    public void removeEffectDoesNothingWhenNotInUse() {
        DefaultScopeAttachment scopeAttachment = new DefaultScopeAttachment(audioEmitter);
        boolean removed = scopeAttachment.removeEffect();

        assertThat(removed).isFalse();
    }

    @Test
    public void removeEffectRemovesCurrentEffectFromCurrentUser() {
        Location userLocation = new Location(null, 1, 1, 1);

        ScopeUser scopeUser = mock(ScopeUser.class);
        when(scopeUser.getLocation()).thenReturn(userLocation);

        DefaultScopeAttachment scopeAttachment = new DefaultScopeAttachment(audioEmitter);
        scopeAttachment.configureMagnifications(MAGNIFICATIONS);
        scopeAttachment.configureStopSounds(STOP_SOUNDS);
        scopeAttachment.applyEffect(scopeUser);
        boolean removed = scopeAttachment.removeEffect();

        assertThat(removed).isTrue();

        verify(audioEmitter).playSounds(STOP_SOUNDS, userLocation);
        verify(scopeUser).removeEffect(any(ItemEffect.class));
    }

    @Test
    public void nextMagnificationReturnsFalseWhenNoMagnificationsAreConfigured() {
        DefaultScopeAttachment scopeAttachment = new DefaultScopeAttachment(audioEmitter);
        boolean result = scopeAttachment.nextMagnification();

        assertThat(result).isFalse();
    }

    @Test
    public void nextMagnificationReturnsTrueAndChangesMagnificationByOneValue() {
        ScopeUser scopeUser = mock(ScopeUser.class);

        DefaultScopeAttachment scopeAttachment = new DefaultScopeAttachment(audioEmitter);
        scopeAttachment.configureMagnifications(MAGNIFICATIONS);
        boolean result = scopeAttachment.nextMagnification();
        scopeAttachment.applyEffect(scopeUser);

        assertThat(result).isTrue();

        verify(scopeUser).applyViewMagnification(-0.15f);
    }

    @Test
    public void nextMagnificationKeepsResettingMagnificationWhenAllValuesHaveBeenChosen() {
        Location userLocation = new Location(null, 1, 1, 1);

        ScopeUser scopeUser = mock(ScopeUser.class);
        when(scopeUser.getLocation()).thenReturn(userLocation);

        DefaultScopeAttachment scopeAttachment = new DefaultScopeAttachment(audioEmitter);
        scopeAttachment.configureMagnifications(MAGNIFICATIONS);
        scopeAttachment.configureChangeMagnificationSounds(CHANGE_MAGNIFICATION_SOUNDS);
        scopeAttachment.applyEffect(scopeUser);
        scopeAttachment.nextMagnification();
        scopeAttachment.nextMagnification();
        scopeAttachment.nextMagnification();

        verify(audioEmitter, times(3)).playSounds(CHANGE_MAGNIFICATION_SOUNDS, userLocation);
        verify(scopeUser, times(2)).applyViewMagnification(-0.1f);
        verify(scopeUser).applyViewMagnification(-0.15f);
        verify(scopeUser).applyViewMagnification(-0.2f);
    }
}
