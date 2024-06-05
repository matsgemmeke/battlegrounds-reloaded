package nl.matsgemmeke.battlegrounds.nms.v1_20_R2;

import nl.matsgemmeke.battlegrounds.InternalsProvider;
import net.minecraft.network.protocol.game.PacketPlayOutAbilities;
import net.minecraft.network.protocol.game.PacketPlayOutPosition;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.entity.player.PlayerAbilities;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class V1_20_R2 implements InternalsProvider {

    public void setPlayerRotation(@NotNull Player player, float yaw, float pitch) {
        PacketPlayOutPosition packet = new PacketPlayOutPosition(0.0d, 0.0d, 0.0d, yaw, pitch, RelativeMovement.f, 0);

        (((CraftPlayer)player).getHandle()).c.b(packet);
    }

    public void setWalkSpeed(@NotNull Player player, float scopeLevel) {
        PlayerAbilities abilities = new PlayerAbilities();
        abilities.g = scopeLevel;

        PacketPlayOutAbilities packet = new PacketPlayOutAbilities(abilities);

        ((CraftPlayer) player).getHandle().c.b(packet);
    }
}
