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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemTemplateTest {

    private static final UUID TEMPLATE_ID = UUID.randomUUID();
    private static final Material MATERIAL = Material.IRON_HOE;
    private static final int DAMAGE = 10;

    @Mock
    private ItemFactory itemFactory;
    @Mock
    private Plugin plugin;

    private MockedStatic<Bukkit> bukkit;
    private NamespacedKey templateKey;

    @BeforeEach
    void setUp() {
        itemFactory = mock(ItemFactory.class);

        plugin = mock(Plugin.class);
        when(plugin.getName()).thenReturn("battlegrounds");

        templateKey = new NamespacedKey(plugin, "test-key");

        bukkit = mockStatic(Bukkit.class);
        bukkit.when(Bukkit::getItemFactory).thenReturn(itemFactory);
    }

    @AfterEach
    void tearDown() {
        bukkit.close();
    }

    @Test
    @DisplayName("createItemStack returns ItemStack without ItemMeta when given material does not have item metas")
    void createItemStack_withoutItemMeta() {
        when(itemFactory.getItemMeta(MATERIAL)).thenReturn(null);

        ItemTemplate itemTemplate = ItemTemplate.builder(templateKey, TEMPLATE_ID, MATERIAL).build();
        ItemStack itemStack = itemTemplate.createItemStack(new HashMap<>());

        assertThat(itemStack.getType()).isEqualTo(MATERIAL);
        assertThat(itemStack.getItemMeta()).isNull();
    }

    @Test
    @DisplayName("createItemStack returns ItemStack with damage when ItemMeta is damageable")
    void createItemStack_withDamage() {
        PersistentDataContainer dataContainer = mock(PersistentDataContainer.class);

        Damageable itemMeta = mock(Damageable.class);
        when(itemMeta.getPersistentDataContainer()).thenReturn(dataContainer);
        when(itemFactory.getItemMeta(MATERIAL)).thenReturn(itemMeta);

        ItemTemplate itemTemplate = ItemTemplate.builder(templateKey, TEMPLATE_ID, MATERIAL)
                .damage(DAMAGE)
                .build();
        ItemStack itemStack = itemTemplate.createItemStack(new HashMap<>());

        assertThat(itemStack.getItemMeta()).isEqualTo(itemMeta);
        assertThat(itemStack.getType()).isEqualTo(MATERIAL);

        verify(dataContainer).set(eq(templateKey), any(UUIDDataType.class), any(UUID.class));
        verify(itemMeta).setDamage(DAMAGE);
    }

    @Test
    @DisplayName("createItemStack returns ItemStack with placeholders in the display name")
    void createItemStack_withDisplayNamePlaceholders() {
        Map<String, Object> values = Map.of("item", "test item");
        TextTemplate displayNameTemplate = new TextTemplate("&fThis is a %item%");

        PersistentDataContainer dataContainer = mock(PersistentDataContainer.class);

        ItemMeta itemMeta = mock(ItemMeta.class);
        when(itemMeta.getPersistentDataContainer()).thenReturn(dataContainer);
        when(itemFactory.getItemMeta(MATERIAL)).thenReturn(itemMeta);

        ItemTemplate itemTemplate = ItemTemplate.builder(templateKey, TEMPLATE_ID, MATERIAL)
                .displayNameTemplate(displayNameTemplate)
                .build();
        ItemStack itemStack = itemTemplate.createItemStack(values);

        assertThat(itemStack.getType()).isEqualTo(MATERIAL);

        verify(dataContainer).set(eq(templateKey), any(UUIDDataType.class), any(UUID.class));
        verify(itemMeta).setDisplayName("§fThis is a test item");
    }

    @Test
    @DisplayName("createItemStack returns ItemStack with persistent data entries")
    void createItemStack_withDataEntries() {
        PersistentDataContainer persistentDataContainer = mock(PersistentDataContainer.class);
        NamespacedKey namespacedKey = new NamespacedKey(plugin, "my-text-value");
        PersistentDataEntry<String, String> dataEntry = new PersistentDataEntry<>(namespacedKey, PersistentDataType.STRING, "a cool text");

        ItemMeta itemMeta = mock(ItemMeta.class);
        when(itemMeta.getPersistentDataContainer()).thenReturn(persistentDataContainer);

        when(itemFactory.getItemMeta(MATERIAL)).thenReturn(itemMeta);

        ItemTemplate itemTemplate = ItemTemplate.builder(templateKey, TEMPLATE_ID, MATERIAL)
                .dataEntries(Set.of(dataEntry))
                .build();
        ItemStack itemStack = itemTemplate.createItemStack();

        assertThat(itemStack.getItemMeta()).isEqualTo(itemMeta);
        assertThat(itemStack.getType()).isEqualTo(MATERIAL);

        verify(persistentDataContainer).set(namespacedKey, PersistentDataType.STRING, "a cool text");
    }

    @Test
    @DisplayName("matchesTemplate returns true when key in ItemMeta equals template id")
    void matchesTemplate_keyInItemMetaEqualsTemplateId() {
        ItemStack itemStack = new ItemStack(MATERIAL);

        PersistentDataContainer dataContainer = mock(PersistentDataContainer.class);
        when(dataContainer.get(eq(templateKey), any(UUIDDataType.class))).thenReturn(TEMPLATE_ID);

        ItemMeta itemMeta = mock(ItemMeta.class);
        when(itemMeta.getPersistentDataContainer()).thenReturn(dataContainer);

        when(itemFactory.getItemMeta(MATERIAL)).thenReturn(itemMeta);

        ItemTemplate itemTemplate = ItemTemplate.builder(templateKey, TEMPLATE_ID, MATERIAL).build();
        boolean matches = itemTemplate.matchesTemplate(itemStack);

        assertThat(matches).isTrue();
    }

    @Test
    @DisplayName("matchesTemplate returns false when ItemMeta is null")
    void matchesTemplate_itemMetaIsNull() {
        ItemStack itemStack = new ItemStack(MATERIAL);

        when(itemFactory.getItemMeta(MATERIAL)).thenReturn(null);

        ItemTemplate itemTemplate = ItemTemplate.builder(templateKey, TEMPLATE_ID, MATERIAL).build();
        boolean matches = itemTemplate.matchesTemplate(itemStack);

        assertThat(matches).isFalse();
    }

    @Test
    @DisplayName("matchesTemplate returns false when key in ItemMeta is null")
    void matchesTemplate_keyInItemMetaIsNull() {
        ItemStack itemStack = new ItemStack(MATERIAL);

        PersistentDataContainer dataContainer = mock(PersistentDataContainer.class);
        when(dataContainer.get(eq(templateKey), any(UUIDDataType.class))).thenReturn(null);

        ItemMeta itemMeta = mock(ItemMeta.class);
        when(itemMeta.getPersistentDataContainer()).thenReturn(dataContainer);

        when(itemFactory.getItemMeta(MATERIAL)).thenReturn(itemMeta);

        ItemTemplate itemTemplate = ItemTemplate.builder(templateKey, TEMPLATE_ID, MATERIAL).build();
        boolean matches = itemTemplate.matchesTemplate(itemStack);

        assertThat(matches).isFalse();
    }

    @Test
    @DisplayName("matchesTemplate returns false when value in ItemMeta key does not equal template id")
    void matchesTemplate_valueInKeyDoesNotEqualTemplateId() {
        ItemStack itemStack = new ItemStack(MATERIAL);
        UUID otherTemplateId = UUID.randomUUID();

        PersistentDataContainer dataContainer = mock(PersistentDataContainer.class);
        when(dataContainer.get(eq(templateKey), any(UUIDDataType.class))).thenReturn(otherTemplateId);

        ItemMeta itemMeta = mock(ItemMeta.class);
        when(itemMeta.getPersistentDataContainer()).thenReturn(dataContainer);

        when(itemFactory.getItemMeta(MATERIAL)).thenReturn(itemMeta);

        ItemTemplate itemTemplate = ItemTemplate.builder(templateKey, TEMPLATE_ID, MATERIAL).build();
        boolean matches = itemTemplate.matchesTemplate(itemStack);

        assertThat(matches).isFalse();
    }
}
