package nl.matsgemmeke.battlegrounds.item.effect.combustion;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.CollisionDetector;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.game.damage.Damage;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.RangeProfile;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.activation.ItemEffectActivation;
import nl.matsgemmeke.battlegrounds.item.effect.source.EffectSource;
import nl.matsgemmeke.battlegrounds.util.MetadataValueEditor;
import nl.matsgemmeke.battlegrounds.util.Procedure;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
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
    private static final long MAX_DURATION = 600L;
    private static final long TICKS_BETWEEN_FIRE_SPREAD = 5L;

    private AudioEmitter audioEmitter;
    private CollisionDetector collisionDetector;
    private CombustionProperties properties;
    private ItemEffectActivation effectActivation;
    private MetadataValueEditor metadataValueEditor;
    private RangeProfile rangeProfile;
    private TargetFinder targetFinder;
    private TaskRunner taskRunner;

    @BeforeEach
    public void setUp() {
        audioEmitter = mock(AudioEmitter.class);
        collisionDetector = mock(CollisionDetector.class);
        properties = new CombustionProperties(COMBUSTION_SOUNDS, RADIUS, TICKS_BETWEEN_FIRE_SPREAD, MAX_DURATION, BURN_BLOCKS, SPREAD_FIRE);
        effectActivation = mock(ItemEffectActivation.class);
        metadataValueEditor = mock(MetadataValueEditor.class);
        rangeProfile = new RangeProfile(LONG_RANGE_DAMAGE, LONG_RANGE_DISTANCE, MEDIUM_RANGE_DAMAGE, MEDIUM_RANGE_DISTANCE, SHORT_RANGE_DAMAGE, SHORT_RANGE_DISTANCE);
        targetFinder = mock(TargetFinder.class);
        taskRunner = mock(TaskRunner.class);
    }

    @Test
    public void activateInstantlyDoesNotPerformEffectIfItWasAlreadyActivated() {
        ItemHolder holder = mock(ItemHolder.class);
        Location sourceLocation = new Location(null, 1, 1, 1);

        EffectSource source = mock(EffectSource.class);
        when(source.exists()).thenReturn(true);
        when(source.getLocation()).thenReturn(sourceLocation);

        ItemEffectContext context = new ItemEffectContext(holder, source);

        CombustionEffect effect = new CombustionEffect(effectActivation, properties, rangeProfile, audioEmitter, collisionDetector, metadataValueEditor, targetFinder, taskRunner);
        effect.prime(context);

        ArgumentCaptor<Procedure> procedureCaptor = ArgumentCaptor.forClass(Procedure.class);
        verify(effectActivation).prime(eq(context), procedureCaptor.capture());

        procedureCaptor.getValue().apply();
        effect.activateInstantly();

        verify(effectActivation, never()).cancel();
    }

    @Test
    public void activateInstantlyPerformsEffect() {
        ItemHolder holder = mock(ItemHolder.class);
        Location sourceLocation = new Location(null, 1, 1, 1);

        EffectSource source = mock(EffectSource.class);
        when(source.exists()).thenReturn(true);
        when(source.getLocation()).thenReturn(sourceLocation);

        ItemEffectContext context = new ItemEffectContext(holder, source);

        CombustionEffect effect = new CombustionEffect(effectActivation, properties, rangeProfile, audioEmitter, collisionDetector, metadataValueEditor, targetFinder, taskRunner);
        effect.prime(context);
        effect.activateInstantly();

        verify(effectActivation).cancel();
        verify(source).remove();
    }

    @Test
    public void cancelActivationDoesNotCancelActivationIfNotPrimed() {
        CombustionEffect effect = new CombustionEffect(effectActivation, properties, rangeProfile, audioEmitter, collisionDetector, metadataValueEditor, targetFinder, taskRunner);
        effect.cancelActivation();

        verify(effectActivation, never()).cancel();
    }

    @Test
    public void cancelActivationDoesNotCancelActivationIfAlreadyActivated() {
        Location sourceLocation = new Location(null, 1, 1, 1);

        EffectSource source = mock(EffectSource.class);
        when(source.getLocation()).thenReturn(sourceLocation);

        ItemHolder holder = mock(ItemHolder.class);
        ItemEffectContext context = new ItemEffectContext(holder, source);

        CombustionEffect effect = new CombustionEffect(effectActivation, properties, rangeProfile, audioEmitter, collisionDetector, metadataValueEditor, targetFinder, taskRunner);
        effect.prime(context);

        ArgumentCaptor<Procedure> procedureCaptor = ArgumentCaptor.forClass(Procedure.class);
        verify(effectActivation).prime(eq(context), procedureCaptor.capture());

        procedureCaptor.getValue().apply();
        effect.cancelActivation();

        verify(effectActivation, never()).cancel();
    }

    @Test
    public void cancelActivationCancelsActivationIfPrimed() {
        EffectSource source = mock(EffectSource.class);
        ItemHolder holder = mock(ItemHolder.class);
        ItemEffectContext context = new ItemEffectContext(holder, source);

        CombustionEffect effect = new CombustionEffect(effectActivation, properties, rangeProfile, audioEmitter, collisionDetector, metadataValueEditor, targetFinder, taskRunner);
        effect.prime(context);
        effect.cancelActivation();

        verify(effectActivation).cancel();
    }

    @Test
    public void deployChangesTheSourceOfTheContext() {
        ItemHolder holder = mock(ItemHolder.class);
        EffectSource oldSource = mock(EffectSource.class);
        EffectSource newSource = mock(EffectSource.class);

        ItemEffectContext context = new ItemEffectContext(holder, oldSource);

        CombustionEffect effect = new CombustionEffect(effectActivation, properties, rangeProfile, audioEmitter, collisionDetector, metadataValueEditor, targetFinder, taskRunner);
        effect.prime(context);
        effect.deploy(newSource);

        assertEquals(newSource, context.getSource());
    }

    @Test
    public void deployDoesNothingIfEffectIsNotPrimedYet() {
        EffectSource source = mock(EffectSource.class);

        CombustionEffect effect = new CombustionEffect(effectActivation, properties, rangeProfile, audioEmitter, collisionDetector, metadataValueEditor, targetFinder, taskRunner);

        // This method currently has no side effects to verify, refactor later?
        assertDoesNotThrow(() -> effect.deploy(source));
    }

    @Test
    public void isAwaitingDeploymentReturnsFalseIfEffectIsNotPrimed() {
        CombustionEffect effect = new CombustionEffect(effectActivation, properties, rangeProfile, audioEmitter, collisionDetector, metadataValueEditor, targetFinder, taskRunner);
        boolean awaitingDeployment = effect.isAwaitingDeployment();

        assertFalse(awaitingDeployment);
    }

    @Test
    public void isAwaitingDeploymentReturnsFalseIfContextSourceIsDeployed() {
        EffectSource source = mock(EffectSource.class);
        when(source.isDeployed()).thenReturn(true);

        ItemHolder holder = mock(ItemHolder.class);
        ItemEffectContext context = new ItemEffectContext(holder, source);

        CombustionEffect effect = new CombustionEffect(effectActivation, properties, rangeProfile, audioEmitter, collisionDetector, metadataValueEditor, targetFinder, taskRunner);
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

        CombustionEffect effect = new CombustionEffect(effectActivation, properties, rangeProfile, audioEmitter, collisionDetector, metadataValueEditor, targetFinder, taskRunner);
        effect.prime(context);
        boolean awaitingDeployment = effect.isAwaitingDeployment();

        assertTrue(awaitingDeployment);
    }

    @Test
    public void isPrimedReturnsFalseIfEffectWasNotPrimed() {
        CombustionEffect effect = new CombustionEffect(effectActivation, properties, rangeProfile, audioEmitter, collisionDetector, metadataValueEditor, targetFinder, taskRunner);
        boolean primed = effect.isPrimed();

        assertFalse(primed);
    }

    @Test
    public void isPrimedReturnsTrueIfEffectWasPrimed() {
        ItemHolder holder = mock(ItemHolder.class);
        EffectSource source = mock(EffectSource.class);
        ItemEffectContext context = new ItemEffectContext(holder, source);

        CombustionEffect effect = new CombustionEffect(effectActivation, properties, rangeProfile, audioEmitter, collisionDetector, metadataValueEditor, targetFinder, taskRunner);
        effect.prime(context);
        boolean primed = effect.isPrimed();

        assertTrue(primed);
    }

    @Test
    public void primeCreatesFireCircleAtSourceLocationAndResetsEffectAfterMaxDuration() {
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

        CombustionEffect effect = new CombustionEffect(effectActivation, properties, rangeProfile, audioEmitter, collisionDetector, metadataValueEditor, targetFinder, taskRunner);
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

        ArgumentCaptor<Runnable> resetRunnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskRunner).runTaskLater(resetRunnableCaptor.capture(), eq(MAX_DURATION));

        resetRunnableCaptor.getValue().run();

        verify(middleBlock).setType(Material.FIRE);
        verify(middleBlock).setType(Material.AIR);
        verify(metadataValueEditor).addFixedMetadataValue(middleBlock, "battlegrounds-burn-blocks", BURN_BLOCKS);
        verify(metadataValueEditor).addFixedMetadataValue(middleBlock, "battlegrounds-spread-fire", SPREAD_FIRE);
        verify(metadataValueEditor).removeMetadata(middleBlock, "battlegrounds-spread-fire");
        verify(metadataValueEditor).removeMetadata(middleBlock, "battlegrounds-spread-fire");

        verify(leftBlock).setType(Material.FIRE);
        verify(leftBlock).setType(Material.AIR);
        verify(metadataValueEditor).addFixedMetadataValue(leftBlock, "battlegrounds-burn-blocks", BURN_BLOCKS);
        verify(metadataValueEditor).addFixedMetadataValue(leftBlock, "battlegrounds-spread-fire", SPREAD_FIRE);
        verify(metadataValueEditor).removeMetadata(leftBlock, "battlegrounds-spread-fire");
        verify(metadataValueEditor).removeMetadata(leftBlock, "battlegrounds-spread-fire");

        verify(rightBlock).setType(Material.FIRE);
        verify(rightBlock).setType(Material.AIR);
        verify(metadataValueEditor).addFixedMetadataValue(rightBlock, "battlegrounds-burn-blocks", BURN_BLOCKS);
        verify(metadataValueEditor).addFixedMetadataValue(rightBlock, "battlegrounds-spread-fire", SPREAD_FIRE);
        verify(metadataValueEditor).removeMetadata(rightBlock, "battlegrounds-spread-fire");
        verify(metadataValueEditor).removeMetadata(rightBlock, "battlegrounds-spread-fire");

        verify(upperBlock).setType(Material.FIRE);
        verify(upperBlock).setType(Material.AIR);
        verify(metadataValueEditor).addFixedMetadataValue(upperBlock, "battlegrounds-burn-blocks", BURN_BLOCKS);
        verify(metadataValueEditor).addFixedMetadataValue(upperBlock, "battlegrounds-spread-fire", SPREAD_FIRE);
        verify(metadataValueEditor).removeMetadata(upperBlock, "battlegrounds-spread-fire");
        verify(metadataValueEditor).removeMetadata(upperBlock, "battlegrounds-spread-fire");

        verify(lowerBlock, never()).setType(Material.FIRE);
        verify(lowerBlock, never()).setType(Material.AIR);
        verify(metadataValueEditor, never()).addFixedMetadataValue(eq(lowerBlock), anyString(), any());
        verify(metadataValueEditor, never()).removeMetadata(eq(lowerBlock), anyString());

        verify(blockOutsideLineOfSight, never()).setType(Material.FIRE);
        verify(blockOutsideLineOfSight, never()).setType(Material.AIR);
        verify(metadataValueEditor, never()).addFixedMetadataValue(eq(blockOutsideLineOfSight), anyString(), any());
        verify(metadataValueEditor, never()).removeMetadata(eq(blockOutsideLineOfSight), anyString());

        verify(audioEmitter).playSounds(COMBUSTION_SOUNDS, sourceLocation);
        verify(source).remove();
        verify(task).cancel();
    }

    @Test
    public void primeDamagesNearbyEntitiesInsideTheLongRangeDistance() {
        World world = mock(World.class);
        Location holderLocation = new Location(world, 4, 0, 0);
        Location sourceLocation = new Location(world, 0, 0, 0);
        Location targetLocation = new Location(world, 2, 0, 0);

        GameEntity target = mock(GameEntity.class);
        when(target.getLocation()).thenReturn(targetLocation);

        ItemHolder holder = mock(ItemHolder.class);
        when(holder.getLocation()).thenReturn(holderLocation);

        EffectSource source = mock(EffectSource.class);
        when(source.getLocation()).thenReturn(sourceLocation);
        when(source.getWorld()).thenReturn(world);

        ItemEffectContext context = new ItemEffectContext(holder, source);

        when(targetFinder.findTargets(holder, sourceLocation, LONG_RANGE_DISTANCE)).thenReturn(List.of(holder, target));

        CombustionEffect effect = new CombustionEffect(effectActivation, properties, rangeProfile, audioEmitter, collisionDetector, metadataValueEditor, targetFinder, taskRunner);
        effect.prime(context);

        ArgumentCaptor<Procedure> procedureCaptor = ArgumentCaptor.forClass(Procedure.class);
        verify(effectActivation).prime(eq(context), procedureCaptor.capture());

        procedureCaptor.getValue().apply();

        verify(holder).damage(new Damage(MEDIUM_RANGE_DAMAGE, DamageType.FIRE_DAMAGE));
        verify(target).damage(new Damage(SHORT_RANGE_DAMAGE, DamageType.FIRE_DAMAGE));
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
