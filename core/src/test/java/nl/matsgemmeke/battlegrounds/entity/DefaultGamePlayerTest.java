package nl.matsgemmeke.battlegrounds.entity;

import nl.matsgemmeke.battlegrounds.InternalsProvider;
import nl.matsgemmeke.battlegrounds.item.Item;
import nl.matsgemmeke.battlegrounds.item.Weapon;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

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

        Item item = mock(Item.class);
        when(item.isMatching(itemStack)).thenReturn(true);

        DefaultGamePlayer gamePlayer = new DefaultGamePlayer(player, internals);
        gamePlayer.addItem(item);

        assertEquals(item, gamePlayer.getItem(itemStack));
    }

    @Test
    public void returnsNullWhenFindingWeaponWithUnknownItemStack() {
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);

        Weapon weapon = mock(Weapon.class);

        DefaultGamePlayer gamePlayer = new DefaultGamePlayer(player, internals);
        gamePlayer.addWeapon(weapon);

        assertNull(gamePlayer.getWeapon(itemStack));
    }

    @Test
    public void canFindWeaponByItemStack() {
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);

        Weapon weapon = mock(Weapon.class);
        when(weapon.isMatching(itemStack)).thenReturn(true);

        DefaultGamePlayer gamePlayer = new DefaultGamePlayer(player, internals);
        gamePlayer.addWeapon(weapon);

        assertEquals(weapon, gamePlayer.getWeapon(itemStack));
    }

    @Test
    public void shouldReturnPlayerTargetBlocks() {
        int maxDistance = 3;
        List<Block> targetBlocks = List.of(mock(Block.class), mock(Block.class));

        when(player.getLastTwoTargetBlocks(null, maxDistance)).thenReturn(targetBlocks);

        DefaultGamePlayer gamePlayer = new DefaultGamePlayer(player, internals);
        List<Block> result = gamePlayer.getLastTwoTargetBlocks(maxDistance);

        assertEquals(targetBlocks, result);
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
        gamePlayer.applyReloadingState();

        verify(player).setFoodLevel(6);
    }

    @Test
    public void resettingOperatingStateSetsFoodLevel() {
        when(player.getFoodLevel()).thenReturn(10);

        DefaultGamePlayer gamePlayer = new DefaultGamePlayer(player, internals);
        gamePlayer.applyReloadingState();
        gamePlayer.resetReloadingState();

        verify(player).setFoodLevel(10);
    }

    @Test
    public void shouldCallInternalFunctionWhenApplyingViewMagnification() {
        float magnification = -0.1f;

        DefaultGamePlayer gamePlayer = new DefaultGamePlayer(player, internals);
        gamePlayer.applyViewMagnification(magnification);

        verify(internals).setWalkSpeed(player, magnification);
    }

    @Test
    public void shouldCallInternalFunctionWhenModifyingCameraRotation() {
        DefaultGamePlayer gamePlayer = new DefaultGamePlayer(player, internals);
        gamePlayer.modifyCameraRotation(1.0f, 1.0f);

        verify(internals).setPlayerRotation(player, 1.0f, 1.0f);
    }

    @Test
    public void shouldReturnThePlayersPositionAsAudioPlayLocation() {
        Location location = new Location(null, 1.0, 1.0, 1.0);

        when(player.getLocation()).thenReturn(location);

        DefaultGamePlayer gamePlayer = new DefaultGamePlayer(player, internals);
        Location result = gamePlayer.getAudioPlayLocation();

        assertEquals(location, result);
    }

    @Test
    public void shouldReturnSlightlyAlteredEyeLocationAsShootingDirection() {
        Location eyeLocation = new Location(null, 1.0, 1.0, 1.0);

        when(player.getEyeLocation()).thenReturn(eyeLocation.clone());

        DefaultGamePlayer gamePlayer = new DefaultGamePlayer(player, internals);
        Location result = gamePlayer.getShootingDirection();

        assertNotEquals(eyeLocation, result);
        assertEquals(eyeLocation.getX(), result.getX(), 0.0);
        assertNotEquals(eyeLocation.getY(), result.getY());
        assertEquals(eyeLocation.getZ(), result.getZ(), 0.0);
    }
}
