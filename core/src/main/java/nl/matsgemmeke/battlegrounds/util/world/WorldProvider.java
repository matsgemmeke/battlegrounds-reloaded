package nl.matsgemmeke.battlegrounds.util.world;

import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.Optional;

public class WorldProvider {

    public Optional<World> getWorld(String name) {
        return Optional.ofNullable(Bukkit.getWorld(name));
    }
}
