package com.github.matsgemmeke.battlegounds.game;

import com.github.matsgemmeke.battlegrounds.TaskRunner;
import com.github.matsgemmeke.battlegrounds.api.entity.BattlePlayer;
import com.github.matsgemmeke.battlegrounds.api.item.BattleItem;
import com.github.matsgemmeke.battlegrounds.game.DefaultFreemodeContext;
import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class FreemodeContextTest {

    private TaskRunner taskRunner;

    @Before
    public void setUp() {
        this.taskRunner = mock(TaskRunner.class);
    }

    @Test
    public void executesLeftClickMethodIfActionIsLeftClick() {
        BattleItem battleItem = mock(BattleItem.class);
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);
        PlayerInteractEvent event = new PlayerInteractEvent(null, Action.LEFT_CLICK_AIR, itemStack, null, null);

        BattlePlayer battlePlayer = mock(BattlePlayer.class);
        when(battlePlayer.getBattleItem(itemStack)).thenReturn(battleItem);

        DefaultFreemodeContext context = new DefaultFreemodeContext(taskRunner);
        boolean result = context.onInteract(battlePlayer, event);

        verify(battleItem).onLeftClick(battlePlayer);

        assertTrue(result);
        assertTrue(event.isCancelled());
    }

    @Test
    public void executesRightClickMethodIfActionIsRightClick() {
        BattleItem battleItem = mock(BattleItem.class);
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);
        PlayerInteractEvent event = new PlayerInteractEvent(null, Action.RIGHT_CLICK_AIR, itemStack, null, null);

        BattlePlayer battlePlayer = mock(BattlePlayer.class);
        when(battlePlayer.getBattleItem(itemStack)).thenReturn(battleItem);

        DefaultFreemodeContext context = new DefaultFreemodeContext(taskRunner);
        boolean result = context.onInteract(battlePlayer, event);

        verify(battleItem).onRightClick(battlePlayer);

        assertTrue(result);
        assertTrue(event.isCancelled());
    }

    @Test
    public void doesNothingWithInteractionWithoutItemStack() {
        BattlePlayer battlePlayer = mock(BattlePlayer.class);
        PlayerInteractEvent event = new PlayerInteractEvent(null, null, null, null, null);

        DefaultFreemodeContext context = new DefaultFreemodeContext(taskRunner);
        boolean result = context.onInteract(battlePlayer, event);

        assertFalse(result);
    }

    @Test
    public void doesNothingWithInteractionWhenBattleItemDoesNotExist() {
        BattlePlayer battlePlayer = mock(BattlePlayer.class);
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);
        PlayerInteractEvent event = new PlayerInteractEvent(null, null, itemStack, null, null);

        DefaultFreemodeContext context = new DefaultFreemodeContext(taskRunner);
        boolean result = context.onInteract(battlePlayer, event);

        assertFalse(result);
    }
}
