package nl.matsgemmeke.battlegrounds.item.representation;

import nl.matsgemmeke.battlegrounds.configuration.item.DataSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.ItemSpec;
import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import nl.matsgemmeke.battlegrounds.util.NamespacedKeyCreator;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemTemplateFactoryTest {

    private static final String DISPLAY_NAME = "My test item";
    private static final int DAMAGE = 3;

    private Plugin plugin;

    @Mock
    private ItemFactory itemFactory;
    @Mock
    private NamespacedKeyCreator namespacedKeyCreator;
    @InjectMocks
    private ItemTemplateFactory itemTemplateFactory;

    @BeforeEach
    void setUp() {
        MockBukkit.mock();
        plugin = MockBukkit.createMockPlugin("Battlegrounds");

        NamespacedKey key = new NamespacedKey(plugin, "template-id");

        when(namespacedKeyCreator.create("template-id")).thenReturn(key);
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    @DisplayName("create throws ItemTemplateDefinitionException when given spec has integer data with invalid value")
    void create_invalidIntegerData() {
        DataSpec integerDataSpec = new DataSpec();
        integerDataSpec.key = "integer-data";
        integerDataSpec.type = "INTEGER";
        integerDataSpec.value = "invalid";

        ItemSpec spec = new ItemSpec();
        spec.material = "IRON_HOE";
        spec.data = Map.of("integer-data", integerDataSpec);
        spec.unbreakable = true;

        assertThatThrownBy(() -> itemTemplateFactory.create(spec))
                .isInstanceOf(ItemTemplateDefinitionException.class)
                .hasMessage("Data entry was defined as an integer, but the value \"invalid\" is invalid");
    }

    @Test
    @DisplayName("create throws ItemTemplateDefinitionException when given spec has data with invalid type")
    void create_invalidDataType() {
        DataSpec invalidDataSpec = new DataSpec();
        invalidDataSpec.key = "invalid-data";
        invalidDataSpec.type = "INVALID";
        invalidDataSpec.value = "invalid";

        ItemSpec spec = new ItemSpec();
        spec.material = "IRON_HOE";
        spec.data = Map.of("invalid-data", invalidDataSpec);
        spec.unbreakable = true;

        assertThatThrownBy(() -> itemTemplateFactory.create(spec))
                .isInstanceOf(ItemTemplateDefinitionException.class)
                .hasMessage("Unsupported data type: INVALID");
    }

    @Test
    @DisplayName("create returns ItemTemplate instance with ItemSpec properties")
    void create_returnsItemTemplate() {
        NamespacedKey stringDataKey = new NamespacedKey(plugin, "string-data");
        NamespacedKey integerDataKey = new NamespacedKey(plugin, "integer-data");

        DataSpec stringDataSpec = new DataSpec();
        stringDataSpec.key = "string-data";
        stringDataSpec.type = "STRING";
        stringDataSpec.value = "test";

        DataSpec integerDataSpec = new DataSpec();
        integerDataSpec.key = "integer-data";
        integerDataSpec.type = "INTEGER";
        integerDataSpec.value = "5";

        ItemSpec spec = new ItemSpec();
        spec.material = "IRON_HOE";
        spec.displayName = DISPLAY_NAME;
        spec.damage = DAMAGE;
        spec.itemFlags = List.of("HIDE_ATTRIBUTES");
        spec.data = Map.of("string-data", stringDataSpec, "integer-data", integerDataSpec);
        spec.unbreakable = true;

        when(namespacedKeyCreator.create("string-data")).thenReturn(stringDataKey);
        when(namespacedKeyCreator.create("integer-data")).thenReturn(integerDataKey);

        ItemTemplate itemTemplate = itemTemplateFactory.create(spec);
        ItemStack itemStack = itemTemplate.createItemStack();

        assertThat(itemStack.getType()).isEqualTo(Material.IRON_HOE);
        assertThat(itemStack.getItemMeta()).isInstanceOfSatisfying(Damageable.class, itemMeta -> {
            assertThat(itemMeta.getDamage()).isEqualTo(DAMAGE);
            assertThat(itemMeta.getDisplayName()).isEqualTo(DISPLAY_NAME);
            assertThat(itemMeta.getItemFlags()).containsExactly(ItemFlag.HIDE_ATTRIBUTES);
            assertThat(itemMeta.isUnbreakable()).isTrue();
            assertThat(itemMeta.getPersistentDataContainer()).satisfies(persistentDataContainer -> {
                assertThat(persistentDataContainer.get(stringDataKey, PersistentDataType.STRING)).isEqualTo("test");
                assertThat(persistentDataContainer.get(integerDataKey, PersistentDataType.INTEGER)).isEqualTo(5);
            });
        });
    }
}
