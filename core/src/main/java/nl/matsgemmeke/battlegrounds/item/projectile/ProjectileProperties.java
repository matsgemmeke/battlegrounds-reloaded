package nl.matsgemmeke.battlegrounds.item.projectile;

import nl.matsgemmeke.battlegrounds.item.projectile.effect.ProjectileEffect;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ProjectileProperties {

    @NotNull
    private List<ProjectileEffect> effects;

    public ProjectileProperties() {
        this.effects = new ArrayList<>();
    }

    @NotNull
    public List<ProjectileEffect> getEffects() {
        return effects;
    }
}
