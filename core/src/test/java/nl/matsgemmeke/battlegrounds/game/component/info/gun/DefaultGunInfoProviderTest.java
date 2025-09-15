package nl.matsgemmeke.battlegrounds.game.component.info.gun;

import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.game.component.item.GunRegistry;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DefaultGunInfoProviderTest {

    private static final UUID UNIQUE_ID = UUID.randomUUID();

    private GunRegistry gunRegistry;
    private PlayerRegistry playerRegistry;

    @BeforeEach
    public void setUp() {
        gunRegistry = mock(GunRegistry.class);
        playerRegistry = mock(PlayerRegistry.class);
    }

    @Test
    public void getGunFireSimulationInfoReturnsEmptyOptionalWhenPlayerRegistryDoesNotKnowGivenEntityId() {
        when(playerRegistry.findByUniqueId(UNIQUE_ID)).thenReturn(Optional.empty());

        DefaultGunInfoProvider gunInfoProvider = new DefaultGunInfoProvider(gunRegistry, playerRegistry);
        Optional<GunFireSimulationInfo> gunFireSimulationInfo = gunInfoProvider.getGunFireSimulationInfo(UNIQUE_ID);

        assertThat(gunFireSimulationInfo).isEmpty();
    }

    @Test
    public void getGunFireSimulationInfoReturnsEmptyOptionalWhenFoundGunHolderDoesNotHaveGuns() {
        GamePlayer gamePlayer = mock(GamePlayer.class);

        when(playerRegistry.findByUniqueId(UNIQUE_ID)).thenReturn(Optional.of(gamePlayer));

        DefaultGunInfoProvider gunInfoProvider = new DefaultGunInfoProvider(gunRegistry, playerRegistry);
        Optional<GunFireSimulationInfo> gunFireSimulationInfo = gunInfoProvider.getGunFireSimulationInfo(UNIQUE_ID);

        assertThat(gunFireSimulationInfo).isEmpty();
    }

    @Test
    public void getGunFireSimulationInfoReturnsNewObjectBasedOnFirstHeldGun() {
        UUID entityId = UUID.randomUUID();
        GamePlayer gamePlayer = mock(GamePlayer.class);
        List<GameSound> shotSounds = Collections.emptyList();
        int rateOfFire = 600;

        Gun gun = mock(Gun.class);
        when(gun.getRateOfFire()).thenReturn(rateOfFire);
        when(gun.getShotSounds()).thenReturn(shotSounds);

        when(gunRegistry.getAssignedGuns(gamePlayer)).thenReturn(List.of(gun));
        when(playerRegistry.findByUniqueId(entityId)).thenReturn(Optional.of(gamePlayer));

        DefaultGunInfoProvider gunInfoProvider = new DefaultGunInfoProvider(gunRegistry, playerRegistry);
        Optional<GunFireSimulationInfo> gunFireSimulationInfo = gunInfoProvider.getGunFireSimulationInfo(entityId);

        assertThat(gunFireSimulationInfo).hasValueSatisfying(value -> {
            assertThat(value.shotSounds()).isEqualTo(shotSounds);
            assertThat(value.rateOfFire()).isEqualTo(rateOfFire);
        });
    }
}
