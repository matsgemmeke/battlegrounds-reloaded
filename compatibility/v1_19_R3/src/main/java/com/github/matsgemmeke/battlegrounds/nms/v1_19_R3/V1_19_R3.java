package com.github.matsgemmeke.battlegrounds.nms.v1_19_R3;

import com.github.matsgemmeke.battlegrounds.InternalsProvider;
import net.minecraft.network.protocol.game.PacketPlayOutAbilities;
import net.minecraft.world.entity.player.PlayerAbilities;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class V1_19_R3 implements InternalsProvider {

    public void setScopeLevel(@NotNull Player player, float scopeLevel) {
        PlayerAbilities abilities = new PlayerAbilities();
        abilities.g = scopeLevel;

        PacketPlayOutAbilities packet = new PacketPlayOutAbilities(abilities);

        ((CraftPlayer) player).getHandle().b.a(packet);
    }
}
