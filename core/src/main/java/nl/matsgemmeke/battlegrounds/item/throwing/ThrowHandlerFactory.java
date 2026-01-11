package nl.matsgemmeke.battlegrounds.item.throwing;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.configuration.item.melee.ThrowingSpec;
import nl.matsgemmeke.battlegrounds.item.representation.ItemRepresentation;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.ProjectileLauncher;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.ProjectileLauncherFactory;

public class ThrowHandlerFactory {

    private final ProjectileLauncherFactory projectileLauncherFactory;

    @Inject
    public ThrowHandlerFactory(ProjectileLauncherFactory projectileLauncherFactory) {
        this.projectileLauncherFactory = projectileLauncherFactory;
    }

    public ThrowHandler create(ThrowingSpec spec, ItemRepresentation itemRepresentation) {
        ProjectileLauncher projectileLauncher = projectileLauncherFactory.create(spec.projectile);

        int throwsAmount = spec.throwsAmount;

        return new ThrowHandler(itemRepresentation, projectileLauncher, throwsAmount);
    }
}
