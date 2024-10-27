package nl.matsgemmeke.battlegrounds.item.effect.explosion;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.item.RangeProfile;
import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.effect.source.ActivationSource;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.mockito.Mockito.*;

public class ExplosionEffectTest {

    private static final double LONG_RANGE_DAMAGE = 25.0;
    private static final double LONG_RANGE_DISTANCE = 10.0;
    private static final double MEDIUM_RANGE_DAMAGE = 75.0;
    private static final double MEDIUM_RANGE_DISTANCE = 5.0;
    private static final double SHORT_RANGE_DAMAGE = 150.0;
    private static final double SHORT_RANGE_DISTANCE = 2.5;

    private boolean breakBlocks;
    private boolean setFire;
    private float power;
    private RangeProfile rangeProfile;
    private TargetFinder targetFinder;

    @Before
    public void setUp() {
        breakBlocks = false;
        setFire = false;
        power = 2.0F;
        rangeProfile = new RangeProfile(LONG_RANGE_DAMAGE, LONG_RANGE_DISTANCE, MEDIUM_RANGE_DAMAGE, MEDIUM_RANGE_DISTANCE, SHORT_RANGE_DAMAGE, SHORT_RANGE_DISTANCE);
        targetFinder = mock(TargetFinder.class);
    }

    @Test
    public void createExplosionAtSourceLocation() {
        Entity entity = mock(Entity.class);
        World world = mock(World.class);
        Location sourceLocation = new Location(world, 1, 1, 1);

        ActivationSource source = mock(ActivationSource.class);
        when(source.getLocation()).thenReturn(sourceLocation);
        when(source.getWorld()).thenReturn(world);

        ItemHolder holder = mock(ItemHolder.class);
        when(holder.getEntity()).thenReturn(entity);

        ExplosionSettings settings = new ExplosionSettings(power, setFire, breakBlocks);

        ExplosionEffect effect = new ExplosionEffect(settings, rangeProfile, targetFinder);
        effect.activate(holder, source);

        verify(source).remove();
        verify(world).createExplosion(sourceLocation, power, setFire, breakBlocks, entity);
    }

    @Test
    public void shouldInflictRangedDamageOnAllEntitiesInsideTheLongRangeDistance() {
        World world = mock(World.class);
        Location sourceLocation = new Location(world, 1, 1, 1);
        Location targetLocation = new Location(world, 8, 1, 1);

        Entity holderEntity = mock(Entity.class);
        when(holderEntity.getLocation()).thenReturn(sourceLocation);
        when(holderEntity.getWorld()).thenReturn(world);

        Entity targetEntity = mock(Entity.class);
        when(targetEntity.getLocation()).thenReturn(targetLocation);
        when(targetEntity.getWorld()).thenReturn(world);

        ItemHolder holder = mock(ItemHolder.class);
        when(holder.getEntity()).thenReturn(holderEntity);

        GameEntity target = mock(GameEntity.class);
        when(target.getEntity()).thenReturn(targetEntity);

        ActivationSource source = mock(ActivationSource.class);
        when(source.getLocation()).thenReturn(sourceLocation);
        when(source.getWorld()).thenReturn(world);

        when(targetFinder.findTargets(holder, sourceLocation, LONG_RANGE_DISTANCE)).thenReturn(List.of(holder, target));

        ExplosionSettings settings = new ExplosionSettings(power, setFire, breakBlocks);

        ExplosionEffect effect = new ExplosionEffect(settings, rangeProfile, targetFinder);
        effect.activate(holder, source);

        verify(source).remove();
        verify(holder).damage(SHORT_RANGE_DAMAGE);
        verify(target).damage(LONG_RANGE_DAMAGE);
    }
}
