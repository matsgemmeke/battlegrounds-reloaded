package nl.matsgemmeke.battlegrounds.storage.state;

import nl.matsgemmeke.battlegrounds.storage.state.equipment.EquipmentState;
import nl.matsgemmeke.battlegrounds.storage.state.gun.GunState;
import nl.matsgemmeke.battlegrounds.storage.state.melee.MeleeWeaponState;

import java.util.List;
import java.util.UUID;

public record PlayerState(
        UUID playerUuid,
        List<GunState> gunStates,
        List<EquipmentState> equipmentStates,
        List<MeleeWeaponState> meleeWeaponStates
) {
}
