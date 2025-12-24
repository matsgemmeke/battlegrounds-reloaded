package nl.matsgemmeke.battlegrounds.item.shoot.launcher.item;

import nl.matsgemmeke.battlegrounds.MockUtils;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.damage.DamageSource;
import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.projectile.ItemProjectile;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.LaunchContext;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.ProjectileLaunchSource;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerContext;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerExecutor;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerObserver;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerRun;
import nl.matsgemmeke.battlegrounds.scheduling.Schedule;
import nl.matsgemmeke.battlegrounds.scheduling.ScheduleTask;
import nl.matsgemmeke.battlegrounds.scheduling.Scheduler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemLauncherTest {

    private static final double VELOCITY = 2.0;
    private static final ItemStack ITEM_STACK = new ItemStack(Material.STICK);
    private static final long GAME_SOUND_DELAY = 5L;
    private static final Location LAUNCH_DIRECTION = new Location(null, 1, 1, 1);

    @Mock
    private AudioEmitter audioEmitter;
    @Mock
    private DamageSource damageSource;
    @Mock
    private ItemEffect itemEffect;
    @Mock
    private ProjectileLaunchSource projectileSource;
    @Mock
    private Scheduler scheduler;

    @Test
    void cancelStopsOngoingSoundPlaySchedules() {
        Location direction = new Location(null, 0, 0, 0, 100.0f, 0);
        Item item = mock(Item.class);
        Schedule soundPlaySchedule = mock(Schedule.class);

        World world = mock(World.class);
        when(world.dropItem(direction, ITEM_STACK)).thenReturn(item);

        ItemTemplate itemTemplate = mock(ItemTemplate.class);
        when(itemTemplate.createItemStack()).thenReturn(ITEM_STACK);

        GameSound gameSound = mock(GameSound.class);
        when(gameSound.getDelay()).thenReturn(GAME_SOUND_DELAY);

        when(scheduler.createSingleRunSchedule(GAME_SOUND_DELAY)).thenReturn(soundPlaySchedule);

        ItemLaunchProperties properties = new ItemLaunchProperties(itemTemplate, List.of(gameSound), VELOCITY);
        LaunchContext launchContext = new LaunchContext(damageSource, projectileSource, direction, () -> LAUNCH_DIRECTION, world);

        ItemLauncher itemLauncher = new ItemLauncher(audioEmitter, scheduler, itemEffect, properties);
        itemLauncher.launch(launchContext);
        itemLauncher.cancel();

        verify(soundPlaySchedule).stop();
    }

    @Test
    void launchDropItemAndStartTriggerRunsThatActivateItemEffect() {
        Item item = mock(Item.class);
        Location direction = new Location(null, 0, 0, 0, 100.0f, 0);

        ItemTemplate itemTemplate = mock(ItemTemplate.class);
        when(itemTemplate.createItemStack()).thenReturn(ITEM_STACK);

        World world = mock(World.class);
        when(world.dropItem(direction, ITEM_STACK)).thenReturn(item);

        TriggerRun triggerRun = mock(TriggerRun.class);
        doAnswer(MockUtils.RUN_TRIGGER_OBSERVER).when(triggerRun).addObserver(any(TriggerObserver.class));

        TriggerExecutor triggerExecutor = mock(TriggerExecutor.class);
        when(triggerExecutor.createTriggerRun(any(TriggerContext.class))).thenReturn(triggerRun);

        Schedule soundPlaySchedule = mock(Schedule.class);
        doAnswer(MockUtils.RUN_SCHEDULE_TASK).when(soundPlaySchedule).addTask(any(ScheduleTask.class));

        GameSound gameSound = mock(GameSound.class);
        when(gameSound.getDelay()).thenReturn(GAME_SOUND_DELAY);

        ItemLaunchProperties properties = new ItemLaunchProperties(itemTemplate, List.of(gameSound), VELOCITY);
        LaunchContext launchContext = new LaunchContext(damageSource, projectileSource, direction, () -> LAUNCH_DIRECTION, world);

        when(scheduler.createSingleRunSchedule(GAME_SOUND_DELAY)).thenReturn(soundPlaySchedule);

        ItemLauncher itemLauncher = new ItemLauncher(audioEmitter, scheduler, itemEffect, properties);
        itemLauncher.addTriggerExecutor(triggerExecutor);
        itemLauncher.launch(launchContext);

        ArgumentCaptor<ItemEffectContext> effectContextCaptor = ArgumentCaptor.forClass(ItemEffectContext.class);
        verify(itemEffect).startPerformance(effectContextCaptor.capture());

        assertThat(effectContextCaptor.getValue()).satisfies(itemEffectContext -> {
            assertThat(itemEffectContext.getDamageSource()).isEqualTo(damageSource);
            assertThat(itemEffectContext.getEffectSource()).isInstanceOf(ItemProjectile.class);
            assertThat(itemEffectContext.getInitiationLocation()).isEqualTo(direction);
        });

        verify(audioEmitter).playSound(gameSound, LAUNCH_DIRECTION);
        verify(item).setPickupDelay(10000);
        verify(item).setVelocity(new Vector(-1.969615506024416,-0.0,-0.3472963553338606));
    }
}
