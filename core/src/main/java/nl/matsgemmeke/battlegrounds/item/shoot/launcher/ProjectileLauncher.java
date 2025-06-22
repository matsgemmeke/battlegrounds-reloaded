package nl.matsgemmeke.battlegrounds.item.shoot.launcher;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public interface ProjectileLauncher {

    void launch(@NotNull Location launchDirection);
}
