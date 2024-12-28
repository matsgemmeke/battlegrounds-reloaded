package nl.matsgemmeke.battlegrounds.item.effect.combustion;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.CollisionDetector;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.RangeProfile;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.activation.ItemEffectActivation;
import nl.matsgemmeke.battlegrounds.item.effect.source.EffectSource;
import nl.matsgemmeke.battlegrounds.util.MetadataValueCreator;
import nl.matsgemmeke.battlegrounds.util.Procedure;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class CombustionEffectTest {

    private static final boolean BURN_BLOCKS = false;
    private static final boolean SPREAD_FIRE = true;
    private static final double LONG_RANGE_DAMAGE = 25.0;
    private static final double LONG_RANGE_DISTANCE = 10.0;
    private static final double MEDIUM_RANGE_DAMAGE = 75.0;
    private static final double MEDIUM_RANGE_DISTANCE = 5.0;
    private static final double SHORT_RANGE_DAMAGE = 150.0;
    private static final double SHORT_RANGE_DISTANCE = 2.5;
    private static final int RADIUS = 2;
    private static final List<GameSound> COMBUSTION_SOUNDS = Collections.emptyList();
    private static final long TICKS_BETWEEN_FIRE_SPREAD = 5L;

    private AudioEmitter audioEmitter;
    private CollisionDetector collisionDetector;
    private CombustionProperties properties;
    private ItemEffectActivation effectActivation;
    private MetadataValueCreator metadataValueCreator;
    private RangeProfile rangeProfile;
    private TargetFinder targetFinder;
    private TaskRunner taskRunner;

    @BeforeEach
    public void setUp() {
        audioEmitter = mock(AudioEmitter.class);
        collisionDetector = mock(CollisionDetector.class);
        properties = new CombustionProperties(COMBUSTION_SOUNDS, RADIUS, TICKS_BETWEEN_FIRE_SPREAD, BURN_BLOCKS, SPREAD_FIRE);
        effectActivation = mock(ItemEffectActivation.class);
        metadataValueCreator = mock(MetadataValueCreator.class);
        rangeProfile = new RangeProfile(LONG_RANGE_DAMAGE, LONG_RANGE_DISTANCE, MEDIUM_RANGE_DAMAGE, MEDIUM_RANGE_DISTANCE, SHORT_RANGE_DAMAGE, SHORT_RANGE_DISTANCE);
        targetFinder = mock(TargetFinder.class);
        taskRunner = mock(TaskRunner.class);
    }

    @Test
    public void activateInstantlyPerformsEffectIfContextIsNotNull() {
        ItemHolder holder = mock(ItemHolder.class);
        Location sourceLocation = new Location(null, 1, 1, 1);

        EffectSource source = mock(EffectSource.class);
        when(source.getLocation()).thenReturn(sourceLocation);

        ItemEffectContext context = new ItemEffectContext(holder, source);

        CombustionEffect effect = new CombustionEffect(effectActivation, properties, rangeProfile, audioEmitter, collisionDetector, metadataValueCreator, targetFinder, taskRunner);
        effect.prime(context);
        effect.activateInstantly();

        verify(audioEmitter).playSounds(COMBUSTION_SOUNDS, sourceLocation);
        verify(source).remove();
    }

    @Test
    public void deployChangesTheSourceOfTheContext() {
        ItemHolder holder = mock(ItemHolder.class);
        EffectSource oldSource = mock(EffectSource.class);
        EffectSource newSource = mock(EffectSource.class);

        ItemEffectContext context = new ItemEffectContext(holder, oldSource);

        CombustionEffect effect = new CombustionEffect(effectActivation, properties, rangeProfile, audioEmitter, collisionDetector, metadataValueCreator, targetFinder, taskRunner);
        effect.prime(context);
        effect.deploy(newSource);

        assertEquals(newSource, context.getSource());
    }

    @Test
    public void deployDoesNothingIfEffectIsNotPrimedYet() {
        EffectSource source = mock(EffectSource.class);

        CombustionEffect effect = new CombustionEffect(effectActivation, properties, rangeProfile, audioEmitter, collisionDetector, metadataValueCreator, targetFinder, taskRunner);

        // This method currently has no side effects to verify, refactor later?
        assertDoesNotThrow(() -> effect.deploy(source));
    }

    @Test
    public void isAwaitingDeploymentReturnsFalseIfEffectIsNotPrimed() {
        CombustionEffect effect = new CombustionEffect(effectActivation, properties, rangeProfile, audioEmitter, collisionDetector, metadataValueCreator, targetFinder, taskRunner);
        boolean awaitingDeployment = effect.isAwaitingDeployment();

        assertFalse(awaitingDeployment);
    }

    @Test
    public void isAwaitingDeploymentReturnsFalseIfContextSourceIsDeployed() {
        EffectSource source = mock(EffectSource.class);
        when(source.isDeployed()).thenReturn(true);

        ItemHolder holder = mock(ItemHolder.class);
        ItemEffectContext context = new ItemEffectContext(holder, source);

        CombustionEffect effect = new CombustionEffect(effectActivation, properties, rangeProfile, audioEmitter, collisionDetector, metadataValueCreator, targetFinder, taskRunner);
        effect.prime(context);
        boolean awaitingDeployment = effect.isAwaitingDeployment();

        assertFalse(awaitingDeployment);
    }

    @Test
    public void isAwaitingDeploymentReturnsFalseIfContextSourceIsNotDeployed() {
        EffectSource source = mock(EffectSource.class);
        when(source.isDeployed()).thenReturn(false);

        ItemHolder holder = mock(ItemHolder.class);
        ItemEffectContext context = new ItemEffectContext(holder, source);

        CombustionEffect effect = new CombustionEffect(effectActivation, properties, rangeProfile, audioEmitter, collisionDetector, metadataValueCreator, targetFinder, taskRunner);
        effect.prime(context);
        boolean awaitingDeployment = effect.isAwaitingDeployment();

        assertTrue(awaitingDeployment);
    }

    @Test
    public void isPrimedReturnsFalseIfEffectWasNotPrimed() {
        CombustionEffect effect = new CombustionEffect(effectActivation, properties, rangeProfile, audioEmitter, collisionDetector, metadataValueCreator, targetFinder, taskRunner);
        boolean primed = effect.isPrimed();

        assertFalse(primed);
    }

    @Test
    public void isPrimedReturnsTrueIfEffectWasPrimed() {
        ItemHolder holder = mock(ItemHolder.class);
        EffectSource source = mock(EffectSource.class);
        ItemEffectContext context = new ItemEffectContext(holder, source);

        CombustionEffect effect = new CombustionEffect(effectActivation, properties, rangeProfile, audioEmitter, collisionDetector, metadataValueCreator, targetFinder, taskRunner);
        effect.prime(context);
        boolean primed = effect.isPrimed();

        assertTrue(primed);
    }

    @Test
    public void activateCreatesFireCircleAtSourceLocation() {
        MetadataValue metadataBurnBlocks = mock(MetadataValue.class);
        MetadataValue metadataSpreadFire = mock(MetadataValue.class);

        when(metadataValueCreator.createFixedMetadataValue(BURN_BLOCKS)).thenReturn(metadataBurnBlocks);
        when(metadataValueCreator.createFixedMetadataValue(SPREAD_FIRE)).thenReturn(metadataSpreadFire);

        World world = mock(World.class);
        when(world.getBlockAt(anyInt(), anyInt(), anyInt())).thenReturn(mock(Block.class));

        ItemHolder holder = mock(ItemHolder.class);
        Location sourceLocation = new Location(world, 0, 0, 0);

        EffectSource source = mock(EffectSource.class);
        when(source.getLocation()).thenReturn(sourceLocation);
        when(source.getWorld()).thenReturn(world);

        ItemEffectContext context = new ItemEffectContext(holder, source);

        Block middleBlock = this.createBlock(world, 0, 0, 0, Material.AIR, true);
        Block leftBlock = this.createBlock(world, -1, 0, 0, Material.AIR, true);
        Block rightBlock = this.createBlock(world, 1, 0, 0, Material.AIR, true);
        Block upperBlock = this.createBlock(world, 0, 0, -1, Material.AIR, true);
        Block lowerBlock = this.createBlock(world, 0, 0, 1, Material.STONE, false);
        Block blockOutsideLineOfSight = this.createBlock(world, 0, 0, 2, Material.AIR, true);

        BukkitTask task = mock(BukkitTask.class);
        when(taskRunner.runTaskTimer(any(Runnable.class), eq(0L), eq(TICKS_BETWEEN_FIRE_SPREAD))).thenReturn(task);

        when(collisionDetector.hasLineOfSight(any(Location.class), any(Location.class))).thenReturn(true);
        when(collisionDetector.hasLineOfSight(blockOutsideLineOfSight.getLocation(), sourceLocation)).thenReturn(false);

        CombustionEffect effect = new CombustionEffect(effectActivation, properties, rangeProfile, audioEmitter, collisionDetector, metadataValueCreator, targetFinder, taskRunner);
        effect.prime(context);

        ArgumentCaptor<Procedure> procedureCaptor = ArgumentCaptor.forClass(Procedure.class);
        verify(effectActivation).prime(eq(context), procedureCaptor.capture());

        procedureCaptor.getValue().apply();

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskRunner).runTaskTimer(runnableCaptor.capture(), anyLong(), anyLong());

        // Simulate the task running three times to exceed the radius
        Runnable runnable = runnableCaptor.getValue();
        runnable.run();
        runnable.run();
        runnable.run();

        verify(middleBlock).setType(Material.FIRE);
        verify(middleBlock).setMetadata("battlegrounds-burn-blocks", metadataBurnBlocks);
        verify(middleBlock).setMetadata("battlegrounds-spread-fire", metadataSpreadFire);

        verify(leftBlock).setType(Material.FIRE);
        verify(leftBlock).setMetadata("battlegrounds-burn-blocks", metadataBurnBlocks);
        verify(leftBlock).setMetadata("battlegrounds-spread-fire", metadataSpreadFire);

        verify(rightBlock).setType(Material.FIRE);
        verify(rightBlock).setMetadata("battlegrounds-burn-blocks", metadataBurnBlocks);
        verify(rightBlock).setMetadata("battlegrounds-spread-fire", metadataSpreadFire);

        verify(upperBlock).setType(Material.FIRE);
        verify(upperBlock).setMetadata("battlegrounds-burn-blocks", metadataBurnBlocks);
        verify(upperBlock).setMetadata("battlegrounds-spread-fire", metadataSpreadFire);

        verify(lowerBlock, never()).setType(Material.FIRE);
        verify(lowerBlock, never()).setMetadata(anyString(), any());

        verify(blockOutsideLineOfSight, never()).setType(Material.FIRE);
        verify(blockOutsideLineOfSight, never()).setMetadata(anyString(), any());

        verify(audioEmitter).playSounds(COMBUSTION_SOUNDS, sourceLocation);
        verify(source).remove();
        verify(task).cancel();
    }

    @Test
    public void activateDamagesNearbyEntitiesInsideTheLongRangeDistance() {
        CombustionProperties properties = new CombustionProperties(Collections.emptyList(), 0, 0, false, false);

        World world = mock(World.class);
        Location holderLocation = new Location(world, 4, 0, 0);
        Location objectLocation = new Location(world, 0, 0, 0);
        Location targetLocation = new Location(world, 2, 0, 0);

        GameEntity target = mock(GameEntity.class);
        when(target.getLocation()).thenReturn(targetLocation);

        ItemHolder holder = mock(ItemHolder.class);
        when(holder.getLocation()).thenReturn(holderLocation);

        EffectSource source = mock(EffectSource.class);
        when(source.getLocation()).thenReturn(objectLocation);
        when(source.getWorld()).thenReturn(world);

        ItemEffectContext context = new ItemEffectContext(holder, source);

        when(targetFinder.findTargets(holder, objectLocation, LONG_RANGE_DISTANCE)).thenReturn(List.of(holder, target));

        CombustionEffect effect = new CombustionEffect(effectActivation, properties, rangeProfile, audioEmitter, collisionDetector, metadataValueCreator, targetFinder, taskRunner);
        effect.prime(context);

        ArgumentCaptor<Procedure> procedureCaptor = ArgumentCaptor.forClass(Procedure.class);
        verify(effectActivation).prime(eq(context), procedureCaptor.capture());

        procedureCaptor.getValue().apply();

        verify(holder).damage(MEDIUM_RANGE_DAMAGE);
        verify(target).damage(SHORT_RANGE_DAMAGE);
    }

    @NotNull
    private Block createBlock(World world, int x, int y, int z, Material material, boolean passable) {
        Block block = mock(Block.class);
        when(block.getLocation()).thenReturn(new Location(world, x, y, z));
        when(block.getType()).thenReturn(material).thenReturn(Material.FIRE);
        when(block.isPassable()).thenReturn(passable);
        when(world.getBlockAt(x, y, z)).thenReturn(block);

        return block;
    }
}
