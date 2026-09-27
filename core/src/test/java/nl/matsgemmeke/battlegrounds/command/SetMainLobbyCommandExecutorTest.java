package nl.matsgemmeke.battlegrounds.command;

import nl.matsgemmeke.battlegrounds.configuration.data.DataConfiguration;
import nl.matsgemmeke.battlegrounds.configuration.model.LocationData;
import nl.matsgemmeke.battlegrounds.fixture.LanguageFixture;
import nl.matsgemmeke.battlegrounds.i18n.TextTemplate;
import nl.matsgemmeke.battlegrounds.i18n.TranslationKey;
import nl.matsgemmeke.battlegrounds.i18n.Translator;
import nl.matsgemmeke.battlegrounds.util.world.LocationMapper;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SetMainLobbyCommandExecutorTest {

    private static final String PLAYER_LOCATION_WORLD = "world";
    private static final double PLAYER_LOCATION_X = 1.1;
    private static final double PLAYER_LOCATION_Y = 2.2;
    private static final double PLAYER_LOCATION_Z = 3.3;
    private static final float PLAYER_LOCATION_YAW = 90.0f;
    private static final float PLAYER_LOCATION_PITCH = 0.0f;

    private static final TextTemplate MAIN_LOBBY_SET_TEXT_TEMPLATE = LanguageFixture.getTextTemplate(TranslationKey.MAIN_LOBBY_SET.getPath());

    @Mock
    private DataConfiguration dataConfiguration;
    @Mock
    private Player player;
    @Mock
    private LocationMapper locationMapper;
    @Mock
    private Translator translator;
    @InjectMocks
    private SetMainLobbyCommandExecutor commandExecutor;

    @Test
    @DisplayName("execute saves main lobby location to data configuration")
    void execute() {
        Location playerLocation = new Location(null, 0, 0, 0, 0, 0);
        LocationData playerLocationData = new LocationData(PLAYER_LOCATION_WORLD, PLAYER_LOCATION_X, PLAYER_LOCATION_Y, PLAYER_LOCATION_Z, PLAYER_LOCATION_YAW, PLAYER_LOCATION_PITCH);

        when(player.getLocation()).thenReturn(playerLocation);
        when(locationMapper.toLocationData(any(Location.class))).thenReturn(playerLocationData);
        when(translator.translate(TranslationKey.MAIN_LOBBY_SET.getPath())).thenReturn(MAIN_LOBBY_SET_TEXT_TEMPLATE);

        commandExecutor.execute(player);

        ArgumentCaptor<LocationData> locationDataCaptor = ArgumentCaptor.forClass(LocationData.class);
        verify(dataConfiguration).setMainLobbyLocation(locationDataCaptor.capture());

        assertThat(locationDataCaptor.getValue()).satisfies(locationData -> {
            assertThat(locationData.world()).isEqualTo(PLAYER_LOCATION_WORLD);
            assertThat(locationData.x()).isEqualTo(PLAYER_LOCATION_X);
            assertThat(locationData.y()).isEqualTo(PLAYER_LOCATION_Y);
            assertThat(locationData.z()).isEqualTo(PLAYER_LOCATION_Z);
            assertThat(locationData.yaw()).isEqualTo(PLAYER_LOCATION_YAW);
            assertThat(locationData.pitch()).isEqualTo(PLAYER_LOCATION_PITCH);
        });

        verify(player).sendMessage("&6You have set the main lobby to your current location.");
    }
}
