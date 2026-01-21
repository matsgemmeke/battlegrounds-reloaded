package nl.matsgemmeke.battlegrounds.item.effect;

import nl.matsgemmeke.battlegrounds.game.damage.DamageTarget;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Provides information about the optional collision that caused the activation of an {@link ItemEffect}.
 */
public class CollisionResult {

    @Nullable
    private final Block hitBlock;
    @Nullable
    private final DamageTarget hitTarget;
    @Nullable
    private final Location hitLocation;

    public CollisionResult(@Nullable Block hitBlock, @Nullable DamageTarget hitTarget, @Nullable Location hitLocation) {
        this.hitBlock = hitBlock;
        this.hitTarget = hitTarget;
        this.hitLocation = hitLocation;
    }

    public Optional<Block> getHitBlock() {
        return Optional.ofNullable(hitBlock);
    }

    public Optional<DamageTarget> getHitTarget() {
        return Optional.ofNullable(hitTarget);
    }

    public Optional<Location> getHitLocation() {
        return Optional.ofNullable(hitLocation);
    }
}
