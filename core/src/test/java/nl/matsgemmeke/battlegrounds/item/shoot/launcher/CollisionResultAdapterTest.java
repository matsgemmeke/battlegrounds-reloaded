package nl.matsgemmeke.battlegrounds.item.shoot.launcher;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.game.component.entity.GameEntityFinder;
import nl.matsgemmeke.battlegrounds.game.component.projectile.ProjectileHitResult;
import nl.matsgemmeke.battlegrounds.game.damage.DamageTarget;
import nl.matsgemmeke.battlegrounds.item.effect.CollisionResult;
import nl.matsgemmeke.battlegrounds.item.trigger.result.DamageTargetTriggerResult;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CollisionResultAdapterTest {

    private static final UUID HIT_ENTITY_UNIQUE_ID = UUID.randomUUID();

    @Mock
    private GameEntityFinder gameEntityFinder;
    @InjectMocks
    private CollisionResultAdapter collisionResultAdapter;

    @MethodSource
    static List<Arguments> rayTraceResultsEntity() {
        return List.of(
                arguments((RayTraceResult) null),
                arguments(new RayTraceResult(new Vector()))
        );
    }

    @ParameterizedTest
    @MethodSource("rayTraceResultsEntity")
    @DisplayName("adapt returns CollisionResult with the entity's center location as hit location when ray trace result hit entity does not equal given hit entity")
    void adapt_rayTraceDoesNotHitHitEntity(RayTraceResult rayTraceResult) {
        BoundingBox boundingBox = new BoundingBox(1, 1, 1, 1, 1, 1);
        GameEntity hitTarget = mock(GameEntity.class);
        Location hitEntityLocation = new Location(null, 2, 2, 2);
        Location projectileLocation = new Location(null, 1, 1, 1);
        Vector projectileVelocity = new Vector(1, 0, 0);

        World world = mock(World.class);
        when(world.rayTraceEntities(projectileLocation, projectileVelocity, 2.0)).thenReturn(rayTraceResult);

        Projectile projectile = mock(Projectile.class);
        when(projectile.getLocation()).thenReturn(projectileLocation);
        when(projectile.getVelocity()).thenReturn(projectileVelocity);
        when(projectile.getWorld()).thenReturn(world);

        Entity hitEntity = mock(Entity.class);
        when(hitEntity.getBoundingBox()).thenReturn(boundingBox);
        when(hitEntity.getLocation()).thenReturn(hitEntityLocation);
        when(hitEntity.getUniqueId()).thenReturn(HIT_ENTITY_UNIQUE_ID);

        ProjectileHitResult projectileHitResult = new ProjectileHitResult(null, hitEntity);

        when(gameEntityFinder.findGameEntityByUniqueId(HIT_ENTITY_UNIQUE_ID)).thenReturn(Optional.of(hitTarget));

        CollisionResult collisionResult = collisionResultAdapter.adapt(projectileHitResult, projectile);

        assertThat(collisionResult.getHitBlock()).isEmpty();
        assertThat(collisionResult.getHitTarget()).hasValue(hitTarget);
        assertThat(collisionResult.getHitLocation()).hasValue(hitEntityLocation);
    }

    @Test
    @DisplayName("adapt returns CollisionResult with the ray trace hit location when ray trace result hit entity equals given hit entity")
    void adapt_rayTraceHitsHitEntity() {
        GameEntity hitTarget = mock(GameEntity.class);
        Location projectileLocation = new Location(null, 1, 1, 1);
        Vector projectileVelocity = new Vector(1, 0, 0);
        Vector hitPosition = new Vector(3, 3, 3);

        Entity hitEntity = mock(Entity.class);
        when(hitEntity.getUniqueId()).thenReturn(HIT_ENTITY_UNIQUE_ID);

        RayTraceResult rayTraceResult = new RayTraceResult(hitPosition, hitEntity);

        World world = mock(World.class);
        when(world.rayTraceEntities(projectileLocation, projectileVelocity, 2.0)).thenReturn(rayTraceResult);

        Projectile projectile = mock(Projectile.class);
        when(projectile.getLocation()).thenReturn(projectileLocation);
        when(projectile.getVelocity()).thenReturn(projectileVelocity);
        when(projectile.getWorld()).thenReturn(world);

        ProjectileHitResult projectileHitResult = new ProjectileHitResult(null, hitEntity);

        when(gameEntityFinder.findGameEntityByUniqueId(HIT_ENTITY_UNIQUE_ID)).thenReturn(Optional.of(hitTarget));

        CollisionResult collisionResult = collisionResultAdapter.adapt(projectileHitResult, projectile);

        assertThat(collisionResult.getHitBlock()).isEmpty();
        assertThat(collisionResult.getHitTarget()).hasValue(hitTarget);
        assertThat(collisionResult.getHitLocation()).hasValue(new Location(world, 3.0, 3.0, 3.0));
    }

    @MethodSource
    static List<Arguments> rayTraceResultsBlock() {
        return List.of(
                arguments((RayTraceResult) null),
                arguments(new RayTraceResult(new Vector()))
        );
    }

    @ParameterizedTest
    @MethodSource("rayTraceResultsBlock")
    @DisplayName("adapt returns CollisionResult with the block's center location as hit location when ray trace result hit block does not equal given hit block")
    void adapt_rayTraceDoesNotHitHitBlock(RayTraceResult rayTraceResult) {
        Location hitBlockLocation = new Location(null, 2, 2, 2);
        Location projectileLocation = new Location(null, 1, 1, 1);
        Vector projectileVelocity = new Vector(1, 0, 0);

        World world = mock(World.class);
        when(world.rayTraceBlocks(projectileLocation, projectileVelocity, 2.0, FluidCollisionMode.NEVER)).thenReturn(rayTraceResult);

        Projectile projectile = mock(Projectile.class);
        when(projectile.getLocation()).thenReturn(projectileLocation);
        when(projectile.getVelocity()).thenReturn(projectileVelocity);
        when(projectile.getWorld()).thenReturn(world);

        Block hitBlock = mock(Block.class);
        when(hitBlock.getLocation()).thenReturn(hitBlockLocation);

        ProjectileHitResult projectileHitResult = new ProjectileHitResult(hitBlock, null);

        CollisionResult collisionResult = collisionResultAdapter.adapt(projectileHitResult, projectile);

        assertThat(collisionResult.getHitBlock()).hasValue(hitBlock);
        assertThat(collisionResult.getHitTarget()).isEmpty();
        assertThat(collisionResult.getHitLocation()).hasValue(new Location(null, 2.5, 2.5, 2.5));
    }

    @Test
    @DisplayName("adapt returns CollisionResult with the ray trace hit location when ray trace result hit block equals given hit block")
    void adapt_rayTraceHitsHitBlock() {
        Block hitBlock = mock(Block.class);
        Location projectileLocation = new Location(null, 1, 1, 1);
        Vector projectileVelocity = new Vector(1, 0, 0);
        Vector hitPosition = new Vector(3, 3, 3);
        RayTraceResult rayTraceResult = new RayTraceResult(hitPosition, hitBlock, BlockFace.EAST);

        World world = mock(World.class);
        when(world.rayTraceBlocks(projectileLocation, projectileVelocity, 2.0, FluidCollisionMode.NEVER)).thenReturn(rayTraceResult);

        Projectile projectile = mock(Projectile.class);
        when(projectile.getLocation()).thenReturn(projectileLocation);
        when(projectile.getVelocity()).thenReturn(projectileVelocity);
        when(projectile.getWorld()).thenReturn(world);

        ProjectileHitResult projectileHitResult = new ProjectileHitResult(hitBlock, null);

        CollisionResult collisionResult = collisionResultAdapter.adapt(projectileHitResult, projectile);

        assertThat(collisionResult.getHitBlock()).hasValue(hitBlock);
        assertThat(collisionResult.getHitTarget()).isEmpty();
        assertThat(collisionResult.getHitLocation()).hasValue(new Location(world, 3.0, 3.0, 3.0));
    }

    @Test
    @DisplayName("adapt throws IllegalStateException when given TriggerResult is not registered")
    void adapt_withUnregisteredTriggerResult() {
        UnregisteredTriggerResult triggerResult = new UnregisteredTriggerResult();

        assertThatThrownBy(() -> collisionResultAdapter.adapt(triggerResult))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("No TriggerResultAdapter registered for UnregisteredTriggerResult");
    }

    @Test
    @DisplayName("adapt returns CollisionResult converted from given TriggerResult")
    void adapt_withRegisteredTriggerResult() {
        DamageTarget hitTarget = mock(DamageTarget.class);
        Location hitLocation = new Location(null, 1, 1, 1);
        DamageTargetTriggerResult triggerResult = new DamageTargetTriggerResult(hitTarget, hitLocation);

        CollisionResult collisionResult = collisionResultAdapter.adapt(triggerResult);

        assertThat(collisionResult.getHitBlock()).isEmpty();
        assertThat(collisionResult.getHitTarget()).hasValue(hitTarget);
        assertThat(collisionResult.getHitLocation()).hasValue(hitLocation);
    }
}
