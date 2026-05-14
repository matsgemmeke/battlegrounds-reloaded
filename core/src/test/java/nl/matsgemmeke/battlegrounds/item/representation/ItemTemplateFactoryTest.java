package nl.matsgemmeke.battlegrounds.item.representation;

import nl.matsgemmeke.battlegrounds.configuration.item.DataSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.ItemSpec;
import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import nl.matsgemmeke.battlegrounds.util.NamespacedKeyCreator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
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

    private MockedStatic<Bukkit> bukkit;

    @Mock
    private ItemFactory itemFactory;
    @Mock
    private NamespacedKeyCreator namespacedKeyCreator;
    @Mock
    private Plugin plugin;
    @InjectMocks
    private ItemTemplateFactory itemTemplateFactory;

    @BeforeEach
    void setUp() {
        when(plugin.getName()).thenReturn("Battlegrounds");

        NamespacedKey key = new NamespacedKey(plugin, "template-id");

        when(namespacedKeyCreator.create("template-id")).thenReturn(key);

        bukkit = mockStatic(Bukkit.class);
        bukkit.when(Bukkit::getItemFactory).thenReturn(itemFactory);
    }

    @AfterEach
    void tearDown() {
        bukkit.close();
    }

    @Test
    @DisplayName("create throws ItemTemplateDefinitionException when given spec has integer data with invalid value")
    void create_invalidIntegerData() {
        DataSpec integerDataSpec = new DataSpec();
        integerDataSpec.key = "integer-data";
        integerDataSpec.type = "INTEGER";
        integerDataSpec.value = "invalid";

        ItemSpec spec = new ItemSpec();
        spec.material = "STICK";
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
        spec.material = "STICK";
        spec.data = Map.of("invalid-data", invalidDataSpec);
        spec.unbreakable = true;

        assertThatThrownBy(() -> itemTemplateFactory.create(spec))
                .isInstanceOf(ItemTemplateDefinitionException.class)
                .hasMessage("Unsupported data type: INVALID");
    }

    @Test
    @DisplayName("create returns ItemTemplate instance with ItemSpec properties")
    void create_returnsItemTemplate() {
        PersistentDataContainer persistentDataContainer = mock(PersistentDataContainer.class);
        NamespacedKey stringDataKey = new NamespacedKey(plugin, "string-data");
        NamespacedKey integerDataKey = new NamespacedKey(plugin, "integer-data");

        Damageable itemMeta = mock(Damageable.class);
        when(itemMeta.getPersistentDataContainer()).thenReturn(persistentDataContainer);

        DataSpec stringDataSpec = new DataSpec();
        stringDataSpec.key = "string-data";
        stringDataSpec.type = "STRING";
        stringDataSpec.value = "test";

        DataSpec integerDataSpec = new DataSpec();
        integerDataSpec.key = "integer-data";
        integerDataSpec.type = "INTEGER";
        integerDataSpec.value = "5";

        ItemSpec spec = new ItemSpec();
        spec.material = "STICK";
        spec.displayName = DISPLAY_NAME;
        spec.damage = DAMAGE;
        spec.itemFlags = List.of("HIDE_ATTRIBUTES");
        spec.data = Map.of("string-data", stringDataSpec, "integer-data", integerDataSpec);
        spec.unbreakable = true;

        when(itemFactory.getItemMeta(Material.STICK)).thenReturn(itemMeta);
        when(namespacedKeyCreator.create("string-data")).thenReturn(stringDataKey);
        when(namespacedKeyCreator.create("integer-data")).thenReturn(integerDataKey);

        ItemTemplate itemTemplate = itemTemplateFactory.create(spec);
        ItemStack itemStack = itemTemplate.createItemStack();

        assertThat(itemStack.getType()).isEqualTo(Material.STICK);
        assertThat(itemStack.getItemMeta()).isEqualTo(itemMeta);

        verify(itemMeta).setDamage(DAMAGE);
        verify(itemMeta).setDisplayName(DISPLAY_NAME);
        verify(itemMeta).addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        verify(itemMeta).setUnbreakable(true);
        verify(persistentDataContainer).set(stringDataKey, PersistentDataType.STRING, "test");
        verify(persistentDataContainer).set(integerDataKey, PersistentDataType.INTEGER, 5);
    }
}
