package nl.matsgemmeke.battlegrounds.item.reload;

import nl.matsgemmeke.battlegrounds.configuration.item.gun.ReloadingSpec;
import nl.matsgemmeke.battlegrounds.item.reload.magazine.MagazineReloadSystem;
import nl.matsgemmeke.battlegrounds.item.reload.magazine.MagazineReloadSystemFactory;
import nl.matsgemmeke.battlegrounds.item.reload.manual.ManualInsertionReloadSystem;
import nl.matsgemmeke.battlegrounds.item.reload.manual.ManualInsertionReloadSystemFactory;
import nl.matsgemmeke.battlegrounds.sound.SoundSequence;
import nl.matsgemmeke.battlegrounds.sound.SoundService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReloadSystemFactoryTest {

    private static final String SOUND_SEQUENCE_ID = "assault-rifle-reload-1";
    private static final SoundSequence SOUND_SEQUENCE = new SoundSequence(null, null);
    private static final long DURATION = 50L;
    private static final ResourceContainer RESOURCE_CONTAINER = new ResourceContainer(30, 30, 90, 300);

    @Mock
    private MagazineReloadSystemFactory magazineReloadSystemFactory;
    @Mock
    private ManualInsertionReloadSystemFactory manualInsertionReloadSystemFactory;
    @Mock
    private SoundService soundService;
    @InjectMocks
    private ReloadSystemFactory reloadSystemFactory;

    @Test
    @DisplayName("create throws ReloadSystemCreationException when sound sequence is specified but its id is invalid")
    void create_invalidSoundSequenceId() {
        ReloadingSpec spec = new ReloadingSpec();
        spec.type = "MAGAZINE";
        spec.duration = 50L;
        spec.soundSequence = SOUND_SEQUENCE_ID;

        when(soundService.getSoundSequence(SOUND_SEQUENCE_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reloadSystemFactory.create(spec, RESOURCE_CONTAINER))
                .isInstanceOf(ReloadSystemCreationException.class)
                .hasMessage("Invalid sound sequence id 'assault-rifle-reload-1'");
    }

    @Test
    @DisplayName("create returns ReloadSystem instance for magazine reload")
    void create_magazineReload() {
        MagazineReloadSystem reloadSystem = mock(MagazineReloadSystem.class);

        ReloadingSpec spec = new ReloadingSpec();
        spec.type = "MAGAZINE";
        spec.duration = DURATION;
        spec.soundSequence = SOUND_SEQUENCE_ID;

        when(soundService.getSoundSequence(SOUND_SEQUENCE_ID)).thenReturn(Optional.of(SOUND_SEQUENCE));
        when(magazineReloadSystemFactory.create(any(ReloadProperties.class), eq(RESOURCE_CONTAINER))).thenReturn(reloadSystem);

        ReloadSystem result = reloadSystemFactory.create(spec, RESOURCE_CONTAINER);

        ArgumentCaptor<ReloadProperties> propertiesCaptor = ArgumentCaptor.forClass(ReloadProperties.class);
        verify(magazineReloadSystemFactory).create(propertiesCaptor.capture(), eq(RESOURCE_CONTAINER));

        assertThat(propertiesCaptor.getValue()).satisfies(properties -> {
            assertThat(properties.soundSequence()).isEqualTo(SOUND_SEQUENCE);
            assertThat(properties.duration()).isEqualTo(DURATION);
        });

        assertThat(result).isInstanceOf(MagazineReloadSystem.class);
    }

    @Test
    @DisplayName("create returns ReloadSystem for manual insertion reload")
    void create_manualInsertionReload() {
        ManualInsertionReloadSystem reloadSystem = mock(ManualInsertionReloadSystem.class);

        ReloadingSpec spec = new ReloadingSpec();
        spec.type = "MANUAL_INSERTION";
        spec.duration = DURATION;
        spec.soundSequence = SOUND_SEQUENCE_ID;

        when(soundService.getSoundSequence(SOUND_SEQUENCE_ID)).thenReturn(Optional.of(SOUND_SEQUENCE));
        when(manualInsertionReloadSystemFactory.create(any(ReloadProperties.class), eq(RESOURCE_CONTAINER))).thenReturn(reloadSystem);

        ReloadSystem result = reloadSystemFactory.create(spec, RESOURCE_CONTAINER);

        ArgumentCaptor<ReloadProperties> propertiesCaptor = ArgumentCaptor.forClass(ReloadProperties.class);
        verify(manualInsertionReloadSystemFactory).create(propertiesCaptor.capture(), eq(RESOURCE_CONTAINER));

        assertThat(propertiesCaptor.getValue()).satisfies(properties -> {
            assertThat(properties.soundSequence()).isEqualTo(SOUND_SEQUENCE);
            assertThat(properties.duration()).isEqualTo(DURATION);
        });

        assertThat(result).isInstanceOf(ManualInsertionReloadSystem.class);
    }
}
