package nl.matsgemmeke.battlegrounds.item.throwing;

import nl.matsgemmeke.battlegrounds.configuration.item.gun.ProjectileSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.melee.ThrowingSpec;
import nl.matsgemmeke.battlegrounds.item.reload.ResourceContainer;
import nl.matsgemmeke.battlegrounds.item.representation.ItemRepresentation;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.ProjectileLauncher;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.ProjectileLauncherFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ThrowHandlerFactoryTest {

    private static final ResourceContainer RESOURCE_CONTAINER = new ResourceContainer(1, 1, 2, 2);

    @Mock
    private ItemRepresentation itemRepresentation;
    @Mock
    private ProjectileLauncherFactory projectileLauncherFactory;
    @InjectMocks
    private ThrowHandlerFactory throwHandlerFactory;

    @Test
    void createReturnsNewInstanceOfThrowHandler() {
        ThrowingSpec spec = this.createSpec();
        ProjectileLauncher projectileLauncher = mock(ProjectileLauncher.class);

        when(projectileLauncherFactory.create(spec.projectile)).thenReturn(projectileLauncher);

        ThrowHandler throwHandler = throwHandlerFactory.create(spec, itemRepresentation, RESOURCE_CONTAINER);

        assertThat(throwHandler).isNotNull();
    }

    private ThrowingSpec createSpec() {
        ProjectileSpec projectileSpec = new ProjectileSpec();

        ThrowingSpec throwingSpec = new ThrowingSpec();
        throwingSpec.projectile = projectileSpec;
        return throwingSpec;
    }
}
