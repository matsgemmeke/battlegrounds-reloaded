package nl.matsgemmeke.battlegrounds.item.melee.controls;

import nl.matsgemmeke.battlegrounds.configuration.item.melee.MeleeWeaponSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.SpecDeserializer;
import nl.matsgemmeke.battlegrounds.game.component.controls.ItemControllerRegistry;
import nl.matsgemmeke.battlegrounds.item.controls.Action;
import nl.matsgemmeke.battlegrounds.item.controls.ActionBinding;
import nl.matsgemmeke.battlegrounds.item.controls.ActionBindingMapper;
import nl.matsgemmeke.battlegrounds.item.controls.ItemController;
import nl.matsgemmeke.battlegrounds.item.melee.MeleeWeapon;
import nl.matsgemmeke.battlegrounds.item.melee.MeleeWeaponUser;
import nl.matsgemmeke.battlegrounds.item.melee.controls.reload.ReloadFunction;
import nl.matsgemmeke.battlegrounds.item.melee.controls.throwing.ThrowFunction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.UUID;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MeleeWeaponControllerFactoryTest {

    private static final UUID MELEE_WEAPON_ID = UUID.randomUUID();

    @Spy
    private ActionBindingMapper actionBindingMapper;
    @Mock
    private ItemController<MeleeWeaponUser> controller;
    @Mock
    private ItemControllerRegistry itemControllerRegistry;
    @Mock
    private MeleeWeapon meleeWeapon;
    @Mock
    private Supplier<ItemController<MeleeWeaponUser>> controllerSupplier;
    @Captor
    private ArgumentCaptor<ActionBinding<MeleeWeaponUser>> bindingCaptor;
    @InjectMocks
    private MeleeWeaponControllerFactory controllerFactory;

    @BeforeEach
    void setUp() {
        when(controllerSupplier.get()).thenReturn(controller);
        when(meleeWeapon.getId()).thenReturn(MELEE_WEAPON_ID);
    }

    @Test
    @DisplayName("creates returns ItemController with reload control")
    void create_withReloadControl() {
        MeleeWeaponSpec spec = this.createMeleeWeaponSpec("src/main/resources/items/melee_weapons/ballistic_knife.yml");
        spec.controls.throwing = null;

        ItemController<MeleeWeaponUser> controller = controllerFactory.create(spec.controls, meleeWeapon);

        verify(controller).bind(eq(Action.LEFT_CLICK), bindingCaptor.capture());

        assertThat(bindingCaptor.getValue()).satisfies(binding -> {
            assertThat(binding.function()).isInstanceOf(ReloadFunction.class);
        });

        verify(itemControllerRegistry).registerMeleeWeaponController(MELEE_WEAPON_ID, controller);
    }

    @Test
    @DisplayName("creates returns ItemController with throwing control")
    void create_withThrowingControl() {
        MeleeWeaponSpec spec = this.createMeleeWeaponSpec("src/main/resources/items/melee_weapons/ballistic_knife.yml");
        spec.controls.reload = null;

        ItemController<MeleeWeaponUser> controller = controllerFactory.create(spec.controls, meleeWeapon);

        verify(controller).bind(eq(Action.RIGHT_CLICK), bindingCaptor.capture());

        assertThat(bindingCaptor.getValue()).satisfies(binding -> {
            assertThat(binding.function()).isInstanceOf(ThrowFunction.class);
        });

        verify(itemControllerRegistry).registerMeleeWeaponController(MELEE_WEAPON_ID, controller);
    }

    private MeleeWeaponSpec createMeleeWeaponSpec(String filePath) {
        File file = new File(filePath);

        SpecDeserializer specDeserializer = new SpecDeserializer();
        return specDeserializer.deserializeSpec(file, MeleeWeaponSpec.class);
    }
}
