package com.github.matsgemmeke.battlegounds.entity;

import com.github.matsgemmeke.battlegrounds.api.item.Item;
import com.github.matsgemmeke.battlegrounds.entity.DefaultGamePlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DefaultGamePlayerTest {

    private Player player;

    @Before
    public void setUp() {
        this.player = mock(Player.class);
    }

    @Test
    public void canGetPlayerEntity() {
        DefaultGamePlayer gamePlayer = new DefaultGamePlayer(player);

        assertEquals(player, gamePlayer.getEntity());
    }

    @Test
    public void returnsNullWhenFindingItemWithUnknownItemStack() {
        Item item = mock(Item.class);

        DefaultGamePlayer gamePlayer = new DefaultGamePlayer(player);
        gamePlayer.addItem(item);

        ItemStack itemStack = new ItemStack(Material.IRON_HOE);

        assertNull(gamePlayer.getItem(itemStack));
    }

    @Test
    public void canFindItemByItemStack() {
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);

        ItemStack other = mock(ItemStack.class);
        when(other.isSimilar(itemStack)).thenReturn(true);

        Item item = mock(Item.class);
        when(item.getItemStack()).thenReturn(other);

        DefaultGamePlayer gamePlayer = new DefaultGamePlayer(player);
        gamePlayer.addItem(item);

        assertEquals(item, gamePlayer.getItem(itemStack));
    }

    @Test
    public void shootsNormallyWhenStandingStill() {
        DefaultGamePlayer gamePlayer = new DefaultGamePlayer(player);
        double accuracy = gamePlayer.getRelativeAccuracy();

        assertEquals(1.0, accuracy, 0.0);
    }

    @Test
    public void shootsMoreAccuratelyWhenSneaking() {
        when(player.isSneaking()).thenReturn(true);

        DefaultGamePlayer gamePlayer = new DefaultGamePlayer(player);
        double accuracy = gamePlayer.getRelativeAccuracy();

        assertTrue(accuracy > 1.0);
    }

    @Test
    public void shootsLessAccuratelyWhenSprinting() {
        when(player.isSprinting()).thenReturn(true);

        DefaultGamePlayer gamePlayer = new DefaultGamePlayer(player);
        double accuracy = gamePlayer.getRelativeAccuracy();

        assertTrue(accuracy < 1.0);
    }

    @Test
    public void applyingOperatingStateSetsFoodLevel() {
        DefaultGamePlayer gamePlayer = new DefaultGamePlayer(player);
        gamePlayer.applyOperatingState(true);

        verify(player, times(1)).setFoodLevel(6);
    }

    @Test
    public void resettingOperatingStateSetsFoodLevel() {
        when(player.getFoodLevel()).thenReturn(10);

        DefaultGamePlayer gamePlayer = new DefaultGamePlayer(player);
        gamePlayer.applyOperatingState(true);
        gamePlayer.applyOperatingState(false);

        verify(player, times(1)).setFoodLevel(10);
    }
}
