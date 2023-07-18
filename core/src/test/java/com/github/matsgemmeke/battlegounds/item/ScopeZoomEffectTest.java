package com.github.matsgemmeke.battlegounds.item;

import com.github.matsgemmeke.battlegrounds.InternalsProvider;
import com.github.matsgemmeke.battlegrounds.item.ScopeZoomEffect;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ScopeZoomEffectTest {

    private InternalsProvider internals;
    private Player player;

    @Before
    public void setUp() {
        this.player = mock(Player.class);
        this.internals = mock(InternalsProvider.class);
    }

    @Test
    public void applyingEffectSetsPlayerFOV() {
        float magnification = -0.1f;

        ScopeZoomEffect effect = new ScopeZoomEffect(player, internals, magnification);
        effect.apply();

        verify(internals, times(1)).setScopeLevel(player, magnification);
    }

    @Test
    public void removingEffectResetsPlayerFOV() {
        float magnification = -0.1f;

        ScopeZoomEffect effect = new ScopeZoomEffect(player, internals, magnification);
        effect.remove();

        verify(internals, times(1)).setScopeLevel(player, 0.1f);
    }

    @Test
    public void doesNotUpdateIfMagnificationWasNotChanged() {
        float magnification = -0.1f;

        ScopeZoomEffect effect = new ScopeZoomEffect(player, internals, magnification);
        effect.apply();
        boolean updated = effect.update();

        assertFalse(updated);
    }

    @Test
    public void changesMagnificationWhenUpdating() {
        float magnification = -0.1f;

        ScopeZoomEffect effect = new ScopeZoomEffect(player, internals, magnification);
        effect.apply();
        effect.setMagnification(-0.2f);
        boolean updated = effect.update();

        verify(internals, times(1)).setScopeLevel(player, -0.2f);

        assertTrue(updated);
    }
}
