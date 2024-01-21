package com.github.matsgemmeke.battlegrounds.item;

import com.github.matsgemmeke.battlegrounds.InternalsProvider;
import com.github.matsgemmeke.battlegrounds.api.entity.BattleItemHolder;
import com.github.matsgemmeke.battlegrounds.api.entity.BattlePlayer;
import com.github.matsgemmeke.battlegrounds.api.game.BattleSound;
import com.github.matsgemmeke.battlegrounds.api.game.GameContext;
import com.github.matsgemmeke.battlegrounds.api.item.ScopeAttachment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class DefaultScopeAttachment implements ScopeAttachment {

    @Nullable
    private BattlePlayer currentPlayer;
    @NotNull
    private Collection<Float> magnificationSettings;
    private float currentMagnification;
    @NotNull
    private GameContext context;
    @NotNull
    private InternalsProvider internals;
    private Iterable<BattleSound> stopUseSounds;
    private Iterable<BattleSound> useSounds;
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

    public Iterable<BattleSound> getStopUseSounds() {
        return stopUseSounds;
    }

    public void setStopUseSounds(Iterable<BattleSound> stopUseSounds) {
        this.stopUseSounds = stopUseSounds;
    }

    public Iterable<BattleSound> getUseSounds() {
        return useSounds;
    }

    public void setUseSounds(Iterable<BattleSound> useSounds) {
        this.useSounds = useSounds;
    }

    public boolean applyEffect(@NotNull BattleItemHolder holder) {
        // Do not apply the effect if one is already being used
        if (currentEffect != null) {
            return false;
        }

        if (!(holder instanceof BattlePlayer battlePlayer)) {
            return false;
        }

        context.playSounds(useSounds, battlePlayer.getEntity().getLocation());

        currentEffect = new ScopeZoomEffect(battlePlayer.getEntity(), internals, currentMagnification);
        currentEffect.apply();

        currentPlayer = battlePlayer;

        return battlePlayer.addEffect(currentEffect);
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
