package nl.matsgemmeke.battlegrounds.game.training.component;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.EntityStorage;
import nl.matsgemmeke.battlegrounds.game.storage.DeploymentObjectStorage;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TrainingModeTargetFinderTest {

    private DeploymentObjectStorage deploymentObjectStorage;
    private EntityStorage<GamePlayer> playerStorage;

    @BeforeEach
    public void setUp() {
        deploymentObjectStorage = new DeploymentObjectStorage();
        playerStorage = mock();
    }

    @Test
    public void findDeploymentObjectsReturnsDeploymentObjectsLocatedWithinGivenLocationAndRange() {
        GameEntity gameEntity = mock(GameEntity.class);
        World world = mock(World.class);
        Location location = new Location(world, 1, 1, 1);
        double range = 0.5;

        Location locationInsideRange = new Location(world, 1, 1, 1);
        Location locationOutsideRange = new Location(world, 100, 100, 100);

        DeploymentObject objectInsideRange = mock(DeploymentObject.class);
        when(objectInsideRange.getLocation()).thenReturn(locationInsideRange);

        DeploymentObject objectOutsideRange = mock(DeploymentObject.class);
        when(objectOutsideRange.getLocation()).thenReturn(locationOutsideRange);

        deploymentObjectStorage.addDeploymentObject(objectInsideRange);
        deploymentObjectStorage.addDeploymentObject(objectOutsideRange);

        TrainingModeTargetFinder targetFinder = new TrainingModeTargetFinder(deploymentObjectStorage, playerStorage);
        List<DeploymentObject> deploymentObjects = targetFinder.findDeploymentObjects(gameEntity, location, range);

        assertEquals(1, deploymentObjects.size());
        assertTrue(deploymentObjects.contains(objectInsideRange));
        assertFalse(deploymentObjects.contains(objectOutsideRange));
    }

    @Test
    public void returnNearbyTargetsButWithoutTheGivenGameEntityInstance() {
        Player player = mock(Player.class);
        Player targetEntity = mock(Player.class);

        GamePlayer gamePlayer = mock(GamePlayer.class);
        when(gamePlayer.getEntity()).thenReturn(player);

        GamePlayer target = mock(GamePlayer.class);
        when(target.getEntity()).thenReturn(targetEntity);

        World world = mock(World.class);
        Location location = new Location(world, 1.0, 1.0, 1.0);

        double range = 0.1;

        when(world.getNearbyEntities(location, range, range, range)).thenReturn(List.of(player, targetEntity));

        when(playerStorage.getEntity(player)).thenReturn(gamePlayer);
        when(playerStorage.getEntity(targetEntity)).thenReturn(target);

        TrainingModeTargetFinder targetFinder = new TrainingModeTargetFinder(deploymentObjectStorage, playerStorage);
        Collection<GameEntity> targets = targetFinder.findEnemyTargets(gamePlayer, location, range);

        assertEquals(1, targets.size());
        assertTrue(targets.contains(target));
        assertFalse(targets.contains(gamePlayer));
    }

    @Test
    public void shouldReturnNoTargetsIfGivenLocationHasNoWorld() {
        GameEntity entity = mock(GameEntity.class);
        Location location = new Location(null, 1.0, 1.0, 1.0);

        TrainingModeTargetFinder targetFinder = new TrainingModeTargetFinder(deploymentObjectStorage, playerStorage);
        Collection<GameEntity> targets = targetFinder.findTargets(entity, location, 0.1);

        assertTrue(targets.isEmpty());
    }

    @Test
    public void findTargetsReturnsListOfNearbyPlayers() {
        GameEntity gameEntity = mock(GameEntity.class);
        World world = mock(World.class);

        Location location = new Location(world, 1, 1, 1);
        double range = 0.1;

        Entity entity = mock(Entity.class);

        GamePlayer gamePlayer = mock(GamePlayer.class);
        when(gamePlayer.isPassive()).thenReturn(false);

        when(playerStorage.getEntity(entity)).thenReturn(gamePlayer);
        when(world.getNearbyEntities(location, range, range, range)).thenReturn(List.of(entity));

        TrainingModeTargetFinder targetFinder = new TrainingModeTargetFinder(deploymentObjectStorage, playerStorage);
        List<GameEntity> targets = targetFinder.findTargets(gameEntity, location, range);

        assertEquals(1, targets.size());
        assertEquals(gamePlayer, targets.get(0));
    }

    @Test
    public void findTargetsReturnsEmptyListIfFoundPlayersArePassive() {
        GameEntity gameEntity = mock(GameEntity.class);
        World world = mock(World.class);

        Location location = new Location(world, 1, 1, 1);
        double range = 0.1;

        Entity entity = mock(Entity.class);

        GamePlayer gamePlayer = mock(GamePlayer.class);
        when(gamePlayer.isPassive()).thenReturn(true);

        when(playerStorage.getEntity(entity)).thenReturn(gamePlayer);
        when(world.getNearbyEntities(location, range, range, range)).thenReturn(List.of(entity));

        TrainingModeTargetFinder targetFinder = new TrainingModeTargetFinder(deploymentObjectStorage, playerStorage);
        List<GameEntity> targets = targetFinder.findTargets(gameEntity, location, range);

        assertTrue(targets.isEmpty());
    }

    @Test
    public void findTargetsReturnsEntitiesThatAreNotPlayers() {
        GameEntity gameEntity = mock(GameEntity.class);
        World world = mock(World.class);

        Location location = new Location(world, 1, 1, 1);
        double range = 0.1;

        Entity entity = mock(Zombie.class);
        when(entity.getType()).thenReturn(EntityType.ZOMBIE);

        when(world.getNearbyEntities(location, range, range, range)).thenReturn(List.of(entity));

        TrainingModeTargetFinder targetFinder = new TrainingModeTargetFinder(deploymentObjectStorage, playerStorage);
        List<GameEntity> targets = targetFinder.findTargets(gameEntity, location, range);

        assertEquals(1, targets.size());
        assertEquals(entity, targets.get(0).getEntity());
    }
}
