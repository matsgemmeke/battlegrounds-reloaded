package com.github.matsgemmeke.battlegounds.entity;

import com.github.matsgemmeke.battlegrounds.api.item.BattleItem;
import com.github.matsgemmeke.battlegrounds.entity.DefaultBattlePlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DefaultBattlePlayerTest {

    private Player player;

    @Before
    public void setUp() {
        this.player = mock(Player.class);
    }

    @Test
    public void canGetPlayerEntity() {
        DefaultBattlePlayer battlePlayer = new DefaultBattlePlayer(player);

        assertEquals(player, battlePlayer.getEntity());
    }

    @Test
    public void returnsNullWhenFindingBattleItemWithUnknownItemStack() {
        BattleItem item = mock(BattleItem.class);

        DefaultBattlePlayer battlePlayer = new DefaultBattlePlayer(player);
        battlePlayer.getItems().add(item);

        ItemStack itemStack = new ItemStack(Material.IRON_HOE);

        assertNull(battlePlayer.getBattleItem(itemStack));
    }

    @Test
    public void canFindBattleItemByItemStack() {
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);

        ItemStack other = mock(ItemStack.class);
        when(other.isSimilar(itemStack)).thenReturn(true);

        BattleItem item = mock(BattleItem.class);
        when(item.getItemStack()).thenReturn(other);

        DefaultBattlePlayer battlePlayer = new DefaultBattlePlayer(player);
        battlePlayer.getItems().add(item);

        assertEquals(item, battlePlayer.getBattleItem(itemStack));
    }

    @Test
    public void shootsNormallyWhenStandingStill() {
        DefaultBattlePlayer battlePlayer = new DefaultBattlePlayer(player);
        double accuracy = battlePlayer.getRelativeAccuracy();

        assertEquals(1.0, accuracy, 0.0);
    }

    @Test
    public void shootsMoreAccuratelyWhenSneaking() {
        when(player.isSneaking()).thenReturn(true);

        DefaultBattlePlayer battlePlayer = new DefaultBattlePlayer(player);
        double accuracy = battlePlayer.getRelativeAccuracy();

        assertTrue(accuracy > 1.0);
    }

    @Test
    public void shootsLessAccuratelyWhenSprinting() {
        when(player.isSprinting()).thenReturn(true);

        DefaultBattlePlayer battlePlayer = new DefaultBattlePlayer(player);
        double accuracy = battlePlayer.getRelativeAccuracy();

        assertTrue(accuracy < 1.0);
    }

    @Test
    public void applyingOperatingStateSetsFoodLevel() {
        DefaultBattlePlayer battlePlayer = new DefaultBattlePlayer(player);
        battlePlayer.applyOperatingState(true);

        verify(player, times(1)).setFoodLevel(6);
    }

    @Test
    public void resettingOperatingStateSetsFoodLevel() {
        when(player.getFoodLevel()).thenReturn(10);

        DefaultBattlePlayer battlePlayer = new DefaultBattlePlayer(player);
        battlePlayer.applyOperatingState(true);
        battlePlayer.applyOperatingState(false);

        verify(player, times(1)).setFoodLevel(10);
    }
}
