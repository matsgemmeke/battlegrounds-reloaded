package nl.matsgemmeke.battlegrounds.game.openmode.component;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.entity.GameMob;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.entity.PotionEffectReceiver;
import nl.matsgemmeke.battlegrounds.entity.hitbox.Hitbox;
import nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxResolver;
import nl.matsgemmeke.battlegrounds.game.component.TargetQuery;
import nl.matsgemmeke.battlegrounds.game.component.TargetType;
import nl.matsgemmeke.battlegrounds.game.component.deploy.DeploymentInfoProvider;
import nl.matsgemmeke.battlegrounds.game.component.entity.MobRegistry;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.game.damage.Target;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentObject;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OpenModeTargetFinderTest {

    private static final double RANGE = 0.1;
    private static final UUID ENTITY_UNIQUE_ID = UUID.randomUUID();

    @Mock
    private DeploymentInfoProvider deploymentInfoProvider;
    @Mock
    private HitboxResolver hitboxResolver;
    @Mock
    private MobRegistry mobRegistry;
    @Mock
    private PlayerRegistry playerRegistry;
    @InjectMocks
    private OpenModeTargetFinder targetFinder;

    @Test
    void containsTargetsReturnsFalseWhenSearchUsingQueryDoesNotReturnTargets() {
        World world = mock(World.class);
        Location findingLocation = new Location(world, 1.0, 1.0, 1.0);
        TargetQuery targetQuery = new TargetQuery().forLocation(findingLocation);

        boolean containsTargets = targetFinder.containsTargets(targetQuery);

        assertThat(containsTargets).isFalse();
    }

    @Test
    void findDeploymentObjectsReturnsDeploymentObjectsLocatedWithinGivenLocationAndRange() {
        UUID entityId = UUID.randomUUID();
        World world = mock(World.class);
        Location location = new Location(world, 1, 1, 1);
        double range = 0.5;

        Location locationInsideRange = new Location(world, 1, 1, 1);
        Location locationOutsideRange = new Location(world, 100, 100, 100);

        DeploymentObject objectInsideRange = mock(DeploymentObject.class);
        when(objectInsideRange.getLocation()).thenReturn(locationInsideRange);

        DeploymentObject objectOutsideRange = mock(DeploymentObject.class);
        when(objectOutsideRange.getLocation()).thenReturn(locationOutsideRange);

        when(deploymentInfoProvider.getAllDeploymentObjects()).thenReturn(List.of(objectInsideRange, objectOutsideRange));

        List<DeploymentObject> deploymentObjects = targetFinder.findDeploymentObjects(entityId, location, range);

        assertEquals(1, deploymentObjects.size());
        assertTrue(deploymentObjects.contains(objectInsideRange));
        assertFalse(deploymentObjects.contains(objectOutsideRange));
    }

    @Test
    void findEnemyTargetsReturnsEmptyListWhenGivenLocationWorldIsNull() {
        UUID entityId = UUID.randomUUID();
        Location location = new Location(null, 1.0, 1.0, 1.0);

        Collection<GameEntity> targets = targetFinder.findEnemyTargets(entityId, location, 0.1);

        assertThat(targets).isEmpty();
    }

    @Test
    void findEnemyTargetsReturnsNearbyTargetsButWithoutEntitiesContainingTheGivenEntityId() {
        UUID entityUniqueId = UUID.randomUUID();
        UUID targetUniqueId = UUID.randomUUID();
        double range = 0.1;

        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(entityUniqueId);

        Player targetPlayer = mock(Player.class);
        when(targetPlayer.getUniqueId()).thenReturn(targetUniqueId);

        GamePlayer gamePlayer = mock(GamePlayer.class);
        when(gamePlayer.getUniqueId()).thenReturn(entityUniqueId);

        GamePlayer target = mock(GamePlayer.class);
        when(target.getUniqueId()).thenReturn(targetUniqueId);

        World world = mock(World.class);
        Location location = new Location(world, 1.0, 1.0, 1.0);

        when(world.getNearbyEntities(location, range, range, range)).thenReturn(List.of(player, targetPlayer));

        when(playerRegistry.findByUniqueId(entityUniqueId)).thenReturn(Optional.of(gamePlayer));
        when(playerRegistry.findByUniqueId(targetUniqueId)).thenReturn(Optional.of(target));

        Collection<GameEntity> targets = targetFinder.findEnemyTargets(entityUniqueId, location, range);

        assertEquals(1, targets.size());
        assertTrue(targets.contains(target));
        assertFalse(targets.contains(gamePlayer));
    }

    @Test
    void findPotionEffectReceiversReturnsListOfPotionEffectReceiversInsideGivenRange() {
        World world = mock(World.class);
        Location givenLocation = new Location(world, 1, 1, 1);
        Location playerLocation = new Location(world, 1.05, 1.05, 1.05);

        GamePlayer gamePlayer = mock(GamePlayer.class);
        when(gamePlayer.getLocation()).thenReturn(playerLocation);

        LivingEntity entity = mock(LivingEntity.class);
        when(entity.getType()).thenReturn(EntityType.UNKNOWN);

        Hitbox gameMobHitbox = mock(Hitbox.class);
        when(gameMobHitbox.intersects(givenLocation)).thenReturn(true);

        GameMob gameMob = mock(GameMob.class);
        when(gameMob.getHitbox()).thenReturn(gameMobHitbox);

        when(playerRegistry.getAll()).thenReturn(List.of(gamePlayer));
        when(mobRegistry.register(entity)).thenReturn(gameMob);
        when(world.getNearbyEntities(givenLocation, RANGE, RANGE, RANGE)).thenReturn(List.of(entity));

        List<PotionEffectReceiver> targets = targetFinder.findPotionEffectReceivers(givenLocation, RANGE);

        assertThat(targets).containsExactly(gamePlayer, gameMob);
    }

    @Test
    void findTargetsThrowsIllegalArgumentExceptionWhenQueryHasNoLocation() {
        TargetQuery targetQuery = new TargetQuery();

        assertThatThrownBy(() -> targetFinder.findTargets(targetQuery))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No location provided");
    }

    @Test
    void findTargetsReturnsEmptyListWhenNoRangesAreDefined() {
        World world = mock(World.class);
        Location findingLocation = new Location(world, 1.0, 1.0, 1.0);
        TargetQuery targetQuery = new TargetQuery().forLocation(findingLocation);

        List<Target> targets = targetFinder.findTargets(targetQuery);

        assertThat(targets).isEmpty();
    }

    @Test
    void findTargetsReturnsEntityTargetsWhoseDistanceIsLessThanRange() {
        World world = mock(World.class);
        Location findingLocation = new Location(world, 1.0, 1.0, 1.0);
        Location playerLocation = new Location(world, 2.0, 1.0, 1.0);

        GamePlayer gamePlayer = mock(GamePlayer.class);
        when(gamePlayer.getLocation()).thenReturn(playerLocation);

        Entity entity = mock(Entity.class);
        when(entity.getType()).thenReturn(EntityType.ZOMBIE);

        when(playerRegistry.getAll()).thenReturn(List.of(gamePlayer));
        when(world.getNearbyEntities(eq(findingLocation), anyDouble(), anyDouble(), anyDouble())).thenReturn(List.of(entity));

        TargetQuery targetQuery = new TargetQuery()
                .forLocation(findingLocation)
                .withRange(TargetType.ENTITY, Double.MAX_VALUE);

        List<Target> targets = targetFinder.findTargets(targetQuery);

        assertThat(targets).containsExactly(gamePlayer);
    }

    @Test
    void findTargetsReturnsListWithoutEntitiesWithMatchingIdsWhenOnlyFindingEnemies() {
        World world = mock(World.class);
        Location findingLocation = new Location(world, 1.0, 1.0, 1.0);
        UUID playerUniqueId = UUID.randomUUID();

        GamePlayer gamePlayer = mock(GamePlayer.class);
        when(gamePlayer.getUniqueId()).thenReturn(playerUniqueId);

        when(playerRegistry.getAll()).thenReturn(List.of(gamePlayer));

        TargetQuery targetQuery = new TargetQuery()
                .forEntity(playerUniqueId)
                .forLocation(findingLocation)
                .withRange(TargetType.ENTITY, Double.MAX_VALUE)
                .enemiesOnly(true);

        List<Target> targets = targetFinder.findTargets(targetQuery);

        assertThat(targets).isEmpty();
    }

    @Test
    void findTargetsReturnsDeploymentObjectTargetsWhoseDistanceIsLessThanRange() {
        World world = mock(World.class);
        Location findingLocation = new Location(world, 1.0, 1.0, 1.0);
        Location deploymentObjectLocation = new Location(world, 2.0, 1.0, 1.0);

        DeploymentObject deploymentObject = mock(DeploymentObject.class);
        when(deploymentObject.getLocation()).thenReturn(deploymentObjectLocation);

        when(deploymentInfoProvider.getAllDeploymentObjects()).thenReturn(List.of(deploymentObject));

        TargetQuery targetQuery = new TargetQuery()
                .forLocation(findingLocation)
                .withRange(TargetType.DEPLOYMENT_OBJECT, Double.MAX_VALUE);

        List<Target> targets = targetFinder.findTargets(targetQuery);

        assertThat(targets).containsExactly(deploymentObject);
    }

    @Test
    void findTargetsReturnsListWithoutDeploymentObjectsWithMatchingIdsWhenOnlyFindingEnemies() {
        World world = mock(World.class);
        Location findingLocation = new Location(world, 1.0, 1.0, 1.0);
        UUID deploymentObjectUniqueId = UUID.randomUUID();

        DeploymentObject deploymentObject = mock(DeploymentObject.class);
        when(deploymentObject.getUniqueId()).thenReturn(deploymentObjectUniqueId);

        when(deploymentInfoProvider.getAllDeploymentObjects()).thenReturn(List.of(deploymentObject));

        TargetQuery targetQuery = new TargetQuery()
                .forEntity(deploymentObjectUniqueId)
                .forLocation(findingLocation)
                .withRange(TargetType.DEPLOYMENT_OBJECT, Double.MAX_VALUE)
                .enemiesOnly(true);

        List<Target> targets = targetFinder.findTargets(targetQuery);

        assertThat(targets).isEmpty();
    }

    @Test
    void shouldReturnNoTargetsIfGivenLocationHasNoWorld() {
        UUID entityId = UUID.randomUUID();
        Location location = new Location(null, 1.0, 1.0, 1.0);

        Collection<GameEntity> targets = targetFinder.findTargets(entityId, location, 0.1);

        assertTrue(targets.isEmpty());
    }

    @Test
    void findTargetsReturnsListOfNearbyPlayers() {
        UUID entityUniqueId = UUID.randomUUID();
        UUID targetUniqueId = UUID.randomUUID();
        World world = mock(World.class);

        Location location = new Location(world, 1, 1, 1);
        double range = 0.1;

        Entity targetEntity = mock(Entity.class);
        when(targetEntity.getUniqueId()).thenReturn(targetUniqueId);

        GamePlayer target = mock(GamePlayer.class);
        when(target.isPassive()).thenReturn(false);

        when(playerRegistry.findByUniqueId(targetUniqueId)).thenReturn(Optional.of(target));
        when(world.getNearbyEntities(location, range, range, range)).thenReturn(List.of(targetEntity));

        List<GameEntity> targets = targetFinder.findTargets(entityUniqueId, location, range);

        assertEquals(1, targets.size());
        assertEquals(target, targets.get(0));
    }

    @Test
    void findTargetsReturnsEmptyListIfFoundPlayersArePassive() {
        UUID entityUniqueId = UUID.randomUUID();
        UUID targetUniqueId = UUID.randomUUID();
        World world = mock(World.class);

        Location location = new Location(world, 1, 1, 1);
        double range = 0.1;

        Entity targetEntity = mock(Entity.class);
        when(targetEntity.getUniqueId()).thenReturn(targetUniqueId);

        GamePlayer target = mock(GamePlayer.class);
        when(target.isPassive()).thenReturn(true);

        when(playerRegistry.findByUniqueId(targetUniqueId)).thenReturn(Optional.of(target));
        when(world.getNearbyEntities(location, range, range, range)).thenReturn(List.of(targetEntity));

        List<GameEntity> targets = targetFinder.findTargets(entityUniqueId, location, range);

        assertTrue(targets.isEmpty());
    }

    @Test
    void findTargetsReturnsEntitiesThatAreNotPlayers() {
        World world = mock(World.class);
        Location location = new Location(world, 1, 1, 1);
        double range = 0.1;

        Entity entity = mock(Zombie.class);
        when(entity.getType()).thenReturn(EntityType.ZOMBIE);
        when(entity.getUniqueId()).thenReturn(ENTITY_UNIQUE_ID);

        when(world.getNearbyEntities(location, range, range, range)).thenReturn(List.of(entity));

        List<GameEntity> targets = targetFinder.findTargets(ENTITY_UNIQUE_ID, location, range);

        assertThat(targets).satisfiesExactly(target -> {
            assertThat(target.getUniqueId()).isEqualTo(ENTITY_UNIQUE_ID);
        });
    }
}
