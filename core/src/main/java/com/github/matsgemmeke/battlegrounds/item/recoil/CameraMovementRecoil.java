package com.github.matsgemmeke.battlegrounds.item.recoil;

import com.github.matsgemmeke.battlegrounds.InternalsProvider;
import com.github.matsgemmeke.battlegrounds.api.entity.BattleItemHolder;
import com.github.matsgemmeke.battlegrounds.api.entity.BattlePlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Timer;

public class CameraMovementRecoil implements RecoilSystem {

    private static final long defaultRotationMillis = 20;

    private float horizontalRecoil;
    private float verticalRecoil;
    @NotNull
    private InternalsProvider internals;
    private long recoveryDuration;
    private long rotationDuration;
    @NotNull
    private Timer timer;

    public CameraMovementRecoil(@NotNull InternalsProvider internals, @NotNull Timer timer) {
        this.internals = internals;
        this.timer = timer;
    }

    public float getHorizontalRecoil() {
        return horizontalRecoil;
    }

    public void setHorizontalRecoil(float horizontalRecoil) {
        this.horizontalRecoil = horizontalRecoil;
    }

    public long getRecoveryDuration() {
        return recoveryDuration;
    }

    public void setRecoveryDuration(long recoveryDuration) {
        this.recoveryDuration = recoveryDuration;
    }

    public long getRotationDuration() {
        return rotationDuration;
    }

    public void setRotationDuration(long rotationDuration) {
        this.rotationDuration = rotationDuration;
    }

    public float getVerticalRecoil() {
        return verticalRecoil;
    }

    public void setVerticalRecoil(float verticalRecoil) {
        this.verticalRecoil = verticalRecoil;
    }

    @NotNull
    public Location produceRecoil(@NotNull BattleItemHolder holder, @NotNull Location direction, double relativeAccuracy) {
        // Only apply this recoil type to player entities
        if (!(holder instanceof BattlePlayer battlePlayer)) {
            return direction;
        }

        Player player = battlePlayer.getEntity();

        // If the duration of the recoil rotation is zero, simply set the camera rotation
        if (rotationDuration <= 0.0) {
            internals.setPlayerRotation(player, horizontalRecoil, verticalRecoil);
            return direction;
        }

        int rotationAmount = (int) (rotationDuration / defaultRotationMillis);
        float yawRotation = horizontalRecoil / rotationAmount;
        float pitchRotation = verticalRecoil / rotationAmount;

        CameraMovementTask task = new CameraMovementTask(player, internals);
        task.setRecoverDirection(player.getEyeLocation());
        task.setRotationAmount(rotationAmount);
        task.setYawRotation(yawRotation);
        task.setPitchRotation(pitchRotation);

        timer.scheduleAtFixedRate(task, 0, defaultRotationMillis);

        // Camera movement recoil does not affect the projectile direction, so return the original direction
        return direction;
    }
}
