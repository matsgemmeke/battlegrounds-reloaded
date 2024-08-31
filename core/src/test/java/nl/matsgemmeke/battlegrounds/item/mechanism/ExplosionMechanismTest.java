package nl.matsgemmeke.battlegrounds.item.mechanism;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import nl.matsgemmeke.battlegrounds.game.component.CollisionDetector;
import nl.matsgemmeke.battlegrounds.item.RangeProfile;
import nl.matsgemmeke.battlegrounds.item.deployment.Deployable;
import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.mockito.Mockito.*;

public class ExplosionMechanismTest {

    private static final double LONG_RANGE_DAMAGE = 25.0;
    private static final double LONG_RANGE_DISTANCE = 10.0;
    private static final double MEDIUM_RANGE_DAMAGE = 75.0;
    private static final double MEDIUM_RANGE_DISTANCE = 5.0;
    private static final double SHORT_RANGE_DAMAGE = 150.0;
    private static final double SHORT_RANGE_DISTANCE = 2.5;

    private boolean breakBlocks;
    private boolean setFire;
    private CollisionDetector collisionDetector;
    private float power;
    private RangeProfile rangeProfile;

    @Before
    public void setUp() {
        collisionDetector = mock(CollisionDetector.class);
        breakBlocks = false;
        setFire = false;
        power = 2.0F;
        rangeProfile = new RangeProfile(LONG_RANGE_DAMAGE, LONG_RANGE_DISTANCE, MEDIUM_RANGE_DAMAGE, MEDIUM_RANGE_DISTANCE, SHORT_RANGE_DAMAGE, SHORT_RANGE_DISTANCE);
    }

    @Test
    public void shouldCreateExplosionAtLocationOfHolder() {
        Entity entity = mock(Entity.class);
        World world = mock(World.class);
        Location location = new Location(world, 1, 1, 1);

        ItemHolder holder = mock(ItemHolder.class);
        when(holder.getEntity()).thenReturn(entity);
        when(holder.getLocation()).thenReturn(location);
        when(holder.getWorld()).thenReturn(world);

        ExplosionMechanism explosionMechanism = new ExplosionMechanism(collisionDetector, rangeProfile, power, setFire, breakBlocks);
        explosionMechanism.activate(holder);

        verify(world).createExplosion(location, power, setFire, breakBlocks, entity);
    }

    @Test
    public void shouldCreateExplosionAtDeployedObjectLocation() {
        World world = mock(World.class);
        Location location = new Location(world, 1, 1, 1);
        Item itemEntity = mock(Item.class);

        Deployable object = mock(Deployable.class);
        when(object.getDamageSource()).thenReturn(itemEntity);
        when(object.getLocation()).thenReturn(location);
        when(object.getWorld()).thenReturn(world);

        ItemHolder holder = mock(ItemHolder.class);

        ExplosionMechanism explosionMechanism = new ExplosionMechanism(collisionDetector, rangeProfile, power, setFire, breakBlocks);
        explosionMechanism.activate(holder, object);

        verify(object).remove();
        verify(world).createExplosion(location, power, setFire, breakBlocks, itemEntity);
    }

    @Test
    public void shouldInflictRangedDamageOnAllEntitiesInsideTheLongRangeDistance() {
        World world = mock(World.class);
        Location holderLocation = new Location(world, 1, 1, 1);
        Location targetLocation = new Location(world, 8, 1, 1);

        Entity holderEntity = mock(Entity.class);
        when(holderEntity.getLocation()).thenReturn(holderLocation);
        when(holderEntity.getWorld()).thenReturn(world);

        Entity targetEntity = mock(Entity.class);
        when(targetEntity.getLocation()).thenReturn(targetLocation);
        when(targetEntity.getWorld()).thenReturn(world);

        ItemHolder holder = mock(ItemHolder.class);
        when(holder.getEntity()).thenReturn(holderEntity);
        when(holder.getLocation()).thenReturn(holderLocation);
        when(holder.getWorld()).thenReturn(world);

        GameEntity target = mock(GameEntity.class);
        when(target.getEntity()).thenReturn(targetEntity);

        when(collisionDetector.findTargets(holder, holderLocation, LONG_RANGE_DISTANCE)).thenReturn(List.of(holder, target));

        ExplosionMechanism explosionMechanism = new ExplosionMechanism(collisionDetector, rangeProfile, power, setFire, breakBlocks);
        explosionMechanism.activate(holder);

        verify(holder).damage(SHORT_RANGE_DAMAGE);
        verify(target).damage(LONG_RANGE_DAMAGE);
    }
}
