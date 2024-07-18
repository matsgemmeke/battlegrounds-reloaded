package nl.matsgemmeke.battlegrounds.item.equipment;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import nl.matsgemmeke.battlegrounds.TaskRunner;
import nl.matsgemmeke.battlegrounds.configuration.ItemConfiguration;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.Game;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.item.ItemStorage;
import nl.matsgemmeke.battlegrounds.item.mechanism.ItemMechanism;
import nl.matsgemmeke.battlegrounds.item.mechanism.ItemMechanismFactory;
import nl.matsgemmeke.battlegrounds.item.mechanism.activation.ItemMechanismActivation;
import nl.matsgemmeke.battlegrounds.item.mechanism.activation.ItemMechanismActivationFactory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
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

    private ItemMechanismActivationFactory mechanismActivationFactory;
    private ItemMechanismFactory mechanismFactory;
    private Game game;
    private GameContext context;
    private ItemConfiguration configuration;
    private ItemFactory itemFactory;
    private Section rootSection;
    private TaskRunner taskRunner;

    @Before
    public void setUp() {
        mechanismActivationFactory = mock(ItemMechanismActivationFactory.class);
        mechanismFactory = mock(ItemMechanismFactory.class);
        game = mock(Game.class);
        context = mock(GameContext.class);
        configuration = mock(ItemConfiguration.class);
        itemFactory = mock(ItemFactory.class);
        taskRunner = mock(TaskRunner.class);

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

        ItemStorage<Equipment, EquipmentHolder> storage = (ItemStorage<Equipment, EquipmentHolder>) mock(ItemStorage.class);
        when(game.getEquipmentStorage()).thenReturn(storage);

        EquipmentFactory factory = new EquipmentFactory(mechanismFactory, mechanismActivationFactory, taskRunner);
        Equipment equipment = factory.make(configuration, game, context);

        assertEquals("name", equipment.getName());
        assertEquals("description", equipment.getDescription());
        assertEquals(Material.FLINT_AND_STEEL, equipment.getItemStack().getType());

        verify(itemMeta).setDamage((short) 1);
        verify(storage).addUnassignedItem(equipment);
    }

    @Test
    public void shouldCreateEquipmentItemWithThrowControls() {
        Section controlsSection = mock(Section.class);
        when(controlsSection.getString("throw")).thenReturn("LEFT_CLICK");

        when(rootSection.getSection("controls")).thenReturn(controlsSection);
        when(rootSection.getString("throwing.throw-sound")).thenReturn("AMBIENT_CAVE-1-1-1");

        ItemStorage<Equipment, EquipmentHolder> storage = (ItemStorage<Equipment, EquipmentHolder>) mock(ItemStorage.class);
        when(game.getEquipmentStorage()).thenReturn(storage);

        ItemMechanism mechanism = mock(ItemMechanism.class);
        ItemMechanismActivation activation = mock(ItemMechanismActivation.class);
        GamePlayer gamePlayer = mock(GamePlayer.class);

        when(mechanismFactory.make(any(), eq(context))).thenReturn(mechanism);
        when(mechanismActivationFactory.make(any(), any(), eq(mechanism))).thenReturn(activation);

        EquipmentFactory factory = new EquipmentFactory(mechanismFactory, mechanismActivationFactory, taskRunner);
        Equipment equipment = factory.make(configuration, game, context, gamePlayer);

        assertNotNull(equipment);
        assertTrue(equipment instanceof DefaultEquipment);

        verify(storage).addAssignedItem(equipment, gamePlayer);
    }

    @Test(expected = CreateEquipmentException.class)
    public void shouldThrowErrorWhenThrowActionConfigurationValueIsInvalid() {
        Section controlsSection = mock(Section.class);
        when(controlsSection.getString("throw")).thenReturn("fail");

        when(rootSection.getSection("controls")).thenReturn(controlsSection);

        EquipmentFactory factory = new EquipmentFactory(mechanismFactory, mechanismActivationFactory, taskRunner);
        factory.make(configuration, game, context);
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

        ItemStorage<Equipment, EquipmentHolder> storage = (ItemStorage<Equipment, EquipmentHolder>) mock(ItemStorage.class);
        when(game.getEquipmentStorage()).thenReturn(storage);

        when(mechanismFactory.make(any(), eq(context))).thenReturn(mechanism);
        when(mechanismActivationFactory.make(any(), any(), eq(mechanism))).thenReturn(activation);

        EquipmentFactory factory = new EquipmentFactory(mechanismFactory, mechanismActivationFactory, taskRunner);
        Equipment equipment = factory.make(configuration, game, context, gamePlayer);

        assertNotNull(equipment);
        assertTrue(equipment instanceof DefaultEquipment);
    }

    @Test(expected = CreateEquipmentException.class)
    public void shouldThrowErrorWhenCookActionConfigurationValueIsInvalid() {
        Section controlsSection = mock(Section.class);
        when(controlsSection.getString("cook")).thenReturn("fail");
        when(controlsSection.getString("throw")).thenReturn("LEFT_CLICK");

        when(rootSection.getSection("controls")).thenReturn(controlsSection);

        EquipmentFactory factory = new EquipmentFactory(mechanismFactory, mechanismActivationFactory, taskRunner);
        factory.make(configuration, game, context);
    }
}
