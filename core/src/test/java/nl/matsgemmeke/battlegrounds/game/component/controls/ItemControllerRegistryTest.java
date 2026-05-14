package nl.matsgemmeke.battlegrounds.game.component.controls;

import nl.matsgemmeke.battlegrounds.item.controls.ItemController;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentUser;
import nl.matsgemmeke.battlegrounds.item.gun.GunUser;
import nl.matsgemmeke.battlegrounds.item.melee.MeleeWeaponUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ItemControllerRegistryTest {

    private static final UUID GUN_ID = UUID.randomUUID();
    private static final UUID EQUIPMENT_ID = UUID.randomUUID();
    private static final UUID MELEE_WEAPON_ID = UUID.randomUUID();

    private ItemControllerRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new ItemControllerRegistry();
    }

    @Test
    @DisplayName("getEquipmentController returns empty optional when given equipment id is not registered")
    void getEquipmentController_unknownId() {
        Optional<ItemController<EquipmentUser>> controllerOptional = registry.getEquipmentController(EQUIPMENT_ID);

        assertThat(controllerOptional).isEmpty();
    }

    @Test
    @DisplayName("getEquipmentController returns optional with corresponding equipment controller")
    void getEquipmentController_successful() {
        ItemController<EquipmentUser> controller = new ItemController<>();

        registry.registerEquipmentController(EQUIPMENT_ID, controller);
        Optional<ItemController<EquipmentUser>> controllerOptional = registry.getEquipmentController(EQUIPMENT_ID);

        assertThat(controllerOptional).hasValue(controller);
    }

    @Test
    @DisplayName("getGunController returns empty optional when given gun id is not registered")
    void getGunController_unknownId() {
        Optional<ItemController<GunUser>> controllerOptional = registry.getGunController(GUN_ID);

        assertThat(controllerOptional).isEmpty();
    }

    @Test
    @DisplayName("getGunController returns optional with corresponding gun controller")
    void getGunController_successful() {
        ItemController<GunUser> controller = new ItemController<>();

        registry.registerGunController(GUN_ID, controller);
        Optional<ItemController<GunUser>> controllerOptional = registry.getGunController(GUN_ID);

        assertThat(controllerOptional).hasValue(controller);
    }

    @Test
    @DisplayName("getMeleeWeaponController returns empty optional when given melee weapon id is not registered")
    void getMeleeWeaponController_unknownId() {
        Optional<ItemController<MeleeWeaponUser>> controllerOptional = registry.getMeleeWeaponController(MELEE_WEAPON_ID);

        assertThat(controllerOptional).isEmpty();
    }

    @Test
    @DisplayName("getMeleeWeaponController returns optional with corresponding melee weapon controller")
    void getMeleeWeaponController_successful() {
        ItemController<MeleeWeaponUser> controller = new ItemController<>();

        registry.registerMeleeWeaponController(MELEE_WEAPON_ID, controller);
        Optional<ItemController<MeleeWeaponUser>> controllerOptional = registry.getMeleeWeaponController(MELEE_WEAPON_ID);

        assertThat(controllerOptional).hasValue(controller);
    }
}
