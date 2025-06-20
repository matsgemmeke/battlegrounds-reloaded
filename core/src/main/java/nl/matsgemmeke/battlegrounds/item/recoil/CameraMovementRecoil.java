package nl.matsgemmeke.battlegrounds.item.recoil;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.Timer;

public class CameraMovementRecoil implements Recoil {

    private Float[] horizontalRecoilValues;
    private Float[] verticalRecoilValues;
    private float recoveryRate;
    private long kickbackDuration;
    private long recoveryDuration;
    private long rotationDuration;
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

    public long getKickbackDuration() {
        return kickbackDuration;
    }

    public void setKickbackDuration(long kickbackDuration) {
        this.kickbackDuration = kickbackDuration;
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

    public long getRotationDuration() {
        return rotationDuration;
    }

    public void setRotationDuration(long rotationDuration) {
        this.rotationDuration = rotationDuration;
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

        // Do not schedule a rotation task or if the duration of the kickback or rotation is zero
        if (kickbackDuration <= 0 || rotationDuration <= 0) {
            receiver.modifyCameraRotation(horizontalRecoil, verticalRecoil);
            return direction;
        }

        int rotationAmount = (int) (kickbackDuration / rotationDuration);
        float yawRotation = horizontalRecoil / rotationAmount / receiver.getRelativeAccuracy();
        float pitchRotation = verticalRecoil / rotationAmount / receiver.getRelativeAccuracy();

        int recoveryRotationAmount = (int) ((double) recoveryDuration / (double) kickbackDuration * rotationAmount);

        CameraMovementTask task = new CameraMovementTask(receiver);
        task.setRecoilRotations(rotationAmount);
        task.setRecoveryRate(recoveryRate);
        task.setRecoveryRotations(recoveryRotationAmount);
        task.setYawRotation(yawRotation);
        task.setPitchRotation(pitchRotation);

        timer.scheduleAtFixedRate(task, 0, rotationDuration);

        // Camera movement recoil does not affect the projectile direction, so return the original direction
        return direction;
    }
}
