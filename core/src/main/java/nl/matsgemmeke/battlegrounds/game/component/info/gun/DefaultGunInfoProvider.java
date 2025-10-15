package nl.matsgemmeke.battlegrounds.game.component.info.gun;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.game.component.item.GunRegistry;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;

import java.util.*;

public class DefaultGunInfoProvider implements GunInfoProvider {

    private final GunRegistry gunRegistry;
    private final Map<UUID, GunFireSimulationInfo> gunFireSimulationInfoByGunId;
    private final PlayerRegistry playerRegistry;

    @Inject
    public DefaultGunInfoProvider(GunRegistry gunRegistry, PlayerRegistry playerRegistry) {
        this.gunRegistry = gunRegistry;
        this.playerRegistry = playerRegistry;
        this.gunFireSimulationInfoByGunId = new HashMap<>();
    }

    @Override
    public Optional<GunFireSimulationInfo> getGunFireSimulationInfo(UUID uniqueId) {
        GamePlayer gamePlayer = playerRegistry.findByUniqueId(uniqueId).orElse(null);

        if (gamePlayer == null) {
            return Optional.empty();
        }

        Gun gun = gunRegistry.getAssignedGuns(gamePlayer).stream().findFirst().orElse(null);

        if (gun == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(gunFireSimulationInfoByGunId.get(gun.getId()));
    }

    @Override
    public void registerGunFireSimulationInfo(UUID gunId, GunFireSimulationInfo info) {
        gunFireSimulationInfoByGunId.put(gunId, info);
    }
}
