package nl.matsgemmeke.battlegrounds.storage.state.melee;

import java.util.UUID;

public record MeleeWeaponState(UUID playerUuid, String meleeWeaponName, int itemSlot) {
}
