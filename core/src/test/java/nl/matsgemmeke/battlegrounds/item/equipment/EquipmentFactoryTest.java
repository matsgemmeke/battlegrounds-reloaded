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
        when(rootSection.getString("display-name")).thenReturn("name");
        when(rootSection.getString("description")).thenReturn("description");
        when(rootSection.getShort("item.durability")).thenReturn((short) 1);
        when(rootSection.getString("item.material")).thenReturn("FLINT_AND_STEEL");

        when(configuration.getRoot()).thenReturn(rootSection);

        PowerMockito.mockStatic(Bukkit.class);
        Mockito.when(Bukkit.getItemFactory()).thenReturn(itemFactory);
    }

    @Test
    public void shouldCreateSimpleEquipmentItem() {
        Damageable itemMeta = mock(Damageable.class);
        when(itemFactory.getItemMeta(Material.FLINT_AND_STEEL)).thenReturn(itemMeta);

        ItemRegistry<Equipment, EquipmentHolder> registry = (ItemRegistry<Equipment, EquipmentHolder>) mock(ItemRegistry.class);
        when(context.getEquipmentRegistry()).thenReturn(registry);

        EquipmentFactory factory = new EquipmentFactory(mechanismFactory, mechanismActivationFactory, taskRunner);
        Equipment equipment = factory.make(configuration, context);

        assertEquals("name", equipment.getName());
        assertEquals("description", equipment.getDescription());
        assertEquals(Material.FLINT_AND_STEEL, equipment.getItemStack().getType());

        verify(itemMeta).setDamage((short) 1);
        verify(registry).registerItem(equipment);
    }

    @Test
    public void shouldCreateEquipmentItemWithThrowControls() {
        Section controlsSection = mock(Section.class);
        when(controlsSection.getString("throw")).thenReturn("LEFT_CLICK");

        when(rootSection.getSection("controls")).thenReturn(controlsSection);
        when(rootSection.getString("throwing.throw-sound")).thenReturn("AMBIENT_CAVE-1-1-1");

        ItemRegistry<Equipment, EquipmentHolder> registry = (ItemRegistry<Equipment, EquipmentHolder>) mock(ItemRegistry.class);
        when(context.getEquipmentRegistry()).thenReturn(registry);

        ItemMechanism mechanism = mock(ItemMechanism.class);
        ItemMechanismActivation activation = mock(ItemMechanismActivation.class);
        GamePlayer gamePlayer = mock(GamePlayer.class);

        when(mechanismFactory.make(any())).thenReturn(mechanism);
        when(mechanismActivationFactory.make(any(), any(), eq(mechanism))).thenReturn(activation);

        EquipmentFactory factory = new EquipmentFactory(mechanismFactory, mechanismActivationFactory, taskRunner);
        Equipment equipment = factory.make(configuration, context, gamePlayer);

        assertNotNull(equipment);
        assertTrue(equipment instanceof DefaultEquipment);

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

    @Test
    public void shouldCreateEquipmentItemWithCookControls() {
        Section controlsSection = mock(Section.class);
        when(controlsSection.getString("cook")).thenReturn("RIGHT_CLICK");
        when(controlsSection.getString("throw")).thenReturn("LEFT_CLICK");

        when(rootSection.getSection("controls")).thenReturn(controlsSection);

        ItemMechanism mechanism = mock(ItemMechanism.class);
        ItemMechanismActivation activation = mock(ItemMechanismActivation.class);
        GamePlayer gamePlayer = mock(GamePlayer.class);

        ItemRegistry<Equipment, EquipmentHolder> registry = (ItemRegistry<Equipment, EquipmentHolder>) mock(ItemRegistry.class);
        when(context.getEquipmentRegistry()).thenReturn(registry);

        when(mechanismFactory.make(any())).thenReturn(mechanism);
        when(mechanismActivationFactory.make(any(), any(), eq(mechanism))).thenReturn(activation);

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
}
