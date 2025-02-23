package nl.matsgemmeke.battlegrounds.entity;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface DefaultGamePlayerFactory {

    @NotNull
    GamePlayer create(@NotNull Player player);
}
