package nl.matsgemmeke.battlegrounds.item;

import net.kyori.adventure.text.TextComponent;
import nl.matsgemmeke.battlegrounds.i18n.TextTemplate;
import nl.matsgemmeke.battlegrounds.util.UUIDDataType;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ItemTemplateTest {

    private static final UUID TEMPLATE_ID = UUID.randomUUID();
    private static final int DAMAGE = 10;

    @Mock
    private Plugin plugin;

    private NamespacedKey templateKey;

    @BeforeEach
    void setUp() {
        MockBukkit.mock();

        plugin = MockBukkit.createMockPlugin("Battlegrounds");
        templateKey = new NamespacedKey(plugin, "test-key");
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    @DisplayName("createItemStack returns ItemStack without ItemMeta when given material does not have item metas")
    void createItemStack_withoutItemMeta() {
        ItemTemplate itemTemplate = ItemTemplate.builder(templateKey, TEMPLATE_ID, Material.AIR).build();
        ItemStack itemStack = itemTemplate.createItemStack(new HashMap<>());

        assertThat(itemStack.getType()).isEqualTo(Material.AIR);
        assertThat(itemStack.getItemMeta()).isNull();
    }

    @Test
    @DisplayName("createItemStack returns ItemStack with damage when ItemMeta is damageable")
    void createItemStack_withDamage() {
        ItemTemplate itemTemplate = ItemTemplate.builder(templateKey, TEMPLATE_ID, Material.IRON_HOE)
                .damage(DAMAGE)
                .build();
        ItemStack itemStack = itemTemplate.createItemStack(new HashMap<>());

        assertThat(itemStack.getItemMeta()).isInstanceOfSatisfying(Damageable.class, itemMeta -> {
            assertThat(itemMeta.getDamage()).isEqualTo(DAMAGE);
            assertThat(itemMeta.getPersistentDataContainer()).satisfies(pdc -> {
                assertThat(pdc.get(templateKey, UUIDDataType.getInstance())).isEqualTo(TEMPLATE_ID);
            });
        });
        assertThat(itemStack.getType()).isEqualTo(Material.IRON_HOE);
    }

    @Test
    @DisplayName("createItemStack returns ItemStack with placeholders in the display name")
    void createItemStack_withDisplayNamePlaceholders() {
        Map<String, Object> values = Map.of("item", "test item");
        TextTemplate displayNameTemplate = new TextTemplate("&fThis is a %item%");

        ItemTemplate itemTemplate = ItemTemplate.builder(templateKey, TEMPLATE_ID, Material.IRON_HOE)
                .displayNameTemplate(displayNameTemplate)
                .build();
        ItemStack itemStack = itemTemplate.createItemStack(values);

        assertThat(itemStack.getType()).isEqualTo(Material.IRON_HOE);
        assertThat(itemStack.getItemMeta()).satisfies(itemMeta -> {
            assertThat(itemMeta.displayName()).isInstanceOfSatisfying(TextComponent.class, textComponent -> {
                assertThat(textComponent.content()).isEqualTo("This is a test item");
            });
            assertThat(itemMeta.getPersistentDataContainer()).satisfies(pdc -> {
                assertThat(pdc.get(templateKey, UUIDDataType.getInstance())).isEqualTo(TEMPLATE_ID);
            });
        });
    }

    @Test
    @DisplayName("createItemStack returns ItemStack with persistent data entries")
    void createItemStack_withDataEntries() {
        NamespacedKey namespacedKey = new NamespacedKey(plugin, "my-text-value");
        PersistentDataEntry<String, String> dataEntry = new PersistentDataEntry<>(namespacedKey, PersistentDataType.STRING, "a cool text");

        ItemTemplate itemTemplate = ItemTemplate.builder(templateKey, TEMPLATE_ID, Material.IRON_HOE)
                .dataEntries(Set.of(dataEntry))
                .build();
        ItemStack itemStack = itemTemplate.createItemStack();

        assertThat(itemStack.getType()).isEqualTo(Material.IRON_HOE);
        assertThat(itemStack.getItemMeta()).satisfies(itemMeta -> {
            assertThat(itemMeta.getPersistentDataContainer()).satisfies(pdc -> {
                assertThat(pdc.get(namespacedKey, PersistentDataType.STRING)).isEqualTo("a cool text");
            });
        });
    }

    @Test
    @DisplayName("matchesTemplate returns false when ItemMeta is null")
    void matchesTemplate_itemMetaIsNull() {
        ItemStack itemStack = new ItemStack(Material.AIR);

        ItemTemplate itemTemplate = ItemTemplate.builder(templateKey, TEMPLATE_ID, Material.IRON_HOE).build();
        boolean matches = itemTemplate.matchesTemplate(itemStack);

        assertThat(matches).isFalse();
    }

    @Test
    @DisplayName("matchesTemplate returns false when key in ItemMeta is null")
    void matchesTemplate_keyInItemMetaIsNull() {
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);

        ItemTemplate itemTemplate = ItemTemplate.builder(templateKey, TEMPLATE_ID, Material.IRON_HOE).build();
        boolean matches = itemTemplate.matchesTemplate(itemStack);

        assertThat(matches).isFalse();
    }

    @Test
    @DisplayName("matchesTemplate returns false when value in ItemMeta key does not equal template id")
    void matchesTemplate_valueInKeyDoesNotEqualTemplateId() {
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);
        UUID otherTemplateId = UUID.randomUUID();

        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.getPersistentDataContainer().set(templateKey, UUIDDataType.getInstance(), otherTemplateId);

        itemStack.setItemMeta(itemMeta);

        ItemTemplate itemTemplate = ItemTemplate.builder(templateKey, TEMPLATE_ID, Material.IRON_HOE).build();
        boolean matches = itemTemplate.matchesTemplate(itemStack);

        assertThat(matches).isFalse();
    }

    @Test
    @DisplayName("matchesTemplate returns true when key in ItemMeta equals template id")
    void matchesTemplate_keyInItemMetaEqualsTemplateId() {
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);

        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.getPersistentDataContainer().set(templateKey, UUIDDataType.getInstance(), TEMPLATE_ID);

        itemStack.setItemMeta(itemMeta);

        ItemTemplate itemTemplate = ItemTemplate.builder(templateKey, TEMPLATE_ID, Material.IRON_HOE).build();
        boolean matches = itemTemplate.matchesTemplate(itemStack);

        assertThat(matches).isTrue();
    }
}
