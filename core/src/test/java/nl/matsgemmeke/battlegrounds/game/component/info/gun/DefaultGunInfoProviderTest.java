package nl.matsgemmeke.battlegrounds.game.component.info.gun;

import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.entity.PlayerRegistry;
import nl.matsgemmeke.battlegrounds.game.component.item.GunRegistry;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultGunInfoProviderTest {

    private static final UUID ENTITY_ID = UUID.randomUUID();
    private static final UUID GUN_ID = UUID.randomUUID();

    @Mock
    private GunRegistry gunRegistry;
    @Mock
    private PlayerRegistry playerRegistry;
    @InjectMocks
    private DefaultGunInfoProvider gunInfoProvider;

    @Test
    void getGunFireSimulationInfoReturnsEmptyOptionalWhenPlayerRegistryDoesNotKnowGivenEntityId() {
        when(playerRegistry.findByUniqueId(ENTITY_ID)).thenReturn(Optional.empty());

        Optional<GunFireSimulationInfo> gunFireSimulationInfoOptional = gunInfoProvider.getGunFireSimulationInfo(ENTITY_ID);

        assertThat(gunFireSimulationInfoOptional).isEmpty();
    }

    @Test
    void getGunFireSimulationInfoReturnsEmptyOptionalWhenFoundGunHolderDoesNotHaveGuns() {
        GamePlayer gamePlayer = mock(GamePlayer.class);

        when(playerRegistry.findByUniqueId(ENTITY_ID)).thenReturn(Optional.of(gamePlayer));

        Optional<GunFireSimulationInfo> gunFireSimulationInfoOptional = gunInfoProvider.getGunFireSimulationInfo(ENTITY_ID);

        assertThat(gunFireSimulationInfoOptional).isEmpty();
    }

    @Test
    void getGunFireSimulationInfoReturnsEmptyOptionalWhenHeldGunIsNotRegistered() {
        GamePlayer gamePlayer = mock(GamePlayer.class);

        Gun gun = mock(Gun.class);
        when(gun.getId()).thenReturn(GUN_ID);

        when(gunRegistry.getAssignedGuns(gamePlayer)).thenReturn(List.of(gun));
        when(playerRegistry.findByUniqueId(ENTITY_ID)).thenReturn(Optional.of(gamePlayer));

        Optional<GunFireSimulationInfo> gunFireSimulationInfoOptional = gunInfoProvider.getGunFireSimulationInfo(ENTITY_ID);

        assertThat(gunFireSimulationInfoOptional).isEmpty();
    }

    @Test
    void getGunFireSimulationInfoReturnsOptionalWithGunFireSimulationInfoCorrespondingToGunId() {
        GamePlayer gamePlayer = mock(GamePlayer.class);
        List<GameSound> shotSounds = Collections.emptyList();
        int rateOfFire = 600;
        GunFireSimulationInfo gunFireSimulationInfo = new GunFireSimulationInfo(shotSounds, rateOfFire);

        Gun gun = mock(Gun.class);
        when(gun.getId()).thenReturn(GUN_ID);

        when(gunRegistry.getAssignedGuns(gamePlayer)).thenReturn(List.of(gun));
        when(playerRegistry.findByUniqueId(ENTITY_ID)).thenReturn(Optional.of(gamePlayer));

        gunInfoProvider.registerGunFireSimulationInfo(GUN_ID, gunFireSimulationInfo);
        Optional<GunFireSimulationInfo> gunFireSimulationInfoOptional = gunInfoProvider.getGunFireSimulationInfo(ENTITY_ID);

        assertThat(gunFireSimulationInfoOptional).hasValue(gunFireSimulationInfo);
    }
}
