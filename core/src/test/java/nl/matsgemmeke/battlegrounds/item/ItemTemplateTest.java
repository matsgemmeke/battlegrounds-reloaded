package nl.matsgemmeke.battlegrounds.item;

import nl.matsgemmeke.battlegrounds.text.TextTemplate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Bukkit.class)
public class ItemTemplateTest {

    private ItemFactory itemFactory;
    private Material material;

    @Before
    public void setUp() {
        itemFactory = mock(ItemFactory.class);
        material = Material.IRON_HOE;

        PowerMockito.mockStatic(Bukkit.class);
        when(Bukkit.getItemFactory()).thenReturn(itemFactory);
    }

    @Test
    public void createItemStackWithDamage() {
        int damage = 10;

        Damageable itemMeta = mock(Damageable.class);
        when(itemFactory.getItemMeta(material)).thenReturn(itemMeta);

        ItemTemplate itemTemplate = new ItemTemplate(material);
        itemTemplate.setDamage(damage);

        ItemStack itemStack = itemTemplate.createItemStack(new HashMap<>());

        assertEquals(itemMeta, itemStack.getItemMeta());
        assertEquals(material, itemStack.getType());

        verify(itemMeta).setDamage(damage);
    }

    @Test
    public void createItemStackWithDisplayNamesWithPlaceholders() {
        Map<String, Object> values = Map.of("item", "test item");
        TextTemplate dislayNameTemplate = new TextTemplate("This is a %item%");

        ItemMeta itemMeta = mock(ItemMeta.class);
        when(itemFactory.getItemMeta(material)).thenReturn(itemMeta);

        ItemTemplate itemTemplate = new ItemTemplate(material);
        itemTemplate.setDisplayNameTemplate(dislayNameTemplate);

        ItemStack itemStack = itemTemplate.createItemStack(values);

        assertEquals(material, itemStack.getType());

        verify(itemMeta).setDisplayName("This is a test item");
    }
}
