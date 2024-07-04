package nl.matsgemmeke.battlegrounds.item.mechanism;

import nl.matsgemmeke.battlegrounds.entity.ItemHolder;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExplosionMechanism implements ItemMechanism {

    private boolean breakBlocks;
    private boolean setFire;
    private float power;
    @NotNull
    private GameContext context;

    public ExplosionMechanism(@NotNull GameContext context, float power, boolean setFire, boolean breakBlocks) {
        this.context = context;
        this.power = power;
        this.setFire = setFire;
        this.breakBlocks = breakBlocks;
    }

    public void activate(@Nullable Item droppedItem, @NotNull ItemHolder holder) {
        Entity source;
        Location location;
        World world;

        if (droppedItem != null) {
            source = droppedItem;
            location = droppedItem.getLocation();
            world = droppedItem.getWorld();
            droppedItem.remove();
        } else {
            source = holder.getEntity();
            location = holder.getEntity().getLocation();
            world = holder.getEntity().getWorld();
        }

        world.createExplosion(location, power, setFire, breakBlocks, source);
    }
}
