package nl.matsgemmeke.battlegrounds.game.component.info.gun;

import java.util.Optional;
import java.util.UUID;

public interface GunInfoProvider {

    Optional<GunFireSimulationInfo> getGunFireSimulationInfo(UUID entityId);

    void registerGunFireSimulationInfo(UUID gunId, GunFireSimulationInfo info);
}
