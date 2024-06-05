package nl.matsgemmeke.battlegrounds.item.equipment;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import nl.matsgemmeke.battlegrounds.configuration.ItemConfiguration;
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

    @Before
    public void setUp() {
        game = mock(Game.class);
        context = mock(GameContext.class);
        configuration = mock(ItemConfiguration.class);
        itemFactory = mock(ItemFactory.class);

        PowerMockito.mockStatic(Bukkit.class);
        Mockito.when(Bukkit.getItemFactory()).thenReturn(itemFactory);
    }

    @Test
    public void shouldCreateSimpleEquipmentItem() {
        Section section = mock(Section.class);
        when(section.getString("display-name")).thenReturn("name");
        when(section.getString("description")).thenReturn("description");
        when(section.getShort("item.durability")).thenReturn((short) 1);
        when(section.getString("item.material")).thenReturn("GUNPOWDER");

        when(configuration.getRoot()).thenReturn(section);

        Damageable itemMeta = mock(Damageable.class);
        when(itemFactory.getItemMeta(Material.GUNPOWDER)).thenReturn(itemMeta);

        ItemRegister<Equipment, EquipmentHolder> register = (ItemRegister<Equipment, EquipmentHolder>) mock(ItemRegister.class);
        when(game.getEquipmentRegister()).thenReturn(register);

        EquipmentFactory equipmentFactory = new EquipmentFactory();
        Equipment equipment = equipmentFactory.make(configuration, game, context);

        assertEquals("name", equipment.getName());
        assertEquals("description", equipment.getDescription());
        assertEquals(Material.GUNPOWDER, equipment.getItemStack().getType());

        verify(itemMeta).setDamage((short) 1);
        verify(register).addUnassignedItem(equipment);
    }
}
