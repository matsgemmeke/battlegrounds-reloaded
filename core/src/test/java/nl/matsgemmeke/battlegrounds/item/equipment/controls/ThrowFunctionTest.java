package nl.matsgemmeke.battlegrounds.item.equipment.controls;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import nl.matsgemmeke.battlegrounds.item.controls.ItemFunctionException;
import nl.matsgemmeke.battlegrounds.item.effect.activation.ItemEffectActivation;
import nl.matsgemmeke.battlegrounds.item.effect.source.DroppedItem;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentHolder;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.ProjectileEffect;
import nl.matsgemmeke.battlegrounds.item.projectile.ProjectileProperties;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ThrowFunctionTest {

    private static final double HEALTH = 50.0;
    private static final double VELOCITY = 2.0;
    private static final Iterable<GameSound> THROW_SOUNDS = Collections.emptySet();
    private static final long DELAY_AFTER_THROW = 1L;

    private AudioEmitter audioEmitter;
    private Equipment equipment;
    private TaskRunner taskRunner;
    private ThrowProperties properties;

    @BeforeEach
    public void setUp() {
        audioEmitter = mock(AudioEmitter.class);
        equipment = mock(Equipment.class);
        taskRunner = mock(TaskRunner.class);
        properties = new ThrowProperties(THROW_SOUNDS, HEALTH, VELOCITY, DELAY_AFTER_THROW);
    }

    @Test
    public void shouldNotBePerformingIfNoThrowsWereExecuted() {
        ThrowFunction function = new ThrowFunction(audioEmitter, taskRunner, equipment, properties);
        boolean performing = function.isPerforming();

        assertFalse(performing);
    }

    @Test
    public void shouldBePerformingIfThrowsWereRecentlyExecuted() {
        ItemStack itemStack = new ItemStack(Material.SHEARS);

        ItemTemplate throwItemTemplate = mock(ItemTemplate.class);
        when(throwItemTemplate.createItemStack()).thenReturn(itemStack);

        when(equipment.getThrowItemTemplate()).thenReturn(throwItemTemplate);

        ItemEffectActivation effectActivation = mock(ItemEffectActivation.class);
        when(equipment.getEffectActivation()).thenReturn(effectActivation);

        World world = mock(World.class);
        when(world.dropItem(any(Location.class), eq(itemStack))).thenReturn(mock(Item.class));

        Location throwingDirection = new Location(world, 1.0, 1.0, 1.0);

        EquipmentHolder holder = mock(EquipmentHolder.class);
        when(holder.getThrowingDirection()).thenReturn(throwingDirection);
        when(holder.getWorld()).thenReturn(world);

        ThrowFunction function = new ThrowFunction(audioEmitter, taskRunner, equipment, properties);
        function.perform(holder);

        boolean performing = function.isPerforming();

        assertTrue(performing);
    }

    @Test
    public void performThrowsExceptionIfEquipmentHasNoEffectActivation() {
        EquipmentHolder holder = mock(EquipmentHolder.class);

        when(equipment.getEffectActivation()).thenReturn(null);

        ThrowFunction function = new ThrowFunction(audioEmitter, taskRunner, equipment, properties);

        assertThrows(ItemFunctionException.class, () -> function.perform(holder));
    }

    @Test
    public void performThrowsExceptionIfEquipmentHasNoThrowItemTemplate() {
        EquipmentHolder holder = mock(EquipmentHolder.class);
        ItemEffectActivation effectActivation = mock(ItemEffectActivation.class);

        when(equipment.getEffectActivation()).thenReturn(effectActivation);
        when(equipment.getThrowItemTemplate()).thenReturn(null);

        ThrowFunction function = new ThrowFunction(audioEmitter, taskRunner, equipment, properties);

        assertThrows(ItemFunctionException.class, () -> function.perform(holder));
    }

    @Test
    public void performReturnsTrueAndPrimesDroppedItem() {
        Location location = new Location(null, 1.0, 1.0, 1.0, 0.0f, 0.0f);

        Item itemEntity = mock(Item.class);
        when(itemEntity.getLocation()).thenReturn(location);

        ItemStack itemStack = new ItemStack(Material.SHEARS);

        World world = mock(World.class);
        when(world.dropItem(location, itemStack)).thenReturn(itemEntity);

        ItemTemplate throwItemTemplate = mock(ItemTemplate.class);
        when(throwItemTemplate.createItemStack()).thenReturn(itemStack);

        when(equipment.getThrowItemTemplate()).thenReturn(throwItemTemplate);

        ItemEffectActivation effectActivation = mock(ItemEffectActivation.class);
        when(equipment.getEffectActivation()).thenReturn(effectActivation);

        ProjectileEffect projectileEffect = mock(ProjectileEffect.class);

        ProjectileProperties projectileProperties = new ProjectileProperties();
        projectileProperties.getEffects().add(projectileEffect);

        when(equipment.getProjectileProperties()).thenReturn(projectileProperties);

        EquipmentHolder holder = mock(EquipmentHolder.class);
        when(holder.getLocation()).thenReturn(location);
        when(holder.getThrowingDirection()).thenReturn(location);
        when(holder.getWorld()).thenReturn(world);

        ThrowFunction function = new ThrowFunction(audioEmitter, taskRunner, equipment, properties);
        boolean performed = function.perform(holder);

        ArgumentCaptor<DroppedItem> droppedItemCaptor = ArgumentCaptor.forClass(DroppedItem.class);
        verify(effectActivation).prime(eq(holder), droppedItemCaptor.capture());

        DroppedItem droppedItem = droppedItemCaptor.getValue();

        assertTrue(performed);
        assertEquals(HEALTH, droppedItem.getHealth());
        assertEquals(location, droppedItem.getLocation());

        verify(equipment).addDeploymentObject(droppedItem);
        verify(projectileEffect).onLaunch(droppedItem);
        verify(taskRunner).runTaskLater(any(Runnable.class), eq(DELAY_AFTER_THROW));
        verify(world).dropItem(location, itemStack);
    }
}
