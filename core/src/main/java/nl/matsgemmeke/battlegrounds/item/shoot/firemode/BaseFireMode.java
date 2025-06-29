package nl.matsgemmeke.battlegrounds.item.shoot.firemode;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseFireMode implements FireMode {

    @NotNull
    private final List<ShotObserver> shotObservers;

    public BaseFireMode() {
        this.shotObservers = new ArrayList<>();
    }

    public void addShotObserver(@NotNull ShotObserver observer) {
        shotObservers.add(observer);
    }

    public void notifyShotObservers() {
        shotObservers.forEach(ShotObserver::onShotFired);
    }
}
