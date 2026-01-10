package nl.matsgemmeke.battlegrounds.item.melee;

import nl.matsgemmeke.battlegrounds.configuration.item.melee.ControlsSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.melee.MeleeWeaponSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.SpecDeserializer;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.component.item.MeleeWeaponRegistry;
import nl.matsgemmeke.battlegrounds.item.controls.ItemControls;
import nl.matsgemmeke.battlegrounds.item.melee.controls.MeleeWeaponControlsFactory;
import nl.matsgemmeke.battlegrounds.util.NamespacedKeyCreator;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
class MeleeWeaponFactoryTest {

    @Mock
    private MeleeWeaponControlsFactory controlsFactory;
    @Mock
    private MeleeWeaponRegistry meleeWeaponRegistry;
    @Mock
    private NamespacedKeyCreator namespacedKeyCreator;
    @InjectMocks
    private MeleeWeaponFactory meleeWeaponFactory;

    private MockedStatic<Bukkit> bukkit;

    @BeforeEach
    void setUp() {
        Plugin plugin = mock(Plugin.class);
        when(plugin.getName()).thenReturn("Battlegrounds");

        ItemFactory itemFactory = mock(ItemFactory.class);
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
    void createReturnsMeleeWeaponWithoutAssignedHolder() {
        MeleeWeaponSpec spec = this.createMeleeWeaponSpec();

        MeleeWeapon meleeWeapon = meleeWeaponFactory.create(spec);

        assertThat(meleeWeapon.getName()).isEqualTo("Combat Knife");
        assertThat(meleeWeapon.getDescription()).isEqualTo("Standard issue military knife. Fast, quiet and deadly.");
        assertThat(meleeWeapon.getHolder()).isEmpty();
        assertThat(meleeWeapon.getAttackDamage()).isEqualTo(75.0);

        verify(meleeWeaponRegistry).register(meleeWeapon);
    }

    @Test
    void createReturnsMeleeWeaponWithControls() {
        ItemControls<MeleeWeaponHolder> controls = new ItemControls<>();

        ControlsSpec controlsSpec = new ControlsSpec();
        controlsSpec.throwing = "RIGHT_CLICK";

        MeleeWeaponSpec spec = this.createMeleeWeaponSpec();
        spec.controls = controlsSpec;

        when(controlsFactory.create(eq(controlsSpec), any(MeleeWeapon.class))).thenReturn(controls);

        MeleeWeapon meleeWeapon = meleeWeaponFactory.create(spec);

        assertThat(meleeWeapon.getName()).isEqualTo("Combat Knife");
        assertThat(meleeWeapon.getDescription()).isEqualTo("Standard issue military knife. Fast, quiet and deadly.");
        assertThat(meleeWeapon.getHolder()).isEmpty();
        assertThat(meleeWeapon.getAttackDamage()).isEqualTo(75.0);

        verify(meleeWeaponRegistry).register(meleeWeapon);
    }

    @Test
    void createReturnsMeleeWeaponWithAssignedHolder() {
        MeleeWeaponSpec spec = this.createMeleeWeaponSpec();
        GamePlayer gamePlayer = mock(GamePlayer.class);

        MeleeWeapon meleeWeapon = meleeWeaponFactory.create(spec, gamePlayer);

        assertThat(meleeWeapon.getName()).isEqualTo("Combat Knife");
        assertThat(meleeWeapon.getDescription()).isEqualTo("Standard issue military knife. Fast, quiet and deadly.");
        assertThat(meleeWeapon.getHolder()).hasValue(gamePlayer);
        assertThat(meleeWeapon.getAttackDamage()).isEqualTo(75.0);

        verify(meleeWeaponRegistry).register(meleeWeapon, gamePlayer);
    }

    private MeleeWeaponSpec createMeleeWeaponSpec() {
        File file = new File("src/main/resources/items/melee_weapons/combat_knife.yml");

        SpecDeserializer specDeserializer = new SpecDeserializer();
        return specDeserializer.deserializeSpec(file, MeleeWeaponSpec.class);
    }
}
