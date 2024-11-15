package nl.matsgemmeke.battlegrounds.item;

import nl.matsgemmeke.battlegrounds.text.TextTemplate;
import nl.matsgemmeke.battlegrounds.util.UUIDDataType;
import nl.matsgemmeke.battlegrounds.util.UUIDGenerator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ItemTemplateTest {

    private static final long LEAST_SIG_BITS = -5938845633481916672L;
    private static final long MOST_SIG_BITS = -1081404222592891663L;

    private ItemFactory itemFactory;
    private Material material;
    private MockedStatic<Bukkit> bukkit;
    private NamespacedKey key;
    private UUIDGenerator uuidGenerator;

    @BeforeEach
    public void setUp() {
        itemFactory = mock(ItemFactory.class);
        material = Material.IRON_HOE;

        uuidGenerator = mock(UUIDGenerator.class);
        when(uuidGenerator.generateRandom()).thenReturn(new UUID(MOST_SIG_BITS, LEAST_SIG_BITS));

        Plugin plugin = mock(Plugin.class);
        when(plugin.getName()).thenReturn("battlegrounds");

        key = new NamespacedKey(plugin, "battlegrounds-test");

        bukkit = mockStatic(Bukkit.class);
        bukkit.when(Bukkit::getItemFactory).thenReturn(itemFactory);
    }

    @AfterEach
    public void tearDown() {
        bukkit.close();
    }

    @Test
    public void createItemStackWithoutItemMeta() {
        when(itemFactory.getItemMeta(material)).thenReturn(null);

        ItemTemplate itemTemplate = new ItemTemplate(key, material, uuidGenerator);
        ItemStack itemStack = itemTemplate.createItemStack(new HashMap<>());

        assertEquals(material, itemStack.getType());
        assertNull(itemStack.getItemMeta());
    }

    @Test
    public void createItemStackWithDamage() {
        int damage = 10;

        PersistentDataContainer dataContainer = mock(PersistentDataContainer.class);

        Damageable itemMeta = mock(Damageable.class);
        when(itemMeta.getPersistentDataContainer()).thenReturn(dataContainer);
        when(itemFactory.getItemMeta(material)).thenReturn(itemMeta);

        ItemTemplate itemTemplate = new ItemTemplate(key, material, uuidGenerator);
        itemTemplate.setDamage(damage);

        ItemStack itemStack = itemTemplate.createItemStack(new HashMap<>());

        assertEquals(itemMeta, itemStack.getItemMeta());
        assertEquals(material, itemStack.getType());

        verify(dataContainer).set(eq(key), any(UUIDDataType.class), any(UUID.class));
        verify(itemMeta).setDamage(damage);
    }

    @Test
    public void createItemStackWithDisplayNamesWithPlaceholders() {
        Map<String, Object> values = Map.of("item", "test item");
        TextTemplate dislayNameTemplate = new TextTemplate("&fThis is a %item%");

        PersistentDataContainer dataContainer = mock(PersistentDataContainer.class);

        ItemMeta itemMeta = mock(ItemMeta.class);
        when(itemMeta.getPersistentDataContainer()).thenReturn(dataContainer);
        when(itemFactory.getItemMeta(material)).thenReturn(itemMeta);

        ItemTemplate itemTemplate = new ItemTemplate(key, material, uuidGenerator);
        itemTemplate.setDisplayNameTemplate(dislayNameTemplate);

        ItemStack itemStack = itemTemplate.createItemStack(values);

        assertEquals(material, itemStack.getType());

        verify(dataContainer).set(eq(key), any(UUIDDataType.class), any(UUID.class));
        verify(itemMeta).setDisplayName("§fThis is a test item");
    }

    @Test
    public void matchesWithItemStackIfKeyInItemMetaEqualsUUID() {
        ItemStack itemStack = new ItemStack(material);
        UUID uuid = new UUID(MOST_SIG_BITS, LEAST_SIG_BITS);

        PersistentDataContainer dataContainer = mock(PersistentDataContainer.class);
        when(dataContainer.get(eq(key), any(UUIDDataType.class))).thenReturn(uuid);

        ItemMeta itemMeta = mock(ItemMeta.class);
        when(itemMeta.getPersistentDataContainer()).thenReturn(dataContainer);

        when(itemFactory.getItemMeta(material)).thenReturn(itemMeta);

        ItemTemplate itemTemplate = new ItemTemplate(key, material, uuidGenerator);
        boolean matches = itemTemplate.matchesTemplate(itemStack);

        assertTrue(matches);
    }

    @Test
    public void doesNotMatchWithItemStackIfItemMetaIsNull() {
        ItemStack itemStack = new ItemStack(material);

        when(itemFactory.getItemMeta(material)).thenReturn(null);

        ItemTemplate itemTemplate = new ItemTemplate(key, material, uuidGenerator);
        boolean matches = itemTemplate.matchesTemplate(itemStack);

        assertFalse(matches);
    }

    @Test
    public void doesNotMatchWithIitemStackIfKeyInItemMetaIsNull() {
        ItemStack itemStack = new ItemStack(material);

        PersistentDataContainer dataContainer = mock(PersistentDataContainer.class);
        when(dataContainer.get(eq(key), any(UUIDDataType.class))).thenReturn(null);

        ItemMeta itemMeta = mock(ItemMeta.class);
        when(itemMeta.getPersistentDataContainer()).thenReturn(dataContainer);

        when(itemFactory.getItemMeta(material)).thenReturn(itemMeta);

        ItemTemplate itemTemplate = new ItemTemplate(key, material, uuidGenerator);
        boolean matches = itemTemplate.matchesTemplate(itemStack);

        assertFalse(matches);
    }

    @Test
    public void doesNotMatchWithIitemStackIfValueInKeyDoesNotEqualUUID() {
        ItemStack itemStack = new ItemStack(material);
        UUID uuid = new UUID(MOST_SIG_BITS + 1000, LEAST_SIG_BITS + 1000);

        PersistentDataContainer dataContainer = mock(PersistentDataContainer.class);
        when(dataContainer.get(eq(key), any(UUIDDataType.class))).thenReturn(uuid);

        ItemMeta itemMeta = mock(ItemMeta.class);
        when(itemMeta.getPersistentDataContainer()).thenReturn(dataContainer);

        when(itemFactory.getItemMeta(material)).thenReturn(itemMeta);

        ItemTemplate itemTemplate = new ItemTemplate(key, material, uuidGenerator);
        boolean matches = itemTemplate.matchesTemplate(itemStack);

        assertFalse(matches);
    }
}
