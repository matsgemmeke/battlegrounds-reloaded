package nl.matsgemmeke.battlegrounds.item.scope;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class DefaultScopeAttachmentTest {

    private Iterable<Float> magnificationSettings;

    @Before
    public void setUp() {
        this.magnificationSettings = List.of(-0.1f, -0.15f, -0.2f);
    }

    @Test
    public void doesNotApplyEffectIfAlreadyBeingUsed() {
        ScopeUser user = mock(ScopeUser.class);

        DefaultScopeAttachment scopeAttachment = new DefaultScopeAttachment(magnificationSettings);
        scopeAttachment.applyEffect(user);

        boolean applied = scopeAttachment.applyEffect(user);

        assertFalse(applied);
    }

    @Test
    public void appliesEffectAndAddsItToUser() {
        ScopeUser user = mock(ScopeUser.class);
        when(user.addEffect(any())).thenReturn(true);

        DefaultScopeAttachment scopeAttachment = new DefaultScopeAttachment(magnificationSettings);
        boolean applied = scopeAttachment.applyEffect(user);

        verify(user).addEffect(any());

        assertTrue(applied);
    }

    @Test
    public void isNotScopedWhenEffectNotApplied() {
        DefaultScopeAttachment scopeAttachment = new DefaultScopeAttachment(magnificationSettings);
        boolean scoped = scopeAttachment.isScoped();

        assertFalse(scoped);
    }

    @Test
    public void isScopedWhenEffectApplied() {
        ScopeUser user = mock(ScopeUser.class);

        DefaultScopeAttachment scopeAttachment = new DefaultScopeAttachment(magnificationSettings);
        scopeAttachment.applyEffect(user);
        boolean scoped = scopeAttachment.isScoped();

        assertTrue(scoped);
    }

    @Test
    public void doNotRemoveEffectWhenNotInUse() {
        DefaultScopeAttachment scopeAttachment = new DefaultScopeAttachment(magnificationSettings);
        boolean removed = scopeAttachment.removeEffect();

        assertFalse(removed);
    }

    @Test
    public void removingScopeAlsoRemovesEffect() {
        ScopeUser user = mock(ScopeUser.class);

        DefaultScopeAttachment scopeAttachment = new DefaultScopeAttachment(magnificationSettings);
        scopeAttachment.applyEffect(user);
        boolean removed = scopeAttachment.removeEffect();

        verify(user).removeEffect(any());

        assertTrue(removed);
    }

    @Test
    public void shouldNotChangeMagnificationIfItHasNoMagnificationSettings() {
        Iterable<Float> magnificationSettings = List.of(-0.1f);

        ScopeUser user = mock(ScopeUser.class);

        DefaultScopeAttachment scopeAttachment = new DefaultScopeAttachment(magnificationSettings);
        scopeAttachment.nextMagnification();
        scopeAttachment.applyEffect(user);

        verify(user).applyViewMagnification(-0.1f);
    }

    @Test
    public void shouldChangeMagnificationByOneValue() {
        ScopeUser user = mock(ScopeUser.class);

        DefaultScopeAttachment scopeAttachment = new DefaultScopeAttachment(magnificationSettings);
        scopeAttachment.nextMagnification();
        scopeAttachment.applyEffect(user);

        verify(user).applyViewMagnification(-0.15f);
    }

    @Test
    public void shouldKeepsResettingMagnificationIfAllValuesHaveBeenChosen() {
        ScopeUser user = mock(ScopeUser.class);

        DefaultScopeAttachment scopeAttachment = new DefaultScopeAttachment(magnificationSettings);
        scopeAttachment.nextMagnification();
        scopeAttachment.nextMagnification();
        scopeAttachment.nextMagnification();
        scopeAttachment.applyEffect(user);

        verify(user).applyViewMagnification(-0.1f);
    }

    @Test
    public void shouldUpdateEffectIfBeingUsedWhenChangingMagnification() {
        ScopeUser user = mock(ScopeUser.class);

        DefaultScopeAttachment scopeAttachment = new DefaultScopeAttachment(magnificationSettings);
        scopeAttachment.applyEffect(user);
        scopeAttachment.nextMagnification();

        verify(user).applyViewMagnification(-0.15f);
    }
}
