package nl.matsgemmeke.battlegrounds.item.effect.source;

import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class HeldItemTest {

    private ItemHolder holder;
    private ItemStack itemStack;

    @BeforeEach
    public void setUp() {
        holder = mock(ItemHolder.class);
        itemStack = new ItemStack(Material.SHEARS);
    }

    @Test
    public void existsReturnsTrueIfTheHolderEntityIsNotDead() {
        Player player = mock(Player.class);
        when(player.isDead()).thenReturn(false);
        when(holder.getEntity()).thenReturn(player);

        HeldItem heldItem = new HeldItem(holder, itemStack);
        boolean exists = heldItem.exists();

        assertTrue(exists);
    }

    @Test
    public void existsReturnsFalseIfTheHolderEntityIsDead() {
        Player player = mock(Player.class);
        when(player.isDead()).thenReturn(true);
        when(holder.getEntity()).thenReturn(player);

        HeldItem heldItem = new HeldItem(holder, itemStack);
        boolean exists = heldItem.exists();

        assertFalse(exists);
    }

    @Test
    public void getLocationReturnsTheHolderLocation() {
        Location holderLocation = new Location(null, 0, 0, 0);
        when(holder.getLocation()).thenReturn(holderLocation);

        HeldItem heldItem = new HeldItem(holder, itemStack);
        Location itemLocation = heldItem.getLocation();

        assertEquals(holderLocation, itemLocation);
    }

    @Test
    public void getWorldReturnsTheHolderWorld() {
        World holderWorld = mock(World.class);
        when(holder.getWorld()).thenReturn(holderWorld);

        HeldItem heldItem = new HeldItem(holder, itemStack);
        World itemWorld = heldItem.getWorld();

        assertEquals(holderWorld, itemWorld);
    }

    @Test
    public void isDeployedAlwaysReturnsFalse() {
        HeldItem heldItem = new HeldItem(holder, itemStack);
        boolean deployed = heldItem.isDeployed();

        assertFalse(deployed);
    }

    @Test
    public void removeRemovesTheItemStackFromTheHolder() {
        HeldItem heldItem = new HeldItem(holder, itemStack);
        heldItem.remove();

        verify(holder).removeItem(itemStack);
    }
}
