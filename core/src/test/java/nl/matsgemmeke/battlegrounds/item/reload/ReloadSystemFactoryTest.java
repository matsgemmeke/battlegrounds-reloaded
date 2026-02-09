package nl.matsgemmeke.battlegrounds.item.reload;

import nl.matsgemmeke.battlegrounds.configuration.item.gun.ReloadingSpec;
import nl.matsgemmeke.battlegrounds.item.reload.magazine.MagazineReloadSystem;
import nl.matsgemmeke.battlegrounds.item.reload.magazine.MagazineReloadSystemFactory;
import nl.matsgemmeke.battlegrounds.item.reload.manual.ManualInsertionReloadSystem;
import nl.matsgemmeke.battlegrounds.item.reload.manual.ManualInsertionReloadSystemFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReloadSystemFactoryTest {

    private static final ResourceContainer RESOURCE_CONTAINER = new ResourceContainer(30, 30, 90, 300);

    @Mock
    private MagazineReloadSystemFactory magazineReloadSystemFactory;
    @Mock
    private ManualInsertionReloadSystemFactory manualInsertionReloadSystemFactory;
    @InjectMocks
    private ReloadSystemFactory reloadSystemFactory;

    @Test
    @DisplayName("create returns ReloadSystem instance for magazine reload")
    void create_magazineReload() {
        ReloadingSpec spec = new ReloadingSpec();
        spec.type = "MAGAZINE";
        spec.duration = 50L;

        MagazineReloadSystem reloadSystem = mock(MagazineReloadSystem.class);
        when(magazineReloadSystemFactory.create(any(ReloadProperties.class), eq(RESOURCE_CONTAINER))).thenReturn(reloadSystem);

        ReloadSystemFactory factory = new ReloadSystemFactory(magazineReloadSystemFactory, manualInsertionReloadSystemFactory);
        ReloadSystem result = factory.create(spec, RESOURCE_CONTAINER);

        ArgumentCaptor<ReloadProperties> propertiesCaptor = ArgumentCaptor.forClass(ReloadProperties.class);
        verify(magazineReloadSystemFactory).create(propertiesCaptor.capture(), eq(RESOURCE_CONTAINER));

        ReloadProperties properties = propertiesCaptor.getValue();
        assertThat(properties.duration()).isEqualTo(50L);

        assertThat(result).isInstanceOf(MagazineReloadSystem.class);
    }

    @Test
    @DisplayName("create returns ReloadSystem for manual insertion reload")
    void create_manualInsertionReload() {
        ReloadingSpec spec = new ReloadingSpec();
        spec.type = "MANUAL_INSERTION";
        spec.duration = 50L;

        ManualInsertionReloadSystem reloadSystem = mock(ManualInsertionReloadSystem.class);
        when(manualInsertionReloadSystemFactory.create(any(ReloadProperties.class), eq(RESOURCE_CONTAINER))).thenReturn(reloadSystem);

        ReloadSystemFactory factory = new ReloadSystemFactory(magazineReloadSystemFactory, manualInsertionReloadSystemFactory);
        ReloadSystem result = factory.create(spec, RESOURCE_CONTAINER);

        ArgumentCaptor<ReloadProperties> propertiesCaptor = ArgumentCaptor.forClass(ReloadProperties.class);
        verify(manualInsertionReloadSystemFactory).create(propertiesCaptor.capture(), eq(RESOURCE_CONTAINER));

        ReloadProperties properties = propertiesCaptor.getValue();
        assertThat(properties.duration()).isEqualTo(50L);

        assertThat(result).isInstanceOf(ManualInsertionReloadSystem.class);
    }
}
