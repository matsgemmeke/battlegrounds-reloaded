package nl.matsgemmeke.battlegrounds.game.component.info.gun;

import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.ItemContainer;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import nl.matsgemmeke.battlegrounds.item.gun.GunHolder;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class DefaultGunInfoProvider implements GunInfoProvider {

    @NotNull
    private final ItemContainer<Gun, GunHolder> gunContainer;
    @NotNull
    private final PlayerRegistry playerRegistry;

    public DefaultGunInfoProvider(@NotNull ItemContainer<Gun, GunHolder> gunContainer, @NotNull PlayerRegistry playerRegistry) {
        this.gunContainer = gunContainer;
        this.playerRegistry = playerRegistry;
    }

    public Optional<GunFireSimulationInfo> getGunFireSimulationInfo(UUID entityId) {
        GamePlayer gamePlayer = playerRegistry.findByUUID(entityId);

        if (gamePlayer == null) {
            return Optional.empty();
        }

        Gun gun = gunContainer.getAssignedItems(gamePlayer).stream().findFirst().orElse(null);

        if (gun == null) {
            return Optional.empty();
        }

        List<GameSound> shotSounds = gun.getShotSounds();
        int rateOfFire = gun.getRateOfFire();

        return Optional.of(new GunFireSimulationInfo(shotSounds, rateOfFire));
    }
}
