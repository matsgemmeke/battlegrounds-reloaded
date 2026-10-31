package nl.matsgemmeke.battlegrounds.game.component.controls.handler;

import nl.matsgemmeke.battlegrounds.util.NamespacedKeyCreator;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WeaponNameExtractorTest {

    private static final String WEAPON_NAME = "MP5";

    @Mock
    private NamespacedKeyCreator namespacedKeyCreator;
    @InjectMocks
    private WeaponNameExtractor weaponNameExtractor;

    @BeforeEach
    void setUp() {
        MockBukkit.mock();
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    @DisplayName("extractWeaponName returns empty optional when the given item stack has no item meta")
    void extractWeaponName_itemStackWithoutItemMeta() {
        ItemStack itemStack = new ItemStack(Material.AIR);

        Optional<String> weaponNameOptional = weaponNameExtractor.extractWeaponName(itemStack);

        assertThat(weaponNameOptional).isEmpty();
    }

    @Test
    @DisplayName("extractWeaponName returns empty optional when the given item stack's persistent data container has no value at the weapon name key")
    void extractWeaponName_noWeaponNameKeyValue() {
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);

        Optional<String> weaponNameOptional = weaponNameExtractor.extractWeaponName(itemStack);

        assertThat(weaponNameOptional).isEmpty();
    }

    @Test
    @DisplayName("extractWeaponName returns optional with weapon name value in the given item stack's persistent data container")
    void extractWeaponName_successful() {
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);
        Plugin plugin = MockBukkit.createMockPlugin("Battlegrounds");
        NamespacedKey weaponNameKey = new NamespacedKey(plugin, "weapon-name");

        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.getPersistentDataContainer().set(weaponNameKey, PersistentDataType.STRING, WEAPON_NAME);

        itemStack.setItemMeta(itemMeta);

        when(namespacedKeyCreator.create("weapon-name")).thenReturn(weaponNameKey);

        Optional<String> weaponNameOptional = weaponNameExtractor.extractWeaponName(itemStack);

        assertThat(weaponNameOptional).hasValue(WEAPON_NAME);
    }
}
