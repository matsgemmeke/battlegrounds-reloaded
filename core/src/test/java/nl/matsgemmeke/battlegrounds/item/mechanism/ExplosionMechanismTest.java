package nl.matsgemmeke.battlegrounds.item.mechanism;

import nl.matsgemmeke.battlegrounds.entity.ItemHolder;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class ExplosionMechanismTest {

    private boolean breakBlocks;
    private boolean setFire;
    private float power;
    private GameContext context;

    @Before
    public void setUp() {
        context = mock(GameContext.class);
        breakBlocks = false;
        setFire = false;
        power = 2.0F;
    }

    @Test
    public void shouldCreateExplosionAtDroppedItemLocation() {
        World world = mock(World.class);
        Location location = new Location(world, 1, 1, 1);

        Item droppedItem = mock(Item.class);
        when(droppedItem.getLocation()).thenReturn(location);
        when(droppedItem.getWorld()).thenReturn(world);

        ItemHolder holder = mock(ItemHolder.class);

        ExplosionMechanism explosionMechanism = new ExplosionMechanism(context, power, setFire, breakBlocks);
        explosionMechanism.activate(droppedItem, holder);

        verify(world).createExplosion(location, power, setFire, breakBlocks, droppedItem);
    }

    @Test
    public void shouldCreateExplosionAtLocationOfHolderIfNoItemWasThrown() {
        World world = mock(World.class);
        Location location = new Location(world, 1, 1, 1);

        Entity entity = mock(Entity.class);
        when(entity.getLocation()).thenReturn(location);
        when(entity.getWorld()).thenReturn(world);

        ItemHolder holder = mock(ItemHolder.class);
        when(holder.getEntity()).thenReturn(entity);

        ExplosionMechanism explosionMechanism = new ExplosionMechanism(context, power, setFire, breakBlocks);
        explosionMechanism.activate(null, holder);

        verify(world).createExplosion(location, power, setFire, breakBlocks, entity);
    }
}
