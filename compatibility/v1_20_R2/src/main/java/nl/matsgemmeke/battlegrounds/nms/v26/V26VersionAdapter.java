package nl.matsgemmeke.battlegrounds.nms.v26;

import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Abilities;
import nl.matsgemmeke.battlegrounds.VersionAdapter;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class V26VersionAdapter implements VersionAdapter {

    public void setPlayerRotation(Player player, float yaw, float pitch) {
    }

    public void setWalkSpeed(Player player, float scopeLevel) {
        ServerPlayer handle = ((CraftPlayer) player).getHandle();

        Abilities abilities = handle.getAbilities();
        Abilities updatedAbilities = new Abilities();
        updatedAbilities.invulnerable = abilities.invulnerable;
        updatedAbilities.flying = abilities.flying;
        updatedAbilities.mayfly = abilities.mayfly;
        updatedAbilities.instabuild = abilities.instabuild;
        updatedAbilities.mayBuild = abilities.mayBuild;
        updatedAbilities.setFlyingSpeed(abilities.getFlyingSpeed());
        updatedAbilities.setWalkingSpeed(scopeLevel);

        handle.connection.send(new ClientboundPlayerAbilitiesPacket(updatedAbilities));
    }
}
