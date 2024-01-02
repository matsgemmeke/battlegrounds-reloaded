package com.github.matsgemmeke.battlegrounds.item.recoil;

import com.github.matsgemmeke.battlegrounds.InternalsProvider;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.TimerTask;

public class CameraMovementTask extends TimerTask {

    private boolean recovering;
    private float pitchRotation;
    private float yawRotation;
    private float recoveryRate;
    private int recoveryRotationAmount;
    private int rotationAmount;
    private int rotationCount;
    @NotNull
    private InternalsProvider internals;
    @NotNull
    private Player player;
    @NotNull
    private Random random;

    public CameraMovementTask(@NotNull Player player, @NotNull InternalsProvider internals) {
        this.player = player;
        this.internals = internals;
        this.random = new Random();
        this.recovering = false;
        this.rotationCount = 0;
    }

    public float getPitchRotation() {
        return pitchRotation;
    }

    public void setPitchRotation(float pitchRotation) {
        this.pitchRotation = pitchRotation;
    }

    public int getRotationAmount() {
        return rotationAmount;
    }

    public void setRotationAmount(int rotationAmount) {
        this.rotationAmount = rotationAmount;
    }

    public float getRecoveryRate() {
        return recoveryRate;
    }

    public void setRecoveryRate(float recoveryRate) {
        this.recoveryRate = recoveryRate;
    }

    public int getRecoveryRotationAmount() {
        return recoveryRotationAmount;
    }

    public void setRecoveryRotationAmount(int recoveryRotationAmount) {
        this.recoveryRotationAmount = recoveryRotationAmount;
    }

    public float getYawRotation() {
        return yawRotation;
    }

    public void setYawRotation(float yawRotation) {
        this.yawRotation = yawRotation;
    }

    public void run() {
        if (player.isDead() || !player.isOnline()) {
            this.cancel();
            return;
        }

        if (++rotationCount > rotationAmount) {
            if (recoveryRate <= 0.0f || recovering) {
                this.cancel();
                return;
            }

            // The difference between the amount of rotations for the recoil and the recovery
            float recoveryFactor = (float) recoveryRotationAmount / rotationAmount;

            // Modify the rotation values by the recovery rate and the
            yawRotation = (yawRotation * -recoveryRate) / recoveryFactor;
            pitchRotation = (pitchRotation * -recoveryRate) / recoveryFactor;

            recovering = true;
            rotationAmount = recoveryRotationAmount;
            rotationCount = 0;
        }

        internals.setPlayerRotation(player, yawRotation, pitchRotation);
    }
}
