package nl.matsgemmeke.battlegrounds.game.component.info.gun;

import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.ItemContainer;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import nl.matsgemmeke.battlegrounds.item.gun.GunHolder;
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

    private ItemContainer<Gun, GunHolder> gunContainer;
    private PlayerRegistry playerRegistry;

    @BeforeEach
    public void setUp() {
        gunContainer = new ItemContainer<>();
        playerRegistry = mock(PlayerRegistry.class);
    }

    @Test
    public void getGunFireSimulationInfoReturnsEmptyOptionalWhenPlayerRegistryDoesNotKnowGivenEntityId() {
        UUID entityId = UUID.randomUUID();

        when(playerRegistry.findByUUID(entityId)).thenReturn(null);

        DefaultGunInfoProvider gunInfoProvider = new DefaultGunInfoProvider(gunContainer, playerRegistry);
        Optional<GunFireSimulationInfo> gunFireSimulationInfo = gunInfoProvider.getGunFireSimulationInfo(entityId);

        assertThat(gunFireSimulationInfo).isEmpty();
    }

    @Test
    public void getGunFireSimulationInfoReturnsEmptyOptionalWhenFoundGunHolderDoesNotHaveGuns() {
        UUID entityId = UUID.randomUUID();
        GamePlayer gamePlayer = mock(GamePlayer.class);

        when(playerRegistry.findByUUID(entityId)).thenReturn(gamePlayer);

        DefaultGunInfoProvider gunInfoProvider = new DefaultGunInfoProvider(gunContainer, playerRegistry);
        Optional<GunFireSimulationInfo> gunFireSimulationInfo = gunInfoProvider.getGunFireSimulationInfo(entityId);

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

        gunContainer.addAssignedItem(gun, gamePlayer);

        when(playerRegistry.findByUUID(entityId)).thenReturn(gamePlayer);

        DefaultGunInfoProvider gunInfoProvider = new DefaultGunInfoProvider(gunContainer, playerRegistry);
        Optional<GunFireSimulationInfo> gunFireSimulationInfo = gunInfoProvider.getGunFireSimulationInfo(entityId);

        assertThat(gunFireSimulationInfo).hasValueSatisfying(value -> {
            assertThat(value.shotSounds()).isEqualTo(shotSounds);
            assertThat(value.rateOfFire()).isEqualTo(rateOfFire);
        });
    }
}
