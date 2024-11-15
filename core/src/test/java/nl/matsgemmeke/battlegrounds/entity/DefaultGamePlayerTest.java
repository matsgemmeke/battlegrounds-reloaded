package nl.matsgemmeke.battlegrounds.entity;

import nl.matsgemmeke.battlegrounds.InternalsProvider;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DefaultGamePlayerTest {

    private InternalsProvider internals;
    private Player player;

    @BeforeEach
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
    public void applyZeroDamageWhenPlayerIsDead() {
        when(player.getHealth()).thenReturn(0.0);
        when(player.isDead()).thenReturn(true);

        DefaultGamePlayer gamePlayer = new DefaultGamePlayer(player, internals);
        double health = gamePlayer.damage(10.0);

        assertEquals(0.0, health, 0.0);
    }

    @Test
    public void applyZeroDamageWhenPlayerAlreadyHasNoHealth() {
        when(player.getHealth()).thenReturn(0.0);
        when(player.isDead()).thenReturn(false);

        DefaultGamePlayer gamePlayer = new DefaultGamePlayer(player, internals);
        double health = gamePlayer.damage(10.0);

        assertEquals(0.0, health, 0.0);
    }

    @Test
    public void convertDamageToHeartsAndApplyDamageToPlayer() {
        when(player.getHealth()).thenReturn(20.0);
        when(player.isDead()).thenReturn(false);

        DefaultGamePlayer gamePlayer = new DefaultGamePlayer(player, internals);
        double health = gamePlayer.damage(10.0);

        assertEquals(18.0, health, 0.0);
    }

    @Test
    public void shouldReturnItemInMainHand() {
        ItemStack itemStack = new ItemStack(Material.STONE);

        PlayerInventory inventory = mock(PlayerInventory.class);
        when(inventory.getItemInMainHand()).thenReturn(itemStack);
        when(player.getInventory()).thenReturn(inventory);

        DefaultGamePlayer gamePlayer = new DefaultGamePlayer(player, internals);
        ItemStack heldItem = gamePlayer.getHeldItem();

        assertEquals(itemStack, heldItem);
    }

    @Test
    public void shouldSetItemInMainHand() {
        ItemStack itemStack = new ItemStack(Material.STONE);

        PlayerInventory inventory = mock(PlayerInventory.class);
        when(player.getInventory()).thenReturn(inventory);

        DefaultGamePlayer gamePlayer = new DefaultGamePlayer(player, internals);
        gamePlayer.setHeldItem(itemStack);

        verify(inventory).setItemInMainHand(itemStack);
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
    public void removeItemStackFromInventory() {
        ItemStack itemStack = new ItemStack(Material.STONE);

        PlayerInventory inventory = mock(PlayerInventory.class);
        when(player.getInventory()).thenReturn(inventory);

        DefaultGamePlayer gamePlayer = new DefaultGamePlayer(player, internals);
        gamePlayer.removeItem(itemStack);

        verify(inventory).removeItem(itemStack);
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
