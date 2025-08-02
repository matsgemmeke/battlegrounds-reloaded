package nl.matsgemmeke.battlegrounds.entity;

import nl.matsgemmeke.battlegrounds.InternalsProvider;
import nl.matsgemmeke.battlegrounds.game.damage.Damage;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.item.Matchable;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;

public class DefaultGamePlayerTest {

    private InternalsProvider internals;
    private Player player;

    @BeforeEach
    public void setUp() {
        this.internals = mock(InternalsProvider.class);
        this.player = mock(Player.class, RETURNS_DEEP_STUBS);
    }

    @Test
    public void canGetPlayerEntity() {
        DefaultGamePlayer gamePlayer = new DefaultGamePlayer(internals, player);

        assertEquals(player, gamePlayer.getEntity());
    }

    @Test
    public void getLastDamageReturnsNullIfPlayerHasNotTakenDamageYet() {
        DefaultGamePlayer gamePlayer = new DefaultGamePlayer(internals, player);
        Damage lastDamage = gamePlayer.getLastDamage();

        assertNull(lastDamage);
    }

    @Test
    public void getLastDamageReturnsLastDamageDealtToPlayer() {
        Damage damage = new Damage(10.0, DamageType.BULLET_DAMAGE);

        when(player.getHealth()).thenReturn(20.0);
        when(player.isDead()).thenReturn(false);

        DefaultGamePlayer gamePlayer = new DefaultGamePlayer(internals, player);
        gamePlayer.damage(damage);
        Damage lastDamage = gamePlayer.getLastDamage();

        assertEquals(damage, lastDamage);
    }

    @Test
    public void getUniqueIdReturnsPlayerUUID() {
        UUID playerUniqueId = UUID.randomUUID();
        when(player.getUniqueId()).thenReturn(playerUniqueId);

        DefaultGamePlayer gamePlayer = new DefaultGamePlayer(internals, player);
        UUID uniqueId = gamePlayer.getUniqueId();

        assertThat(uniqueId).isEqualTo(playerUniqueId);
    }

    @Test
    public void shouldNotReceiveRecoilWhenPlayerIsNotOnline() {
        when(player.isOnline()).thenReturn(false);

        DefaultGamePlayer gamePlayer = new DefaultGamePlayer(internals, player);

        assertFalse(gamePlayer.canReceiveRecoil());
    }

    @Test
    public void shouldNotReceiveRecoilWhenPlayerIsDead() {
        when(player.isOnline()).thenReturn(true);
        when(player.isDead()).thenReturn(true);

        DefaultGamePlayer gamePlayer = new DefaultGamePlayer(internals, player);

        assertFalse(gamePlayer.canReceiveRecoil());
    }

    @Test
    public void shouldReceiveRecoilWhenPlayerIsActive() {
        when(player.isOnline()).thenReturn(true);
        when(player.isDead()).thenReturn(false);

        DefaultGamePlayer gamePlayer = new DefaultGamePlayer(internals, player);

        assertTrue(gamePlayer.canReceiveRecoil());
    }

    @Test
    public void damageAppliesNoDamageWhenPlayerIsDead() {
        when(player.getHealth()).thenReturn(0.0);
        when(player.isDead()).thenReturn(true);

        Damage damage = new Damage(10.0, DamageType.BULLET_DAMAGE);

        DefaultGamePlayer gamePlayer = new DefaultGamePlayer(internals, player);
        double damageDealt = gamePlayer.damage(damage);

        assertEquals(0.0, damageDealt);

        verify(player, never()).setHealth(anyDouble());
    }

    @Test
    public void damageAppliesNoDamageWhenPlayerHasNoHealth() {
        when(player.getHealth()).thenReturn(0.0);
        when(player.isDead()).thenReturn(false);

        Damage damage = new Damage(10.0, DamageType.BULLET_DAMAGE);

        DefaultGamePlayer gamePlayer = new DefaultGamePlayer(internals, player);
        double damageDealt = gamePlayer.damage(damage);

        assertEquals(0.0, damageDealt);

        verify(player, never()).setHealth(anyDouble());
    }

    @NotNull
    private static Stream<Arguments> damageScenarios() {
        return Stream.of(
                arguments(10.0, 10.0, 20.0, 18.0),
                arguments(1000.0, 1000.0, 20.0, 0.0)
        );
    }

    @ParameterizedTest
    @MethodSource("damageScenarios")
    public void damageReturnsDealtDamageAndLowersHealth(
            double damageAmount,
            double expectedDamageDealt,
            double health,
            double expectedHealth
    ) {
        when(player.getHealth()).thenReturn(health);
        when(player.isDead()).thenReturn(false);

        Damage damage = new Damage(damageAmount, DamageType.BULLET_DAMAGE);

        DefaultGamePlayer gamePlayer = new DefaultGamePlayer(internals, player);
        double damageDealt = gamePlayer.damage(damage);

        assertEquals(expectedDamageDealt, damageDealt);

        verify(player).setHealth(expectedHealth);
    }

    @Test
    public void shouldReturnItemInMainHand() {
        ItemStack itemStack = new ItemStack(Material.STONE);

        PlayerInventory inventory = mock(PlayerInventory.class);
        when(inventory.getItemInMainHand()).thenReturn(itemStack);
        when(player.getInventory()).thenReturn(inventory);

        DefaultGamePlayer gamePlayer = new DefaultGamePlayer(internals, player);
        ItemStack heldItem = gamePlayer.getHeldItem();

        assertEquals(itemStack, heldItem);
    }

    @Test
    public void shouldSetItemInMainHand() {
        ItemStack itemStack = new ItemStack(Material.STONE);

        PlayerInventory inventory = mock(PlayerInventory.class);
        when(player.getInventory()).thenReturn(inventory);

        DefaultGamePlayer gamePlayer = new DefaultGamePlayer(internals, player);
        gamePlayer.setHeldItem(itemStack);

        verify(inventory).setItemInMainHand(itemStack);
    }

    @Test
    public void getDeployLocationReturnsPlayerEyeLocation() {
        Location eyeLocation = new Location(null, 1, 1, 1);
        when(player.getEyeLocation()).thenReturn(eyeLocation);

        DefaultGamePlayer gamePlayer = new DefaultGamePlayer(internals, player);
        Location deployLocation = gamePlayer.getDeployLocation();

        assertThat(deployLocation).isEqualTo(eyeLocation);
    }

    @Test
    public void getItemSlotReturnsOptionalWithSlotNumberOfMatchingItemStack() {
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);
        ItemStack[] contents = new ItemStack[] { null, itemStack, null };

        Matchable item = mock(Matchable.class);
        when(item.isMatching(itemStack)).thenReturn(true);

        when(player.getInventory().getContents()).thenReturn(contents);

        DefaultGamePlayer gamePlayer = new DefaultGamePlayer(internals, player);
        Optional<Integer> itemSlot = gamePlayer.getItemSlot(item);

        assertThat(itemSlot).hasValue(1);
    }

    @Test
    public void getItemSlotReturnsEmptyOptionalWhenNoneOfTheInventoryContentsMatchWithGivenItemStack() {
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);
        ItemStack[] contents = new ItemStack[] { null, itemStack, null };

        Matchable item = mock(Matchable.class);
        when(item.isMatching(itemStack)).thenReturn(false);

        when(player.getInventory().getContents()).thenReturn(contents);

        DefaultGamePlayer gamePlayer = new DefaultGamePlayer(internals, player);
        Optional<Integer> itemSlot = gamePlayer.getItemSlot(item);

        assertThat(itemSlot).isEmpty();
    }

    @Test
    public void shouldReturnPlayerTargetBlocks() {
        int maxDistance = 3;
        List<Block> targetBlocks = List.of(mock(Block.class), mock(Block.class));

        when(player.getLastTwoTargetBlocks(null, maxDistance)).thenReturn(targetBlocks);

        DefaultGamePlayer gamePlayer = new DefaultGamePlayer(internals, player);
        List<Block> result = gamePlayer.getLastTwoTargetBlocks(maxDistance);

        assertEquals(targetBlocks, result);
    }

    @Test
    public void shootsNormallyWhenStandingStill() {
        DefaultGamePlayer gamePlayer = new DefaultGamePlayer(internals, player);
        double accuracy = gamePlayer.getRelativeAccuracy();

        assertEquals(1.0, accuracy, 0.0);
    }

    @Test
    public void shootsMoreAccuratelyWhenSneaking() {
        when(player.isSneaking()).thenReturn(true);

        DefaultGamePlayer gamePlayer = new DefaultGamePlayer(internals, player);
        double accuracy = gamePlayer.getRelativeAccuracy();

        assertTrue(accuracy > 1.0);
    }

    @Test
    public void shootsLessAccuratelyWhenSprinting() {
        when(player.isSprinting()).thenReturn(true);

        DefaultGamePlayer gamePlayer = new DefaultGamePlayer(internals, player);
        double accuracy = gamePlayer.getRelativeAccuracy();

        assertTrue(accuracy < 1.0);
    }

    @Test
    public void applyingOperatingStateSetsFoodLevel() {
        DefaultGamePlayer gamePlayer = new DefaultGamePlayer(internals, player);
        gamePlayer.applyReloadingState();

        verify(player).setFoodLevel(6);
    }

    @Test
    public void resettingOperatingStateSetsFoodLevel() {
        when(player.getFoodLevel()).thenReturn(10);

        DefaultGamePlayer gamePlayer = new DefaultGamePlayer(internals, player);
        gamePlayer.applyReloadingState();
        gamePlayer.resetReloadingState();

        verify(player).setFoodLevel(10);
    }

    @Test
    public void shouldCallInternalFunctionWhenApplyingViewMagnification() {
        float magnification = -0.1f;

        DefaultGamePlayer gamePlayer = new DefaultGamePlayer(internals, player);
        gamePlayer.applyViewMagnification(magnification);

        verify(internals).setWalkSpeed(player, magnification);
    }

    @Test
    public void launchProjectileMakesPlayerLaunchProjectileWithGivenArguments() {
        Class<? extends Projectile> projectileClass = Arrow.class;
        Vector velocity = new Vector(1.0, 1.0, 1.0);

        DefaultGamePlayer gamePlayer = new DefaultGamePlayer(internals, player);
        gamePlayer.launchProjectile(projectileClass, velocity);

        verify(player).launchProjectile(projectileClass, velocity);
    }

    @Test
    public void shouldCallInternalFunctionWhenModifyingCameraRotation() {
        DefaultGamePlayer gamePlayer = new DefaultGamePlayer(internals, player);
        gamePlayer.modifyCameraRotation(1.0f, 1.0f);

        verify(internals).setPlayerRotation(player, 1.0f, 1.0f);
    }

    @Test
    public void removeItemStackFromInventory() {
        ItemStack itemStack = new ItemStack(Material.STONE);

        PlayerInventory inventory = mock(PlayerInventory.class);
        when(player.getInventory()).thenReturn(inventory);

        DefaultGamePlayer gamePlayer = new DefaultGamePlayer(internals, player);
        gamePlayer.removeItem(itemStack);

        verify(inventory).removeItem(itemStack);
    }

    @Test
    public void shouldReturnThePlayersPositionAsAudioPlayLocation() {
        Location location = new Location(null, 1.0, 1.0, 1.0);

        when(player.getLocation()).thenReturn(location);

        DefaultGamePlayer gamePlayer = new DefaultGamePlayer(internals, player);
        Location result = gamePlayer.getAudioPlayLocation();

        assertEquals(location, result);
    }

    @Test
    public void shouldReturnSlightlyAlteredEyeLocationAsShootingDirection() {
        Location eyeLocation = new Location(null, 1.0, 1.0, 1.0);

        when(player.getEyeLocation()).thenReturn(eyeLocation.clone());

        DefaultGamePlayer gamePlayer = new DefaultGamePlayer(internals, player);
        Location result = gamePlayer.getShootingDirection();

        assertNotEquals(eyeLocation, result);
        assertEquals(eyeLocation.getX(), result.getX(), 0.0);
        assertNotEquals(eyeLocation.getY(), result.getY());
        assertEquals(eyeLocation.getZ(), result.getZ(), 0.0);
    }
}
