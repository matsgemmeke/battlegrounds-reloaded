package nl.matsgemmeke.battlegrounds.item.representation;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemTemplateFactoryTest {

    private static final String ACTION_EXECUTOR_ID_VALUE = "gun";
    private static final String DISPLAY_NAME = "My test item";
    private static final int DAMAGE = 3;

    private MockedStatic<Bukkit> bukkit;

    @Mock
    private ItemFactory itemFactory;
    @Mock
    private NamespacedKeyCreator namespacedKeyCreator;
    @InjectMocks
    private ItemTemplateFactory itemTemplateFactory;

    @BeforeEach
    void setUp() {
        Plugin plugin = mock(Plugin.class);
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
    @DisplayName("create returns ItemTemplate instance with ItemSpec properties")
    void create_returnsItemTemplate() {
        PersistentDataContainer persistentDataContainer = mock(PersistentDataContainer.class);

        Damageable itemMeta = mock(Damageable.class);
        when(itemMeta.getPersistentDataContainer()).thenReturn(persistentDataContainer);

        ItemSpec spec = new ItemSpec();
        spec.material = "STICK";
        spec.displayName = DISPLAY_NAME;
        spec.damage = DAMAGE;
        spec.itemFlags = List.of("HIDE_ATTRIBUTES");
        spec.unbreakable = true;

        when(itemFactory.getItemMeta(Material.STICK)).thenReturn(itemMeta);

        ItemTemplate itemTemplate = itemTemplateFactory.create(spec, ACTION_EXECUTOR_ID_VALUE);
        ItemStack itemStack = itemTemplate.createItemStack();

        assertThat(itemStack.getType()).isEqualTo(Material.STICK);
        assertThat(itemStack.getItemMeta()).isEqualTo(itemMeta);

        verify(itemMeta).setDamage(DAMAGE);
        verify(itemMeta).setDisplayName(DISPLAY_NAME);
        verify(itemMeta).addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        verify(itemMeta).setUnbreakable(true);
    }
}
