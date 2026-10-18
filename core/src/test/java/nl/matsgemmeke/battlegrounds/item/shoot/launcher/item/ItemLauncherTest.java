package nl.matsgemmeke.battlegrounds.item.shoot.launcher.item;

import nl.matsgemmeke.battlegrounds.MockUtils;
import nl.matsgemmeke.battlegrounds.entity.damage.DamageSource;
import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import nl.matsgemmeke.battlegrounds.item.actor.ItemActor;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.CollisionResultAdapter;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.LaunchContext;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.ProjectileLaunchSource;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerContext;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerExecutor;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerObserver;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerRun;
import nl.matsgemmeke.battlegrounds.item.trigger.result.DamageTargetTriggerResult;
import nl.matsgemmeke.battlegrounds.sound.api.SoundEmitter;
import nl.matsgemmeke.battlegrounds.sound.api.SoundPlayback;
import nl.matsgemmeke.battlegrounds.sound.api.SoundSequence;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemLauncherTest {

    private static final SoundSequence SOUND_SEQUENCE = new SoundSequence(null, null);
    private static final double VELOCITY = 2.0;
    private static final int PICKUP_DELAY = 100;
    private static final ItemStack ITEM_STACK = new ItemStack(Material.STICK);

    private static final String ITEM_NAME = "Test Item";
    private static final Location LAUNCH_DIRECTION = new Location(null, 1, 1, 1);

    @Mock
    private CollisionResultAdapter collisionResultAdapter;
    @Mock
    private DamageSource damageSource;
    @Mock
    private ItemEffect itemEffect;
    @Mock
    private ProjectileLaunchSource projectileSource;
    @Mock
    private SoundEmitter soundEmitter;

    @Test
    @DisplayName("cancel cancels sound playbacks")
    void cancel() {
        Location direction = new Location(null, 0, 0, 0, 100.0f, 0);
        Item item = mock(Item.class);
        SoundPlayback soundPlayback = mock(SoundPlayback.class);

        World world = mock(World.class);
        when(world.dropItem(direction, ITEM_STACK)).thenReturn(item);

        ItemTemplate itemTemplate = mock(ItemTemplate.class);
        when(itemTemplate.createItemStack()).thenReturn(ITEM_STACK);

        ItemLaunchProperties properties = new ItemLaunchProperties(itemTemplate, SOUND_SEQUENCE, VELOCITY, PICKUP_DELAY);
        LaunchContext launchContext = new LaunchContext(ITEM_NAME, damageSource, projectileSource, direction, () -> LAUNCH_DIRECTION, world);

        when(soundEmitter.playSoundSequence(eq(SOUND_SEQUENCE), Mockito.<Supplier<Location>>argThat(supplier -> supplier.get() == LAUNCH_DIRECTION))).thenReturn(soundPlayback);

        ItemLauncher itemLauncher = new ItemLauncher(collisionResultAdapter, soundEmitter, itemEffect, properties);
        itemLauncher.launch(launchContext);
        itemLauncher.cancel();

        verify(soundPlayback).cancel();
    }

    @Test
    @DisplayName("launch drops item and starts triggers that actives the item effect")
    void launch() {
        Item item = mock(Item.class);
        Location direction = new Location(null, 0, 0, 0, 100.0f, 0);
        DamageTargetTriggerResult triggerResult = mock(DamageTargetTriggerResult.class);

        ItemTemplate itemTemplate = mock(ItemTemplate.class);
        when(itemTemplate.createItemStack()).thenReturn(ITEM_STACK);

        World world = mock(World.class);
        when(world.dropItem(direction, ITEM_STACK)).thenReturn(item);

        TriggerRun triggerRun = mock(TriggerRun.class);
        doAnswer(MockUtils.answerNotifyTriggerObserver(triggerResult)).when(triggerRun).addObserver(any(TriggerObserver.class));

        TriggerExecutor triggerExecutor = mock(TriggerExecutor.class);
        when(triggerExecutor.createTriggerRun(any(TriggerContext.class))).thenReturn(triggerRun);

        ItemLaunchProperties properties = new ItemLaunchProperties(itemTemplate, SOUND_SEQUENCE, VELOCITY, PICKUP_DELAY);
        LaunchContext launchContext = new LaunchContext(ITEM_NAME, damageSource, projectileSource, direction, () -> LAUNCH_DIRECTION, world);

        ItemLauncher itemLauncher = new ItemLauncher(collisionResultAdapter, soundEmitter, itemEffect, properties);
        itemLauncher.addTriggerExecutor(triggerExecutor);
        itemLauncher.launch(launchContext);

        ArgumentCaptor<ItemEffectContext> itemEffectContextCaptor = ArgumentCaptor.forClass(ItemEffectContext.class);
        verify(itemEffect).startPerformance(itemEffectContextCaptor.capture());

        assertThat(itemEffectContextCaptor.getValue()).satisfies(itemEffectContext -> {
            assertThat(itemEffectContext.getItemName()).isEqualTo(ITEM_NAME);
            assertThat(itemEffectContext.getActor()).isInstanceOf(ItemActor.class);
            assertThat(itemEffectContext.getDamageSource()).isEqualTo(damageSource);
            assertThat(itemEffectContext.getStartingLocation()).isEqualTo(direction);
        });

        verify(soundEmitter).playSoundSequence(eq(SOUND_SEQUENCE), Mockito.<Supplier<Location>>argThat(supplier -> supplier.get() == LAUNCH_DIRECTION));
        verify(item).setPickupDelay(PICKUP_DELAY);
        verify(item).setVelocity(new Vector(-1.969615506024416,-0.0,-0.3472963553338606));
    }
}
