package nl.matsgemmeke.battlegrounds.item.shoot.launcher.arrow;

import nl.matsgemmeke.battlegrounds.MockUtils;
import nl.matsgemmeke.battlegrounds.entity.damage.DamageSource;
import nl.matsgemmeke.battlegrounds.entity.damage.DamageTarget;
import nl.matsgemmeke.battlegrounds.game.component.projectile.ProjectileHitAction;
import nl.matsgemmeke.battlegrounds.game.component.projectile.ProjectileHitActionRegistry;
import nl.matsgemmeke.battlegrounds.game.component.projectile.ProjectileHitResult;
import nl.matsgemmeke.battlegrounds.game.component.projectile.ProjectileRegistry;
import nl.matsgemmeke.battlegrounds.item.actor.ProjectileActor;
import nl.matsgemmeke.battlegrounds.item.effect.CollisionResult;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.CollisionResultAdapter;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.LaunchContext;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.ProjectileLaunchSource;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerContext;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerExecutor;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerObserver;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerRun;
import nl.matsgemmeke.battlegrounds.item.trigger.result.TriggerResult;
import nl.matsgemmeke.battlegrounds.sound.api.SoundEmitter;
import nl.matsgemmeke.battlegrounds.sound.api.SoundPlayback;
import nl.matsgemmeke.battlegrounds.sound.api.SoundTemplate;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.AbstractArrow.PickupStatus;
import org.bukkit.entity.Arrow;
import org.bukkit.util.Vector;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArrowLauncherTest {

    private static final SoundTemplate LAUNCH_SOUND_TEMPLATE = new SoundTemplate(null, null);
    private static final double VELOCITY = 3.0;
    private static final Location LAUNCH_DIRECTION = new Location(null, 1, 1, 1);
    private static final String ITEM_NAME = "Test Item";
    private static final UUID ARROW_UNIQUE_ID = UUID.randomUUID();

    @Mock
    private CollisionResultAdapter collisionResultAdapter;
    @Mock
    private DamageSource damageSource;
    @Mock
    private ItemEffect itemEffect;
    @Mock
    private ProjectileHitActionRegistry projectileHitActionRegistry;
    @Mock
    private ProjectileLaunchSource projectileSource;
    @Mock
    private ProjectileRegistry projectileRegistry;
    @Mock
    private SoundEmitter soundEmitter;

    @Test
    @DisplayName("cancel cancels sound playbacks")
    void cancel() {
        World world = mock(World.class);
        Location direction = new Location(world, 1.0, 1.0, 1.0, 0.0F, 0.0F);
        Arrow arrow = mock(Arrow.class);
        SoundPlayback soundPlayback = mock(SoundPlayback.class);

        when(soundEmitter.playSoundTemplate(eq(LAUNCH_SOUND_TEMPLATE), Mockito.<Supplier<Location>>argThat(supplier -> supplier.get() == LAUNCH_DIRECTION))).thenReturn(soundPlayback);
        when(projectileSource.launchProjectile(eq(Arrow.class), any(Vector.class))).thenReturn(arrow);

        ArrowProperties properties = new ArrowProperties(LAUNCH_SOUND_TEMPLATE, VELOCITY);
        LaunchContext launchContext = new LaunchContext(ITEM_NAME, damageSource, projectileSource, direction, () -> LAUNCH_DIRECTION, world);

        ArrowLauncher launcher = new ArrowLauncher(collisionResultAdapter, projectileHitActionRegistry, projectileRegistry, soundEmitter, properties, itemEffect);
        launcher.launch(launchContext);
        launcher.cancel();

        verify(soundPlayback).cancel();
    }

    @Test
    @DisplayName("launch launches arrow projectile and starts item effect when trigger activates")
    void launch_triggerActivatesEffect() {
        World world = mock(World.class);
        Location direction = new Location(world, 1.0, 1.0, 1.0, 0.0F, 0.0F);
        TriggerResult triggerResult = mock(TriggerResult.class);
        DamageTarget hitTarget = mock(DamageTarget.class);
        Location hitLocation = new Location(world, 2, 2, 2);
        CollisionResult collisionResult = new CollisionResult(null, hitTarget, hitLocation);

        Arrow arrow = mock(Arrow.class);
        when(arrow.getUniqueId()).thenReturn(ARROW_UNIQUE_ID);

        ProjectileLaunchSource projectileLaunchSource = mock(ProjectileLaunchSource.class);
        when(projectileLaunchSource.launchProjectile(eq(Arrow.class), any(Vector.class))).thenReturn(arrow);

        TriggerRun triggerRun = mock(TriggerRun.class);
        doAnswer(MockUtils.answerNotifyTriggerObserver(triggerResult)).when(triggerRun).addObserver(any(TriggerObserver.class));

        TriggerExecutor triggerExecutor = mock(TriggerExecutor.class);
        when(triggerExecutor.createTriggerRun(any(TriggerContext.class))).thenReturn(triggerRun);

        ArrowProperties properties = new ArrowProperties(LAUNCH_SOUND_TEMPLATE, VELOCITY);
        LaunchContext launchContext = new LaunchContext(ITEM_NAME, damageSource, projectileLaunchSource, direction, () -> LAUNCH_DIRECTION, world);

        when(collisionResultAdapter.adapt(triggerResult)).thenReturn(collisionResult);

        ArrowLauncher launcher = new ArrowLauncher(collisionResultAdapter, projectileHitActionRegistry, projectileRegistry, soundEmitter, properties, itemEffect);
        launcher.addTriggerExecutor(triggerExecutor);
        launcher.launch(launchContext);

        ArgumentCaptor<ItemEffectContext> itemEffectContextCaptor = ArgumentCaptor.forClass(ItemEffectContext.class);
        verify(itemEffect).startPerformance(itemEffectContextCaptor.capture());

        assertThat(itemEffectContextCaptor.getValue()).satisfies(itemEffectContext -> {
            assertThat(itemEffectContext.getActor()).isInstanceOf(ProjectileActor.class);
            assertThat(itemEffectContext.getCollisionResult()).satisfies(result -> {
                assertThat(result.getHitBlock()).isEmpty();
                assertThat(result.getHitTarget()).hasValue(hitTarget);
                assertThat(result.getHitLocation()).hasValue(hitLocation);
            });
            assertThat(itemEffectContext.getDamageSource()).isEqualTo(damageSource);
            assertThat(itemEffectContext.getStartingLocation()).isEqualTo(direction);
        });

        verify(arrow).remove();
        verify(arrow).setPickupStatus(PickupStatus.DISALLOWED);
        verify(arrow).setVelocity(new Vector(0, 0, 3.0));
        verify(soundEmitter).playSoundTemplate(eq(LAUNCH_SOUND_TEMPLATE), Mockito.<Supplier<Location>>argThat(supplier -> supplier.get() == LAUNCH_DIRECTION));
        verify(projectileRegistry).register(ARROW_UNIQUE_ID);
        verify(projectileRegistry).unregister(ARROW_UNIQUE_ID);
    }

    @Test
    @DisplayName("launch launches arrow projectile and starts item effect when it hits something")
    void launch_arrowHitActivatesEffect() {
        World world = mock(World.class);
        Location direction = new Location(world, 1.0, 1.0, 1.0, 0.0F, 0.0F);
        ProjectileHitResult projectileHitResult = new ProjectileHitResult(null, null);
        DamageTarget hitTarget = mock(DamageTarget.class);
        Location hitLocation = new Location(world, 2, 2, 2);
        CollisionResult collisionResult = new CollisionResult(null, hitTarget, hitLocation);

        Arrow arrow = mock(Arrow.class);
        when(arrow.getUniqueId()).thenReturn(ARROW_UNIQUE_ID);

        ProjectileLaunchSource projectileLaunchSource = mock(ProjectileLaunchSource.class);
        when(projectileLaunchSource.launchProjectile(eq(Arrow.class), any(Vector.class))).thenReturn(arrow);

        ArrowProperties properties = new ArrowProperties(LAUNCH_SOUND_TEMPLATE, VELOCITY);
        LaunchContext launchContext = new LaunchContext(ITEM_NAME, damageSource, projectileLaunchSource, direction, () -> LAUNCH_DIRECTION, world);

        when(collisionResultAdapter.adapt(projectileHitResult, arrow)).thenReturn(collisionResult);
        doAnswer(MockUtils.answerRunProjectileHitAction(projectileHitResult)).when(projectileHitActionRegistry).registerProjectileHitAction(eq(arrow), any(ProjectileHitAction.class));

        ArrowLauncher launcher = new ArrowLauncher(collisionResultAdapter, projectileHitActionRegistry, projectileRegistry, soundEmitter, properties, itemEffect);
        launcher.launch(launchContext);

        ArgumentCaptor<ItemEffectContext> itemEffectContextCaptor = ArgumentCaptor.forClass(ItemEffectContext.class);
        verify(itemEffect).startPerformance(itemEffectContextCaptor.capture());

        assertThat(itemEffectContextCaptor.getValue()).satisfies(itemEffectContext -> {
            assertThat(itemEffectContext.getActor()).isInstanceOf(ProjectileActor.class);
            assertThat(itemEffectContext.getCollisionResult()).satisfies(result -> {
                assertThat(result.getHitBlock()).isEmpty();
                assertThat(result.getHitTarget()).hasValue(hitTarget);
                assertThat(result.getHitLocation()).hasValue(hitLocation);
            });
            assertThat(itemEffectContext.getDamageSource()).isEqualTo(damageSource);
            assertThat(itemEffectContext.getStartingLocation()).isEqualTo(direction);
        });

        verify(arrow).remove();
        verify(arrow).setPickupStatus(PickupStatus.DISALLOWED);
        verify(arrow).setVelocity(new Vector(0, 0, 3.0));
        verify(soundEmitter).playSoundTemplate(eq(LAUNCH_SOUND_TEMPLATE), Mockito.<Supplier<Location>>argThat(supplier -> supplier.get() == LAUNCH_DIRECTION));
        verify(projectileRegistry).register(ARROW_UNIQUE_ID);
        verify(projectileRegistry).unregister(ARROW_UNIQUE_ID);
    }
}
