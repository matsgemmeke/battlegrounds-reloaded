package nl.matsgemmeke.battlegrounds.game.training;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.entity.GameItem;
import nl.matsgemmeke.battlegrounds.game.BlockCollisionChecker;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DefaultTrainingModeContextTest {

    private BlockCollisionChecker collisionChecker;
    private TrainingMode trainingMode;

    @Before
    public void setUp() {
        collisionChecker = mock(BlockCollisionChecker.class);
        trainingMode = mock(TrainingMode.class);
    }

    @Test
    public void returnsNoTargetsIfGivenLocationHasNoWorld() {
        GameEntity entity = mock(GameEntity.class);
        Location location = new Location(null, 1.0, 1.0, 1.0);

        DefaultTrainingModeContext context = new DefaultTrainingModeContext(trainingMode, collisionChecker);
        Collection<GameEntity> targets = context.getTargets(entity, location, 0.1);

        assertEquals(0, targets.size());
    }

    @Test
    public void returnsListOfTargetsBasedOnNearbyEntities() {
        double range = 0.1;

        GameEntity entity = mock(GameEntity.class);
        World world = mock(World.class);

        List<Entity> nearbyEntities = Collections.singletonList(mock(Player.class));
        Location location = new Location(world, 1.0, 1.0, 1.0);

        when(world.getNearbyEntities(location, range, range, range)).thenReturn(nearbyEntities);

        DefaultTrainingModeContext context = new DefaultTrainingModeContext(trainingMode, collisionChecker);
        Collection<GameEntity> targets = context.getTargets(entity, location, range);

        assertEquals(1, targets.size());
    }

    @Test
    public void doesNotPlaySoundIfGivenLocationHasNoWorld() {
        GameSound sound = mock(GameSound.class);

        Location location = new Location(null, 1.0, 1.0, 1.0);

        DefaultTrainingModeContext context = new DefaultTrainingModeContext(trainingMode, collisionChecker);
        context.playSound(sound, location);

        verifyNoInteractions(sound);
    }

    @Test
    public void shouldPlaySoundToAllPlayersInTheWorldOfTheLocation() {
        GameSound sound = mock(GameSound.class);
        Player player = mock(Player.class);

        World world = mock(World.class);
        when(world.getPlayers()).thenReturn(List.of(player));

        Location location = new Location(world, 1.0, 1.0, 1.0);

        DefaultTrainingModeContext context = new DefaultTrainingModeContext(trainingMode, collisionChecker);
        context.playSounds(List.of(sound), location);

        verify(player).playSound(eq(location), (Sound) isNull(), anyFloat(), anyFloat());
    }

    @Test
    public void producesCollisionsBasedOnTheCollisionChecker() {
        Block block = mock(Block.class);
        World world = mock(World.class);

        Location locationNoCollision = new Location(world, 1.0, 1.0, 1.0);
        Location locationYesCollision = new Location(world, 2.0, 2.0, 2.0);

        when(world.getBlockAt(locationNoCollision)).thenReturn(block);
        when(world.getBlockAt(locationYesCollision)).thenReturn(block);

        when(collisionChecker.isSolid(block, locationNoCollision)).thenReturn(false);
        when(collisionChecker.isSolid(block, locationYesCollision)).thenReturn(true);

        DefaultTrainingModeContext context = new DefaultTrainingModeContext(trainingMode, collisionChecker);
        boolean firstCollision = context.producesCollisionAt(locationNoCollision);
        boolean secondCollision = context.producesCollisionAt(locationYesCollision);

        assertFalse(firstCollision);
        assertTrue(secondCollision);
    }

    @Test
    public void shouldAddItemToGameWhenRegistering() {
        Item item = mock(Item.class);

        when(trainingMode.addItem(item)).thenReturn(mock(GameItem.class));

        DefaultTrainingModeContext context = new DefaultTrainingModeContext(trainingMode, collisionChecker);
        context.registerItem(item);

        verify(trainingMode).addItem(item);
    }
}
