package nl.matsgemmeke.battlegrounds.item.scope;

import nl.matsgemmeke.battlegrounds.entity.ScopeUser;
import nl.matsgemmeke.battlegrounds.item.ItemEffect;
import org.jetbrains.annotations.NotNull;

public class ScopeZoomEffect implements ItemEffect {

    private static final float DEFAULT_MAGNIFICATION = 0.1f;

    private float currentMagnification;
    private float magnification;
    @NotNull
    private ScopeUser user;

    public ScopeZoomEffect(@NotNull ScopeUser user, float magnification) {
        this.user = user;
        this.magnification = magnification;
    }

    public float getMagnification() {
        return magnification;
    }

    public void setMagnification(float magnification) {
        this.magnification = magnification;
    }

    public void apply() {
        user.applyViewMagnification(magnification);

        currentMagnification = magnification;
    }

    public void remove() {
        user.applyViewMagnification(DEFAULT_MAGNIFICATION);
    }

    public boolean update() {
        // Do not update if the magnification was unchanged
        if (currentMagnification == magnification) {
            return false;
        }

        currentMagnification = magnification;
        user.applyViewMagnification(magnification);
        return true;
    }
}
