package nl.matsgemmeke.battlegrounds.item.equipment;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.configuration.ItemConfiguration;
import nl.matsgemmeke.battlegrounds.entity.GameItem;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.EntityRegistry;
import nl.matsgemmeke.battlegrounds.game.component.ItemRegistry;
import nl.matsgemmeke.battlegrounds.item.mechanism.ItemMechanism;
import nl.matsgemmeke.battlegrounds.item.mechanism.ItemMechanismFactory;
import nl.matsgemmeke.battlegrounds.item.mechanism.activation.ItemMechanismActivation;
import nl.matsgemmeke.battlegrounds.item.mechanism.activation.ItemMechanismActivationFactory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.meta.Damageable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.when;

@SuppressWarnings("unchecked")
@RunWith(PowerMockRunner.class)
@PrepareForTest(Bukkit.class)
public class EquipmentFactoryTest {

    private AudioEmitter audioEmitter;
    private GameContext context;
    private ItemConfiguration configuration;
    private ItemFactory itemFactory;
    private ItemMechanismActivationFactory mechanismActivationFactory;
    private ItemMechanismFactory mechanismFactory;
    private Section rootSection;
    private TaskRunner taskRunner;

    @Before
    public void setUp() {
        audioEmitter = mock(AudioEmitter.class);
        mechanismActivationFactory = mock(ItemMechanismActivationFactory.class);
        mechanismFactory = mock(ItemMechanismFactory.class);
        configuration = mock(ItemConfiguration.class);
        itemFactory = mock(ItemFactory.class);
        taskRunner = mock(TaskRunner.class);

        EntityRegistry<GameItem, Item> itemRegistry = (EntityRegistry<GameItem, Item>) mock(EntityRegistry.class);

        context = mock(GameContext.class);
        when(context.getAudioEmitter()).thenReturn(audioEmitter);
        when(context.getItemRegistry()).thenReturn(itemRegistry);

        rootSection = mock(Section.class);
        when(rootSection.getString("name")).thenReturn("name");
        when(rootSection.getString("description")).thenReturn("description");
        when(rootSection.getInt("item.damage")).thenReturn(1);
        when(rootSection.getString("item.material")).thenReturn("SHEARS");

        when(configuration.getRoot()).thenReturn(rootSection);

        PowerMockito.mockStatic(Bukkit.class);
        Mockito.when(Bukkit.getItemFactory()).thenReturn(itemFactory);
    }

    @Test
    public void shouldCreateSimpleEquipmentItem() {
        Damageable itemMeta = mock(Damageable.class);
        when(itemFactory.getItemMeta(Material.SHEARS)).thenReturn(itemMeta);

        ItemRegistry<Equipment, EquipmentHolder> registry = (ItemRegistry<Equipment, EquipmentHolder>) mock(ItemRegistry.class);
        when(context.getEquipmentRegistry()).thenReturn(registry);

        EquipmentFactory factory = new EquipmentFactory(mechanismFactory, mechanismActivationFactory, taskRunner);
        Equipment equipment = factory.make(configuration, context);

        assertTrue(equipment instanceof DefaultEquipment);
        assertEquals("name", equipment.getName());
        assertEquals("description", equipment.getDescription());
        assertEquals(Material.SHEARS, equipment.getItemStack().getType());

        verify(itemMeta).setDamage(1);
        verify(registry).registerItem(equipment);
    }

    @Test
    public void createEquipmentItemWithDisplayName() {
        when(rootSection.getString("item.display-name")).thenReturn("&f%name%");

        ItemRegistry<Equipment, EquipmentHolder> registry = (ItemRegistry<Equipment, EquipmentHolder>) mock(ItemRegistry.class);
        when(context.getEquipmentRegistry()).thenReturn(registry);

        EquipmentFactory factory = new EquipmentFactory(mechanismFactory, mechanismActivationFactory, taskRunner);
        Equipment equipment = factory.make(configuration, context);

        assertTrue(equipment instanceof DefaultEquipment);
        assertEquals("§f%name%", ((DefaultEquipment) equipment).getDisplayNameTemplate().getText());
    }

    @Test(expected = CreateEquipmentException.class)
    public void shouldThrowExceptionWhenCreatingEquipmentItemWithInvalidMaterial() {
        when(rootSection.getString("item.material")).thenReturn("fail");

        EquipmentFactory factory = new EquipmentFactory(mechanismFactory, mechanismActivationFactory, taskRunner);
        factory.make(configuration, context);
    }

    @Test
    public void shouldCreateEquipmentItemWithActivatorItem() {
        int damage = 1;

        Damageable itemMeta = mock(Damageable.class);
        when(itemFactory.getItemMeta(Material.FLINT)).thenReturn(itemMeta);

        ItemRegistry<Equipment, EquipmentHolder> registry = (ItemRegistry<Equipment, EquipmentHolder>) mock(ItemRegistry.class);
        when(context.getEquipmentRegistry()).thenReturn(registry);

        Section activatorItemSection = mock(Section.class);
        when(activatorItemSection.getInt("damage")).thenReturn(damage);
        when(activatorItemSection.getString("display-name")).thenReturn("&fActivator");
        when(activatorItemSection.getString("material")).thenReturn("FLINT");

        when(rootSection.getSection("item.activator")).thenReturn(activatorItemSection);

        EquipmentFactory factory = new EquipmentFactory(mechanismFactory, mechanismActivationFactory, taskRunner);
        Equipment equipment = factory.make(configuration, context);

        assertEquals(Material.FLINT, equipment.getActivatorItemStack().getType());

        verify(itemMeta).setDamage(damage);
        verify(itemMeta).setDisplayName("§fActivator");
        verify(registry).registerItem(equipment);
    }

    @Test(expected = CreateEquipmentException.class)
    public void throwExceptionWhenCreatingEquipmentItemWithInvalidActivatorMaterial() {
        Section activatorItemSection = mock(Section.class);
        when(activatorItemSection.getString("material")).thenReturn("fail");
        when(rootSection.getSection("item.activator")).thenReturn(activatorItemSection);

        EquipmentFactory factory = new EquipmentFactory(mechanismFactory, mechanismActivationFactory, taskRunner);
        factory.make(configuration, context);
    }

    @Test
    public void shouldCreateEquipmentItemWithThrowControls() {
        Section controlsSection = mock(Section.class);
        when(controlsSection.getString("throw")).thenReturn("LEFT_CLICK");

        when(rootSection.getSection("controls")).thenReturn(controlsSection);
        when(rootSection.getInt("item.throw-item.damage")).thenReturn(1);
        when(rootSection.getString("item.throw-item.material")).thenReturn("FLINT");
        when(rootSection.getString("throwing.throw-sound")).thenReturn("AMBIENT_CAVE-1-1-1");

        ItemRegistry<Equipment, EquipmentHolder> registry = (ItemRegistry<Equipment, EquipmentHolder>) mock(ItemRegistry.class);
        when(context.getEquipmentRegistry()).thenReturn(registry);

        Damageable itemMeta = mock(Damageable.class);
        ItemMechanism mechanism = mock(ItemMechanism.class);
        ItemMechanismActivation activation = mock(ItemMechanismActivation.class);
        GamePlayer gamePlayer = mock(GamePlayer.class);

        when(itemFactory.getItemMeta(Material.FLINT)).thenReturn(itemMeta);
        when(mechanismFactory.make(any(), eq(context))).thenReturn(mechanism);
        when(mechanismActivationFactory.make(eq(context), eq(mechanism), any())).thenReturn(activation);

        EquipmentFactory factory = new EquipmentFactory(mechanismFactory, mechanismActivationFactory, taskRunner);
        Equipment equipment = factory.make(configuration, context, gamePlayer);

        assertNotNull(equipment);
        assertTrue(equipment instanceof DefaultEquipment);

        verify(itemMeta).setDamage(1);
        verify(registry).registerItem(equipment, gamePlayer);
    }

    @Test(expected = CreateEquipmentException.class)
    public void shouldThrowErrorWhenThrowActionConfigurationValueIsInvalid() {
        Section controlsSection = mock(Section.class);
        when(controlsSection.getString("throw")).thenReturn("fail");

        when(rootSection.getSection("controls")).thenReturn(controlsSection);

        EquipmentFactory factory = new EquipmentFactory(mechanismFactory, mechanismActivationFactory, taskRunner);
        factory.make(configuration, context);
    }

    @Test(expected = CreateEquipmentException.class)
    public void shouldThrowErrorWhenThrowItemMaterialConfigurationValueIsInvalid() {
        Section controlsSection = mock(Section.class);
        when(controlsSection.getString("throw")).thenReturn("LEFT_CLICK");

        when(rootSection.getSection("controls")).thenReturn(controlsSection);
        when(rootSection.getString("item.throw-item.material")).thenReturn("fail");

        EquipmentFactory factory = new EquipmentFactory(mechanismFactory, mechanismActivationFactory, taskRunner);
        factory.make(configuration, context);
    }

    @Test
    public void shouldCreateEquipmentItemWithCookControls() {
        Section controlsSection = mock(Section.class);
        when(controlsSection.getString("cook")).thenReturn("RIGHT_CLICK");
        when(controlsSection.getString("throw")).thenReturn("LEFT_CLICK");

        when(rootSection.getSection("controls")).thenReturn(controlsSection);
        when(rootSection.getString("item.throw-item.material")).thenReturn("SHEARS");

        ItemMechanism mechanism = mock(ItemMechanism.class);
        ItemMechanismActivation activation = mock(ItemMechanismActivation.class);
        GamePlayer gamePlayer = mock(GamePlayer.class);

        ItemRegistry<Equipment, EquipmentHolder> registry = (ItemRegistry<Equipment, EquipmentHolder>) mock(ItemRegistry.class);
        when(context.getEquipmentRegistry()).thenReturn(registry);

        when(mechanismFactory.make(any(), eq(context))).thenReturn(mechanism);
        when(mechanismActivationFactory.make(eq(context), eq(mechanism), any())).thenReturn(activation);

        EquipmentFactory factory = new EquipmentFactory(mechanismFactory, mechanismActivationFactory, taskRunner);
        Equipment equipment = factory.make(configuration, context, gamePlayer);

        assertNotNull(equipment);
        assertTrue(equipment instanceof DefaultEquipment);

        verify(registry).registerItem(equipment, gamePlayer);
    }

    @Test(expected = CreateEquipmentException.class)
    public void shouldThrowErrorWhenCookActionConfigurationValueIsInvalid() {
        Section controlsSection = mock(Section.class);
        when(controlsSection.getString("cook")).thenReturn("fail");
        when(controlsSection.getString("throw")).thenReturn("LEFT_CLICK");

        when(rootSection.getSection("controls")).thenReturn(controlsSection);

        EquipmentFactory factory = new EquipmentFactory(mechanismFactory, mechanismActivationFactory, taskRunner);
        factory.make(configuration, context);
    }

    @Test
    public void shouldCreateEquipmentItemWithPlaceControls() {
        Section controlsSection = mock(Section.class);
        when(controlsSection.getString("place")).thenReturn("RIGHT_CLICK");

        when(rootSection.getSection("controls")).thenReturn(controlsSection);
        when(rootSection.getString("placing.material")).thenReturn("WARPED_BUTTON");

        ItemMechanism mechanism = mock(ItemMechanism.class);
        ItemMechanismActivation activation = mock(ItemMechanismActivation.class);
        GamePlayer gamePlayer = mock(GamePlayer.class);

        ItemRegistry<Equipment, EquipmentHolder> registry = (ItemRegistry<Equipment, EquipmentHolder>) mock(ItemRegistry.class);
        when(context.getEquipmentRegistry()).thenReturn(registry);

        when(mechanismFactory.make(any(), eq(context))).thenReturn(mechanism);
        when(mechanismActivationFactory.make(eq(context), eq(mechanism), any())).thenReturn(activation);

        EquipmentFactory factory = new EquipmentFactory(mechanismFactory, mechanismActivationFactory, taskRunner);
        Equipment equipment = factory.make(configuration, context, gamePlayer);

        assertNotNull(equipment);
        assertTrue(equipment instanceof DefaultEquipment);

        verify(registry).registerItem(equipment, gamePlayer);
    }

    @Test(expected = CreateEquipmentException.class)
    public void shouldThrowErrorWhenPlaceActionConfigurationValueIsInvalid() {
        Section controlsSection = mock(Section.class);
        when(controlsSection.getString("place")).thenReturn("fail");

        when(rootSection.getSection("controls")).thenReturn(controlsSection);

        EquipmentFactory factory = new EquipmentFactory(mechanismFactory, mechanismActivationFactory, taskRunner);
        factory.make(configuration, context);
    }

    @Test(expected = CreateEquipmentException.class)
    public void shouldThrowErrorWhenPlacingMaterialConfigurationValueIsInvalid() {
        Section controlsSection = mock(Section.class);
        when(controlsSection.getString("place")).thenReturn("RIGHT_CLICK");

        when(rootSection.getSection("controls")).thenReturn(controlsSection);
        when(rootSection.getString("placing.material")).thenReturn("fail");

        EquipmentFactory factory = new EquipmentFactory(mechanismFactory, mechanismActivationFactory, taskRunner);
        factory.make(configuration, context);
    }

    @Test
    public void shouldCreateEquipmentItemWithActivateControls() {
        Section controlsSection = mock(Section.class);
        when(controlsSection.getString("activate")).thenReturn("RIGHT_CLICK");

        when(rootSection.getSection("controls")).thenReturn(controlsSection);

        ItemMechanism mechanism = mock(ItemMechanism.class);
        ItemMechanismActivation activation = mock(ItemMechanismActivation.class);
        GamePlayer gamePlayer = mock(GamePlayer.class);

        ItemRegistry<Equipment, EquipmentHolder> registry = (ItemRegistry<Equipment, EquipmentHolder>) mock(ItemRegistry.class);
        when(context.getEquipmentRegistry()).thenReturn(registry);

        when(mechanismFactory.make(any(), eq(context))).thenReturn(mechanism);
        when(mechanismActivationFactory.make(eq(context), eq(mechanism), any())).thenReturn(activation);

        EquipmentFactory factory = new EquipmentFactory(mechanismFactory, mechanismActivationFactory, taskRunner);
        Equipment equipment = factory.make(configuration, context, gamePlayer);

        assertNotNull(equipment);
        assertTrue(equipment instanceof DefaultEquipment);

        verify(registry).registerItem(equipment, gamePlayer);
    }

    @Test(expected = CreateEquipmentException.class)
    public void shouldThrowErrorWhenActivateActionConfigurationValueIsInvalid() {
        Section controlsSection = mock(Section.class);
        when(controlsSection.getString("activate")).thenReturn("fail");

        when(rootSection.getSection("controls")).thenReturn(controlsSection);

        EquipmentFactory factory = new EquipmentFactory(mechanismFactory, mechanismActivationFactory, taskRunner);
        factory.make(configuration, context);
    }
}
