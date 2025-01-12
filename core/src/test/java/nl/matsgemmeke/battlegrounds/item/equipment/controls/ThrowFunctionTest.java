package nl.matsgemmeke.battlegrounds.item.equipment.controls;

import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.damage.DamageType;
import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import nl.matsgemmeke.battlegrounds.item.controls.ItemFunctionException;
import nl.matsgemmeke.battlegrounds.item.deploy.DeploymentProperties;
import nl.matsgemmeke.battlegrounds.item.deploy.DroppedItem;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectContext;
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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ThrowFunctionTest {

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
        properties = new ThrowProperties(THROW_SOUNDS, VELOCITY, DELAY_AFTER_THROW);
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

        ItemEffect effect = mock(ItemEffect.class);
        when(equipment.getEffect()).thenReturn(effect);

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

        when(equipment.getEffect()).thenReturn(null);

        ThrowFunction function = new ThrowFunction(audioEmitter, taskRunner, equipment, properties);

        assertThrows(ItemFunctionException.class, () -> function.perform(holder));
    }

    @Test
    public void performThrowsExceptionIfEquipmentHasNoThrowItemTemplate() {
        EquipmentHolder holder = mock(EquipmentHolder.class);
        ItemEffect effect = mock(ItemEffect.class);

        when(equipment.getEffect()).thenReturn(effect);
        when(equipment.getThrowItemTemplate()).thenReturn(null);

        ThrowFunction function = new ThrowFunction(audioEmitter, taskRunner, equipment, properties);

        assertThrows(ItemFunctionException.class, () -> function.perform(holder));
    }

    @Test
    public void performReturnsTrueAndPrimesDroppedItem() {
        double droppedItemHealth = 10.0;
        Location location = new Location(null, 1.0, 1.0, 1.0, 0.0f, 0.0f);

        Item itemEntity = mock(Item.class);
        when(itemEntity.getLocation()).thenReturn(location);

        ItemStack itemStack = new ItemStack(Material.SHEARS);

        World world = mock(World.class);
        when(world.dropItem(location, itemStack)).thenReturn(itemEntity);

        ItemTemplate throwItemTemplate = mock(ItemTemplate.class);
        when(throwItemTemplate.createItemStack()).thenReturn(itemStack);

        when(equipment.getThrowItemTemplate()).thenReturn(throwItemTemplate);

        ItemEffect effect = mock(ItemEffect.class);
        when(effect.isPrimed()).thenReturn(false);

        when(equipment.getEffect()).thenReturn(effect);

        DeploymentProperties deploymentProperties = new DeploymentProperties();
        deploymentProperties.setHealth(droppedItemHealth);

        when(equipment.getDeploymentProperties()).thenReturn(deploymentProperties);

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
        verify(equipment).onDeployDeploymentObject(droppedItemCaptor.capture());

        ArgumentCaptor<ItemEffectContext> contextCaptor = ArgumentCaptor.forClass(ItemEffectContext.class);
        verify(effect).prime(contextCaptor.capture());

        DroppedItem droppedItem = droppedItemCaptor.getValue();
        ItemEffectContext context = contextCaptor.getValue();

        assertTrue(performed);
        assertEquals(location, droppedItem.getLocation());

        verify(effect).prime(context);
        verify(equipment).onDeployDeploymentObject(droppedItem);
        verify(projectileEffect).onLaunch(droppedItem);
        verify(taskRunner).runTaskLater(any(Runnable.class), eq(DELAY_AFTER_THROW));
        verify(world).dropItem(location, itemStack);
    }

    @Test
    public void performReturnsTrueAndDeploysDroppedItem() {
        double droppedItemHealth = 10.0;
        Location location = new Location(null, 1.0, 1.0, 1.0, 0.0f, 0.0f);
        Map<DamageType, Double> resistances = Map.of(DamageType.BULLET_DAMAGE, 0.5);

        Item itemEntity = mock(Item.class);
        when(itemEntity.getLocation()).thenReturn(location);

        ItemStack itemStack = new ItemStack(Material.SHEARS);

        World world = mock(World.class);
        when(world.dropItem(location, itemStack)).thenReturn(itemEntity);

        ItemTemplate throwItemTemplate = mock(ItemTemplate.class);
        when(throwItemTemplate.createItemStack()).thenReturn(itemStack);

        ItemEffect effect = mock(ItemEffect.class);
        when(effect.isPrimed()).thenReturn(true);

        DeploymentProperties deploymentProperties = new DeploymentProperties();
        deploymentProperties.setHealth(droppedItemHealth);
        deploymentProperties.setResistances(resistances);

        ProjectileEffect projectileEffect = mock(ProjectileEffect.class);

        ProjectileProperties projectileProperties = new ProjectileProperties();
        projectileProperties.getEffects().add(projectileEffect);

        when(equipment.getDeploymentProperties()).thenReturn(deploymentProperties);
        when(equipment.getEffect()).thenReturn(effect);
        when(equipment.getProjectileProperties()).thenReturn(projectileProperties);
        when(equipment.getThrowItemTemplate()).thenReturn(throwItemTemplate);

        EquipmentHolder holder = mock(EquipmentHolder.class);
        when(holder.getLocation()).thenReturn(location);
        when(holder.getThrowingDirection()).thenReturn(location);
        when(holder.getWorld()).thenReturn(world);

        ThrowFunction function = new ThrowFunction(audioEmitter, taskRunner, equipment, properties);
        boolean performed = function.perform(holder);

        ArgumentCaptor<DroppedItem> droppedItemCaptor = ArgumentCaptor.forClass(DroppedItem.class);
        verify(equipment).onDeployDeploymentObject(droppedItemCaptor.capture());

        DroppedItem droppedItem = droppedItemCaptor.getValue();

        assertTrue(performed);
        assertEquals(droppedItemHealth, droppedItem.getHealth());
        assertEquals(location, droppedItem.getLocation());
        assertEquals(resistances, droppedItem.getResistances());

        verify(effect).deploy(droppedItem);
        verify(equipment).onDeployDeploymentObject(droppedItem);
        verify(projectileEffect).onLaunch(droppedItem);
        verify(taskRunner).runTaskLater(any(Runnable.class), eq(DELAY_AFTER_THROW));
        verify(world).dropItem(location, itemStack);
    }
}
