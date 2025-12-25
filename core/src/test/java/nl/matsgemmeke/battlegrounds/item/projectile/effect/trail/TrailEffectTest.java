package nl.matsgemmeke.battlegrounds.item.projectile.effect.trail;

import nl.matsgemmeke.battlegrounds.game.damage.DamageSource;
import nl.matsgemmeke.battlegrounds.item.data.ParticleEffect;
import nl.matsgemmeke.battlegrounds.item.projectile.Projectile;
import nl.matsgemmeke.battlegrounds.item.trigger.*;
import nl.matsgemmeke.battlegrounds.util.world.ParticleEffectSpawner;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrailEffectTest {

    private static final int MAX_ACTIVATIONS = 2;
    private static final ParticleEffect PARTICLE_EFFECT = new ParticleEffect(Particle.FLAME, 1, 0, 0, 0, 0, null, null);
    private static final TrailProperties PROPERTIES = new TrailProperties(PARTICLE_EFFECT, MAX_ACTIVATIONS);

    @Mock
    private DamageSource damageSource;
    @Mock
    private ParticleEffectSpawner particleEffectSpawner;
    @Mock
    private Projectile projectile;
    @Mock
    private TriggerExecutor triggerExecutor;
    @Mock
    private TriggerRun triggerRun;

    private TrailEffect effect;

    @BeforeEach
    void setUp() {
        when(triggerExecutor.createTriggerRun(any(TriggerContext.class))).thenReturn(triggerRun);

        effect = new TrailEffect(particleEffectSpawner, PROPERTIES);
    }

    @Test
    void onLaunchStartsTriggerRunThatCancelWhenProjectileNoLongerExists() {
        when(projectile.exists()).thenReturn(false);

        effect.addTriggerExecutor(triggerExecutor);
        effect.onLaunch(damageSource, projectile);

        ArgumentCaptor<TriggerObserver> triggerObserverCaptor = ArgumentCaptor.forClass(TriggerObserver.class);
        verify(triggerRun).addObserver(triggerObserverCaptor.capture());

        triggerObserverCaptor.getValue().onActivate();

        verify(triggerRun).cancel();
        verify(particleEffectSpawner, never()).spawnParticleEffect(any(ParticleEffect.class), any(Location.class));
    }

    @Test
    void onLaunchStartsTriggersWithObserverThatSpawnsParticleAtProjectileLocation() {
        Location projectileLocation = new Location(null, 0, 0, 0);

        when(projectile.exists()).thenReturn(true);
        when(projectile.getLocation()).thenReturn(projectileLocation);

        effect.addTriggerExecutor(triggerExecutor);
        effect.onLaunch(damageSource, projectile);

        ArgumentCaptor<TriggerObserver> triggerObserverCaptor = ArgumentCaptor.forClass(TriggerObserver.class);
        verify(triggerRun).addObserver(triggerObserverCaptor.capture());

        triggerObserverCaptor.getValue().onActivate();

        verify(particleEffectSpawner).spawnParticleEffect(PARTICLE_EFFECT, projectileLocation);
    }
}
