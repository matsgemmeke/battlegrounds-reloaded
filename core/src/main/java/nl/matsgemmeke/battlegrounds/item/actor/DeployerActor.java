package nl.matsgemmeke.battlegrounds.item.actor;

import nl.matsgemmeke.battlegrounds.entity.GameEntity;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class DeployerActor implements Actor {

    // Currently only works for humanoid entities
    private static final double HAND_HEIGHT_OFFSET = 1.0;

    private final GameEntity gameEntity;

    public DeployerActor(GameEntity gameEntity) {
        this.gameEntity = gameEntity;
    }

    @Override
    public boolean exists() {
        return gameEntity.isValid();
    }

    @Override
    public Location getLocation() {
        return gameEntity.getLocation().add(0, HAND_HEIGHT_OFFSET, 0);
    }

    @Override
    public Vector getVelocity() {
        return gameEntity.getVelocity();
    }

    @Override
    public World getWorld() {
        return gameEntity.getWorld();
    }
}
