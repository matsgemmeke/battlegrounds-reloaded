package nl.matsgemmeke.battlegrounds;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface InternalsProvider {

    void setPlayerRotation(@NotNull Player player, float yaw, float pitch);

    void setWalkSpeed(@NotNull Player player, float walkSpeed);
}
