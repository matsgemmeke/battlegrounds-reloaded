package com.github.matsgemmeke.battlegrounds.item.recoil;

import com.github.matsgemmeke.battlegrounds.api.entity.RecoilReceiver;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.Timer;

public class CameraMovementRecoil implements RecoilSystem {

    // The default value is 20 milliseconds, which equals to 50 times per second
    private static final long defaultRotationDuration = 20;

    private Float[] horizontalRecoilValues;
    private Float[] verticalRecoilValues;
    private float recoveryRate;
    private long recoilDuration;
    private long recoveryDuration;
    @NotNull
    private Random random;
    @NotNull
    private Timer timer;

    public CameraMovementRecoil(@NotNull Timer timer) {
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
    public Location produceRecoil(@NotNull RecoilReceiver receiver, @NotNull Location direction) {
        // Select random values from the given recoil value arrays
        float horizontalRecoil = horizontalRecoilValues[random.nextInt(horizontalRecoilValues.length)];
        float verticalRecoil = verticalRecoilValues[random.nextInt(verticalRecoilValues.length)];

        // If the duration of the recoil rotation is zero, simply set the camera rotation
        if (recoilDuration <= 0) {
            receiver.modifyCameraRotation(horizontalRecoil, verticalRecoil);
            return direction;
        }

        int rotationAmount = (int) (recoilDuration / defaultRotationDuration);
        float yawRotation = horizontalRecoil / rotationAmount / receiver.getRelativeAccuracy();
        float pitchRotation = verticalRecoil / rotationAmount / receiver.getRelativeAccuracy();

        int recoveryRotationAmount = (int) ((double) recoveryDuration / (double) recoilDuration * rotationAmount);

        CameraMovementTask task = new CameraMovementTask(receiver);
        task.setRecoilRotations(rotationAmount);
        task.setRecoveryRate(recoveryRate);
        task.setRecoveryRotations(recoveryRotationAmount);
        task.setYawRotation(yawRotation);
        task.setPitchRotation(pitchRotation);

        timer.scheduleAtFixedRate(task, 0, defaultRotationDuration);

        // Camera movement recoil does not affect the projectile direction, so return the original direction
        return direction;
    }
}
