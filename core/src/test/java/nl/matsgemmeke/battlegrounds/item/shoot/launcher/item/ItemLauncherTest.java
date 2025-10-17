package nl.matsgemmeke.battlegrounds.item.shoot.launcher.item;

import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
import nl.matsgemmeke.battlegrounds.item.effect.StaticSource;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.LaunchContext;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.ProjectileLaunchSource;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerContext;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerExecutor;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerObserver;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerRun;
import nl.matsgemmeke.battlegrounds.scheduling.Scheduler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
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

    @Mock
    private AudioEmitter audioEmitter;
    @Mock
    private ItemEffect itemEffect;
    @Mock
    private Scheduler scheduler;

    @Test
    void launchDropItemAndStartTriggerRunsThatActivateItemEffect() {
        Item item = mock(Item.class);

        ItemTemplate itemTemplate = mock(ItemTemplate.class);
        when(itemTemplate.createItemStack()).thenReturn(ITEM_STACK);

        Entity entity = mock(Entity.class);
        ProjectileLaunchSource launchSource = mock(ProjectileLaunchSource.class);
        Location direction = new Location(null, 0, 0, 0, 100.0f, 0);

        World world = mock(World.class);
        when(world.dropItem(direction, ITEM_STACK)).thenReturn(item);

        TriggerRun triggerRun = mock(TriggerRun.class);
        doAnswer(invocation -> {
            TriggerObserver observer = invocation.getArgument(0);
            observer.onActivate();
            return null;
        }).when(triggerRun).addObserver(any(TriggerObserver.class));

        TriggerExecutor triggerExecutor = mock(TriggerExecutor.class);
        when(triggerExecutor.createTriggerRun(any(TriggerContext.class))).thenReturn(triggerRun);

        ItemLaunchProperties properties = new ItemLaunchProperties(itemTemplate, List.of(), VELOCITY);
        LaunchContext launchContext = new LaunchContext(entity, launchSource, direction, world);

        ItemLauncher itemLauncher = new ItemLauncher(audioEmitter, scheduler, itemEffect, properties);
        itemLauncher.addTriggerExecutor(triggerExecutor);
        itemLauncher.launch(launchContext);

        ArgumentCaptor<ItemEffectContext> effectContextCaptor = ArgumentCaptor.forClass(ItemEffectContext.class);
        verify(itemEffect).startPerformance(effectContextCaptor.capture());

        ItemEffectContext effectContext = effectContextCaptor.getValue();
        assertThat(effectContext.getEntity()).isEqualTo(entity);
        assertThat(effectContext.getSource()).isInstanceOf(StaticSource.class);
        assertThat(effectContext.getInitiationLocation()).isEqualTo(direction);

//        verify(audioEmitter).playSounds(properties.shotSounds(), direction);
        verify(item).setPickupDelay(10000);
        verify(item).setVelocity(new Vector(-1.969615506024416,-0.0,-0.3472963553338606));
    }
}
