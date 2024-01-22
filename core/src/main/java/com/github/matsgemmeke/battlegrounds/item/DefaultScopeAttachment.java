package com.github.matsgemmeke.battlegrounds.item;

import com.github.matsgemmeke.battlegrounds.InternalsProvider;
import com.github.matsgemmeke.battlegrounds.api.entity.GamePlayer;
import com.github.matsgemmeke.battlegrounds.api.entity.ItemHolder;
import com.github.matsgemmeke.battlegrounds.api.game.GameContext;
import com.github.matsgemmeke.battlegrounds.api.game.GameSound;
import com.github.matsgemmeke.battlegrounds.api.item.ScopeAttachment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class DefaultScopeAttachment implements ScopeAttachment {

    @NotNull
    private Collection<Float> magnificationSettings;
    private float currentMagnification;
    @NotNull
    private GameContext context;
    @Nullable
    private GamePlayer currentPlayer;
    @NotNull
    private InternalsProvider internals;
    private Iterable<GameSound> stopUseSounds;
    private Iterable<GameSound> useSounds;
    @Nullable
    private ScopeZoomEffect currentEffect;

    public DefaultScopeAttachment(
            @NotNull GameContext context,
            @NotNull InternalsProvider internals,
            float currentMagnification
    ) {
        this.context = context;
        this.internals = internals;
        this.currentMagnification = currentMagnification;
    }

    public float getCurrentMagnification() {
        return currentMagnification;
    }

    public Iterable<GameSound> getStopUseSounds() {
        return stopUseSounds;
    }

    public void setStopUseSounds(Iterable<GameSound> stopUseSounds) {
        this.stopUseSounds = stopUseSounds;
    }

    public Iterable<GameSound> getUseSounds() {
        return useSounds;
    }

    public void setUseSounds(Iterable<GameSound> useSounds) {
        this.useSounds = useSounds;
    }

    public boolean applyEffect(@NotNull ItemHolder holder) {
        // Do not apply the effect if one is already being used
        if (currentEffect != null) {
            return false;
        }

        if (!(holder instanceof GamePlayer gamePlayer)) {
            return false;
        }

        context.playSounds(useSounds, gamePlayer.getEntity().getLocation());

        currentEffect = new ScopeZoomEffect(gamePlayer.getEntity(), internals, currentMagnification);
        currentEffect.apply();

        currentPlayer = gamePlayer;

        return gamePlayer.addEffect(currentEffect);
    }

    public boolean isScoped() {
        return currentPlayer != null;
    }

    public boolean nextMagnification() {
        return false;
    }

    public boolean removeEffect() {
        if (currentEffect == null || currentPlayer == null) {
            return false;
        }

        context.playSounds(stopUseSounds, currentPlayer.getEntity().getLocation());

        currentEffect.remove();
        currentPlayer.removeEffect(currentEffect);

        currentEffect = null;
        currentPlayer = null;

        return true;
    }
}
