package com.github.matsgemmeke.battlegrounds.item.recoil;

import com.github.matsgemmeke.battlegrounds.InternalsProvider;
import com.github.matsgemmeke.battlegrounds.api.entity.BattleItemHolder;
import com.github.matsgemmeke.battlegrounds.api.entity.BattlePlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.Timer;

public class CameraMovementRecoil implements RecoilSystem {

    // The default value is 20 milliseconds, which equals to 50 times per second
    private static final long defaultRotationDuration = 20;

    private Float[] horizontalRecoilValues;
    private Float[] verticalRecoilValues;
    private float recoveryRate;
    @NotNull
    private InternalsProvider internals;
    private long recoilDuration;
    private long recoveryDuration;
    @NotNull
    private Random random;
    @NotNull
    private Timer timer;

    public CameraMovementRecoil(@NotNull InternalsProvider internals, @NotNull Timer timer) {
        this.internals = internals;
        this.timer = timer;
        this.random = new Random();
    }

    public Float[] getHorizontalRecoilValues() {
        return horizontalRecoilValues;
    }

    public void setHorizontalRecoilValues(Float[] horizontalRecoilValues) {
        this.horizontalRecoilValues = horizontalRecoilValues;
    }

    public long getRecoilDuration() {
        return recoilDuration;
    }

    public void setRecoilDuration(long recoilDuration) {
        this.recoilDuration = recoilDuration;
    }

    public long getRecoveryDuration() {
        return recoveryDuration;
    }

    public void setRecoveryDuration(long recoveryDuration) {
        this.recoveryDuration = recoveryDuration;
    }

    public float getRecoveryRate() {
        return recoveryRate;
    }

    public void setRecoveryRate(float recoveryRate) {
        this.recoveryRate = recoveryRate;
    }

    public Float[] getVerticalRecoilValues() {
        return verticalRecoilValues;
    }

    public void setVerticalRecoilValues(Float[] verticalRecoilValues) {
        this.verticalRecoilValues = verticalRecoilValues;
    }

    @NotNull
    public Location produceRecoil(@NotNull BattleItemHolder holder, @NotNull Location direction) {
        // Only apply this recoil type to player entities
        if (!(holder instanceof BattlePlayer battlePlayer)) {
            return direction;
        }

        Player player = battlePlayer.getEntity();

        // Select random values from the given recoil value arrays
        float horizontalRecoil = horizontalRecoilValues[random.nextInt(horizontalRecoilValues.length)];
        float verticalRecoil = verticalRecoilValues[random.nextInt(verticalRecoilValues.length)];

        // If the duration of the recoil rotation is zero, simply set the camera rotation
        if (recoilDuration <= 0) {
            internals.setPlayerRotation(player, horizontalRecoil, verticalRecoil);
            return direction;
        }

        int rotationAmount = (int) (recoilDuration / defaultRotationDuration);
        float yawRotation = horizontalRecoil / rotationAmount / (float) holder.getRelativeAccuracy();
        float pitchRotation = verticalRecoil / rotationAmount / (float) holder.getRelativeAccuracy();

        CameraMovementTask task = new CameraMovementTask(player, internals);
        task.setRotationAmount(rotationAmount);
        task.setYawRotation(yawRotation);
        task.setPitchRotation(pitchRotation);

        timer.scheduleAtFixedRate(task, 0, defaultRotationDuration);

        // Camera movement recoil does not affect the projectile direction, so return the original direction
        return direction;
    }
}
