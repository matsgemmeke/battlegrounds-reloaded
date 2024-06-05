package nl.matsgemmeke.battlegrounds.item.recoil;

import nl.matsgemmeke.battlegrounds.entity.RecoilReceiver;
import org.jetbrains.annotations.NotNull;

import java.util.TimerTask;

public class CameraMovementTask extends TimerTask {

    private boolean recovering;
    private float pitchRotation;
    private float yawRotation;
    private float recoveryRate;
    private int recoilRotations;
    private int recoveryRotations;
    private int rotationCount;
    @NotNull
    private RecoilReceiver receiver;

    public CameraMovementTask(@NotNull RecoilReceiver receiver) {
        this.receiver = receiver;
        this.recovering = false;
        this.rotationCount = 0;
    }

    public float getPitchRotation() {
        return pitchRotation;
    }

    public void setPitchRotation(float pitchRotation) {
        this.pitchRotation = pitchRotation;
    }

    public int getRecoilRotations() {
        return recoilRotations;
    }

    public void setRecoilRotations(int recoilRotations) {
        this.recoilRotations = recoilRotations;
    }

    public float getRecoveryRate() {
        return recoveryRate;
    }

    public void setRecoveryRate(float recoveryRate) {
        this.recoveryRate = recoveryRate;
    }

    public int getRecoveryRotations() {
        return recoveryRotations;
    }

    public void setRecoveryRotations(int recoveryRotations) {
        this.recoveryRotations = recoveryRotations;
    }

    public float getYawRotation() {
        return yawRotation;
    }

    public void setYawRotation(float yawRotation) {
        this.yawRotation = yawRotation;
    }

    public void run() {
        if (!receiver.canReceiveRecoil()) {
            this.cancel();
            return;
        }

        if (++rotationCount > recoilRotations) {
            if (recoveryRate <= 0.0f || recovering) {
                this.cancel();
                return;
            }

            // The difference between the amount of rotations for the recoil and the recovery
            float recoveryFactor = (float) recoveryRotations / (float) recoilRotations;

            // Recalculate the rotations and reset the counter so the recoil gets repeated but with the recovery values
            yawRotation = (yawRotation * -recoveryRate) / recoveryFactor;
            pitchRotation = (pitchRotation * -recoveryRate) / recoveryFactor;

            recovering = true;
            recoilRotations = recoveryRotations;
            rotationCount = 0;
        }

        receiver.modifyCameraRotation(yawRotation, pitchRotation);
    }
}
