package nl.matsgemmeke.battlegrounds.storage.state;

import java.util.List;
import java.util.UUID;

public record GamePlayerState(UUID playerUuid, List<GunState> gunStates) { }
