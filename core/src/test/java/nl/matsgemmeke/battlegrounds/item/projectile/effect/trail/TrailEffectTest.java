package nl.matsgemmeke.battlegrounds.item.projectile.effect.trail;

import nl.matsgemmeke.battlegrounds.item.data.ParticleEffect;
import nl.matsgemmeke.battlegrounds.item.projectile.Projectile;
import nl.matsgemmeke.battlegrounds.item.trigger.Trigger;
import nl.matsgemmeke.battlegrounds.item.trigger.TriggerObserver;
import nl.matsgemmeke.battlegrounds.util.world.ParticleEffectSpawner;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.mockito.Mockito.*;

public class TrailEffectTest {

    private static final int MAX_ACTIVATIONS = 2;
    private static final ParticleEffect PARTICLE_EFFECT = new ParticleEffect(Particle.FLAME, 1, 0, 0, 0, 0, null, null);

    private Entity deployerEntity;
    private ParticleEffectSpawner particleEffectSpawner;
    private Projectile projectile;
    private TrailProperties properties;
    private Trigger trigger;

    @BeforeEach
    public void setUp() {
        deployerEntity = mock(Entity.class);
        particleEffectSpawner = mock(ParticleEffectSpawner.class);
        projectile = mock(Projectile.class);
        properties = new TrailProperties(PARTICLE_EFFECT, MAX_ACTIVATIONS);
        trigger = mock(Trigger.class);
    }

    @Test
    public void onLaunchStartsTriggersWithObserverThatSpawnsParticleAtProjectileLocation() {
        Location projectileLocation = new Location(null, 0, 0, 0);
        when(projectile.getLocation()).thenReturn(projectileLocation);

        TrailEffect effect = new TrailEffect(particleEffectSpawner, properties);
        effect.addTrigger(trigger);
        effect.onLaunch(deployerEntity, projectile);

        ArgumentCaptor<TriggerObserver> triggerObserverCaptor = ArgumentCaptor.forClass(TriggerObserver.class);
        verify(trigger).addObserver(triggerObserverCaptor.capture());

        triggerObserverCaptor.getValue().onActivate();

        verify(particleEffectSpawner).spawnParticleEffect(PARTICLE_EFFECT, projectileLocation);
    }
}
