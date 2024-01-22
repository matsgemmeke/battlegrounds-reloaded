package com.github.matsgemmeke.battlegounds.item;

import com.github.matsgemmeke.battlegrounds.InternalsProvider;
import com.github.matsgemmeke.battlegrounds.api.entity.GamePlayer;
import com.github.matsgemmeke.battlegrounds.api.entity.ItemHolder;
import com.github.matsgemmeke.battlegrounds.api.game.GameContext;
import com.github.matsgemmeke.battlegrounds.item.DefaultScopeAttachment;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DefaultScopeAttachmentTest {

    private float magnification;
    private GameContext context;
    private InternalsProvider internals;

    @Before
    public void setUp() {
        this.context = mock(GameContext.class);
        this.internals = mock(InternalsProvider.class);
        this.magnification = -0.1f;
    }

    @Test
    public void doesNotApplyEffectIfAlreadyBeingUsed() {
        GamePlayer gamePlayer = mock(GamePlayer.class);
        Player player = mock(Player.class);

        when(gamePlayer.getEntity()).thenReturn(player);

        DefaultScopeAttachment scopeAttachment = new DefaultScopeAttachment(context, internals, magnification);
        scopeAttachment.applyEffect(gamePlayer);

        boolean applied = scopeAttachment.applyEffect(gamePlayer);

        assertFalse(applied);
    }

    @Test
    public void doesNotApplyEffectToEntitiesOtherThanPlayers() {
        ItemHolder holder = mock(ItemHolder.class);

        DefaultScopeAttachment scopeAttachment = new DefaultScopeAttachment(context, internals, magnification);
        boolean applied = scopeAttachment.applyEffect(holder);

        assertFalse(applied);
    }

    @Test
    public void appliesEffectAndAddsItToGamePlayer() {
        GamePlayer gamePlayer = mock(GamePlayer.class);
        Player player = mock(Player.class);

        when(gamePlayer.addEffect(any())).thenReturn(true);
        when(gamePlayer.getEntity()).thenReturn(player);

        DefaultScopeAttachment scopeAttachment = new DefaultScopeAttachment(context, internals, magnification);
        boolean applied = scopeAttachment.applyEffect(gamePlayer);

        verify(gamePlayer, times(1)).addEffect(any());

        assertTrue(applied);
    }

    @Test
    public void isNotScopedWhenEffectNotApplied() {
        DefaultScopeAttachment scopeAttachment = new DefaultScopeAttachment(context, internals, magnification);
        boolean scoped = scopeAttachment.isScoped();

        assertFalse(scoped);
    }

    @Test
    public void isScopedWhenEffectApplied() {
        GamePlayer gamePlayer = mock(GamePlayer.class);
        Player player = mock(Player.class);

        when(gamePlayer.getEntity()).thenReturn(player);

        DefaultScopeAttachment scopeAttachment = new DefaultScopeAttachment(context, internals, magnification);
        scopeAttachment.applyEffect(gamePlayer);
        boolean scoped = scopeAttachment.isScoped();

        assertTrue(scoped);
    }

    @Test
    public void doNotRemoveEffectWhenNotInUse() {
        DefaultScopeAttachment scopeAttachment = new DefaultScopeAttachment(context, internals, magnification);
        boolean removed = scopeAttachment.removeEffect();

        assertFalse(removed);
    }

    @Test
    public void removingScopeAlsoRemovesEffect() {
        GamePlayer gamePlayer = mock(GamePlayer.class);
        Player player = mock(Player.class);

        when(gamePlayer.getEntity()).thenReturn(player);

        DefaultScopeAttachment scopeAttachment = new DefaultScopeAttachment(context, internals, magnification);
        scopeAttachment.applyEffect(gamePlayer);
        boolean removed = scopeAttachment.removeEffect();

        verify(gamePlayer, times(1)).removeEffect(any());

        assertTrue(removed);
    }
}
