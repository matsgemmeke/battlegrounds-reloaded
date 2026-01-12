package nl.matsgemmeke.battlegrounds.item.shoot.launcher;

import nl.matsgemmeke.battlegrounds.configuration.item.ItemSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.ParticleEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.TriggerSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.gun.ProjectileSpec;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffect;
import nl.matsgemmeke.battlegrounds.item.effect.ItemEffectFactory;
import nl.matsgemmeke.battlegrounds.item.mapper.particle.ParticleEffectMapper;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.arrow.ArrowLauncher;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.arrow.ArrowLauncherFactory;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.arrow.ArrowProperties;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.fireball.FireballLauncher;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.fireball.FireballLauncherFactory;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.fireball.FireballProperties;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.hitscan.HitscanLauncher;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.hitscan.HitscanLauncherFactory;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.hitscan.HitscanProperties;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.item.ItemLaunchProperties;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.item.ItemLauncher;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.item.ItemLauncherFactory;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerExecutor;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerExecutorFactory;
import nl.matsgemmeke.battlegrounds.util.NamespacedKeyCreator;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectileLauncherFactoryTest {

    private static final String TEMPLATE_ID_KEY = "template-id";
    private static final double VELOCITY = 2.0;

    @Mock
    private ArrowLauncherFactory arrowLauncherFactory;
    @Mock
    private FireballLauncherFactory fireballLauncherFactory;
    @Mock
    private HitscanLauncherFactory hitscanLauncherFactory;
    @Mock
    private ItemEffectFactory itemEffectFactory;
    @Mock
    private ItemLauncherFactory itemLauncherFactory;
    @Mock
    private NamespacedKeyCreator namespacedKeyCreator;
    @Spy
    private ParticleEffectMapper particleEffectMapper = new ParticleEffectMapper();
    @Mock
    private TriggerExecutorFactory triggerExecutorFactory;
    @InjectMocks
    private ProjectileLauncherFactory projectileLauncherFactory;

    @Test
    void createReturnsInstanceOfArrowLauncher() {
        ArrowLauncher arrowLauncher = mock(ArrowLauncher.class);
        ProjectileSpec projectileSpec = this.createProjectileSpec("ARROW");
        ItemEffect itemEffect = mock(ItemEffect.class);

        when(arrowLauncherFactory.create(any(ArrowProperties.class), eq(itemEffect))).thenReturn(arrowLauncher);
        when(itemEffectFactory.create(projectileSpec.effect)).thenReturn(itemEffect);

        ProjectileLauncher createdProjectileLauncher = projectileLauncherFactory.create(projectileSpec);

        assertThat(createdProjectileLauncher).isEqualTo(arrowLauncher);

        ArgumentCaptor<ArrowProperties> arrowPropertiesCaptor = ArgumentCaptor.forClass(ArrowProperties.class);
        verify(arrowLauncherFactory).create(arrowPropertiesCaptor.capture(), eq(itemEffect));

        ArrowProperties arrowProperties = arrowPropertiesCaptor.getValue();
        assertThat(arrowProperties.launchSounds()).isEmpty();
        assertThat(arrowProperties.velocity()).isEqualTo(VELOCITY);
    }

    @Test
    void createReturnsInstanceOfFireballLauncher() {
        FireballLauncher fireballLauncher = mock(FireballLauncher.class);
        ProjectileSpec projectileSpec = this.createProjectileSpec("FIREBALL");
        ItemEffect itemEffect = mock(ItemEffect.class);

        when(fireballLauncherFactory.create(any(FireballProperties.class), eq(itemEffect))).thenReturn(fireballLauncher);
        when(itemEffectFactory.create(projectileSpec.effect)).thenReturn(itemEffect);

        ProjectileLauncher createdProjectileLauncher = projectileLauncherFactory.create(projectileSpec);

        ArgumentCaptor<FireballProperties> fireballPropertiesCaptor = ArgumentCaptor.forClass(FireballProperties.class);
        verify(fireballLauncherFactory).create(fireballPropertiesCaptor.capture(), eq(itemEffect));

        FireballProperties fireballProperties = fireballPropertiesCaptor.getValue();
        assertThat(fireballProperties.launchSounds()).isEmpty();
        assertThat(fireballProperties.trajectoryParticleEffect()).satisfies(particleEffect -> {
            assertThat(particleEffect.particle()).isEqualTo(Particle.FLAME);
            assertThat(particleEffect.count()).isEqualTo(1);
            assertThat(particleEffect.offsetX()).isEqualTo(0.1);
            assertThat(particleEffect.offsetY()).isEqualTo(0.2);
            assertThat(particleEffect.offsetZ()).isEqualTo(0.3);
            assertThat(particleEffect.extra()).isEqualTo(0.0);
        });
        assertThat(fireballProperties.velocity()).isEqualTo(VELOCITY);

        assertThat(createdProjectileLauncher).isEqualTo(fireballLauncher);
    }

    @Test
    void createThrowsProjectileLauncherCreationExceptionWhenCreatingFireballLauncherWhileRequiredVariablesAreMissing() {
        ProjectileSpec projectileSpec = this.createProjectileSpec("FIREBALL");
        projectileSpec.velocity = null;

        assertThatThrownBy(() -> projectileLauncherFactory.create(projectileSpec))
                .isInstanceOf(ProjectileLauncherCreationException.class)
                .hasMessage("Cannot create projectile launcher for type FIREBALL because of invalid spec: Required 'velocity' value is missing");
    }
    
    @Test
    void createReturnsInstanceOfHitscanLauncher() {
        HitscanLauncher hitscanLauncher = mock(HitscanLauncher.class);
        ProjectileSpec projectileSpec = this.createProjectileSpec("HITSCAN");
        ItemEffect itemEffect = mock(ItemEffect.class);

        when(hitscanLauncherFactory.create(any(HitscanProperties.class), eq(itemEffect))).thenReturn(hitscanLauncher);
        when(itemEffectFactory.create(projectileSpec.effect)).thenReturn(itemEffect);

        ProjectileLauncher createdProjectileLauncher = projectileLauncherFactory.create(projectileSpec);

        ArgumentCaptor<HitscanProperties> hitscanPropertiesCaptor = ArgumentCaptor.forClass(HitscanProperties.class);
        verify(hitscanLauncherFactory).create(hitscanPropertiesCaptor.capture(), eq(itemEffect));
        
        HitscanProperties hitscanProperties = hitscanPropertiesCaptor.getValue();
        assertThat(hitscanProperties.launchSounds()).isEmpty();
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

    @Test
    void createReturnsInstanceOfItemLauncher() {
        ProjectileSpec projectileSpec = this.createProjectileSpec("ITEM");
        ItemEffect itemEffect = mock(ItemEffect.class);
        ItemLauncher itemLauncher = mock(ItemLauncher.class);
        TriggerExecutor triggerExecutor = mock(TriggerExecutor.class);

        Plugin plugin = mock(Plugin.class);
        when(plugin.getName()).thenReturn("Battlegrounds");

        NamespacedKey templateKey = new NamespacedKey(plugin, TEMPLATE_ID_KEY);

        when(itemEffectFactory.create(projectileSpec.effect)).thenReturn(itemEffect);
        when(itemLauncherFactory.create(any(ItemLaunchProperties.class), eq(itemEffect))).thenReturn(itemLauncher);
        when(namespacedKeyCreator.create(TEMPLATE_ID_KEY)).thenReturn(templateKey);
        when(triggerExecutorFactory.create(projectileSpec.triggers.get("impact"))).thenReturn(triggerExecutor);

        ProjectileLauncher createdProjectileLauncher = projectileLauncherFactory.create(projectileSpec);

        ArgumentCaptor<ItemLaunchProperties> propertiesCaptor = ArgumentCaptor.forClass(ItemLaunchProperties.class);
        verify(itemLauncherFactory).create(propertiesCaptor.capture(), eq(itemEffect));

        ItemLaunchProperties properties = propertiesCaptor.getValue();
        assertThat(properties.launchSounds()).isEmpty();
        assertThat(properties.velocity()).isEqualTo(VELOCITY);

        assertThat(createdProjectileLauncher).isEqualTo(itemLauncher);

        verify(itemLauncher).addTriggerExecutor(triggerExecutor);
    }

    private ProjectileSpec createProjectileSpec(String type) {
        ParticleEffectSpec trajectoryParticleEffectSpec = new ParticleEffectSpec();
        trajectoryParticleEffectSpec.particle = "FLAME";
        trajectoryParticleEffectSpec.count = 1;
        trajectoryParticleEffectSpec.offsetX = 0.1;
        trajectoryParticleEffectSpec.offsetY = 0.2;
        trajectoryParticleEffectSpec.offsetZ = 0.3;
        trajectoryParticleEffectSpec.extra = 0.0;

        ItemSpec itemSpec = new ItemSpec();
        itemSpec.material = "STICK";
        itemSpec.damage = 1;

        TriggerSpec triggerSpec = new TriggerSpec();
        triggerSpec.type = "BLOCK_IMPACT";
        triggerSpec.delay = 5L;
        triggerSpec.interval = 1L;

        ProjectileSpec projectileSpec = new ProjectileSpec();
        projectileSpec.type = type;
        projectileSpec.item = itemSpec;
        projectileSpec.trajectoryParticleEffect = trajectoryParticleEffectSpec;
        projectileSpec.velocity = VELOCITY;
        projectileSpec.triggers = Map.of("impact", triggerSpec);
        return projectileSpec;
    }
}
