package nl.matsgemmeke.battlegrounds.item.scope;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class ScopeZoomEffectTest {

    private ScopeUser user;

    @BeforeEach
    public void setUp() {
        this.user = mock(ScopeUser.class);
    }

    @Test
    public void applyingEffectAltersUserViewMagnification() {
        float magnification = -0.1f;

        ScopeZoomEffect effect = new ScopeZoomEffect(user, magnification);
        effect.apply();

        verify(user).applyViewMagnification(magnification);
    }

    @Test
    public void removingEffectResetsUserViewMagnification() {
        float magnification = -0.1f;

        ScopeZoomEffect effect = new ScopeZoomEffect(user, magnification);
        effect.remove();

        verify(user).applyViewMagnification(0.1f);
    }

    @Test
    public void doesNotUpdateIfMagnificationWasNotChanged() {
        float magnification = -0.1f;

        ScopeZoomEffect effect = new ScopeZoomEffect(user, magnification);
        effect.apply();
        boolean updated = effect.update();

        assertFalse(updated);
    }

    @Test
    public void changesMagnificationWhenUpdating() {
        float magnification = -0.1f;

        ScopeZoomEffect effect = new ScopeZoomEffect(user, magnification);
        effect.apply();
        effect.setMagnification(-0.2f);
        boolean updated = effect.update();

        verify(user).applyViewMagnification(-0.2f);

        assertTrue(updated);
    }
}
