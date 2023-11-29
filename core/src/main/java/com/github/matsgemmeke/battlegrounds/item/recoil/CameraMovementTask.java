package com.github.matsgemmeke.battlegrounds.item.recoil;

import com.github.matsgemmeke.battlegrounds.InternalsProvider;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;
import java.util.TimerTask;

public class CameraMovementTask extends TimerTask {

    private float pitchRotation;
    private float yawRotation;
    private int rotationAmount;
    private int rotationCount;
    @NotNull
    private InternalsProvider internals;
    @Nullable
    private Location recoverDirection;
    @NotNull
    private Player player;
    @NotNull
    private Random random;

    public CameraMovementTask(@NotNull Player player, @NotNull InternalsProvider internals) {
        this.player = player;
        this.internals = internals;
        this.random = new Random();
        this.rotationCount = 0;
    }

    public float getPitchRotation() {
        return pitchRotation;
    }

    public void setPitchRotation(float pitchRotation) {
        this.pitchRotation = pitchRotation;
    }

    @Nullable
    public Location getRecoverDirection() {
        return recoverDirection;
    }

    public void setRecoverDirection(@Nullable Location recoverDirection) {
        this.recoverDirection = recoverDirection;
    }

    public int getRotationAmount() {
        return rotationAmount;
    }

    public void setRotationAmount(int rotationAmount) {
        this.rotationAmount = rotationAmount;
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

        if (++ rotationCount > rotationAmount) {
            this.cancel();
            return;
        }

        internals.setPlayerRotation(player, yawRotation, pitchRotation);
    }
}
