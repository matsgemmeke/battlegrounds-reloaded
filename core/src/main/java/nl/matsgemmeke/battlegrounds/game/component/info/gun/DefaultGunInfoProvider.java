package nl.matsgemmeke.battlegrounds.game.component.info.gun;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.game.component.item.GunRegistry;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class DefaultGunInfoProvider implements GunInfoProvider {

    @NotNull
    private final GunRegistry gunRegistry;
    @NotNull
    private final PlayerRegistry playerRegistry;

    @Inject
    public DefaultGunInfoProvider(@NotNull GunRegistry gunRegistry, @NotNull PlayerRegistry playerRegistry) {
        this.gunRegistry = gunRegistry;
        this.playerRegistry = playerRegistry;
    }

    public Optional<GunFireSimulationInfo> getGunFireSimulationInfo(UUID entityId) {
        GamePlayer gamePlayer = playerRegistry.findByUUID(entityId);

        if (gamePlayer == null) {
            return Optional.empty();
        }

        Gun gun = gunRegistry.getAssignedGuns(gamePlayer).stream().findFirst().orElse(null);

        if (gun == null) {
            return Optional.empty();
        }

        List<GameSound> shotSounds = gun.getShotSounds();
        int rateOfFire = gun.getRateOfFire();

        return Optional.of(new GunFireSimulationInfo(shotSounds, rateOfFire));
    }
}
