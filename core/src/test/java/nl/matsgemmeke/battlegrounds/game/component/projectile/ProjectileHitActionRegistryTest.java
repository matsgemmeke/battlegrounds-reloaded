package nl.matsgemmeke.battlegrounds.game.component.projectile;

import org.bukkit.entity.Projectile;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class ProjectileHitActionRegistryTest {

    @Test
    public void getProjectileHitActionReturnsEmptyOptionalWhenNoMatchesAreFoundForGivenProjectile() {
        Projectile projectile = mock(Projectile.class);

        ProjectileHitActionRegistry projectileHitActionRegistry = new ProjectileHitActionRegistry();
        Optional<ProjectileHitAction> result = projectileHitActionRegistry.getProjectileHitAction(projectile);

        assertThat(result).isEmpty();
    }

    @Test
    public void getProjectileHitActionReturnsOptionalWithProjectileHitActionForGivenProjectile() {
        Projectile projectile = mock(Projectile.class);
        ProjectileHitAction projectileHitAction = mock(ProjectileHitAction.class);

        ProjectileHitActionRegistry projectileHitActionRegistry = new ProjectileHitActionRegistry();
        projectileHitActionRegistry.registerProjectileHitAction(projectile, projectileHitAction);
        Optional<ProjectileHitAction> result = projectileHitActionRegistry.getProjectileHitAction(projectile);

        assertThat(result).hasValue(projectileHitAction);
    }

    @Test
    public void removeProjectileHitActionRemovesProjectileHitActionRegistrationFromProjectile() {
        Projectile projectile = mock(Projectile.class);
        ProjectileHitAction projectileHitAction = mock(ProjectileHitAction.class);

        ProjectileHitActionRegistry projectileHitActionRegistry = new ProjectileHitActionRegistry();
        projectileHitActionRegistry.registerProjectileHitAction(projectile, projectileHitAction);
        projectileHitActionRegistry.removeProjectileHitAction(projectile);
        Optional<ProjectileHitAction> result = projectileHitActionRegistry.getProjectileHitAction(projectile);

        assertThat(result).isEmpty();
    }
}
