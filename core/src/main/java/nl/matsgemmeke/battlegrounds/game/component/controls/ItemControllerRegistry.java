package nl.matsgemmeke.battlegrounds.game.component.controls;

import nl.matsgemmeke.battlegrounds.item.controls.ItemController;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentUser;
import nl.matsgemmeke.battlegrounds.item.gun.GunUser;
import nl.matsgemmeke.battlegrounds.item.melee.MeleeWeaponUser;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class ItemControllerRegistry {

    private final Map<UUID, ItemController<GunUser>> gunControllers;
    private final Map<UUID, ItemController<EquipmentUser>> equipmentControllers;
    private final Map<UUID, ItemController<MeleeWeaponUser>> meleeWeaponControllers;

    public ItemControllerRegistry() {
        this.gunControllers = new HashMap<>();
        this.equipmentControllers = new HashMap<>();
        this.meleeWeaponControllers = new HashMap<>();
    }

    public void registerGunController(UUID gunId, ItemController<GunUser> gunController) {
        gunControllers.put(gunId, gunController);
    }

    public void registerEquipmentController(UUID equipmentId, ItemController<EquipmentUser> equipmentController) {
        equipmentControllers.put(equipmentId, equipmentController);
    }

    public void registerMeleeWeaponController(UUID meleeWeaponId, ItemController<MeleeWeaponUser> meleeWeaponController) {
        meleeWeaponControllers.put(meleeWeaponId, meleeWeaponController);
    }

    public Optional<ItemController<GunUser>> getGunController(UUID gunId) {
        return Optional.ofNullable(gunControllers.get(gunId));
    }
}
