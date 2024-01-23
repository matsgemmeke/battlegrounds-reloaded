package com.github.matsgemmeke.battlegounds.entity;

import com.github.matsgemmeke.battlegrounds.InternalsProvider;
import com.github.matsgemmeke.battlegrounds.api.item.Item;
import com.github.matsgemmeke.battlegrounds.entity.DefaultGamePlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DefaultGamePlayerTest {

    private InternalsProvider internals;
    private Player player;

    @Before
    public void setUp() {
        this.internals = mock(InternalsProvider.class);
        this.player = mock(Player.class);
    }

    @Test
    public void canGetPlayerEntity() {
        DefaultGamePlayer gamePlayer = new DefaultGamePlayer(player, internals);

        assertEquals(player, gamePlayer.getEntity());
    }

    @Test
    public void shouldNotReceiveRecoilWhenPlayerIsNotOnline() {
        when(player.isOnline()).thenReturn(false);

        DefaultGamePlayer gamePlayer = new DefaultGamePlayer(player, internals);

        assertFalse(gamePlayer.canReceiveRecoil());
    }

    @Test
    public void shouldNotReceiveRecoilWhenPlayerIsDead() {
        when(player.isOnline()).thenReturn(true);
        when(player.isDead()).thenReturn(true);

        DefaultGamePlayer gamePlayer = new DefaultGamePlayer(player, internals);

        assertFalse(gamePlayer.canReceiveRecoil());
    }

    @Test
    public void shouldReceiveRecoilWhenPlayerIsActive() {
        when(player.isOnline()).thenReturn(true);
        when(player.isDead()).thenReturn(false);

        DefaultGamePlayer gamePlayer = new DefaultGamePlayer(player, internals);

        assertTrue(gamePlayer.canReceiveRecoil());
    }

    @Test
    public void shouldGiveAimDirectionThatIsSlightlyBelowTheHeadOfThePlayer() {
        Location eyeLocation = new Location(null, 0, 1.0, 0);

        when(player.getEyeLocation()).thenReturn(eyeLocation.clone());

        DefaultGamePlayer gamePlayer = new DefaultGamePlayer(player, internals);
        Location aimDirection = gamePlayer.getAimDirection();

        assertTrue(aimDirection.getY() < eyeLocation.getY());
    }

    @Test
    public void returnsNullWhenFindingItemWithUnknownItemStack() {
        Item item = mock(Item.class);

        DefaultGamePlayer gamePlayer = new DefaultGamePlayer(player, internals);
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

        DefaultGamePlayer gamePlayer = new DefaultGamePlayer(player, internals);
        gamePlayer.addItem(item);

        assertEquals(item, gamePlayer.getItem(itemStack));
    }

    @Test
    public void shootsNormallyWhenStandingStill() {
        DefaultGamePlayer gamePlayer = new DefaultGamePlayer(player, internals);
        double accuracy = gamePlayer.getRelativeAccuracy();

        assertEquals(1.0, accuracy, 0.0);
    }

    @Test
    public void shootsMoreAccuratelyWhenSneaking() {
        when(player.isSneaking()).thenReturn(true);

        DefaultGamePlayer gamePlayer = new DefaultGamePlayer(player, internals);
        double accuracy = gamePlayer.getRelativeAccuracy();

        assertTrue(accuracy > 1.0);
    }

    @Test
    public void shootsLessAccuratelyWhenSprinting() {
        when(player.isSprinting()).thenReturn(true);

        DefaultGamePlayer gamePlayer = new DefaultGamePlayer(player, internals);
        double accuracy = gamePlayer.getRelativeAccuracy();

        assertTrue(accuracy < 1.0);
    }

    @Test
    public void applyingOperatingStateSetsFoodLevel() {
        DefaultGamePlayer gamePlayer = new DefaultGamePlayer(player, internals);
        gamePlayer.applyOperatingState(true);

        verify(player, times(1)).setFoodLevel(6);
    }

    @Test
    public void resettingOperatingStateSetsFoodLevel() {
        when(player.getFoodLevel()).thenReturn(10);

        DefaultGamePlayer gamePlayer = new DefaultGamePlayer(player, internals);
        gamePlayer.applyOperatingState(true);
        gamePlayer.applyOperatingState(false);

        verify(player, times(1)).setFoodLevel(10);
    }

    @Test
    public void shouldCallInternalFunctionWhenModifyCameraRotation() {
        DefaultGamePlayer gamePlayer = new DefaultGamePlayer(player, internals);
        gamePlayer.modifyCameraRotation(1.0f, 1.0f);

        verify(internals).setPlayerRotation(player, 1.0f, 1.0f);
    }
}
