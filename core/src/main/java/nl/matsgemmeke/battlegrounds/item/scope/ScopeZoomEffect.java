package nl.matsgemmeke.battlegrounds.item.scope;

import nl.matsgemmeke.battlegrounds.item.ItemEffect;
import org.jetbrains.annotations.NotNull;

public class ScopeZoomEffect implements ItemEffect {

    private static final float DEFAULT_MAGNIFICATION = 0.1f;

    private float currentMagnification;
    private float magnification;
    @NotNull
    private ScopeUser scopeUser;

    public ScopeZoomEffect(@NotNull ScopeUser scopeUser, float magnification) {
        this.scopeUser = scopeUser;
        this.magnification = magnification;
    }

    public float getMagnification() {
        return magnification;
    }

    public void setMagnification(float magnification) {
        this.magnification = magnification;
    }

    public void apply() {
        scopeUser.applyViewMagnification(magnification);

        currentMagnification = magnification;
    }

    public void remove() {
        scopeUser.applyViewMagnification(DEFAULT_MAGNIFICATION);
    }

    public boolean update() {
        // Do not update if the magnification was unchanged
        if (currentMagnification == magnification) {
            return false;
        }

        currentMagnification = magnification;
        scopeUser.applyViewMagnification(magnification);
        return true;
    }
}
