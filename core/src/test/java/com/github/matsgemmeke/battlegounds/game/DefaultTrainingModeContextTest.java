package com.github.matsgemmeke.battlegounds.game;

import com.github.matsgemmeke.battlegrounds.api.entity.BattleEntity;
import com.github.matsgemmeke.battlegrounds.api.game.BattleSound;
import com.github.matsgemmeke.battlegrounds.game.BlockCollisionChecker;
import com.github.matsgemmeke.battlegrounds.game.DefaultTrainingModeContext;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
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

    @Before
    public void setUp() {
        this.collisionChecker = mock(BlockCollisionChecker.class);
    }

    @Test
    public void returnsNoTargetsIfGivenLocationHasNoWorld() {
        BattleEntity entity = mock(BattleEntity.class);
        Location location = new Location(null, 1.0, 1.0, 1.0);

        DefaultTrainingModeContext context = new DefaultTrainingModeContext(collisionChecker);
        Collection<BattleEntity> targets = context.getTargets(entity, location, 0.1);

        assertEquals(0, targets.size());
    }

    @Test
    public void returnsListOfTargetsBasedOnNearbyEntities() {
        double range = 0.1;

        BattleEntity entity = mock(BattleEntity.class);
        World world = mock(World.class);

        List<Entity> nearbyEntities = Collections.singletonList(mock(Player.class));
        Location location = new Location(world, 1.0, 1.0, 1.0);

        when(world.getNearbyEntities(location, range, range, range)).thenReturn(nearbyEntities);

        DefaultTrainingModeContext context = new DefaultTrainingModeContext(collisionChecker);
        Collection<BattleEntity> targets = context.getTargets(entity, location, range);

        assertEquals(1, targets.size());
    }

    @Test
    public void doesNotPlaySoundIfGivenLocationHasNoWorld() {
        BattleSound sound = mock(BattleSound.class);

        Location location = new Location(null, 1.0, 1.0, 1.0);

        DefaultTrainingModeContext context = new DefaultTrainingModeContext(collisionChecker);
        context.playSound(sound, location);

        verifyNoInteractions(sound);
    }

    @Test
    public void shouldPlaySoundToAllPlayersInTheWorldOfTheLocation() {
        BattleSound sound = mock(BattleSound.class);
        Player player = mock(Player.class);

        World world = mock(World.class);
        when(world.getPlayers()).thenReturn(List.of(player));

        Location location = new Location(world, 1.0, 1.0, 1.0);

        DefaultTrainingModeContext context = new DefaultTrainingModeContext(collisionChecker);
        context.playSounds(List.of(sound), location);

        verify(player).playSound(eq(location), (Sound) isNull(), anyFloat(), anyFloat());
    }

    @Test
    public void producesCollisionsBasedOnTheCollisionChecker() {
        Block block = mock(Block.class);
        Location locationNoCollision = new Location(null, 1.0, 1.0, 1.0);
        Location locationYesCollision = new Location(null, 2.0, 2.0, 2.0);

        when(collisionChecker.isSolid(block, locationNoCollision)).thenReturn(false);
        when(collisionChecker.isSolid(block, locationYesCollision)).thenReturn(true);

        DefaultTrainingModeContext context = new DefaultTrainingModeContext(collisionChecker);
        boolean firstCollision = context.producesCollisionAt(block, locationNoCollision);
        boolean secondCollision = context.producesCollisionAt(block, locationYesCollision);

        assertFalse(firstCollision);
        assertTrue(secondCollision);
    }
}
