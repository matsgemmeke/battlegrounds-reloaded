package nl.matsgemmeke.battlegrounds.item.scope;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class ScopeZoomEffectTest {

    private ScopeUser scopeUser;

    @BeforeEach
    public void setUp() {
        this.scopeUser = mock(ScopeUser.class);
    }

    @Test
    public void applyingEffectAltersUserViewMagnification() {
        float magnification = -0.1f;

        ScopeZoomEffect effect = new ScopeZoomEffect(scopeUser, magnification);
        effect.apply();

        verify(scopeUser).applyViewMagnification(magnification);
    }

    @Test
    public void removingEffectResetsUserViewMagnification() {
        float magnification = -0.1f;

        ScopeZoomEffect effect = new ScopeZoomEffect(scopeUser, magnification);
        effect.remove();

        verify(scopeUser).applyViewMagnification(0.1f);
    }

    @Test
    public void doesNotUpdateIfMagnificationWasNotChanged() {
        float magnification = -0.1f;

        ScopeZoomEffect effect = new ScopeZoomEffect(scopeUser, magnification);
        effect.apply();
        boolean updated = effect.update();

        assertFalse(updated);
    }

    @Test
    public void changesMagnificationWhenUpdating() {
        float magnification = -0.1f;

        ScopeZoomEffect effect = new ScopeZoomEffect(scopeUser, magnification);
        effect.apply();
        effect.setMagnification(-0.2f);
        boolean updated = effect.update();

        verify(scopeUser).applyViewMagnification(-0.2f);

        assertTrue(updated);
    }
}
