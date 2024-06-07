package nl.matsgemmeke.battlegrounds.item.equipment;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import nl.matsgemmeke.battlegrounds.configuration.ItemConfiguration;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.Game;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.item.ItemRegister;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.when;

@SuppressWarnings("unchecked")
@RunWith(PowerMockRunner.class)
@PrepareForTest(Bukkit.class)
public class EquipmentFactoryTest {

    private Game game;
    private GameContext context;
    private ItemConfiguration configuration;
    private ItemFactory itemFactory;
    private Section rootSection;

    @Before
    public void setUp() {
        game = mock(Game.class);
        context = mock(GameContext.class);
        configuration = mock(ItemConfiguration.class);
        itemFactory = mock(ItemFactory.class);

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

        ItemRegister<Equipment, EquipmentHolder> register = (ItemRegister<Equipment, EquipmentHolder>) mock(ItemRegister.class);
        when(game.getEquipmentRegister()).thenReturn(register);

        EquipmentFactory equipmentFactory = new EquipmentFactory();
        Equipment equipment = equipmentFactory.make(configuration, game, context);

        assertEquals("name", equipment.getName());
        assertEquals("description", equipment.getDescription());
        assertEquals(Material.FLINT_AND_STEEL, equipment.getItemStack().getType());

        verify(itemMeta).setDamage((short) 1);
        verify(register).addUnassignedItem(equipment);
    }

    @Test
    public void shouldCreateEquipmentItemWithThrowControls() {
        Section controlsSection = mock(Section.class);
        when(controlsSection.getString("throw")).thenReturn("LEFT_CLICK");

        when(rootSection.getSection("controls")).thenReturn(controlsSection);
        when(rootSection.getString("throwing.sound")).thenReturn("AMBIENT_CAVE-1-1-1");

        ItemRegister<Equipment, EquipmentHolder> register = (ItemRegister<Equipment, EquipmentHolder>) mock(ItemRegister.class);
        when(game.getEquipmentRegister()).thenReturn(register);

        GamePlayer gamePlayer = mock(GamePlayer.class);

        EquipmentFactory factory = new EquipmentFactory();
        Equipment equipment = factory.make(configuration, game, context, gamePlayer);

        assertNotNull(equipment);

        verify(register).addAssignedItem(equipment, gamePlayer);
    }

    @Test(expected = CreateEquipmentException.class)
    public void shouldThrowErrorWhenThrowActionConfigurationValueIsInvalid() {
        Section controlsSection = mock(Section.class);
        when(controlsSection.getString("throw")).thenReturn("fail");

        when(rootSection.getSection("controls")).thenReturn(controlsSection);

        EquipmentFactory factory = new EquipmentFactory();
        factory.make(configuration, game, context);
    }
}
