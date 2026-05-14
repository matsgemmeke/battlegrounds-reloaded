package nl.matsgemmeke.battlegrounds;

import org.bukkit.entity.Player;

public interface InternalsProvider {

    void setPlayerRotation(Player player, float yaw, float pitch);

    void setWalkSpeed(Player player, float walkSpeed);
}
