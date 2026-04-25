package nl.matsgemmeke.battlegrounds.game.component.controls;

import nl.matsgemmeke.battlegrounds.item.controls.ItemController;
import nl.matsgemmeke.battlegrounds.item.gun.GunUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ItemControllerRegistryTest {

    private static final UUID GUN_ID = UUID.randomUUID();

    private ItemControllerRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new ItemControllerRegistry();
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
}
