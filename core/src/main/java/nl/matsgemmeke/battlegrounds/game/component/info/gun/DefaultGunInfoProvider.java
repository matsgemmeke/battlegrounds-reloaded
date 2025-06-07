package nl.matsgemmeke.battlegrounds.game.component.info.gun;

import nl.matsgemmeke.battlegrounds.game.ItemContainer;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import nl.matsgemmeke.battlegrounds.item.gun.GunHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DefaultGunInfoProvider implements GunInfoProvider {

    @NotNull
    private ItemContainer<Gun, GunHolder> gunContainer;

    public DefaultGunInfoProvider(@NotNull ItemContainer<Gun, GunHolder> gunContainer) {
        this.gunContainer = gunContainer;
    }

    @Nullable
    public GunFireSimulationInfo getGunFireSimulationInfo(@NotNull GunHolder holder) {
        Gun gun = gunContainer.getAssignedItems(holder).stream().findFirst().orElse(null);

        if (gun == null) {
            return null;
        }

        List<GameSound> shotSounds = gun.getShotSounds();
        int rateOfFire = gun.getFireMode().getRateOfFire();

        return new GunFireSimulationInfo(shotSounds, rateOfFire);
    }
}
