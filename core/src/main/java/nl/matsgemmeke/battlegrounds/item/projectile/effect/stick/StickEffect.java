package nl.matsgemmeke.battlegrounds.item.projectile.effect.stick;

import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.item.projectile.Projectile;
import nl.matsgemmeke.battlegrounds.item.projectile.effect.BaseProjectileEffect;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class StickEffect extends BaseProjectileEffect {

    private static final Vector ZERO = new Vector(0, 0, 0);

    @NotNull
    private final AudioEmitter audioEmitter;
    @NotNull
    private final List<GameSound> stickSounds;

    public StickEffect(@NotNull AudioEmitter audioEmitter, @NotNull List<GameSound> stickSounds) {
        this.audioEmitter = audioEmitter;
        this.stickSounds = stickSounds;
    }

    public void onLaunch(@NotNull Projectile projectile) {
    }

    public void performEffect(@NotNull Projectile projectile) {
        audioEmitter.playSounds(stickSounds, projectile.getLocation());

        projectile.setGravity(false);
        projectile.setVelocity(ZERO);
    }
}
