package nl.matsgemmeke.battlegrounds.item.shoot.launcher;

import nl.matsgemmeke.battlegrounds.configuration.item.ParticleEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.gun.ProjectileSpec;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectFactory;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectNew;
import nl.matsgemmeke.battlegrounds.item.mapper.particle.ParticleEffectMapper;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.fireball.FireballLauncher;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.fireball.FireballLauncherFactory;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.fireball.FireballProperties;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.hitscan.HitscanLauncher;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.hitscan.HitscanLauncherFactory;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.hitscan.HitscanProperties;
import org.bukkit.Particle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ProjectileLauncherFactoryTest {

    private FireballLauncherFactory fireballLauncherFactory;
    private HitscanLauncherFactory hitscanLauncherFactory;
    private ItemEffectFactory itemEffectFactory;
    private ParticleEffectMapper particleEffectMapper;

    @BeforeEach
    public void setUp() {
        fireballLauncherFactory = mock(FireballLauncherFactory.class);
        hitscanLauncherFactory = mock(HitscanLauncherFactory.class);
        itemEffectFactory = mock(ItemEffectFactory.class);
        particleEffectMapper = new ParticleEffectMapper();
    }

    @Test
    public void createReturnsInstanceOfFireballLauncher() {
        FireballLauncher fireballLauncher = mock(FireballLauncher.class);
        ProjectileSpec projectileSpec = this.createProjectileSpec("FIREBALL");
        ItemEffectNew itemEffect = mock(ItemEffectNew.class);

        when(fireballLauncherFactory.create(any(FireballProperties.class), eq(itemEffect))).thenReturn(fireballLauncher);
        when(itemEffectFactory.create(projectileSpec.effect)).thenReturn(itemEffect);

        ProjectileLauncherFactory projectileLauncherFactory = new ProjectileLauncherFactory(fireballLauncherFactory, hitscanLauncherFactory, itemEffectFactory, particleEffectMapper);
        ProjectileLauncher createdProjectileLauncher = projectileLauncherFactory.create(projectileSpec);

        ArgumentCaptor<FireballProperties> fireballPropertiesCaptor = ArgumentCaptor.forClass(FireballProperties.class);
        verify(fireballLauncherFactory).create(fireballPropertiesCaptor.capture(), eq(itemEffect));

        FireballProperties fireballProperties = fireballPropertiesCaptor.getValue();
        assertThat(fireballProperties.shotSounds()).isEmpty();
        assertThat(fireballProperties.trajectoryParticleEffect()).satisfies(particleEffect -> {
            assertThat(particleEffect.particle()).isEqualTo(Particle.FLAME);
            assertThat(particleEffect.count()).isEqualTo(1);
            assertThat(particleEffect.offsetX()).isEqualTo(0.1);
            assertThat(particleEffect.offsetY()).isEqualTo(0.2);
            assertThat(particleEffect.offsetZ()).isEqualTo(0.3);
            assertThat(particleEffect.extra()).isEqualTo(0.0);
        });
        assertThat(fireballProperties.velocity()).isEqualTo(2.0);

        assertThat(createdProjectileLauncher).isEqualTo(fireballLauncher);
    }

    @Test
    public void createThrowsProjectileLauncherCreationExceptionWhenCreatingFireballLauncherWhileRequiredVariablesAreMissing() {
        ProjectileSpec projectileSpec = this.createProjectileSpec("FIREBALL");
        projectileSpec.velocity = null;

        ProjectileLauncherFactory projectileLauncherFactory = new ProjectileLauncherFactory(fireballLauncherFactory, hitscanLauncherFactory, itemEffectFactory, particleEffectMapper);

        assertThatThrownBy(() -> projectileLauncherFactory.create(projectileSpec))
                .isInstanceOf(ProjectileLauncherCreationException.class)
                .hasMessage("Cannot create projectile launcher for type FIREBALL because of invalid spec: Required 'velocity' value is missing");
    }
    
    @Test
    public void createReturnsInstanceOfHitscanLauncher() {
        HitscanLauncher hitscanLauncher = mock(HitscanLauncher.class);
        ProjectileSpec projectileSpec = this.createProjectileSpec("HITSCAN");
        ItemEffectNew itemEffect = mock(ItemEffectNew.class);

        when(hitscanLauncherFactory.create(any(HitscanProperties.class), eq(itemEffect))).thenReturn(hitscanLauncher);
        when(itemEffectFactory.create(projectileSpec.effect)).thenReturn(itemEffect);

        ProjectileLauncherFactory projectileLauncherFactory = new ProjectileLauncherFactory(fireballLauncherFactory, hitscanLauncherFactory, itemEffectFactory, particleEffectMapper);
        ProjectileLauncher createdProjectileLauncher = projectileLauncherFactory.create(projectileSpec);

        ArgumentCaptor<HitscanProperties> hitscanPropertiesCaptor = ArgumentCaptor.forClass(HitscanProperties.class);
        verify(hitscanLauncherFactory).create(hitscanPropertiesCaptor.capture(), eq(itemEffect));
        
        HitscanProperties hitscanProperties = hitscanPropertiesCaptor.getValue();
        assertThat(hitscanProperties.shotSounds()).isEmpty();
        assertThat(hitscanProperties.trajectoryParticleEffect()).satisfies(particleEffect -> {
            assertThat(particleEffect.particle()).isEqualTo(Particle.FLAME);
            assertThat(particleEffect.count()).isEqualTo(1);
            assertThat(particleEffect.offsetX()).isEqualTo(0.1);
            assertThat(particleEffect.offsetY()).isEqualTo(0.2);
            assertThat(particleEffect.offsetZ()).isEqualTo(0.3);
            assertThat(particleEffect.extra()).isEqualTo(0.0);
        });

        assertThat(createdProjectileLauncher).isEqualTo(hitscanLauncher);
    }

    private ProjectileSpec createProjectileSpec(String type) {
        ParticleEffectSpec trajectoryParticleEffectSpec = new ParticleEffectSpec();
        trajectoryParticleEffectSpec.particle = "FLAME";
        trajectoryParticleEffectSpec.count = 1;
        trajectoryParticleEffectSpec.offsetX = 0.1;
        trajectoryParticleEffectSpec.offsetY = 0.2;
        trajectoryParticleEffectSpec.offsetZ = 0.3;
        trajectoryParticleEffectSpec.extra = 0.0;

        ProjectileSpec projectileSpec = new ProjectileSpec();
        projectileSpec.type = type;
        projectileSpec.trajectoryParticleEffect = trajectoryParticleEffectSpec;
        projectileSpec.velocity = 2.0;
        return projectileSpec;
    }
}
