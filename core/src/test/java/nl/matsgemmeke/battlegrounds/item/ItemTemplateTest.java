package nl.matsgemmeke.battlegrounds.item;

import nl.matsgemmeke.battlegrounds.text.TextTemplate;
import nl.matsgemmeke.battlegrounds.util.UUIDDataType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ItemTemplateTest {

    private ItemFactory itemFactory;
    private Material material;
    private MockedStatic<Bukkit> bukkit;
    private NamespacedKey key;
    private Plugin plugin;
    private UUID uuid;

    @BeforeEach
    public void setUp() {
        itemFactory = mock(ItemFactory.class);
        material = Material.IRON_HOE;
        uuid = UUID.randomUUID();

        plugin = mock(Plugin.class);
        when(plugin.getName()).thenReturn("battlegrounds");

        key = new NamespacedKey(plugin, "test-key");

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

        ItemTemplate itemTemplate = new ItemTemplate(uuid, key, material);
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

        ItemTemplate itemTemplate = new ItemTemplate(uuid, key, material);
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

        ItemTemplate itemTemplate = new ItemTemplate(uuid, key, material);
        itemTemplate.setDisplayNameTemplate(dislayNameTemplate);

        ItemStack itemStack = itemTemplate.createItemStack(values);

        assertEquals(material, itemStack.getType());

        verify(dataContainer).set(eq(key), any(UUIDDataType.class), any(UUID.class));
        verify(itemMeta).setDisplayName("§fThis is a test item");
    }

    @Test
    public void createItemStackWithDataEntries() {
        PersistentDataContainer persistentDataContainer = mock(PersistentDataContainer.class);
        NamespacedKey namespacedKey = new NamespacedKey(plugin, "my-text-value");
        PersistentDataEntry<String, String> dataEntry = new PersistentDataEntry<>(namespacedKey, PersistentDataType.STRING, "a cool text");

        ItemMeta itemMeta = mock(ItemMeta.class);
        when(itemMeta.getPersistentDataContainer()).thenReturn(persistentDataContainer);

        when(itemFactory.getItemMeta(material)).thenReturn(itemMeta);

        ItemTemplate itemTemplate = new ItemTemplate(uuid, key, material);
        itemTemplate.addPersistentDataEntry(dataEntry);
        ItemStack itemStack = itemTemplate.createItemStack();

        assertThat(itemStack.getItemMeta()).isEqualTo(itemMeta);
        assertThat(itemStack.getType()).isEqualTo(material);

        verify(persistentDataContainer).set(namespacedKey, PersistentDataType.STRING, "a cool text");
    }

    @Test
    public void matchesWithItemStackIfKeyInItemMetaEqualsUUID() {
        ItemStack itemStack = new ItemStack(material);

        PersistentDataContainer dataContainer = mock(PersistentDataContainer.class);
        when(dataContainer.get(eq(key), any(UUIDDataType.class))).thenReturn(uuid);

        ItemMeta itemMeta = mock(ItemMeta.class);
        when(itemMeta.getPersistentDataContainer()).thenReturn(dataContainer);

        when(itemFactory.getItemMeta(material)).thenReturn(itemMeta);

        ItemTemplate itemTemplate = new ItemTemplate(uuid, key, material);
        boolean matches = itemTemplate.matchesTemplate(itemStack);

        assertTrue(matches);
    }

    @Test
    public void doesNotMatchWithItemStackIfItemMetaIsNull() {
        ItemStack itemStack = new ItemStack(material);

        when(itemFactory.getItemMeta(material)).thenReturn(null);

        ItemTemplate itemTemplate = new ItemTemplate(uuid, key, material);
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

        ItemTemplate itemTemplate = new ItemTemplate(uuid, key, material);
        boolean matches = itemTemplate.matchesTemplate(itemStack);

        assertFalse(matches);
    }

    @Test
    public void doesNotMatchWithIitemStackIfValueInKeyDoesNotEqualUUID() {
        ItemStack itemStack = new ItemStack(material);
        UUID otherUUID = UUID.randomUUID();

        PersistentDataContainer dataContainer = mock(PersistentDataContainer.class);
        when(dataContainer.get(eq(key), any(UUIDDataType.class))).thenReturn(otherUUID);

        ItemMeta itemMeta = mock(ItemMeta.class);
        when(itemMeta.getPersistentDataContainer()).thenReturn(dataContainer);

        when(itemFactory.getItemMeta(material)).thenReturn(itemMeta);

        ItemTemplate itemTemplate = new ItemTemplate(uuid, key, material);
        boolean matches = itemTemplate.matchesTemplate(itemStack);

        assertFalse(matches);
    }
}
