package nl.matsgemmeke.battlegrounds.nms.v26;

import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.phys.Vec3;
import nl.matsgemmeke.battlegrounds.VersionAdapter;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Set;

public class V26VersionAdapter implements VersionAdapter {

    private static final Set<Relative> PLAYER_ROTATION_RELATIVES = Set.of(
            Relative.X, Relative.Y, Relative.Z,
            Relative.DELTA_X, Relative.DELTA_Y, Relative.DELTA_Z,
            Relative.X_ROT, Relative.Y_ROT
    );

    @Override
    public void setPlayerRotation(Player player, float yaw, float pitch) {
        ServerPlayer handle = ((CraftPlayer) player).getHandle();

        PositionMoveRotation positionMoveRotation = new PositionMoveRotation(Vec3.ZERO, Vec3.ZERO, yaw, pitch);
        ClientboundPlayerPositionPacket packet = new ClientboundPlayerPositionPacket(0, positionMoveRotation, PLAYER_ROTATION_RELATIVES);

        handle.connection.send(packet);
    }

    @Override
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
