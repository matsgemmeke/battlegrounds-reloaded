package nl.matsgemmeke.battlegrounds.game.training.component;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.BlockCollisionChecker;
import nl.matsgemmeke.battlegrounds.game.EntityStorage;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TrainingModeCollisionDetectorTest {

    private BlockCollisionChecker blockCollisionChecker;
    private EntityStorage<GamePlayer> playerStorage;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        blockCollisionChecker = mock(BlockCollisionChecker.class);
        playerStorage = (EntityStorage<GamePlayer>) mock(EntityStorage.class);
    }

    @Test
    public void shouldReturnNoTargetsIfGivenLocationHasNoWorld() {
        GameEntity entity = mock(GameEntity.class);
        Location location = new Location(null, 1.0, 1.0, 1.0);

        TrainingModeCollisionDetector collisionDetector = new TrainingModeCollisionDetector(blockCollisionChecker, playerStorage);
        Collection<GameEntity> targets = collisionDetector.findTargets(entity, location, 0.1);

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

        TrainingModeCollisionDetector collisionDetector = new TrainingModeCollisionDetector(blockCollisionChecker, playerStorage);
        List<GameEntity> targets = collisionDetector.findTargets(gameEntity, location, range);

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

        TrainingModeCollisionDetector collisionDetector = new TrainingModeCollisionDetector(blockCollisionChecker, playerStorage);
        List<GameEntity> targets = collisionDetector.findTargets(gameEntity, location, range);

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

        TrainingModeCollisionDetector collisionDetector = new TrainingModeCollisionDetector(blockCollisionChecker, playerStorage);
        List<GameEntity> targets = collisionDetector.findTargets(gameEntity, location, range);

        assertEquals(1, targets.size());
        assertEquals(entity, targets.get(0).getEntity());
    }

    @Test
    public void shouldReturnWhetherGivenLocationIsInOpenSegmentOfBlock() {
        World world = mock(World.class);
        Location location = new Location(world, 1, 1, 1);

        when(blockCollisionChecker.isSolid(any(), eq(location))).thenReturn(true);

        TrainingModeCollisionDetector collisionDetector = new TrainingModeCollisionDetector(blockCollisionChecker, playerStorage);
        boolean collision = collisionDetector.producesBlockCollisionAt(location);

        assertTrue(collision);
    }
}
