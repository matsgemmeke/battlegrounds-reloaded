package nl.matsgemmeke.battlegrounds.game.openmode.component;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.entity.hitbox.HitboxResolver;
import nl.matsgemmeke.battlegrounds.game.component.TargetQuery;
import nl.matsgemmeke.battlegrounds.game.component.TargetType;
import nl.matsgemmeke.battlegrounds.game.component.deploy.DeploymentInfoProvider;
import nl.matsgemmeke.battlegrounds.game.component.entity.LivingEntityRegistry;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.game.damage.Target;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentObject;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

public class OpenModeTargetFinderTest {

    private DeploymentInfoProvider deploymentInfoProvider;
    private HitboxResolver hitboxResolver;
    private LivingEntityRegistry livingEntityRegistry;
    private PlayerRegistry playerRegistry;

    @BeforeEach
    public void setUp() {
        deploymentInfoProvider = mock(DeploymentInfoProvider.class);
        hitboxResolver = mock(HitboxResolver.class);
        livingEntityRegistry = mock(LivingEntityRegistry.class);
        playerRegistry = mock(PlayerRegistry.class);
    }

    @Test
    public void containsTargetsReturnsFalseWhenSearchUsingQueryDoesNotReturnTargets() {
        World world = mock(World.class);
        Location findingLocation = new Location(world, 1.0, 1.0, 1.0);
        TargetQuery targetQuery = new TargetQuery().forLocation(findingLocation);

        OpenModeTargetFinder targetFinder = new OpenModeTargetFinder(deploymentInfoProvider, hitboxResolver, livingEntityRegistry, playerRegistry);
        boolean containsTargets = targetFinder.containsTargets(targetQuery);

        assertThat(containsTargets).isFalse();
    }

    @Test
    public void findDeploymentObjectsReturnsDeploymentObjectsLocatedWithinGivenLocationAndRange() {
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

        OpenModeTargetFinder targetFinder = new OpenModeTargetFinder(deploymentInfoProvider, hitboxResolver, livingEntityRegistry, playerRegistry);
        List<DeploymentObject> deploymentObjects = targetFinder.findDeploymentObjects(entityId, location, range);

        assertEquals(1, deploymentObjects.size());
        assertTrue(deploymentObjects.contains(objectInsideRange));
        assertFalse(deploymentObjects.contains(objectOutsideRange));
    }

    @Test
    public void findEnemyTargetsReturnsEmptyListWhenGivenLocationWorldIsNull() {
        UUID entityId = UUID.randomUUID();
        Location location = new Location(null, 1.0, 1.0, 1.0);

        OpenModeTargetFinder targetFinder = new OpenModeTargetFinder(deploymentInfoProvider, hitboxResolver, livingEntityRegistry, playerRegistry);
        Collection<GameEntity> targets = targetFinder.findEnemyTargets(entityId, location, 0.1);

        assertThat(targets).isEmpty();
    }

    @Test
    public void findEnemyTargetsReturnsNearbyTargetsButWithoutEntitiesContainingTheGivenEntityId() {
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

        OpenModeTargetFinder targetFinder = new OpenModeTargetFinder(deploymentInfoProvider, hitboxResolver, livingEntityRegistry, playerRegistry);
        Collection<GameEntity> targets = targetFinder.findEnemyTargets(entityUniqueId, location, range);

        assertEquals(1, targets.size());
        assertTrue(targets.contains(target));
        assertFalse(targets.contains(gamePlayer));
    }

    @Test
    public void findTargetsThrowsIllegalArgumentExceptionWhenQueryHasNoLocation() {
        TargetQuery targetQuery = new TargetQuery();

        OpenModeTargetFinder targetFinder = new OpenModeTargetFinder(deploymentInfoProvider, hitboxResolver, livingEntityRegistry, playerRegistry);

        assertThatThrownBy(() -> targetFinder.findTargets(targetQuery))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No location provided");
    }

    @Test
    public void findTargetsReturnsEmptyListWhenNoRangesAreDefined() {
        World world = mock(World.class);
        Location findingLocation = new Location(world, 1.0, 1.0, 1.0);
        TargetQuery targetQuery = new TargetQuery().forLocation(findingLocation);

        OpenModeTargetFinder targetFinder = new OpenModeTargetFinder(deploymentInfoProvider, hitboxResolver, livingEntityRegistry, playerRegistry);
        List<Target> targets = targetFinder.findTargets(targetQuery);

        assertThat(targets).isEmpty();
    }

    @Test
    public void findTargetsReturnsEntityTargetsWhoseDistanceIsLessThanRange() {
        World world = mock(World.class);
        Location findingLocation = new Location(world, 1.0, 1.0, 1.0);
        Location playerLocation = new Location(world, 2.0, 1.0, 1.0);
        Location entityLocation = new Location(world, 2.0, 1.0, 1.0);
        UUID playerUniqueId = UUID.randomUUID();
        UUID entityUniqueId = UUID.randomUUID();

        GamePlayer gamePlayer = mock(GamePlayer.class);
        when(gamePlayer.getLocation()).thenReturn(playerLocation);
        when(gamePlayer.getUniqueId()).thenReturn(playerUniqueId);

        Entity entity = mock(Entity.class);
        when(entity.getLocation()).thenReturn(entityLocation);
        when(entity.getType()).thenReturn(EntityType.ZOMBIE);
        when(entity.getUniqueId()).thenReturn(entityUniqueId);

        when(playerRegistry.getAll()).thenReturn(List.of(gamePlayer));
        when(world.getNearbyEntities(eq(findingLocation), anyDouble(), anyDouble(), anyDouble())).thenReturn(List.of(entity));

        TargetQuery targetQuery = new TargetQuery()
                .forLocation(findingLocation)
                .withRange(TargetType.ENTITY, Double.MAX_VALUE);

        OpenModeTargetFinder targetFinder = new OpenModeTargetFinder(deploymentInfoProvider, hitboxResolver, livingEntityRegistry, playerRegistry);
        List<Target> targets = targetFinder.findTargets(targetQuery);

        assertThat(targets).containsExactly(gamePlayer);
    }

    @Test
    public void findTargetsReturnsListWithoutEntitiesWithMatchingIdsWhenOnlyFindingEnemies() {
        World world = mock(World.class);
        Location findingLocation = new Location(world, 1.0, 1.0, 1.0);
        Location playerLocation = new Location(world, 2.0, 1.0, 1.0);
        UUID playerUniqueId = UUID.randomUUID();

        GamePlayer gamePlayer = mock(GamePlayer.class);
        when(gamePlayer.getLocation()).thenReturn(playerLocation);
        when(gamePlayer.getUniqueId()).thenReturn(playerUniqueId);

        when(playerRegistry.getAll()).thenReturn(List.of(gamePlayer));

        TargetQuery targetQuery = new TargetQuery()
                .forEntity(playerUniqueId)
                .forLocation(findingLocation)
                .withRange(TargetType.ENTITY, Double.MAX_VALUE)
                .enemiesOnly(true);

        OpenModeTargetFinder targetFinder = new OpenModeTargetFinder(deploymentInfoProvider, hitboxResolver, livingEntityRegistry, playerRegistry);
        List<Target> targets = targetFinder.findTargets(targetQuery);

        assertThat(targets).isEmpty();
    }

    @Test
    public void findTargetsReturnsDeploymentObjectTargetsWhoseDistanceIsLessThanRange() {
        World world = mock(World.class);
        Location findingLocation = new Location(world, 1.0, 1.0, 1.0);
        Location deploymentObjectLocation = new Location(world, 2.0, 1.0, 1.0);
        UUID deploymentObjectUniqueId = UUID.randomUUID();

        DeploymentObject deploymentObject = mock(DeploymentObject.class);
        when(deploymentObject.getLocation()).thenReturn(deploymentObjectLocation);
        when(deploymentObject.getUniqueId()).thenReturn(deploymentObjectUniqueId);

        when(deploymentInfoProvider.getAllDeploymentObjects()).thenReturn(List.of(deploymentObject));

        TargetQuery targetQuery = new TargetQuery()
                .forLocation(findingLocation)
                .withRange(TargetType.DEPLOYMENT_OBJECT, Double.MAX_VALUE);

        OpenModeTargetFinder targetFinder = new OpenModeTargetFinder(deploymentInfoProvider, hitboxResolver, livingEntityRegistry, playerRegistry);
        List<Target> targets = targetFinder.findTargets(targetQuery);

        assertThat(targets).containsExactly(deploymentObject);
    }

    @Test
    public void findTargetsReturnsListWithoutDeploymentObjectsWithMatchingIdsWhenOnlyFindingEnemies() {
        World world = mock(World.class);
        Location findingLocation = new Location(world, 1.0, 1.0, 1.0);
        Location deploymentObjectLocation = new Location(world, 2.0, 1.0, 1.0);
        UUID deploymentObjectUniqueId = UUID.randomUUID();

        DeploymentObject deploymentObject = mock(DeploymentObject.class);
        when(deploymentObject.getLocation()).thenReturn(deploymentObjectLocation);
        when(deploymentObject.getUniqueId()).thenReturn(deploymentObjectUniqueId);

        when(deploymentInfoProvider.getAllDeploymentObjects()).thenReturn(List.of(deploymentObject));

        TargetQuery targetQuery = new TargetQuery()
                .forEntity(deploymentObjectUniqueId)
                .forLocation(findingLocation)
                .withRange(TargetType.DEPLOYMENT_OBJECT, Double.MAX_VALUE)
                .enemiesOnly(true);

        OpenModeTargetFinder targetFinder = new OpenModeTargetFinder(deploymentInfoProvider, hitboxResolver, livingEntityRegistry, playerRegistry);
        List<Target> targets = targetFinder.findTargets(targetQuery);

        assertThat(targets).isEmpty();
    }

    @Test
    public void shouldReturnNoTargetsIfGivenLocationHasNoWorld() {
        UUID entityId = UUID.randomUUID();
        Location location = new Location(null, 1.0, 1.0, 1.0);

        OpenModeTargetFinder targetFinder = new OpenModeTargetFinder(deploymentInfoProvider, hitboxResolver, livingEntityRegistry, playerRegistry);
        Collection<GameEntity> targets = targetFinder.findTargets(entityId, location, 0.1);

        assertTrue(targets.isEmpty());
    }

    @Test
    public void findTargetsReturnsListOfNearbyPlayers() {
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

        OpenModeTargetFinder targetFinder = new OpenModeTargetFinder(deploymentInfoProvider, hitboxResolver, livingEntityRegistry, playerRegistry);
        List<GameEntity> targets = targetFinder.findTargets(entityUniqueId, location, range);

        assertEquals(1, targets.size());
        assertEquals(target, targets.get(0));
    }

    @Test
    public void findTargetsReturnsEmptyListIfFoundPlayersArePassive() {
        UUID entityUniqueId = UUID.randomUUID();
        UUID targetUniqueId = UUID.randomUUID();
        World world = mock(World.class);

        Location location = new Location(world, 1, 1, 1);
        double range = 0.1;

        Entity targetEntity = mock(Entity.class);
        when(targetEntity.getUniqueId()).thenReturn(targetUniqueId);

        GamePlayer target = mock(GamePlayer.class);
        when(target.isPassive()).thenReturn(true);

        when(playerRegistry.findByUniqueId(entityUniqueId)).thenReturn(Optional.of(target));
        when(world.getNearbyEntities(location, range, range, range)).thenReturn(List.of(targetEntity));

        OpenModeTargetFinder targetFinder = new OpenModeTargetFinder(deploymentInfoProvider, hitboxResolver, livingEntityRegistry, playerRegistry);
        List<GameEntity> targets = targetFinder.findTargets(entityUniqueId, location, range);

        assertTrue(targets.isEmpty());
    }

    @Test
    public void findTargetsReturnsEntitiesThatAreNotPlayers() {
        UUID entityId = UUID.randomUUID();
        World world = mock(World.class);

        Location location = new Location(world, 1, 1, 1);
        double range = 0.1;

        Entity entity = mock(Zombie.class);
        when(entity.getType()).thenReturn(EntityType.ZOMBIE);

        when(world.getNearbyEntities(location, range, range, range)).thenReturn(List.of(entity));

        OpenModeTargetFinder targetFinder = new OpenModeTargetFinder(deploymentInfoProvider, hitboxResolver, livingEntityRegistry, playerRegistry);
        List<GameEntity> targets = targetFinder.findTargets(entityId, location, range);

        assertEquals(1, targets.size());
        assertEquals(entity, targets.get(0).getEntity());
    }
}
