package nl.matsgemmeke.battlegrounds.storage.state.gun;

import java.util.UUID;

public record GunState(UUID playerUuid, String gunName, int magazineAmmo, int reserveAmmo, int itemSlot) {
}
