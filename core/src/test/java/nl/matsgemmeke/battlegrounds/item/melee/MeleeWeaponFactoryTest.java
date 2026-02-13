package nl.matsgemmeke.battlegrounds.item.melee;

import nl.matsgemmeke.battlegrounds.configuration.item.melee.ControlsSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.melee.MeleeWeaponSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.SpecDeserializer;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.component.item.MeleeWeaponRegistry;
import nl.matsgemmeke.battlegrounds.item.controls.ItemControls;
import nl.matsgemmeke.battlegrounds.item.melee.controls.MeleeWeaponControlsFactory;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadSystem;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadSystemFactory;
import nl.matsgemmeke.battlegrounds.item.reload.ResourceContainer;
import nl.matsgemmeke.battlegrounds.item.representation.ItemRepresentation;
import nl.matsgemmeke.battlegrounds.item.throwing.ThrowHandler;
import nl.matsgemmeke.battlegrounds.item.throwing.ThrowHandlerFactory;
import nl.matsgemmeke.battlegrounds.util.NamespacedKeyCreator;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFactory;
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
    @Mock
    private ReloadSystemFactory reloadSystemFactory;
    @Mock
    private ThrowHandlerFactory throwHandlerFactory;
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
    @DisplayName("create returns MeleeWeapon without assigned holder")
    void create_withoutHolder() {
        MeleeWeaponSpec spec = this.createMeleeWeaponSpec("combat_knife");
        ReloadSystem reloadSystem = mock(ReloadSystem.class);

        when(reloadSystemFactory.create(eq(spec.reloading), any(ResourceContainer.class))).thenReturn(reloadSystem);

        MeleeWeapon result = meleeWeaponFactory.create(spec);

        assertThat(result).isInstanceOfSatisfying(DefaultMeleeWeapon.class, meleeWeapon -> {
            assertThat(meleeWeapon.getName()).isEqualTo("Combat Knife");
            assertThat(meleeWeapon.getDescription()).isEqualTo("Standard issue military knife. Fast, quiet and deadly.");
            assertThat(meleeWeapon.getHolder()).isEmpty();
            assertThat(meleeWeapon.getAttackDamage()).isEqualTo(75.0);
            assertThat(meleeWeapon.getReloadSystem()).isEqualTo(reloadSystem);
            assertThat(meleeWeapon.getResourceContainer()).satisfies(resourceContainer -> {
               assertThat(resourceContainer.getCapacity()).isEqualTo(1);
               assertThat(resourceContainer.getLoadedAmount()).isEqualTo(1);
               assertThat(resourceContainer.getReserveAmount()).isZero();
               assertThat(resourceContainer.getMaxReserveAmount()).isZero();
            });
        });

        verify(meleeWeaponRegistry).register(result);
    }

    @Test
    @DisplayName("create returns MeleeWeapon with controls")
    void create_withControls() {
        ItemControls<MeleeWeaponHolder> controls = new ItemControls<>();
        ReloadSystem reloadSystem = mock(ReloadSystem.class);

        ControlsSpec controlsSpec = new ControlsSpec();
        controlsSpec.throwing = "RIGHT_CLICK";

        MeleeWeaponSpec spec = this.createMeleeWeaponSpec("combat_knife");
        spec.controls = controlsSpec;

        when(controlsFactory.create(eq(controlsSpec), any(MeleeWeapon.class))).thenReturn(controls);
        when(reloadSystemFactory.create(eq(spec.reloading), any(ResourceContainer.class))).thenReturn(reloadSystem);

        MeleeWeapon result = meleeWeaponFactory.create(spec);

        assertThat(result).isInstanceOfSatisfying(DefaultMeleeWeapon.class, meleeWeapon -> {
            assertThat(meleeWeapon.getName()).isEqualTo("Combat Knife");
            assertThat(meleeWeapon.getDescription()).isEqualTo("Standard issue military knife. Fast, quiet and deadly.");
            assertThat(meleeWeapon.getHolder()).isEmpty();
            assertThat(meleeWeapon.getAttackDamage()).isEqualTo(75.0);
            assertThat(meleeWeapon.getReloadSystem()).isEqualTo(reloadSystem);
        });

        verify(meleeWeaponRegistry).register(result);
    }

    @Test
    @DisplayName("create returns MeleeWeapon with throwing")
    void create_withThrowing() {
        MeleeWeaponSpec spec = this.createMeleeWeaponSpec("tomahawk");
        ReloadSystem reloadSystem = mock(ReloadSystem.class);
        ThrowHandler throwHandler = mock(ThrowHandler.class);

        when(reloadSystemFactory.create(eq(spec.reloading), any(ResourceContainer.class))).thenReturn(reloadSystem);
        when(throwHandlerFactory.create(eq(spec.throwing), any(ItemRepresentation.class), any(ResourceContainer.class))).thenReturn(throwHandler);

        MeleeWeapon result = meleeWeaponFactory.create(spec);

        assertThat(result).isInstanceOfSatisfying(DefaultMeleeWeapon.class, meleeWeapon -> {
            assertThat(meleeWeapon.getName()).isEqualTo("Tomahawk");
            assertThat(meleeWeapon.getDescription()).isEqualTo("Retrievable throwing hatchet that causes instant death on impact.");
            assertThat(meleeWeapon.getHolder()).isEmpty();
            assertThat(meleeWeapon.getAttackDamage()).isEqualTo(50.0);
            assertThat(meleeWeapon.getReloadSystem()).isEqualTo(reloadSystem);
        });

        verify(meleeWeaponRegistry).register(result);
    }

    @Test
    @DisplayName("create return MeleeWeapon with assigned holder")
    void create_withHolder() {
        MeleeWeaponSpec spec = this.createMeleeWeaponSpec("combat_knife");
        ReloadSystem reloadSystem = mock(ReloadSystem.class);
        GamePlayer gamePlayer = mock(GamePlayer.class);

        when(reloadSystemFactory.create(eq(spec.reloading), any(ResourceContainer.class))).thenReturn(reloadSystem);

        MeleeWeapon result = meleeWeaponFactory.create(spec, gamePlayer);

        assertThat(result).isInstanceOfSatisfying(DefaultMeleeWeapon.class, meleeWeapon -> {
            assertThat(meleeWeapon.getName()).isEqualTo("Combat Knife");
            assertThat(meleeWeapon.getDescription()).isEqualTo("Standard issue military knife. Fast, quiet and deadly.");
            assertThat(meleeWeapon.getHolder()).hasValue(gamePlayer);
            assertThat(meleeWeapon.getAttackDamage()).isEqualTo(75.0);
            assertThat(meleeWeapon.getReloadSystem()).isEqualTo(reloadSystem);
        });

        verify(meleeWeaponRegistry).register(result, gamePlayer);
    }

    private MeleeWeaponSpec createMeleeWeaponSpec(String fileName) {
        File file = new File("src/main/resources/items/melee_weapons/" + fileName + ".yml");

        SpecDeserializer specDeserializer = new SpecDeserializer();
        return specDeserializer.deserializeSpec(file, MeleeWeaponSpec.class);
    }
}
