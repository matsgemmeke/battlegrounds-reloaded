package nl.matsgemmeke.battlegrounds.item.scope;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
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

        List<GameSound> scopeUseSounds = List.of();
        List<GameSound> scopeStopSounds = List.of();
        List<Float> magnificationSettings = List.of(-0.1f, -0.15f, -0.2f);

        properties = new ScopeProperties(scopeUseSounds, scopeStopSounds, magnificationSettings);
    }

    @Test
    public void doesNotApplyEffectIfAlreadyBeingUsed() {
        ScopeUser scopeUser = mock(ScopeUser.class);

        DefaultScopeAttachment scopeAttachment = new DefaultScopeAttachment(properties, audioEmitter);
        scopeAttachment.applyEffect(scopeUser);

        boolean applied = scopeAttachment.applyEffect(scopeUser);

        assertFalse(applied);
    }

    @Test
    public void appliesEffectAndAddsItToUser() {
        ScopeUser scopeUser = mock(ScopeUser.class);
        when(scopeUser.addEffect(any())).thenReturn(true);

        DefaultScopeAttachment scopeAttachment = new DefaultScopeAttachment(properties, audioEmitter);
        boolean applied = scopeAttachment.applyEffect(scopeUser);

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
    public void doNotRemoveEffectWhenNotInUse() {
        DefaultScopeAttachment scopeAttachment = new DefaultScopeAttachment(properties, audioEmitter);
        boolean removed = scopeAttachment.removeEffect();

        assertFalse(removed);
    }

    @Test
    public void removingScopeAlsoRemovesEffect() {
        ScopeUser scopeUser = mock(ScopeUser.class);

        DefaultScopeAttachment scopeAttachment = new DefaultScopeAttachment(properties, audioEmitter);
        scopeAttachment.applyEffect(scopeUser);
        boolean removed = scopeAttachment.removeEffect();

        verify(scopeUser).removeEffect(any());

        assertTrue(removed);
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
