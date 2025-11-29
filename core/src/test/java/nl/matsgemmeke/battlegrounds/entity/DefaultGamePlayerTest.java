package nl.matsgemmeke.battlegrounds.entity;

import nl.matsgemmeke.battlegrounds.InternalsProvider;
import nl.matsgemmeke.battlegrounds.entity.hitbox.Hitbox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.provider.HitboxProvider;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultGamePlayerTest {

    @Mock
    private InternalsProvider internals;
    @Mock
    private HitboxProvider hitboxProvider;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Player player;
    @InjectMocks
    private DefaultGamePlayer gamePlayer;

    @Test
    void getLastDamageReturnsNullIfPlayerHasNotTakenDamageYet() {
        Damage lastDamage = gamePlayer.getLastDamage();

        assertThat(lastDamage).isNull();
    }

    @Test
    void getLastDamageReturnsLastDamageDealtToPlayer() {
        Damage damage = new Damage(10.0, DamageType.BULLET_DAMAGE);

        when(player.getHealth()).thenReturn(20.0);
        when(player.isDead()).thenReturn(false);

        gamePlayer.damage(damage);
        Damage lastDamage = gamePlayer.getLastDamage();

        assertThat(lastDamage).isEqualTo(damage);
    }

    @Test
    void getUniqueIdReturnsPlayerUUID() {
        UUID playerUniqueId = UUID.randomUUID();
        when(player.getUniqueId()).thenReturn(playerUniqueId);

        UUID uniqueId = gamePlayer.getUniqueId();

        assertThat(uniqueId).isEqualTo(playerUniqueId);
    }

    @Test
    void canReceiveRecoilReturnsFalseWhenPlayerIsNotOnline() {
        when(player.isOnline()).thenReturn(false);

        boolean receiveRecoil = gamePlayer.canReceiveRecoil();

        assertThat(receiveRecoil).isFalse();
    }

    @Test
    void canReceiveRecoilReturnsFalseWhenPlayerIsDead() {
        when(player.isOnline()).thenReturn(true);
        when(player.isDead()).thenReturn(true);

        boolean receiveRecoil = gamePlayer.canReceiveRecoil();

        assertThat(receiveRecoil).isFalse();
    }

    @Test
    void canReceiveRecoilReturnsTrueWhenPlayerIsActive() {
        when(player.isOnline()).thenReturn(true);
        when(player.isDead()).thenReturn(false);

        boolean receiveRecoil = gamePlayer.canReceiveRecoil();

        assertThat(receiveRecoil).isTrue();
    }

    @Test
    void damageAppliesNoDamageWhenPlayerIsDead() {
        when(player.isDead()).thenReturn(true);

        Damage damage = new Damage(10.0, DamageType.BULLET_DAMAGE);

        double damageDealt = gamePlayer.damage(damage);

        assertThat(damageDealt).isEqualTo(0.0);

        verify(player, never()).setHealth(anyDouble());
    }

    @Test
    void damageAppliesNoDamageWhenPlayerHasNoHealth() {
        when(player.getHealth()).thenReturn(0.0);
        when(player.isDead()).thenReturn(false);

        Damage damage = new Damage(10.0, DamageType.BULLET_DAMAGE);

        double damageDealt = gamePlayer.damage(damage);

        assertThat(damageDealt).isEqualTo(0.0);

        verify(player, never()).setHealth(anyDouble());
    }

    @NotNull
    static Stream<Arguments> damageScenarios() {
        return Stream.of(
                arguments(10.0, 10.0, 20.0, 18.0),
                arguments(1000.0, 1000.0, 20.0, 0.0)
        );
    }

    @ParameterizedTest
    @MethodSource("damageScenarios")
    void damageReturnsDealtDamageAndLowersHealth(double damageAmount, double expectedDamageDealt, double health, double expectedHealth) {
        when(player.getHealth()).thenReturn(health);
        when(player.isDead()).thenReturn(false);

        Damage damage = new Damage(damageAmount, DamageType.BULLET_DAMAGE);

        double damageDealt = gamePlayer.damage(damage);

        assertThat(damageDealt).isEqualTo(expectedDamageDealt);

        verify(player).setHealth(expectedHealth);
    }

    @Test
    void getAttackStrengthReturnsPlayerAttackCooldown() {
        float attackCooldown = 0.5F;

        when(player.getAttackCooldown()).thenReturn(attackCooldown);

        float attackStrength = gamePlayer.getAttackStrength();

        assertThat(attackStrength).isEqualTo(attackCooldown);
    }

    @Test
    void getHeldItemReturnsItemInMainHand() {
        ItemStack itemStack = new ItemStack(Material.STONE);

        PlayerInventory inventory = mock(PlayerInventory.class);
        when(inventory.getItemInMainHand()).thenReturn(itemStack);

        when(player.getInventory()).thenReturn(inventory);

        ItemStack heldItem = gamePlayer.getHeldItem();

        assertThat(heldItem).isEqualTo(itemStack);
    }

    @Test
    void setHeldItemSetsItemInMainHand() {
        ItemStack itemStack = new ItemStack(Material.STONE);

        PlayerInventory inventory = mock(PlayerInventory.class);
        when(player.getInventory()).thenReturn(inventory);

        gamePlayer.setHeldItem(itemStack);

        verify(inventory).setItemInMainHand(itemStack);
    }

    @Test
    void getDeployLocationReturnsPlayerEyeLocation() {
        Location eyeLocation = new Location(null, 1, 1, 1);
        when(player.getEyeLocation()).thenReturn(eyeLocation);

        Location deployLocation = gamePlayer.getDeployLocation();

        assertThat(deployLocation).isEqualTo(eyeLocation);
    }

    @Test
    void getHitboxReturnsHitboxInstanceCreatedFromHitboxProvider() {
        Hitbox hitbox = mock(Hitbox.class);

        when(hitboxProvider.provideHitbox(player)).thenReturn(hitbox);

        Hitbox result = gamePlayer.getHitbox();

        assertThat(result).isEqualTo(hitbox);
    }

    @Test
    void getItemSlotReturnsOptionalWithSlotNumberOfMatchingItemStack() {
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);
        ItemStack[] contents = new ItemStack[] { null, itemStack, null };

        Matchable item = mock(Matchable.class);
        when(item.isMatching(itemStack)).thenReturn(true);

        when(player.getInventory().getContents()).thenReturn(contents);

        Optional<Integer> itemSlot = gamePlayer.getItemSlot(item);

        assertThat(itemSlot).hasValue(1);
    }

    @Test
    void getItemSlotReturnsEmptyOptionalWhenNoneOfTheInventoryContentsMatchWithGivenItemStack() {
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);
        ItemStack[] contents = new ItemStack[] { null, itemStack, null };

        Matchable item = mock(Matchable.class);
        when(item.isMatching(itemStack)).thenReturn(false);

        when(player.getInventory().getContents()).thenReturn(contents);

        Optional<Integer> itemSlot = gamePlayer.getItemSlot(item);

        assertThat(itemSlot).isEmpty();
    }

    @Test
    void getLastTwoTargetBlocksReturnsPlayerTargetBlocks() {
        int maxDistance = 3;
        List<Block> targetBlocks = List.of(mock(Block.class), mock(Block.class));

        when(player.getLastTwoTargetBlocks(null, maxDistance)).thenReturn(targetBlocks);

        List<Block> result = gamePlayer.getLastTwoTargetBlocks(maxDistance);

        assertThat(result).isEqualTo(targetBlocks);
    }

    @Test
    void getRelativeAccuracyReturnsNormalValueWhenStandingStill() {
        float accuracy = gamePlayer.getRelativeAccuracy();

        assertThat(accuracy).isEqualTo(1.0f);
    }

    @Test
    void getRelativeAccuracyReturnsMoreAccurateleFireWhenSneaking() {
        when(player.isSneaking()).thenReturn(true);

        float accuracy = gamePlayer.getRelativeAccuracy();

        assertThat(accuracy).isEqualTo(2.0f);
    }

    @Test
    void getRelativeAccuracyReturnsLessAccurateleFireWhenSprinting() {
        when(player.isSprinting()).thenReturn(true);

        float accuracy = gamePlayer.getRelativeAccuracy();

        assertThat(accuracy).isEqualTo(0.5f);
    }

    @Test
    void applyReloadingStateSetsFoodLevel() {
        gamePlayer.applyReloadingState();

        verify(player).setFoodLevel(6);
    }

    @Test
    void resetReloadingStateResetsFoodLevel() {
        when(player.getFoodLevel()).thenReturn(10);

        gamePlayer.applyReloadingState();
        gamePlayer.resetReloadingState();

        verify(player).setFoodLevel(10);
    }

    @Test
    void applyViewMagnificationCallsInternalSetWalkSpeedFunction() {
        float magnification = -0.1f;

        gamePlayer.applyViewMagnification(magnification);

        verify(internals).setWalkSpeed(player, magnification);
    }

    @Test
    void launchProjectileMakesPlayerLaunchProjectileWithGivenArguments() {
        Class<? extends Projectile> projectileClass = Arrow.class;
        Vector velocity = new Vector(1.0, 1.0, 1.0);

        gamePlayer.launchProjectile(projectileClass, velocity);

        verify(player).launchProjectile(projectileClass, velocity);
    }

    @Test
    void modifyCameraRotationCallsInternalPlayerRotationFunction() {
        gamePlayer.modifyCameraRotation(1.0f, 1.0f);

        verify(internals).setPlayerRotation(player, 1.0f, 1.0f);
    }

    @Test
    void removeItemStackFromInventory() {
        ItemStack itemStack = new ItemStack(Material.STONE);

        PlayerInventory inventory = mock(PlayerInventory.class);
        when(player.getInventory()).thenReturn(inventory);

        gamePlayer.removeItem(itemStack);

        verify(inventory).removeItem(itemStack);
    }

    @Test
    void getAudioPlayLocationReturnsPlayersLocation() {
        Location location = new Location(null, 1.0, 1.0, 1.0);

        when(player.getLocation()).thenReturn(location);

        Location result = gamePlayer.getAudioPlayLocation();

        assertThat(result).isEqualTo(location);
    }

    @Test
    void getShootingDirectionReturnSlightlyAlteredEyeLocation() {
        Location eyeLocation = new Location(null, 1.0, 1.0, 1.0);

        when(player.getEyeLocation()).thenReturn(eyeLocation.clone());

        Location result = gamePlayer.getShootingDirection();

        assertThat(result).isNotEqualTo(eyeLocation);
        assertThat(result.getX()).isEqualTo(eyeLocation.getX());
        assertThat(result.getY()).isNotEqualTo(eyeLocation.getY());
        assertThat(result.getZ()).isEqualTo(eyeLocation.getZ());
    }
}
