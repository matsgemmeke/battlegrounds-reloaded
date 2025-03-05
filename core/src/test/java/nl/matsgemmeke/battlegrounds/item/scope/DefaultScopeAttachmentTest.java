package nl.matsgemmeke.battlegrounds.item.scope;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.ItemEffect;
import org.bukkit.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class DefaultScopeAttachmentTest {

    private AudioEmitter audioEmitter;
    private ScopeProperties properties;

    @BeforeEach
    public void setUp() {
        audioEmitter = mock(AudioEmitter.class);

        List<GameSound> scopeUseSounds = List.of(mock(GameSound.class));
        List<GameSound> scopeStopSounds = List.of(mock(GameSound.class));
        List<Float> magnificationSettings = List.of(-0.1f, -0.15f, -0.2f);

        properties = new ScopeProperties(scopeUseSounds, scopeStopSounds, magnificationSettings);
    }

    @Test
    public void applyEffectDoesNothingWhenAlreadyInUse() {
        ScopeUser scopeUser = mock(ScopeUser.class);

        DefaultScopeAttachment scopeAttachment = new DefaultScopeAttachment(properties, audioEmitter);
        scopeAttachment.applyEffect(scopeUser);
        boolean applied = scopeAttachment.applyEffect(scopeUser);

        assertFalse(applied);
    }

    @Test
    public void applyEffectCreatesNewZoomEffectAndAddsItToUser() {
        Location userLocation = new Location(null, 1, 1, 1);

        ScopeUser scopeUser = mock(ScopeUser.class);
        when(scopeUser.addEffect(any())).thenReturn(true);
        when(scopeUser.getLocation()).thenReturn(userLocation);

        DefaultScopeAttachment scopeAttachment = new DefaultScopeAttachment(properties, audioEmitter);
        boolean applied = scopeAttachment.applyEffect(scopeUser);

        verify(audioEmitter).playSounds(properties.scopeUseSounds(), userLocation);
        verify(scopeUser).addEffect(any());

        assertTrue(applied);
    }

    @Test
    public void isNotScopedWhenEffectNotApplied() {
        DefaultScopeAttachment scopeAttachment = new DefaultScopeAttachment(properties, audioEmitter);
        boolean scoped = scopeAttachment.isScoped();

        assertFalse(scoped);
    }

    @Test
    public void isScopedWhenEffectApplied() {
        ScopeUser scopeUser = mock(ScopeUser.class);

        DefaultScopeAttachment scopeAttachment = new DefaultScopeAttachment(properties, audioEmitter);
        scopeAttachment.applyEffect(scopeUser);
        boolean scoped = scopeAttachment.isScoped();

        assertTrue(scoped);
    }

    @Test
    public void removeEffectDoesNothingWhenNotInUse() {
        DefaultScopeAttachment scopeAttachment = new DefaultScopeAttachment(properties, audioEmitter);
        boolean removed = scopeAttachment.removeEffect();

        assertFalse(removed);
    }

    @Test
    public void removeEffectRemovesCurrentEffectFromCurrentUser() {
        Location userLocation = new Location(null, 1, 1, 1);

        ScopeUser scopeUser = mock(ScopeUser.class);
        when(scopeUser.getLocation()).thenReturn(userLocation);

        DefaultScopeAttachment scopeAttachment = new DefaultScopeAttachment(properties, audioEmitter);
        scopeAttachment.applyEffect(scopeUser);
        boolean removed = scopeAttachment.removeEffect();

        assertTrue(removed);

        verify(audioEmitter).playSounds(properties.scopeStopSounds(), userLocation);
        verify(scopeUser).removeEffect(any(ItemEffect.class));
    }

    @Test
    public void shouldNotChangeMagnificationIfItHasNoMagnificationSettings() {
        List<Float> magnificationSettings = List.of(-0.1f);
        ScopeProperties properties = new ScopeProperties(List.of(), List.of(), magnificationSettings);
        ScopeUser scopeUser = mock(ScopeUser.class);

        DefaultScopeAttachment scopeAttachment = new DefaultScopeAttachment(properties, audioEmitter);
        scopeAttachment.nextMagnification();
        scopeAttachment.applyEffect(scopeUser);

        verify(scopeUser).applyViewMagnification(-0.1f);
    }

    @Test
    public void shouldChangeMagnificationByOneValue() {
        ScopeUser scopeUser = mock(ScopeUser.class);

        DefaultScopeAttachment scopeAttachment = new DefaultScopeAttachment(properties, audioEmitter);
        scopeAttachment.nextMagnification();
        scopeAttachment.applyEffect(scopeUser);

        verify(scopeUser).applyViewMagnification(-0.15f);
    }

    @Test
    public void shouldKeepsResettingMagnificationIfAllValuesHaveBeenChosen() {
        ScopeUser scopeUser = mock(ScopeUser.class);

        DefaultScopeAttachment scopeAttachment = new DefaultScopeAttachment(properties, audioEmitter);
        scopeAttachment.nextMagnification();
        scopeAttachment.nextMagnification();
        scopeAttachment.nextMagnification();
        scopeAttachment.applyEffect(scopeUser);

        verify(scopeUser).applyViewMagnification(-0.1f);
    }

    @Test
    public void shouldUpdateEffectIfBeingUsedWhenChangingMagnification() {
        ScopeUser scopeUser = mock(ScopeUser.class);

        DefaultScopeAttachment scopeAttachment = new DefaultScopeAttachment(properties, audioEmitter);
        scopeAttachment.applyEffect(scopeUser);
        scopeAttachment.nextMagnification();

        verify(scopeUser).applyViewMagnification(-0.15f);
    }
}
