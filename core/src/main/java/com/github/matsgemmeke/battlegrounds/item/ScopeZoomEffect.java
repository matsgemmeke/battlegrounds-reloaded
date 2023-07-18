package com.github.matsgemmeke.battlegrounds.item;

import com.github.matsgemmeke.battlegrounds.InternalsProvider;
import com.github.matsgemmeke.battlegrounds.api.item.PlayerEffect;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ScopeZoomEffect implements PlayerEffect {

    private static final float DEFAULT_MAGNIFICATION = 0.1f;

    private float currentMagnification;
    private float magnification;
    @NotNull
    private InternalsProvider internals;
    @NotNull
    private Player player;

    public ScopeZoomEffect(
            @NotNull Player player,
            @NotNull InternalsProvider internals,
            float magnification
    ) {
        this.player = player;
        this.internals = internals;
        this.magnification = magnification;
    }

    public float getMagnification() {
        return magnification;
    }

    public void setMagnification(float magnification) {
        this.magnification = magnification;
    }

    public void apply() {
        internals.setScopeLevel(player, magnification);

        currentMagnification = magnification;
    }

    public void remove() {
        internals.setScopeLevel(player, DEFAULT_MAGNIFICATION);
    }

    public boolean update() {
        if (currentMagnification == magnification) {
            return false;
        }

        currentMagnification = magnification;
        internals.setScopeLevel(player, magnification);
        return true;
    }
}
