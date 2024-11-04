package nl.matsgemmeke.battlegrounds.item.effect;

import nl.matsgemmeke.battlegrounds.item.ItemHolder;
import nl.matsgemmeke.battlegrounds.item.effect.source.EffectSource;
import org.jetbrains.annotations.NotNull;

public class ItemEffectContext {

    @NotNull
    private EffectSource source;
    @NotNull
    private ItemHolder holder;

    public ItemEffectContext(@NotNull ItemHolder holder, @NotNull EffectSource source) {
        this.holder = holder;
        this.source = source;
    }

    @NotNull
    public ItemHolder getHolder() {
        return holder;
    }

    @NotNull
    public EffectSource getSource() {
        return source;
    }

    public void setHolder(@NotNull ItemHolder holder) {
        this.holder = holder;
    }

    public void setSource(@NotNull EffectSource source) {
        this.source = source;
    }
}
