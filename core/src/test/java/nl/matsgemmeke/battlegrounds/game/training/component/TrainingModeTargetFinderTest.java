package nl.matsgemmeke.battlegrounds.game.training.component;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.component.deploy.DeploymentInfoProvider;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TrainingModeTargetFinderTest {

    private DeploymentInfoProvider deploymentInfoProvider;
    private PlayerRegistry playerRegistry;

    @BeforeEach
    public void setUp() {
        deploymentInfoProvider = mock(DeploymentInfoProvider.class);
        playerRegistry = mock(PlayerRegistry.class);
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

        TrainingModeTargetFinder targetFinder = new TrainingModeTargetFinder(deploymentInfoProvider, playerRegistry);
        List<DeploymentObject> deploymentObjects = targetFinder.findDeploymentObjects(entityId, location, range);

        assertEquals(1, deploymentObjects.size());
        assertTrue(deploymentObjects.contains(objectInsideRange));
        assertFalse(deploymentObjects.contains(objectOutsideRange));
    }

    @Test
    public void findEnemyTargetsReturnsEmptyListWhenGivenLocationWorldIsNull() {
        UUID entityId = UUID.randomUUID();
        Location location = new Location(null, 1.0, 1.0, 1.0);

        TrainingModeTargetFinder targetFinder = new TrainingModeTargetFinder(deploymentInfoProvider, playerRegistry);
        Collection<GameEntity> targets = targetFinder.findEnemyTargets(entityId, location, 0.1);

        assertThat(targets).isEmpty();
    }

    @Test
    public void findEnemyTargetsReturnsNearbyTargetsButWithoutEntitiesContainingTheGivenEntityId() {
        UUID entityId = UUID.randomUUID();
        UUID targetId = UUID.randomUUID();
        double range = 0.1;

        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(entityId);

        Player targetPlayer = mock(Player.class);
        when(targetPlayer.getUniqueId()).thenReturn(targetId);

        GamePlayer gamePlayer = mock(GamePlayer.class);
        when(gamePlayer.getEntity()).thenReturn(player);

        GamePlayer target = mock(GamePlayer.class);
        when(target.getEntity()).thenReturn(targetPlayer);

        World world = mock(World.class);
        Location location = new Location(world, 1.0, 1.0, 1.0);

        when(world.getNearbyEntities(location, range, range, range)).thenReturn(List.of(player, targetPlayer));

        when(playerRegistry.findByUUID(entityId)).thenReturn(gamePlayer);
        when(playerRegistry.findByUUID(targetId)).thenReturn(target);

        TrainingModeTargetFinder targetFinder = new TrainingModeTargetFinder(deploymentInfoProvider, playerRegistry);
        Collection<GameEntity> targets = targetFinder.findEnemyTargets(entityId, location, range);

        assertEquals(1, targets.size());
        assertTrue(targets.contains(target));
        assertFalse(targets.contains(gamePlayer));
    }

    @Test
    public void shouldReturnNoTargetsIfGivenLocationHasNoWorld() {
        UUID entityId = UUID.randomUUID();
        Location location = new Location(null, 1.0, 1.0, 1.0);

        TrainingModeTargetFinder targetFinder = new TrainingModeTargetFinder(deploymentInfoProvider, playerRegistry);
        Collection<GameEntity> targets = targetFinder.findTargets(entityId, location, 0.1);

        assertTrue(targets.isEmpty());
    }

    @Test
    public void findTargetsReturnsListOfNearbyPlayers() {
        UUID entityId = UUID.randomUUID();
        UUID targetId = UUID.randomUUID();
        World world = mock(World.class);

        Location location = new Location(world, 1, 1, 1);
        double range = 0.1;

        Entity targetEntity = mock(Entity.class);
        when(targetEntity.getUniqueId()).thenReturn(targetId);

        GamePlayer target = mock(GamePlayer.class);
        when(target.isPassive()).thenReturn(false);

        when(playerRegistry.findByUUID(targetId)).thenReturn(target);
        when(world.getNearbyEntities(location, range, range, range)).thenReturn(List.of(targetEntity));

        TrainingModeTargetFinder targetFinder = new TrainingModeTargetFinder(deploymentInfoProvider, playerRegistry);
        List<GameEntity> targets = targetFinder.findTargets(entityId, location, range);

        assertEquals(1, targets.size());
        assertEquals(target, targets.get(0));
    }

    @Test
    public void findTargetsReturnsEmptyListIfFoundPlayersArePassive() {
        UUID entityId = UUID.randomUUID();
        UUID targetId = UUID.randomUUID();
        World world = mock(World.class);

        Location location = new Location(world, 1, 1, 1);
        double range = 0.1;

        Entity targetEntity = mock(Entity.class);
        when(targetEntity.getUniqueId()).thenReturn(targetId);

        GamePlayer target = mock(GamePlayer.class);
        when(target.isPassive()).thenReturn(true);

        when(playerRegistry.findByUUID(targetId)).thenReturn(target);
        when(world.getNearbyEntities(location, range, range, range)).thenReturn(List.of(targetEntity));

        TrainingModeTargetFinder targetFinder = new TrainingModeTargetFinder(deploymentInfoProvider, playerRegistry);
        List<GameEntity> targets = targetFinder.findTargets(entityId, location, range);

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

        TrainingModeTargetFinder targetFinder = new TrainingModeTargetFinder(deploymentInfoProvider, playerRegistry);
        List<GameEntity> targets = targetFinder.findTargets(entityId, location, range);

        assertEquals(1, targets.size());
        assertEquals(entity, targets.get(0).getEntity());
    }
}
