package nl.matsgemmeke.battlegrounds.item.mechanism;

import nl.matsgemmeke.battlegrounds.entity.ItemHolder;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExplosionMechanism implements ItemMechanism {

    private static final boolean BREAK_BLOCKS = false;
    private static final boolean SET_FIRE = false;

    @NotNull
    private GameContext context;

    public ExplosionMechanism(@NotNull GameContext context) {
        this.context = context;
    }

    public void activate(@Nullable Item droppedItem, @NotNull ItemHolder holder) {
        Location location;
        World world;

        if (droppedItem != null) {
            location = droppedItem.getLocation();
            world = droppedItem.getWorld();
            droppedItem.remove();
        } else {
            location = holder.getEntity().getLocation();
            world = holder.getEntity().getWorld();
        }

        world.createExplosion(location, 2F, SET_FIRE, BREAK_BLOCKS, droppedItem);
        System.out.println("kaboom");
    }
}
