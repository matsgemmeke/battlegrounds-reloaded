package com.github.matsgemmeke.battlegounds.item;

import com.github.matsgemmeke.battlegrounds.InternalsProvider;
import com.github.matsgemmeke.battlegrounds.api.entity.BattleItemHolder;
import com.github.matsgemmeke.battlegrounds.api.entity.BattlePlayer;
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
        BattlePlayer battlePlayer = mock(BattlePlayer.class);
        Player player = mock(Player.class);

        when(battlePlayer.getEntity()).thenReturn(player);

        DefaultScopeAttachment scopeAttachment = new DefaultScopeAttachment(context, internals, magnification);
        scopeAttachment.applyEffect(battlePlayer);

        boolean applied = scopeAttachment.applyEffect(battlePlayer);

        assertFalse(applied);
    }

    @Test
    public void doesNotApplyEffectToEntitiesOtherThanPlayers() {
        BattleItemHolder holder = mock(BattleItemHolder.class);

        DefaultScopeAttachment scopeAttachment = new DefaultScopeAttachment(context, internals, magnification);
        boolean applied = scopeAttachment.applyEffect(holder);

        assertFalse(applied);
    }

    @Test
    public void appliesEffectAndAddsItToBattlePlayer() {
        BattlePlayer battlePlayer = mock(BattlePlayer.class);
        Player player = mock(Player.class);

        when(battlePlayer.addEffect(any())).thenReturn(true);
        when(battlePlayer.getEntity()).thenReturn(player);

        DefaultScopeAttachment scopeAttachment = new DefaultScopeAttachment(context, internals, magnification);
        boolean applied = scopeAttachment.applyEffect(battlePlayer);

        verify(battlePlayer, times(1)).addEffect(any());

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
        BattlePlayer battlePlayer = mock(BattlePlayer.class);
        Player player = mock(Player.class);

        when(battlePlayer.getEntity()).thenReturn(player);

        DefaultScopeAttachment scopeAttachment = new DefaultScopeAttachment(context, internals, magnification);
        scopeAttachment.applyEffect(battlePlayer);
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
        BattlePlayer battlePlayer = mock(BattlePlayer.class);
        Player player = mock(Player.class);

        when(battlePlayer.getEntity()).thenReturn(player);

        DefaultScopeAttachment scopeAttachment = new DefaultScopeAttachment(context, internals, magnification);
        scopeAttachment.applyEffect(battlePlayer);
        boolean removed = scopeAttachment.removeEffect();

        verify(battlePlayer, times(1)).removeEffect(any());

        assertTrue(removed);
    }
}
